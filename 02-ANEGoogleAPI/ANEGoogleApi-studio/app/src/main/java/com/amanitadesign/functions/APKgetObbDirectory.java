package com.amanitadesign.functions;

import android.os.Environment;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.amanitadesign.expansion.ObbExpansionsManager;


public class APKgetObbDirectory implements FREFunction  {

    @Override
    public FREObject call(FREContext ctx, FREObject[] passedArgs) {
        FREObject result = null;
        try{
            result = FREObject.newObject(String.valueOf(ObbExpansionsManager.getObbDir(ctx.getActivity().getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
