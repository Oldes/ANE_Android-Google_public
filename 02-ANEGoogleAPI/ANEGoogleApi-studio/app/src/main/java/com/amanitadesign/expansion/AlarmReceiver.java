package com.amanitadesign.expansion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amanitadesign.GoogleExtension;

public class AlarmReceiver
        extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        try {
            Log.i(GoogleExtension.TAG, "AlarmReceiver -> onReceive();");
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ObbDownloaderService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}