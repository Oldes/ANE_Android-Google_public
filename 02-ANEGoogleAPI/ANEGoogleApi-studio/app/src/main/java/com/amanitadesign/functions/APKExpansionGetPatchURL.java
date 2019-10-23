package com.amanitadesign.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.amanitadesign.GoogleExtension;
import com.google.android.vending.licensing.APKExpansionPolicy;

public class APKExpansionGetPatchURL implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			result = FREObject.newObject(GoogleExtension.mAPKExpansionPolicy.getExpansionURL(APKExpansionPolicy.PATCH_FILE_URL_INDEX));
		} catch (FREWrongThreadException e) {
			e.printStackTrace();
		}

		return result;
	}
}
