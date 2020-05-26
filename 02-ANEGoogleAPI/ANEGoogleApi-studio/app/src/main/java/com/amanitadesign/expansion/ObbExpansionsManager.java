package com.amanitadesign.expansion;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;

import androidx.core.content.pm.PackageInfoCompat;

public class ObbExpansionsManager
{
    private String packageName;
    private int packageVersion;
    private int patchVersion;
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

    public void getStatus()
    {
        Log.d(GoogleExtension.TAG, "Manager -> getStatus()");

        this.packageName = this.context.getPackageName();

        int versionCode = GoogleExtensionContext.getVersionNumber();
        if (versionCode == 1) {
            try
            {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                long longVersionCode= PackageInfoCompat.getLongVersionCode(pInfo);
                versionCode = (int) longVersionCode; // avoid huge version numbers and you will be ok
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        this.packageVersion = versionCode;

        int patchCode = GoogleExtensionContext.getPatchNumber();
        if (patchCode == 1) {
            patchCode = this.packageVersion;
        }
        this.patchVersion = patchCode;

        Log.i(GoogleExtension.TAG, "Manager -> getStatus() -> main version: " + this.packageVersion + " -> patchVersion: " + this.patchVersion + " -> packageName: " + this.packageName);

        this.sm = ((StorageManager)this.context.getSystemService(Context.STORAGE_SERVICE));

        this.patchFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + this.packageName + "/" +
                "patch." + this.patchVersion + "." + this.packageName + ".obb");
        this.mainFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + this.packageName + "/" +
                "main." + this.packageVersion + "." + this.packageName + ".obb");
        if (this.patchFile.exists())
        {
            if (this.sm.isObbMounted(this.patchFile.getAbsolutePath()))
            {
                this.patch = this.sm.getMountedObbPath(this.patchFile.getAbsolutePath());
                requestMountMain();
            }
            else
            {
                this.mainChecker = new MountChecker(false);
                mountPatch();
            }
        }
        else {
            requestMountMain();
        }
        if ((!this.mainFile.exists()) && (!this.patchFile.exists())) {
            this.listener.onFilesNotFound();
        }
    }

    private void requestMountMain()
    {
        if (this.mainChecker != null)
        {
            this.mainChecker.cancel();
            this.mainChecker = null;
        }
        this.mainChecker = new MountChecker(true);
        if (this.sm.isObbMounted(this.mainFile.getAbsolutePath()))
        {
            this.main = this.sm.getMountedObbPath(this.mainFile.getAbsolutePath());
            this.listener.onMountSuccess();
        }
        else
        {
            mountMain();
        }
    }

    private void mountPatch()
    {
        Log.i(GoogleExtension.TAG, "mountPatch()");

        new Timer().schedule(this.mainChecker, 1000L);
        this.sm.mountObb(this.patchFile.getAbsolutePath(), null, new OnObbStateChangeListener()
        {
            public void onObbStateChange(String path, int state)
            {
                Log.d(GoogleExtension.TAG, "Mounting patch file done -> state: " + state);
                super.onObbStateChange(path, state);
                if (state != 1) {
                    ObbExpansionsManager.this.listener.onObbStateChange(path, state);
                }
            }
        });
    }

    private void mountMain()
    {
        if (this.mainFile.exists())
        {
            Log.i(GoogleExtension.TAG, "mountMain()");

            new Timer().schedule(this.mainChecker, 1000L);

            this.sm.mountObb(this.mainFile.getAbsolutePath(), null, new OnObbStateChangeListener()
            {
                public void onObbStateChange(String path, int state)
                {
                    super.onObbStateChange(path, state);
                    if (state == 1)
                    {
                        Log.d(GoogleExtension.TAG, "Mounting main file done.");
                        ObbExpansionsManager.this.main = ObbExpansionsManager.this.sm.getMountedObbPath(ObbExpansionsManager.this.mainFile.getAbsolutePath());
                        ObbExpansionsManager.this.listener.onMountSuccess();
                        ObbExpansionsManager.this.mainChecker.cancel();
                        ObbExpansionsManager.this.mainChecker = null;
                    }
                    else
                    {
                        ObbExpansionsManager.this.listener.onObbStateChange(path, state);
                    }
                }
            });
        }
        else
        {
            Log.d(GoogleExtension.TAG, "Main file not found");
        }
    }

    public static boolean isMainFileExists(Context context)
    {
        String packageName = context.getPackageName();
        int pv = getAppVersionCode(context, false);
        File main = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/" +
                "main." + pv + "." + packageName + ".obb");
        return main.exists();
    }

    public static boolean isPatchFileExists(Context context)
    {
        String packageName = context.getPackageName();
        int pv = getAppVersionCode(context, true);
        File patch = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/" +
                "patch." + pv + "." + packageName + ".obb");
        return patch.exists();
    }

    private static int getAppVersionCode(Context context, Boolean patch)
    {
        int versionCode;
        if (!patch)
        {
            versionCode = GoogleExtensionContext.getVersionNumber();
            if (versionCode == 1) {
                try
                {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    long longVersionCode= PackageInfoCompat.getLongVersionCode(pInfo);
                    versionCode = (int) longVersionCode;
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            versionCode = GoogleExtensionContext.getPatchNumber();
            if (versionCode == 1) {
                try
                {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    long longVersionCode= PackageInfoCompat.getLongVersionCode(pInfo);
                    versionCode = (int) longVersionCode;
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return versionCode;
    }

    public String getMainRoot()
    {
        return this.sm.getMountedObbPath(this.mainFile.getAbsolutePath());
    }

    public String getPatchRoot()
    {
        return this.sm.getMountedObbPath(this.patchFile.getAbsolutePath());
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

    public File getFile(String pathToFile)
    {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(this.patch + pathToFile);
        if (file.exists()) {
            return file;
        }
        file = new File(this.main + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public File getFileFromMain(String pathToFile)
    {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(this.main + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public File getFileFromPatch(String pathToFile)
    {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(this.patch + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
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

                    ObbExpansionsManager.this.requestMountMain();
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
