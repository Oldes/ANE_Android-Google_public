package com.amanitadesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.common.api.Result;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class GoogleApiHelper {
    static final String TAG = "GoogleApiHelper";
    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;
    private AchievementsClient mAchievementsClient = null;
    private SnapshotsClient mSnapshotsClient = null;
    private SavedGame mCurrentSave = null;

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static int RC_ACHIEVEMENT_UI = 9003;
    // Request code for saving the game to a snapshot.
    private static final int RC_SAVE_SNAPSHOT = 9004;
    private static final int RC_LOAD_SNAPSHOT = 9005;

    public GoogleApiHelper(Activity activity) {
        // Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(activity,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
    }

    public boolean isSignInAvailable() {
        return mGoogleSignInClient != null;
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(GoogleExtension.appContext) != null;
    }

    public boolean signIn() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.silentSignIn().addOnCompleteListener(
                    new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInSilently(): success");
                                GoogleExtension.extensionContext.dispatchEvent("ON_SIGN_IN_SUCCESS");
                                GoogleExtension.googleApiHelper.onConnected(task.getResult());
                            } else {
                                Log.d(TAG, "signInSilently(): failure", task.getException());
                                GoogleExtension.extensionContext.dispatchEvent("ON_SIGN_IN_FAIL");
                                GoogleExtension.googleApiHelper.onDisconnected();
                            }
                        }
                    });
        }
        return true;
    }

    public void signOut() {
        Log.d(TAG, "signOut()");
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean successful = task.isSuccessful();
                            Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));
                            onDisconnected();
                        }
                    });
        }
    }

    public void reportAchievement(String id, double percent) {
        if (percent == 0) {// it means we have unlocked it.
            mAchievementsClient.unlock(id);
        } else {
            if (percent > 0 && percent <= 1){
                mAchievementsClient.setSteps(id, (int)(percent * 100));
            }
        }
    }

    private void showAchievements() {
        if (isSignedIn()) {
            mAchievementsClient
                    .getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            GoogleExtension.extensionContext.getActivity().startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                        }
                    });
        }
    }


    public void onConnected(GoogleSignInAccount googleSignInAccount) {
        Context ctx = GoogleExtension.appContext;
        if (mSignedInAccount != googleSignInAccount) {
            mSignedInAccount = googleSignInAccount;
            mAchievementsClient = Games.getAchievementsClient(ctx, GoogleSignIn.getLastSignedInAccount(ctx));
            mSnapshotsClient = Games.getSnapshotsClient(ctx, googleSignInAccount);
        }
    }

    public void onDisconnected() {
    }


    // ========== SNAPSHOTS ======================================================
    //Create a HashMap
    private Map<String, SavedGame> mSaveGamesData =  new HashMap<String, SavedGame>();

    private Task<SnapshotsClient.DataOrConflict<Snapshot>> waitForClosedAndOpen(final String snapshotName) {

        Log.i(TAG, "Opening snapshot using snapshotName: " + snapshotName);

        return SnapshotCoordinator.getInstance()
                .waitForClosed(snapshotName)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        GoogleExtension.handleException(e, "There was a problem waiting for the file to close!");
                    }
                })
                .continueWithTask(new Continuation<Result, Task<SnapshotsClient.DataOrConflict<Snapshot>>>() {
                    @Override
                    public Task<SnapshotsClient.DataOrConflict<Snapshot>> then(@NonNull Task<Result> task) throws Exception {
                        Task<SnapshotsClient.DataOrConflict<Snapshot>>
                                openTask = SnapshotCoordinator.getInstance().open(mSnapshotsClient, snapshotName, true);
                        return openTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                GoogleExtension.handleException(e, "openSnapshotFailed");
                            }
                        });
                    }
                });
    }


    boolean openSnapshot(String name) {
        if (!isSignedIn()) return false;
        SavedGame save = mSaveGamesData.get(name);
        if(save == null) {
            save = new SavedGame(name, null, -3);
            mSaveGamesData.put(name, save);
        }
        if(save.isOpening) return true;

        save.isOpening = true;
        mCurrentSave = save;

        waitForClosedAndOpen(name)
                .addOnSuccessListener(new OnSuccessListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
                    @Override
                    public void onSuccess(SnapshotsClient.DataOrConflict<Snapshot> result) {

                        // if there is a conflict  - then resolve it.
                        Snapshot snapshot = processOpenDataOrConflict(RC_LOAD_SNAPSHOT, result, 0);

                        if (snapshot == null) {
                            Log.w(TAG, "Conflict was not resolved automatically, waiting for user to resolve.");
                        } else {
                            try {
                                readSavedGame(snapshot);
                                Log.i(TAG, "Snapshot loaded.");
                            } catch (IOException e) {
                                Log.e(TAG, "Error while reading snapshot contents: " + e.getMessage());
                            }
                        }

                        SnapshotCoordinator.getInstance().discardAndClose(mSnapshotsClient, snapshot)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        GoogleExtension.handleException(e, "There was a problem discarding the snapshot!");
                                    }
                                });

