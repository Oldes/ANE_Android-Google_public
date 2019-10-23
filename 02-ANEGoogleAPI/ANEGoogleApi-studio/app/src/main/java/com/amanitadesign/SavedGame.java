package com.amanitadesign;

//import android.util.Log;

import com.google.android.gms.games.snapshot.Snapshot;

/**
 * Created by Oldes on 9/26/2016.
 */

public class SavedGame {
    private String mName;
    private byte[] mData;
    private long mTime;

    private Snapshot mSnapshot;

    public SavedGame(String name, byte[] data, long time) {
        mName = name;
        mData = data;
        mTime = time;
    }
    public String getName() {
        return mName;
    }
    public byte[] getData() {
        //Log.d("SaveGame", mName+ " getData: "+mData);
        return mData;
    }
    public long getTime() {
        return mTime;
    }
    public void setData(byte[] data) {
        //Log.d("SaveGame", mName+ " setData: "+data);
        mData = data;
    }
    public void clearData() {
        mData = null;
        mTime = -3;
    }
    public void setTime(long time) {
        mTime = time;
    }
    public Snapshot getSnapshot() {
        return mSnapshot;
    }
    public void setSnapshot(Snapshot snapshot) {
        mSnapshot = snapshot;
        if(snapshot != null) {
            try {
                mTime = (long) snapshot.getMetadata().getPlayedTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean needsWrite;
    public boolean needsDelete;
    public boolean isOpening;

    public String toString() {
        return "[SaveGame] "
                + mName
                + " data: " + (mData == null ? " null" : mData.length)
                + " time: " + mTime
                + " opening: " + isOpening
                + " needsWrite: " + needsWrite
                + " mSnapshot: "+(mSnapshot == null ? "null" : !mSnapshot.getSnapshotContents().isClosed());
    }
    public void dispose() {
        mData = null;
        mTime = -3;
        mSnapshot = null;
        isOpening = false;
        needsWrite = false;
        needsDelete = false;
    }
}