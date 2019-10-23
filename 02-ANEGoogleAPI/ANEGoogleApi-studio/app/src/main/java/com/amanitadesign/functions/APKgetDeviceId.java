package com.amanitadesign.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.amanitadesign.GoogleExtension;


public class APKgetDeviceId implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			result = FREObject.newObject(GoogleExtension.deviceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
