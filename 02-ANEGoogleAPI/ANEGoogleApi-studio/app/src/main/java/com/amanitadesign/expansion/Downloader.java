package com.amanitadesign.expansion;

import android.os.Messenger;
import android.util.Log;

import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

public class Downloader
        implements IDownloaderClient
{
    private static final String EXPANSION_PROGRESS = "EXPANSION_PROGRESS";
    private static final String EXPANSION_STATE = "EXPANSION_STATE";
    private IDownloaderService mRemoteService;
    private IStub mDownloaderClientStub;

    public void startDownload()
    {
        Log.i(GoogleExtension.TAG, "Downloader -> startDownload()");
        this.mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ObbDownloaderService.class);
        this.mDownloaderClientStub.connect(GoogleExtensionContext.getMainActivity().getApplicationContext());
    }

    public void resumeDownload()
    {
        if (this.mRemoteService != null) {
            Log.i(GoogleExtension.TAG, "Downloader -> resumeDownload()");
            this.mRemoteService.requestContinueDownload();
        }
    }

    public void stopDownload()
    {
        if (this.mRemoteService != null) {
            Log.i(GoogleExtension.TAG, "Downloader -> stopDownload()");
            this.mRemoteService.requestPauseDownload();
        }
    }

    public void destroy()
    {
        if (this.mDownloaderClientStub == null) {
            return;
        }
        this.mDownloaderClientStub.disconnect(GoogleExtensionContext.getMainActivity().getApplicationContext());
        GoogleExtensionContext.setDownloader(null);
        this.mDownloaderClientStub = null;
    }

    public void onDownloadProgress(DownloadProgressInfo progress)
    {
        GoogleExtensionContext ctx = GoogleExtensionContext.getExtensionContext();

        ctx.dispatchStatusEventAsync(EXPANSION_PROGRESS, ""
                + progress.mOverallProgress + ";"
                + progress.mOverallTotal + ";"
                + Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal)
        );
        /*
                Helpers.getDownloadProgressPercent(progress.mOverallProgress, progress.mOverallTotal) + ";"
                        + Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal) + ";"
                        + Helpers.getTimeRemaining(progress.mTimeRemaining) + ";"
                        + Helpers.getSpeedString(progress.mCurrentSpeed));
         */
    }

    public void onDownloadStateChanged(int newState)
    {
        Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: "+newState);
        GoogleExtension.extensionContext.dispatchEvent("EXPANSION_STATE", ""+newState);

        if (newState == IDownloaderClient.STATE_COMPLETED) destroy();

        
        boolean showDashboard = true;
        boolean showCellMessage = false;
        boolean paused = false;
        boolean indeterminate = false;

        GoogleExtensionContext ctx = GoogleExtensionContext.getExtensionContext();

        switch (newState)
        {
            case IDownloaderClient.STATE_IDLE:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Idle");
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_FETCHING_URL:
            case IDownloaderClient.STATE_CONNECTING:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Connecting | Fetching URL");
                showDashboard = true;
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_DOWNLOADING:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Downloading");
                showDashboard = true;
                break;
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Failed");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Failed");
                paused = true;
                showDashboard = false;
                break;
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Need Wifi");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Failed_Internet");
                showDashboard = false;
                paused = true;
                showCellMessage = true;
                break;
            case IDownloaderClient.STATE_PAUSED_NETWORK_UNAVAILABLE:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED:
            case IDownloaderClient.STATE_PAUSED_NEED_WIFI:
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_NETWORK_SETUP_FAILURE:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Need Internet");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Failed_Internet");
                showDashboard = false;
                paused = true;
                break;
            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Paused by request");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Paused");
                break;
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Paused by Roaming | SD card unavailable");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Paused");
                break;
            case IDownloaderClient.STATE_COMPLETED:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Complete");
                ctx.dispatchStatusEventAsync(EXPANSION_STATE, "Completed");
                destroy();
                showDashboard = false;
                indeterminate = true;
                return;
            case IDownloaderClient.STATE_FAILED_SDCARD_FULL:
            default:
                Log.i(GoogleExtension.TAG, "Downloader -> onDownloadStateChanged() -> state: Default");
                paused = true;
        }
        
    }

    public void onServiceConnected(Messenger m)
    {
        Log.i(GoogleExtension.TAG, "Downloader -> onServiceConnected()");
        this.mRemoteService = DownloaderServiceMarshaller.CreateProxy(m);
        this.mRemoteService.setDownloadFlags(1);
        this.mRemoteService.onClientUpdated(this.mDownloaderClientStub.getMessenger());
    }

}
