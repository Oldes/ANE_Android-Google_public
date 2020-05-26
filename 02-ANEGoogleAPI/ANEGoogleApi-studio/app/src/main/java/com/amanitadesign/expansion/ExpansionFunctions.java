package com.amanitadesign.expansion;

import com.adobe.fre.FREObject;
import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/**
 * Created by Oldes on 12/1/2016.
 */

public class ExpansionFunctions {
    //private static final byte[] SALT = { 1, 42, -12, -1, 54, 98,
    //        -100, -12, 43, 2, -8, -4, 9, 5, -106, -107, -33, 45, -1, 84 };

    static public class ExpansionFilesStatus implements FREFunction  {

        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(GoogleExtension.TAG, "ExpansionFilesStatusFunction -> call()");
            int versionNumber = 1;
            int patchNumber = 1;
            try {
                versionNumber = args[0].getAsInt();
                patchNumber = args[1].getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (GoogleExtensionContext.getVersionNumber() == 1) {
                GoogleExtensionContext.setVersionNumber(versionNumber);
            }
            if (GoogleExtensionContext.getPatchNumber() == 1) {
                GoogleExtensionContext.setPatchNumber(patchNumber);
            }
            ObbExpansionsManager manager = GoogleExtensionContext.getManager();
            manager.getStatus();

            return null;
        }
    }

    static public class StartDownload implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            try{
                ObbDownloaderService.BASE64_PUBLIC_KEY = args[0].getAsString();

                Intent notifierIntent = new Intent(ctx.getActivity(), ctx.getActivity().getClass());
                notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(ctx.getActivity(), 0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                int startResult = DownloaderService.startDownloadServiceIfRequired(GoogleExtension.appContext,
                        pendingIntent, ObbDownloaderService.class);
                Log.i(GoogleExtension.TAG, "StartDownload result: " + startResult);
                if (startResult != DownloaderService.NO_DOWNLOAD_REQUIRED) {
                    Downloader dl = new Downloader();
                    GoogleExtensionContext.setDownloader(dl);
                    dl.startDownload();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static public class StopDownload implements FREFunction {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(GoogleExtension.TAG, "StopDownload -> call()");

            Downloader dl = GoogleExtensionContext.getDownloader();
            if (dl != null) {
                dl.stopDownload();
            }
            return null;
        }
    }

    static public class ResumeDownload implements FREFunction {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(GoogleExtension.TAG, "ResumeDownload -> call()");

            Downloader dl = GoogleExtensionContext.getDownloader();
            if (dl != null) {
                dl.resumeDownload();
            }
            return null;
        }
    }
}
