package com.amanitadesign.expansion;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class ObbDownloaderService
        extends DownloaderService
{
    public static String BASE64_PUBLIC_KEY = "YourLVLKey";
    public static final byte[] SALT = { 1, 42, -12, -1, 54, 98,
            -100, -12, 43, 2, -8, -4, 9, 5, -106, -107, -33, 45, -1, 84 };

    public String getPublicKey()
    {
        return BASE64_PUBLIC_KEY;
    }

    public byte[] getSALT()
    {
        return SALT;
    }

    public String getAlarmReceiverClassName()
    {
        return AlarmReceiver.class.getName();
    }
}