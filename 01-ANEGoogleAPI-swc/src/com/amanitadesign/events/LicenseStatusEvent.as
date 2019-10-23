/*********************************************************************************************************
* ADOBE SYSTEMS INCORPORATED
* Copyright 2011 Adobe Systems Incorporated
* All Rights Reserved.
*
* NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
* terms of the Adobe license agreement accompanying it.  If you have received this file from a
* source other than Adobe, then your use, modification, or distribution of it requires the prior
* written permission of Adobe.
*
*********************************************************************************************************/


package com.amanitadesign.events
{
	import flash.events.Event;

	/* 
	 * An object dispatches LicenseStatusEvent when the status of license is obtained from the licensing server.
	 */ 
	
	public class LicenseStatusEvent extends Event
	{
		public static const STATUS:String = "licenseStatus";
		
		private var bLicensed:Boolean;
		private var mErrorReason:String;
		
		public function LicenseStatusEvent( type:String, error:String )
		{
			super(type);
			trace("LicenseStatusEvent... " + error);
			bLicensed = (!error || error.length == 0);
			trace(bLicensed);
			mErrorReason = error;
			trace("new LicenseStatusEvent: " + bLicensed + " " + mErrorReason);
		}
		
		/*
		 * This function returns the status of the licensing check made 
		 * and apprises the developer whether the application is licensed or not.    
		 */
		
		public function get status():Boolean { return bLicensed; }
		
		/* 
		 * This function will return the status message in case the Android Licensing server 
		 * response indicates that there is some issue with license. 
		 * The string returned is similar to the applicationErrorCode returned by LVL          
		 */
		
		public function get statusReason():String { return mErrorReason; }
		
		/* 
		 * Creates a copy of the LicenseStatusEvent and copy the value of each property of the original object
		 */ 
		
		public override function clone():Event 
		{
			return new LicenseStatusEvent(STATUS, mErrorReason);
		}
		
	}
}

	