//                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//                            mLoadingDialog.dismiss();
//                            mLoadingDialog = null;
//                        }
                    }
                });
        return true;
    }

    SavedGame readSnapshot(String name) {
        SavedGame save = mSaveGamesData.get(name);
        Log.d(TAG, "[readSnapshot]..."+ name+" "+save);
        //if(save == null) openSnapshot(name);
        return save;
    }

    void saveSnapshot(String name, byte[] data, long time) {
        if(!isSignedIn()) {
            GoogleExtension.extensionContext.dispatchEvent("saveSnapshotFailed", name +" user not connected!");
            return;
        }
        SavedGame save = mSaveGamesData.get(name);
        if (save == null) {
            save = new SavedGame(name, data, time);
            mSaveGamesData.put(name, save);
        } else {
            save.setData(data);
            save.setTime(time);
        }
        writeSnapshotData(save.getSnapshot(), save);
    }

    private void writeSnapshotData(Snapshot toWrite, SavedGame save) {

        if(toWrite == null || toWrite.getSnapshotContents() == null || toWrite.getSnapshotContents().isClosed()) {
            save.needsWrite = true;
            openSnapshot(save.getName());
        } else {
            //Log.d(TAG, "[writeSnapshotData]...writting.. "+ save);
            toWrite.getSnapshotContents().writeBytes(save.getData());
            // Save the snapshot.
            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                    //.setCoverImage(getScreenShot())
                    .setDescription("Modified data at: " + Calendar.getInstance().getTime())
                    .setPlayedTimeMillis(save.getTime())
                    .build();
            SnapshotCoordinator.getInstance().commitAndClose(mSnapshotsClient, toWrite, metadataChange);
            save.setSnapshot(null);
            save.needsWrite = false;
            openSnapshot(save.getName()); //for later use
        }
    }

    void deleteSnapshot(String name) {
        if(!isSignedIn()) {
            GoogleExtension.extensionContext.dispatchEvent("deleteSnapshotFailed", name +" user not connected!");
            return;
        }
        SavedGame save = mSaveGamesData.get(name);
        if (save != null) {
            Snapshot snapshot = save.getSnapshot();
            //Log.d(TAG, "deleteSnapshot: "+name+" "+save.isOpening);
            save.clearData();
            //Log.d(TAG, "deleteSnapshot: "+save);
            SnapshotCoordinator.getInstance().delete(mSnapshotsClient, snapshot.getMetadata());
        }
    }


    private void readSavedGame(Snapshot snapshot) throws IOException {
        mCurrentSave.setData(snapshot.getSnapshotContents().readFully());
    }



    String getCurrentSaveName() {
        return mCurrentSave.getName();
    }

    /**
     * Conflict resolution for when Snapshots are opened.
     *
     * @param requestCode - the request currently being processed.  This is used to forward on the
     *                    information to another activity, or to send the result intent.
     * @param result      The open snapshot result to resolve on open.
     * @param retryCount  - the current iteration of the retry.  The first retry should be 0.
     * @return The opened Snapshot on success; otherwise, returns null.
     */
    Snapshot processOpenDataOrConflict(int requestCode,
                                       SnapshotsClient.DataOrConflict<Snapshot> result,
                                       int retryCount) {

        retryCount++;

        if (!result.isConflict()) {
            return result.getData();
        }

        Log.i(TAG, "Open resulted in a conflict!");

        SnapshotsClient.SnapshotConflict conflict = result.getConflict();
        final Snapshot snapshot = conflict.getSnapshot();
        final Snapshot conflictSnapshot = conflict.getConflictingSnapshot();

     //   ArrayList<Snapshot> snapshotList = new ArrayList<Snapshot>(2);
     //   snapshotList.add(snapshot);
     //   snapshotList.add(conflictSnapshot);

        // Display both snapshots to the user and allow them to select the one to resolve.
     //    selectSnapshotItem(requestCode, snapshotList, conflict.getConflictId(), retryCount);

        // Since we are waiting on the user for input, there is no snapshot available; return null.
        return null;
    }
}
