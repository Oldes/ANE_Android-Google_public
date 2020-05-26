/*
 * Copyright (C) <year> <copyright holders>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amanitadesign;

import java.util.HashMap;
import java.util.Map;

import com.amanitadesign.expansion.Downloader;
import com.amanitadesign.expansion.ExpansionFunctions;
import com.amanitadesign.expansion.ExpansionObbListener;
import com.amanitadesign.expansion.ObbExpansionsManager;
import com.amanitadesign.functions.APKExpansionGetMainFileName;
import com.amanitadesign.functions.APKExpansionGetMainFileSize;
import com.amanitadesign.functions.APKExpansionGetMainURL;
import com.amanitadesign.functions.APKExpansionGetPatchFileName;
import com.amanitadesign.functions.APKExpansionGetPatchFileSize;
import com.amanitadesign.functions.APKExpansionGetPatchURL;
import com.amanitadesign.functions.APKgetDeviceId;
import com.amanitadesign.functions.APKgetExternalStorageDirectory;
import com.amanitadesign.functions.APKgetHostAdress;
import com.amanitadesign.functions.APKgetPackageName;
import com.amanitadesign.functions.CheckLicenseFunction;
import com.amanitadesign.functions.FollowLastLicensingURL;
import com.amanitadesign.functions.GetPackageVersionCodeFunction;
import com.amanitadesign.functions.InitFunction;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import android.content.res.Configuration;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.air.ActivityResultCallback;
import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.AndroidActivityWrapper.ActivityState;
import com.adobe.air.StateChangeCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleExtensionContext extends FREContext implements
	ActivityResultCallback,
	StateChangeCallback//,
	//PurchasesUpdatedListener,
	//SkuDetailsResponseListener,
	//BillingClientStateListener,
	//ConsumeResponseListener
{
	public static final String TAG = "AmanitaContext";
	private AndroidActivityWrapper aaw = null;
	private static GoogleExtensionContext instance = null;
	private static Downloader loader = null;
	private static int versionNumber = 1;
	private static int patchNumber = 1;
	private static ObbExpansionsManager manager = null;
	private static ExpansionObbListener listener = null;

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final int RC_SHOW_ACHIEVEMENTS = 4237;
	final int MAX_SNAPSHOT_RESOLVE_RETRIES = 3;
	
	public static final String ON_BILLING = "ON_BILLING";

	public GoogleExtensionContext()
	{
		instance = this;

		aaw = AndroidActivityWrapper.GetAndroidActivityWrapper();
		aaw.addActivityResultListener(this);
		aaw.addActivityStateChangeListner( this );
	}

	public static ObbExpansionsManager getManager()
	{
		if (manager == null)
		{
			listener = new ExpansionObbListener();
			manager = ObbExpansionsManager.createNewInstance(instance.getActivity().getApplicationContext(), listener);
		}
		return manager;
	}

	public static GoogleExtensionContext getExtensionContext()
	{
		return instance;
	}

	public static Activity getMainActivity()
	{
		return GoogleExtension.extensionContext.getActivity();
	}
	public static void setDownloader(Downloader dl)
	{
		loader = dl;
	}
	public static Downloader getDownloader()
	{
		return loader;
	}
	public static void setVersionNumber(int num)
	{
		versionNumber = num;
	}
	public static int getVersionNumber()
	{
		return versionNumber;
	}
	public static void setPatchNumber(int num)
	{
		patchNumber = num;
	}
	public static int getPatchNumber()
	{
		return patchNumber;
	}

	public Activity getActivity() {
		if (aaw != null) {
			return aaw.getActivity();
		}
		return null;
	}

	@Override
	public void dispose() {
		if (null != aaw) {
			aaw.removeActivityResultListener(this);
			aaw = null;
		}
		if(null != loader) loader.destroy();
		if(null != manager) manager = null;
		if(null != listener) listener = null;

		Log.d(TAG,"Context disposed.");
	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		functions.put("init", new InitFunction());
		functions.put("checkLicense", new CheckLicenseFunction());
		functions.put("followLastLicensingURL", new FollowLastLicensingURL());
		functions.put("getAPKMainURL",       new APKExpansionGetMainURL());
		functions.put("getAPKMainFileName",  new APKExpansionGetMainFileName());
		functions.put("getAPKMainFileSize",  new APKExpansionGetMainFileSize());
		functions.put("getAPKPatchURL",      new APKExpansionGetPatchURL());
		functions.put("getAPKPatchFileName", new APKExpansionGetPatchFileName());
		functions.put("getAPKPatchFileSize", new APKExpansionGetPatchFileSize());
		functions.put("getExternalStorageDirectory", new APKgetExternalStorageDirectory());

		functions.put("getDeviceId", new APKgetDeviceId());
		functions.put("getPackageName", new APKgetPackageName());
		functions.put("getHostAdress", new APKgetHostAdress());
		functions.put("getPackageVersionCode", new GetPackageVersionCodeFunction());

		functions.put("isGameHelperAvailable", new GoogleApiFunctions.IsGameHelperAvailableFunction());
		functions.put("signIn", new GoogleApiFunctions.SignInFunction());
		functions.put("silentSignIn", new GoogleApiFunctions.SilentSignInFunction());
		functions.put("signOut", new GoogleApiFunctions.SignOutFunction());
		functions.put("isSignedIn", new GoogleApiFunctions.IsSignedInFunction());
		functions.put("reportAchievement", new GoogleApiFunctions.ReportAchievementFunction());
		functions.put("showStandardAchievements", new GoogleApiFunctions.ShowAchievementsFunction());

		functions.put("openSnapshot", new GoogleApiFunctions.OpenSnapshot());
		functions.put("writeSnapshot", new GoogleApiFunctions.WriteSnapshot());
		functions.put("readSnapshot", new GoogleApiFunctions.ReadSnapshot());
		functions.put("deleteSnapshot", new GoogleApiFunctions.DeleteSnapshot());

		functions.put("getExpansionStatus", new ExpansionFunctions.ExpansionFilesStatus());
		functions.put("startExpansionDownload", new ExpansionFunctions.StartDownload());
		functions.put("stopExpansionDownload", new ExpansionFunctions.StopDownload());
		functions.put("resumeExpansionDownload", new ExpansionFunctions.ResumeDownload());
		
//		functions.put("billingInit", new BillingFunctions.BillingInit());
//		functions.put("billingEnd", new BillingFunctions.BillingEnd());
//		functions.put("billingReady", new BillingFunctions.BillingReady());
//		functions.put("doPayment", new BillingFunctions.DoPayment());
//		functions.put("doQuerySKU", new BillingFunctions.DoQuerySKU());
//		functions.put("doQueryPurchases", new BillingFunctions.DoQueryPurchases());
//		functions.put("consumeProduct", new BillingFunctions.ConsumeProduct());

		return functions;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(GoogleExtension.VERBOSE>0) Log.d(TAG, "onActivityResult: "+ requestCode +" "+ resultCode );
		GoogleExtension.log("ExtensionContext.onActivityResult" +
				" requestCode:" + Integer.toString(requestCode) +
				" resultCode:" + Integer.toString(resultCode));

		if (GoogleExtension.googleApiHelper != null) {
			GoogleExtension.googleApiHelper.onActivityResult(requestCode, resultCode, intent);
		}
	}

	@Override
	public void onActivityStateChanged( ActivityState state ) {
		switch ( state ) {
			case STARTED:
			case RESTARTED:
			case RESUMED:
			case PAUSED:
			case STOPPED:
			case DESTROYED:
		}
		if(GoogleExtension.VERBOSE>0) Log.d(TAG, "onActivityStateChanged: "+ state);
	}
	@Override
	public void onConfigurationChanged(Configuration paramConfiguration) {
	}

	public void logEvent(String eventName) {
		if(GoogleExtension.VERBOSE>0) Log.i(TAG, eventName);
	}

    public void dispatchEvent(String eventName) {
        dispatchEvent(eventName, "OK");
    }
	public void dispatchEvent(String eventName, String eventData)
	{
		//logEvent(eventName);
		if (eventData == null) eventData = "OK";
		dispatchStatusEventAsync(eventName, eventData);
	}


	private boolean checkPlayServices(Activity activity) {
		GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
		int result = googleAPI.isGooglePlayServicesAvailable(activity.getApplicationContext());
		if(result != ConnectionResult.SUCCESS) {
			if(googleAPI.isUserResolvableError(result)) {
				googleAPI.getErrorDialog(activity, result,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			return false;
		}
		return true;
	}


	/**************************************************************************************/
	/*         BILLING ********************************************************************/
	/*
	private void dispatchBillingStatus(String code) {
		if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG,  "ON_BILLING: "+code);
		dispatchStatusEventAsync(ON_BILLING, code);
	}
	
	public void doQuerySKU(String sku, @SkuType String type) {
		List skuList = new ArrayList<> ();
		skuList.add(sku);
		SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
		params.setSkusList(skuList).setType(type);
		GoogleExtension.billingClient.querySkuDetailsAsync(params.build(), this);
	}
	
	public int doPayment(String sku, @SkuType String type) {
		if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG,  "doPayment");
		int responseCode=-1;
		try {
			BillingFlowParams flowParams = BillingFlowParams.newBuilder()
					 .setSku(sku)
					 .setType(type) // default: SkuType.INAPP, SkuType.SUB for subscription
					 .build();
			responseCode = GoogleExtension.billingClient.launchBillingFlow(
					GoogleExtensionContext.getMainActivity(),flowParams);
		} catch (Exception e) {
			Log.e(TAG, "doPayment failed with: "+ e);
		}
		return responseCode;
	}
	
	public PurchasesResult doQueryPurchases(@SkuType String type)  {
		if(type==null) type = SkuType.INAPP;
		if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG,  "doPayment");
		PurchasesResult purchasesResult = null;
		try {
			purchasesResult = GoogleExtension.billingClient.queryPurchases(type);
			if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG,  "doQueryPurchases -> " + purchasesResult);
		} catch (Exception e) {
			Log.e(TAG, "doQueryPurchases failed with: "+ e);
		}
		return purchasesResult;
	}
	*/
	/********* billing listeners ********************/
	/*
	public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
		if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG, "onBillingSetupFinished -> " + billingResponseCode);
		 if (billingResponseCode == BillingResponse.OK) {
			 // The billing client is ready. You can query purchases here.
			if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG,  "billing client is ready");
			dispatchBillingStatus("setupFinished");
		 } else {
			dispatchBillingStatus("billingResponse|" + billingResponseCode);
		 }
	 }
	 public void onBillingServiceDisconnected() {
		 // Try to restart the connection on the next request to
		 // Google Play by calling the startConnection() method.
		if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG,  "onBillingServiceDisconnected");
		dispatchBillingStatus("serviceDisconnected");
	 }

	public void onSkuDetailsResponse(int responseCode, List<SkuDetails> details) {
		if(GoogleExtension.VERBOSE > 2) Log.i(GoogleExtension.TAG, "onSkuDetailsResponse -> " + responseCode + " " + details);
		String response = "onSkuDetailsResponse|"+responseCode;
		if (responseCode == BillingResponse.OK && details != null) {
			response += "|" + details.size() ;
			for (SkuDetails detail : details) {
				response += "|" + detail.toString();
			}
		}
		dispatchBillingStatus(response);
	}
	
	public void onPurchasesUpdated(@BillingResponse int responseCode, List<Purchase> purchases) {
		if(GoogleExtension.VERBOSE > 2) Log.i(TAG, "onPurchasesUpdated: " + responseCode + " " + purchases);
		
		String response = "onPurchasesUpdated|"+responseCode;
		if (responseCode == BillingResponse.OK && purchases != null) {
			response += "|" + purchases.size() ;
			for (Purchase purchase : purchases) {
				response += "|" + purchase.getOriginalJson();
			}
		}
		dispatchBillingStatus(response);
	}

	public void onConsumeResponse(int responseCode, String purchaseToken) {
		if(GoogleExtension.VERBOSE > 2) Log.i(TAG, "onConsumeResponse: " + responseCode + " " + purchaseToken);
		dispatchBillingStatus("onConsumeResponse|"+ responseCode+"|"+purchaseToken);
		
	}
	*/
}
