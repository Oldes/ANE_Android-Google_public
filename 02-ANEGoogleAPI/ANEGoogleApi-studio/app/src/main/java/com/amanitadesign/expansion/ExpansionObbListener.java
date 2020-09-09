package com.amanitadesign.expansion;

import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;

import android.app.Activity;
import android.util.Log;

public class ExpansionObbListener
        extends ObbExpansionsManager.ObbListener
{
    private static final String EXPANSION_STATUS = "EXPANSION_STATUS";

    public void onFilesNotFound()
    {
        GoogleExtensionContext.getExtensionContext().dispatchStatusEventAsync(EXPANSION_STATUS, "missing");
    }

    public void onMountSuccess()
    {
        /*
        Log.i(GoogleExtension.TAG, "ExpansionObbListener -> onMountSuccess()");
        GoogleExtensionContext ctx = GoogleExtensionContext.getExtensionContext();

        Activity act = GoogleExtensionContext.getMainActivity();

        boolean main = ObbExpansionsManager.isMainFileExists(act);
        boolean patch = ObbExpansionsManager.isPatchFileExists(act);

        ctx.dispatchStatusEventAsync(EXPANSION_STATUS, "found," + main + "," + patch);
        */
    }
}
