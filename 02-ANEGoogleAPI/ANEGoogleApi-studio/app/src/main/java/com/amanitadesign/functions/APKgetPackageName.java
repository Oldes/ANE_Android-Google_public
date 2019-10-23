package com.amanitadesign.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;


public class APKgetPackageName implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			result = FREObject.newObject(ctx.getActivity().getPackageName());
		} catch (Exception e) {
			Log.d("APKgetPackageName", "##### caught exception");
			e.printStackTrace();
		}
		return result;
	}
}
