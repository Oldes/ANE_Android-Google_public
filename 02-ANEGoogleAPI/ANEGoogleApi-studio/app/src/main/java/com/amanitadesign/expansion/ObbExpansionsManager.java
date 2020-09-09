package com.amanitadesign.expansion;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;

import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ObbExpansionsManager
{
    private String main;
    private File mainFile;
    private String patch;
    private File patchFile;
    private StorageManager sm;
    private ObbListener listener;
    private Context context;
    private MountChecker mainChecker;
    private static ObbExpansionsManager instance;

    private ObbExpansionsManager(Context context, ObbListener listener)
    {
        this.context = context;
        this.listener = listener;
    }
    public static String getObbDir(Context context) {
        String dir;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dir = context.getObbDir().getAbsolutePath();
        } else {
            dir = GoogleExtension.getLegacyExternalStorageDirectory().getAbsolutePath() + "/Android/obb/" + context.getPackageName();
        }
        return dir;
    }

    public static ObbExpansionsManager createNewInstance(Context context, ObbListener listener)
    {
        instance = new ObbExpansionsManager(context, listener);
        return instance;
    }

    public static ObbExpansionsManager getInstance()
    {
        return instance;
    }
    public static File getMainOBBFile() {
        File file = null;
        try {
            int packageVersion = GoogleExtensionContext.getVersionNumber();
            Log.d("Amanita", "getMainOBBFile version: " + packageVersion);
            String packageName = instance.context.getPackageName();
            Log.d("Amanita", "getMainOBBFile name: " + packageName);
            file = new File(getObbDir(instance.context)+"/main." + packageVersion + "." + packageName + ".obb");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return file;
    }
    public static File getPatchOBBFile() {
        int packageVersion = GoogleExtensionContext.getVersionNumber();
        String packageName = instance.context.getPackageName();
        return new File(getObbDir(instance.context)+"/patch." + packageVersion + "." + packageName + ".obb");
    }
    public static boolean isMainFileExists() {
        File file = getMainOBBFile();
        return file.exists();
    }
    public static boolean isPatchFileExists() {
        File file = getPatchOBBFile();
        return file.exists();
    }

    public static abstract class ObbListener
            extends OnObbStateChangeListener
    {
        public void onObbStateChange(String path, int state)
        {
            super.onObbStateChange(path, state);
        }

        public abstract void onFilesNotFound();

        public abstract void onMountSuccess();
    }

    private class MountChecker
            extends TimerTask
    {
        private boolean isMainFile;

        private MountChecker(boolean isMainFile)
        {
            this.isMainFile = isMainFile;
        }

        public void run()
        {
            File file = this.isMainFile ? ObbExpansionsManager.this.mainFile : ObbExpansionsManager.this.patchFile;
            if ((ObbExpansionsManager.this.sm != null) && (file != null) && (ObbExpansionsManager.this.sm.isObbMounted(file.getAbsolutePath())))
            {
                if (this.isMainFile)
                {
                    ObbExpansionsManager.this.main = ObbExpansionsManager.this.sm.getMountedObbPath(file.getAbsolutePath());
                    ObbExpansionsManager.this.listener.onMountSuccess();
                }
                else
                {
                    ObbExpansionsManager.this.patch = ObbExpansionsManager.this.sm.getMountedObbPath(file.getAbsolutePath());

                    //ObbExpansionsManager.this.requestMountMain();
                }
            }
            else
            {
                ObbExpansionsManager.this.mainChecker = new MountChecker(this.isMainFile);
                new Timer().schedule(ObbExpansionsManager.this.mainChecker, 1000L);
            }
        }
    }
}
