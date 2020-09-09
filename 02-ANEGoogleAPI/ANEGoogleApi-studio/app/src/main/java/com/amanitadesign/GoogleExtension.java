package com.amanitadesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;
import com.amanitadesign.functions.CheckLicenseFunction;
import com.android.billingclient.api.BillingClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.APKExpansionPolicy;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.ServerManagedPolicy;

import java.io.File;

import static android.provider.Settings.Secure.*;

//import androidx.appcompat.app.AlertDialog;

/**
 * Created by Oldes on 9/16/2016.
 */
public class GoogleExtension implements FREExtension {
    public static final String TAG = "AmanitaGoogleAPI";
    public static final int VERBOSE = 3;

    public static byte[] SALT = new byte[] {
            -9, -101, -2, 6, -7, 11, 124, 9, 2, -95, -34, 112, 33, -40, 76, -47, 21, -9, 46, 43
    };

    public static GoogleExtensionContext extensionContext;
    public static BillingClient billingClient = null;
    public static Context appContext;
    public static String deviceId;
    public static Intent googleAPIActivityIntent;
	public static CheckLicenseFunction licenseCheckerCtx;
    public static LicenseChecker licenseChecker;
    public static GoogleApiHelper googleApiHelper;

    public static APKExpansionPolicy mAPKExpansionPolicy;
    public static ServerManagedPolicy mAPKServerPolicy;

    static public String getResourceString(String id) {
        Resources res = appContext.getResources();
        return res.getString(res.getIdentifier(id, "string", appContext.getPackageName()));
    }
    static public int getResourceStringID(String id) {
        if(VERBOSE>2) Log.d(TAG, "getResourceStringID: "+ id);
        return appContext.getResources().getIdentifier(id, "string", appContext.getPackageName());
    }
    static public int getResource(String defType, String id) {
        Resources res = appContext.getResources();
        return res.getIdentifier(id, defType, appContext.getPackageName());
    }

    public static void notifyLicenseStatus(String status, String error) {
        if(VERBOSE > 1) Log.i(TAG, "notifyLicenseStatus: "+status+" "+error);
        try {
            extensionContext.dispatchStatusEventAsync(status, error);
            if (error == "followLastLicensingUrl") {

            	//licenseCheckerCtx.followLastLicensingUrl();
            	if(VERBOSE > 1) Log.i(TAG, "notifyLicenseStatus: finish: "+GoogleExtensionContext.getMainActivity());
            	//GoogleExtensionContext.getMainActivity().finish();
            	//licenseCheckerCtx.onDestroy();
            }
        } catch (Exception e) {
            Log.e(TAG, "*** Failed to dispatch status!");
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    public static void init(Activity activity, byte salt0) {
        appContext = activity.getApplicationContext();
        deviceId = getString(appContext.getContentResolver(), ANDROID_ID);
        //SALT[0] = salt0;
        Log.i(TAG, "appContext:"+ appContext);
        Log.i(TAG, "deviceId:"+ deviceId);
        try {
            AESObfuscator obfus = new AESObfuscator(SALT, appContext.getPackageName(), deviceId);
            mAPKExpansionPolicy = new APKExpansionPolicy(appContext, obfus);
            mAPKServerPolicy = new ServerManagedPolicy(appContext, obfus);
            googleApiHelper = new GoogleApiHelper(activity);
        } catch(Exception e) {
            Log.e(TAG, "*** init failed!");
            e.printStackTrace();
        }
        if(VERBOSE > 2) {
            Log.i(TAG, "ExternalFilesDir: "+ appContext.getExternalFilesDir(null));
            Log.i(TAG, "ExternalCacheDir: "+ appContext.getExternalCacheDir());
            Log.i(TAG, "OBBDir:           "+ appContext.getObbDir());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.i(TAG, "ExternalMediaDirs: "+ appContext.getExternalMediaDirs());
            }
            Log.i(TAG, "googleApiHelper: "+ googleApiHelper);
        }
    }

    @Override
    public FREContext createContext(String contextType) {
        if(VERBOSE > 0) Log.i(TAG, "createContext: "+ contextType);
        extensionContext = new GoogleExtensionContext();

        return extensionContext;
    }

    @Override
    public void dispose() {
        if(VERBOSE > 0) Log.d(TAG, "Extension disposed.");
        
        if(billingClient != null) {
        	billingClient.endConnection();
        	billingClient = null;
        }
        if(licenseCheckerCtx != null) {
            licenseChecker = null;
            licenseCheckerCtx.onDestroy();
            licenseCheckerCtx = null;
        }
        appContext = null;
        extensionContext = null;
        googleAPIActivityIntent = null;
    }

    @Override
    public void initialize() {
        if(VERBOSE > 0) Log.d(TAG, "Extension initialized.");
    }

    public static void log(String message)
    {
        extensionContext.dispatchStatusEventAsync("LOGGING", message);
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for why the exception
     *                  happened
     */
    public static void handleException(Exception exception, String details) {
        int status = 0;
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }
        Log.i(TAG, "handleException status: " + status);
        exception.printStackTrace();

        String message = details + " (status "+ status +"). "+ exception;

        Activity activity = GoogleExtensionContext.getMainActivity();

        /*
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
         */
        // Note that showing a toast is done here for debugging. Your application should
        // resolve the error appropriately to your app.
        if (status == GamesClientStatusCodes.SNAPSHOT_NOT_FOUND) {
            Log.i(TAG, "Error: Snapshot not found");
            Toast.makeText(activity.getBaseContext(), "Error: Snapshot not found",
                    Toast.LENGTH_SHORT).show();
        } else if (status == GamesClientStatusCodes.SNAPSHOT_CONTENTS_UNAVAILABLE) {
            Log.i(TAG, "Error: Snapshot contents unavailable");
            Toast.makeText(activity.getBaseContext(), "Error: Snapshot contents unavailable",
                    Toast.LENGTH_SHORT).show();
        } else if (status == GamesClientStatusCodes.SNAPSHOT_FOLDER_UNAVAILABLE) {
            Log.i(TAG, "Error: Snapshot folder unavailable");
            Toast.makeText(activity.getBaseContext(), "Error: Snapshot folder unavailable.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressWarnings( "deprecation" )
    public static File getLegacyExternalStorageDirectory() {
        // for API18 and older... deprecated since API29
        return Environment.getExternalStorageDirectory();
    }
}
