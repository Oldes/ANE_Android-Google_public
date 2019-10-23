package com.amanitadesign.functions;

import android.os.Environment;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;


public class APKgetExternalStorageDirectory implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			result = FREObject.newObject(String.valueOf(Environment.getExternalStorageDirectory()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
