package com.amanitadesign.functions;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

public class GetPackageVersionCodeFunction implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			Activity act = ctx.getActivity();
			PackageManager pm = act.getPackageManager() ;
			PackageInfo manager = pm.getPackageInfo(act.getPackageName(), PackageManager.GET_META_DATA);
			result = FREObject.newObject(manager.versionCode);
		} catch (FREWrongThreadException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}
}
