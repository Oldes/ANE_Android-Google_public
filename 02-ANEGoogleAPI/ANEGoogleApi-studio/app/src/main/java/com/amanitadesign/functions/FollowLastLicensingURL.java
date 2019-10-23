package com.amanitadesign.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.amanitadesign.GoogleExtension;

public final class FollowLastLicensingURL implements FREFunction {

    @Override
    public FREObject call(FREContext ctx, FREObject[] passedArgs) {
        try{
            GoogleExtension.mAPKExpansionPolicy.resetPolicy();
            GoogleExtension.licenseChecker.followLastLicensingUrl(GoogleExtension.appContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
