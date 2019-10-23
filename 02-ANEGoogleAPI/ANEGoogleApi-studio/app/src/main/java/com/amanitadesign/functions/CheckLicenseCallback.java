package com.amanitadesign.functions;


import com.amanitadesign.GoogleExtension;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;


/* 
 * LVL after checking with the licensing server and conferring with the policy makes callbacks to communicate  
 * result with the application using callbacks i.e AndroidLicenseCheckerCallback in this case.
 * AndroidLicenseCheckerCallback then dipatches StatusEventAsync event to communicate the result obtained from LVL
 * with the ActionScript library.
 */

public class CheckLicenseCallback implements LicenseCheckerCallback{

	private static final String EMPTY_STRING = ""; 
	private static final String LICENSE_STATUS = "licenseStatus";
	private static final String CHECK_IN_PROGRESS = "checkInProgress";
	private static final String INVALID_PACKAGE_NAME = "invalidPackageName";
	private static final String INVALID_PUBLIC_KEY = "invalidPublicKey";
	private static final String MISSING_PERMISSION = "missingPermission";
	private static final String NON_MATCHING_UID = "nonMatchingUID";
	private static final String NOT_MARKET_MANAGED = "notMarketManaged";
	
	CheckLicenseCallback() {

	}


	/*
 	 * This function maps the ApplicationErrorCode obtained from LVL to the LicenseStatusReason of ActionScript library.
 	 */ 

	@Override
	public void applicationError(int errorCode) {
		// This method is only called if the developer made a mistake setting up or
		// calling the license checker library. Developers should examine the error
		// code and fix the error.
		String errorMessage = EMPTY_STRING;

		switch(errorCode)
		{
			case ERROR_CHECK_IN_PROGRESS :
				errorMessage = CHECK_IN_PROGRESS;
				break;
			case ERROR_INVALID_PACKAGE_NAME :	
				errorMessage = INVALID_PACKAGE_NAME;
				break;
			case ERROR_INVALID_PUBLIC_KEY :
				errorMessage = INVALID_PUBLIC_KEY;
				break;
			case ERROR_MISSING_PERMISSION :
				errorMessage = MISSING_PERMISSION;
				break;
			case ERROR_NON_MATCHING_UID :
				errorMessage = NON_MATCHING_UID;
				break;
			case ERROR_NOT_MARKET_MANAGED :
				errorMessage = NOT_MARKET_MANAGED;
				break;
		}
		GoogleExtension.notifyLicenseStatus(LICENSE_STATUS, errorMessage);
	}


	@Override
	public void allow(int reason) {
		GoogleExtension.notifyLicenseStatus(LICENSE_STATUS, EMPTY_STRING);
		
	}


	@Override
	public void dontAllow(int reason) {
		if ( reason != Policy.RETRY ) {
			GoogleExtension.notifyLicenseStatus(LICENSE_STATUS, "followLastLicensingUrl");
		} else {
			GoogleExtension.notifyLicenseStatus(LICENSE_STATUS, "dontAllowRetry");
		}
		
	}
}
