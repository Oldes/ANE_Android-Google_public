package com.amanitadesign
{
	import com.amanitadesign.events.*;
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.filesystem.File;
	import flash.utils.ByteArray;
	
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	import flash.errors.IllegalOperationError;
	import flash.system.Capabilities;
	import flash.net.SharedObject;
	
	/**
	 * A controller used to interact with the system volume on iOS and
	 * Android devices.  Ways to change the volume programmatically
	 * and to respond to the hardware volume buttons are included.
	 *  
	 * @author Nathan Weber
	 */	
	public class GoogleExtension extends EventDispatcher
	{

		public static const COMPLETED:String = "Completed";
		public static const FAILED:String = "Failed";
		public static const FAILED_INTERNET:String = "Failed_Internet";
		public static const PAUSED:String = "Paused";
		
		/** Extension is supported on Android devices. */
		public static function get isSupported() : Boolean
		{
			return Capabilities.manufacturer.indexOf("Android") != -1;
		}

		//----------------------------------------
		//
		// Variables
		//
		//----------------------------------------
		
		private static var _instance:GoogleExtension;
		private var extContext:ExtensionContext;
		private var GPSO:SharedObject = SharedObject.getLocal("GPsalt");
		
		//----------------------------------------
		//
		// Properties
		//
		//----------------------------------------
		
		private var _systemVolume:Number = NaN;
		
		public function get systemVolume():Number {
			return _systemVolume;
		}
		
		public function set systemVolume( value:Number ):void {
			if ( _systemVolume == value ) {
				return;
			}
			
			_systemVolume = value;
		}
		
		//----------------------------------------
		//
		// Public Methods
		//
		//----------------------------------------
		
		public static function get instance():GoogleExtension {
			if ( !_instance ) {
				_instance = new GoogleExtension( new SingletonEnforcer() );
				var salt0:int = _instance.GPSO.data.salt0;
				if(!salt0) {
					salt0 = _instance.GPSO.data.salt0 = 1;
					_instance.GPSO.flush();
				}
				_instance.init(_instance.GPSO.data.salt0);
			}
			
			return _instance;
		}
		
		/**
		 * Changes the device's system volume.
		 *  
		 * @param newVolume The new system volume.  This value should be between 0 and 1.
		 */		
		public function setVolume( newVolume:Number ):void {
			if ( isNaN(newVolume) )  {
				newVolume = 1;
			}
			
			if ( newVolume < 0 ) {
				newVolume = 0;
			}
			
			if ( newVolume > 1 ) {
				newVolume = 1;
			}
			
			extContext.call( "setVolume", newVolume );
			
			systemVolume = newVolume;
		}
		
	
		/*public function getAPKExpansionData():APKExpansionData {
			return extContext.call( "getAPKExpansionData" ) as APKExpansionData;
		}*/
		public function getAPKMainURL():     String { return extContext.call("getAPKMainURL") as String;}
		public function getAPKMainFileName():String { return extContext.call("getAPKMainFileName") as String; }
		public function getAPKMainFileSize():  uint { return extContext.call("getAPKMainFileSize") as uint; }
		
		public function getAPKPatchURL():      String {return extContext.call("getAPKPatchURL") as String;}
		public function getAPKPatchFileName(): String {return extContext.call("getAPKPatchFileName") as String; }
		public function getAPKPatchFileSize(): uint   {return extContext.call("getAPKPatchFileSize") as uint; }
		
		public function getExternalStorageDirectory(): File { return new File(extContext.call("getExternalStorageDirectory") as String); }
		public function getObbDirectory(): File { return new File(extContext.call("getObbDirectory") as String); }
		public function getDeviceId(): String { return extContext.call("getDeviceId") as String; }
		public function getPackageName(): String { return extContext.call("getPackageName") as String; }
		public function getHostAdress(host:String): String { return extContext.call("getHostAdress", host) as String; }
		public function getPackageVersionCode(): int { return extContext.call("getPackageVersionCode") as int; }
		
		public function askForPermission(permision:String, requestCode:int): Boolean { return extContext.call("askForPermission", permision, requestCode) as Boolean; }
		

		/*
		*  This function creates an Extension Context and calls native code to start the licensing process.
		*  It also adds listener for status and error events, which are dispatched depending upon the
		*  result received from the native code. 
		*/
			
		public function checkLicense($BASE64_PUBLIC_KEY:String):void
		{
			var errorEvent:Event;
			try {
				if ( extContext )
				{
					var retValue:int = extContext.call( "checkLicense", $BASE64_PUBLIC_KEY ) as int;
					if(!retValue)
					{
						trace(" Failed to call checkLicenseNative: "+ retValue);					
						errorEvent = new ErrorEvent(ErrorEvent.ERROR, false, false, "Failed to check license", 0);
						dispatchEvent(errorEvent);
					}
				}
				else
				{
					trace( " Failed to create ExtensionContext " );
					errorEvent = new ErrorEvent(ErrorEvent.ERROR, false, false, "Failed to check license", 0);
					dispatchEvent(errorEvent);
				}
			} catch (errA:ArgumentError) {
				trace(errA.message);
				errorEvent = new ErrorEvent(ErrorEvent.ERROR, false, false, errA.message , 0); 
				dispatchEvent(errorEvent);
			} catch (errIO:IllegalOperationError){
				trace(errIO.message);
				errorEvent = new ErrorEvent(ErrorEvent.ERROR, false, false, errIO.message , 0); 
				dispatchEvent(errorEvent);
			} catch (err:Error) {
				trace(err.message);
				errorEvent = new ErrorEvent(ErrorEvent.ERROR, false, false, err.message , 0); 
				dispatchEvent(errorEvent);
			}
		}


/********************************************************************************************************
 * 
 *       GOOGLE API  ************************************************************************************
 ********************************************************************************************************/

 		public function followLastLicensingURL():void
		{
			_instance.GPSO.data.salt0 += 1;
			if(_instance.GPSO.data.salt0 > 127) _instance.GPSO.data.salt0 = -127
			_instance.GPSO.flush();
			if (GoogleExtension.isSupported)
				extContext.call("followLastLicensingURL");
		}

		public function isGameHelperAvailable():Boolean
		{
			return (GoogleExtension.isSupported && extContext.call("isGameHelperAvailable") as Boolean);
		}
		public function isSignedIn():Boolean
		{
			return (GoogleExtension.isSupported && extContext.call("isSignedIn") as Boolean);
		}
		
		public function signIn():void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("signIn");
			}
		}

		public function silentSignIn():void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("silentSignIn");
			}
		}
		
		public function signOut():void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("signOut");
			}
		}
		
		public function reportAchievement(achievementId:String, percent:Number = 0):void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("reportAchievement", achievementId, percent);
			}
		}
		
		public function showStandardAchievements():void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("showStandardAchievements");
			}
		} 

		
		public function openSnapshot(name:String):void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("openSnapshot", name);
			}
		} 
		
		public function writeSnapshot(name:String, data:ByteArray, time:Number):void
		{
			if (GoogleExtension.isSupported)
			{
				extContext.call("writeSnapshot", name, data, time);
			}
		} 
		
		public function readSnapshot(name:String):ByteArray
		{
			var result:ByteArray;
			if (GoogleExtension.isSupported)
			{
				result = extContext.call("readSnapshot", name) as ByteArray;
			}
			return result;
		} 
		
		public function deleteSnapshot(name:String):ByteArray
		{
			var result:ByteArray;
			if (GoogleExtension.isSupported)
			{
				result = extContext.call("deleteSnapshot", name) as ByteArray;
			}
			return result;
		} 
		
		
		public function getExpansionStatus(mainVersion:int = 1, patchVersion:int = 1):void {
			if (GoogleExtension.isSupported)
			{
				extContext.call("getExpansionStatus", mainVersion, patchVersion);
			}
		}
		
		public function startExpansionDownload(LVLKey:String):void {
			if (GoogleExtension.isSupported)
			{
				extContext.call("startExpansionDownload", LVLKey);
			}
		}
		
		public function stopExpansionDownload():void {
			if (GoogleExtension.isSupported)
			{
				extContext.call("stopExpansionDownload");
			}
		}
		
		public function resumeExpansionDownload():void {
			if (GoogleExtension.isSupported)
			{
				extContext.call("resumeExpansionDownload");
			}
		}

		public function getMainOBBPath():String { return extContext.call("getMainOBBPath") as String; }
		public function getPatchOBBPath():String { return extContext.call("getPatchOBBPath") as String; }

		/****************************************************
		/*
		/* Billing functions
		/*
		/***************************************************/

		public function billingInit(): void { extContext.call("billingInit"); }
		public function billingEnd(): void { extContext.call("billingEnd"); }
		public function billingReady():Boolean {
			return extContext.call("billingReady") as Boolean;
		}

		public function doPayment(skuID:String, skuType:String="inapp"):int {
			return extContext.call("doPayment", skuID, skuType) as int;
		}
		public function doQuerySKU(skuID:String, skuType:String="inapp"):String {
			return extContext.call("doQuerySKU", skuID, skuType) as String;
		}
		public function doQueryPurchases(skuType:String="inapp"):String {
			return extContext.call("doQueryPurchases", skuType) as String;
		}
		public function consumeProduct(token:String):void {
			extContext.call("consumeProduct", token);
		}
		
		/**
		 * Cleans up the instance of the native extension. 
		 */		
		public function dispose():void { 
			extContext.dispose(); 
		}
		
		//----------------------------------------
		//
		// Handlers
		//
		//----------------------------------------
		
		private function init(salt0:int = 0):void {
			extContext.call( "init", salt0);
		}
		
		private function onStatusHandler( event:StatusEvent ):void {
			trace("onStatusHandler: " + event)
			var e:Event;
			switch(event.code) {
				case VolumeEvent.STATUS: 
					systemVolume = Number(event.level);
					//trace("dispaching VolumeEvent");
					e = new VolumeEvent( VolumeEvent.STATUS, systemVolume, false, false )
					break;
				case LicenseStatusEvent.STATUS: 
					//trace("dispaching LicenseStatusEvent");
					e = new LicenseStatusEvent(LicenseStatusEvent.STATUS, event.level)
					break;
				case AirGooglePlayGamesEvent.ON_SIGN_IN_SUCCESS:
				case AirGooglePlayGamesEvent.ON_SIGN_IN_FAIL:
				case AirGooglePlayGamesEvent.ON_SIGN_OUT_SUCCESS:
				case AirGooglePlayGamesEvent.ON_OPEN_SNAPSHOT_READY:
				case AirGooglePlayGamesEvent.ON_OPEN_SNAPSHOT_FAILED:
					e = new AirGooglePlayGamesEvent(event.code, event.level);
					break;
				case ExpansionStatusEvent.EXPANSION_STATUS:
					e = new ExpansionStatusEvent(event.code, event.level);
					break;
				case ExpansionStateEvent.EXPANSION_STATE:
					e = new ExpansionStateEvent(event.code, event.level);
					break;
				case ExpansionProgressEvent.EXPANSION_PROGRESS:
					e = new ExpansionProgressEvent(event.code, event.level);
					break;
				case AirBillingEvent.ON_BILLING:
					e = new AirBillingEvent(event.code, event.level);
					break;
			}
			if(e) {
				this.dispatchEvent(e);
			}
			//
			//
		}
		
	
		//----------------------------------------
		//
		// Constructor
		//
		//----------------------------------------
		
		/**
		 * Constructor. 
		 */		
		public function GoogleExtension( enforcer:SingletonEnforcer ) {
			super();
			
			extContext = ExtensionContext.createExtensionContext( "com.amanitadesign.GoogleExtension", "" );
			
			if ( !extContext ) {
				throw new Error( "GoogleExtension extension is not supported on this platform." );
			}
			
			extContext.addEventListener( StatusEvent.STATUS, onStatusHandler );
		}

	}
}

class SingletonEnforcer {
	
}