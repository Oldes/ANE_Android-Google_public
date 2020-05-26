package com.amanitadesign.expansion;

import com.amanitadesign.GoogleExtension;
import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/**
 * This class demonstrates the minimal client implementation of the
 * DownloaderService from the Downloader library.
 */
public class ObbDownloaderService
        extends DownloaderService
{
    // stuff for LVL -- MODIFY FOR YOUR APPLICATION!
    public static String BASE64_PUBLIC_KEY = "YourLVLKey";


    public String getPublicKey()
    {
        return BASE64_PUBLIC_KEY;
    }

    public byte[] getSALT()
    {
        return GoogleExtension.SALT;
    }

    public String getAlarmReceiverClassName()
    {
        return AlarmReceiver.class.getName();
    }
}