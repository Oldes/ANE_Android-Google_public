package com.amanitadesign.functions;

import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.amanitadesign.GoogleExtension;
import com.google.android.vending.licensing.APKExpansionPolicy;

public class APKExpansionGetPatchFileSize implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			result = FREObject.newObject(GoogleExtension.mAPKExpansionPolicy.getExpansionFileSize(APKExpansionPolicy.PATCH_FILE_URL_INDEX));
		} catch (FREWrongThreadException e) {
			Log.d("APKExpansion", "##### AndroidLicensing - caught FREWrongThreadException");
			e.printStackTrace();
		}

		return result;
	}
}
