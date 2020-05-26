package com.amanitadesign.functions;

import android.app.Activity;
//import android.provider.Settings.Secure;
import android.util.Log;
import android.content.Context;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;
import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;
//import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.StrictPolicy;
//import com.google.android.vending.licensing.ServerManagedPolicy;
//import com.google.android.vending.licensing.StrictPolicy;

public class CheckLicenseFunction implements FREFunction  {
	public static final String TAG = "CheckLicenseFunction";

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;
		String BASE64_PUBLIC_KEY = null;
		Activity act;

		try{
			BASE64_PUBLIC_KEY = passedArgs[0].getAsString();
			act = ctx.getActivity();
			//Log.i(TAG, "check start");
			checkLicense(ctx, act, BASE64_PUBLIC_KEY);
			result = FREObject.newObject(1);
			//Log.i(TAG, "check konec");
		} catch (FRETypeMismatchException e) {
			Log.i(TAG, "##### AndroidLicensing - caught FRETypeMismatchException");
			e.printStackTrace();
		} catch (FREInvalidObjectException e) {
			Log.i(TAG, "##### AndroidLicensing - caught FREInvalidObjectException");
			e.printStackTrace();
		} catch (FREWrongThreadException e) {
			Log.i(TAG, "##### AndroidLicensing - caught FREWrongThreadException");
			e.printStackTrace();
		}
		return result;
	}

	// Generate your own 20 random bytes, and put them here. This is required by License Verification Library (LVL) for obfuscation.
//	private static final byte[] SALT = new byte[] {
//		-9, -101, -2, 6, -7, 11, 124, 9, 2, -95, -34, 112, 33, -40, 76, -47, 21, -9, 46, 43
//	};

	private LicenseCheckerCallback mLicenseCheckerCallback;
	private Context mContext;

	/*
	 * @param freContext  FREContext created during initialization of the native extension.
	 * @param context     Context of the running AIR Application activity.
	 * @param publicKey   Public key obtained from Android Market for licensing purposes.
	 */
	public void checkLicense( FREContext freContext, Context context, String publicKey )
	{
		// This sample code uses Secure.ANDROID_ID only for device identification. Strenthen this string by using as many device specific
		// string so as to make it as unique as possible as this is used for obfuscating the server response.

		// ANDROID_ID is a 64-bit number (as a hex string) that is randomly generated on the device's first boot and should remain
		// constant for the lifetime of the device. This value may change if a factory reset is performed on the device.

		//String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		//Log.i(TAG, "*** checkLicense with id: "+deviceId+" key: "+publicKey);
		try {
			// Library calls this callback when it's done verifying with Android licensing server
			mLicenseCheckerCallback =  new CheckLicenseCallback();

			//Log.i(TAG, "*** 1");
			// Construct the LicenseChecker object with a policy and call checkAccess(). In case a developer wants to change the policy to
			// be used he needs to replace  the "ServerManagedPolicy" with the policy name with any other policy, if required.
			// ServerManagedPolicy is defined in License Verification Library (LVL) provided by android.

			GoogleExtension.licenseCheckerCtx = this;
			mContext = context;
			//-- this code would use cached responses --
			//ServerManagedPolicy policy = new ServerManagedPolicy(context, new AESObfuscator(SALT, context.getPackageName(), deviceId));
			//Log.i(TAG, "*** 2");

			//GoogleExtension.licenseChecker = new LicenseChecker( context, GoogleExtension.mAPKServerPolicy, publicKey);
			GoogleExtension.licenseChecker = new LicenseChecker( context, GoogleExtension.mAPKExpansionPolicy, publicKey);

			//Log.i(TAG, "*** 3");
			//--
			//-- this one not:
			//GoogleExtension.licenseChecker = new LicenseChecker( context, new StrictPolicy(), publicKey);
			//--

			GoogleExtension.licenseChecker.checkAccess(mLicenseCheckerCallback);
		} catch (Exception e) {
			Log.i(TAG, "ERROR?");
			e.printStackTrace();
		}
		//Log.i(TAG, "*** 4");
	}

	public void onDestroy()
	{
		if(GoogleExtension.licenseChecker != null) {
			GoogleExtension.licenseChecker.onDestroy();
			GoogleExtension.licenseChecker = null;
		}
	}

	//public void followLastLicensingUrl()
	//{
	//	GoogleExtension.mAPKExpansionPolicy.resetPolicy();
	//mChecker.followLastLicensingUrl( mContext );
	// Call finish() on the surrounding activity to remove it from the backstack.
	//	GoogleExtensionContext.getMainActivity().this.finish();
	//}
}
