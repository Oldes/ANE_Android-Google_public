package
{
	import flash.desktop.NativeApplication;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.ErrorEvent;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.text.TextFormat;
	import flash.ui.Multitouch;
	import flash.ui.MultitouchInputMode;
	import flash.utils.ByteArray;
	import flash.filesystem.File;
	
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	
	import com.amanitadesign.GoogleExtension;
	import com.amanitadesign.events.AirGooglePlayGamesEvent;
	import com.amanitadesign.events.AirBillingEvent;
	import com.amanitadesign.events.LicenseStatusEvent;

	import com.amanitadesign.events.ExpansionProgressEvent;
	import com.amanitadesign.events.ExpansionStatusEvent;
	import com.amanitadesign.events.ExpansionStateEvent;
	
	/**
	 * ...
	 * @author Oldes
	 */
	public class Main extends Sprite 
	{
		
		public var tf:TextField;
		private var nextBtnX:Number = 20;
		private static const MainOBBVersion:int = 1006;
		private static const gei:GoogleExtension = GoogleExtension.instance;

		private static const GPPublicKey:String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApQBTNw7DW6+ygMMO41nN1d4cRpNYa5rdyreUXsZmmLbWPMdd0tTj20GzWrwHcee792cbizGuiQIdejUx11rsHbHxr/nMdmwlb3q6T1kh3yXbuwp39ctLTkS2k8jHiWBB5XHv63dC6wi/cSjyErujobOTZwjMourrxJ4TTSdbG4ZmEUg00AJelhjRKKSzmQJU7rX7ZlxfNuqKXhKc4WO/M77Y7SUUYbaqTuhO89Pjz2AZc/Jo+T1umlBCjxIs386bgtXaii6ROO6gYR72Pgx4ahpn+kF00nmofehEp/czr939Qk2XL4V8cipIipsHRcv/jDiwr9R4OdAFg3Hjv1t8yQIDAQAB";
			

		private function addButton(label:String, action:Function):void {
			var btn:TextField = new TextField();
			btn.defaultTextFormat = new TextFormat(null, 20, 0xFFFFFF, true);
			btn.background = true;
			btn.backgroundColor = 0xFF0000;
			btn.autoSize = TextFieldAutoSize.CENTER;
			btn.selectable = false;
			btn.text = label;
			
			btn.x = nextBtnX;
			btn.y = stage.stageHeight - btn.height - 20;
			nextBtnX = btn.x + btn.width + 10;
			btn.addEventListener(MouseEvent.CLICK, action);
			stage.addChild(btn);
		}


		
		public function Main() 
		{
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
			stage.addEventListener(Event.DEACTIVATE, deactivate);
			
			// touch or gesture?
			Multitouch.inputMode = MultitouchInputMode.TOUCH_POINT;


			
			tf = new TextField();
			tf.width = stage.stageWidth;
			tf.height = stage.stageHeight;
			tf.wordWrap = true;
			tf.defaultTextFormat = new TextFormat(null, 24);
			addChild(tf);

			//Handle resize
			stage.addEventListener(Event.RESIZE, onResize);

			addButton("SIGNED?", doGPisSigned);
			addButton("SIGN-IN", doGPsignIn);
			addButton("SIGN-OUT", doGPsignOut);
			addButton("ACHs", doGPShowAchievements);
			addButton("LOAD", doGPLoadSnapshot);
			addButton("SAVE", doGPSaveSnapshot);
			addButton("DEL", doGPDeleteSnapshot);

/*
			addButton("PURCHASE", purchaseTest);
			addButton("CONSUME", consumeTest);
			addButton("UNAVAILABLE", unavailableTest);
			addButton("BEER", beerTest)
			addButton("QuerySKU", querySKUTest)
*/

			log("Testing Amanita Android ANE...");
			log("GoogleExtension is supported: " + GoogleExtension.isSupported);
			log("isGameHelperAvailable: " + gei.isGameHelperAvailable());
			log("getPackageName: "+ gei.getPackageName());
			//log(gei.hello());

			gei.addEventListener(ErrorEvent.ERROR, GPErrorHandler);
			gei.addEventListener(LicenseStatusEvent.STATUS, GPLicenseResult);
			gei.checkLicense(GPPublicKey);


//			log("Is billing ready? "+ gei.billingReady()+" <- should be false here.");
			
			log("\nGooglePlay signin test...");
			gei.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_IN_SUCCESS, GPSonSignInSuccess);
			gei.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_OUT_SUCCESS, GPSonSignOutSuccess);
			gei.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_IN_FAIL, GPSonSignInFail);

			gei.addEventListener(AirGooglePlayGamesEvent.ON_OPEN_SNAPSHOT_READY, GPSonSnapshotOpen);
			gei.addEventListener(AirGooglePlayGamesEvent.ON_OPEN_SNAPSHOT_FAILED, GPSonSnapshotOpen);

			gei.silentSignIn();

		}
		
		private function log(value:String):void{
			tf.appendText(value+"\n");
			tf.scrollV = tf.maxScrollV;
		}
		
		private function deactivate(e:Event):void 
		{
			// make sure the app behaves well (or exits) when in background
			//NativeApplication.nativeApplication.exit();
		}

		private function onResize(e:Event):void {
			log("onResize: "+stage.stageWidth+"x"+stage.stageHeight);
			tf.width = stage.stageWidth;
			tf.height = stage.stageHeight;

		}
		
		public function doGPsignIn(e:MouseEvent=null):void {
			log("GoogleExtension.instance.signIn()");
			gei.signIn();
		}

		public function doGPsignOut(e:MouseEvent=null):void {
			log("GoogleExtension.instance.signOut()");
			gei.signOut();
		}
		public function doGPisSigned(e:MouseEvent=null):void {
			log("isSignedIn reports: "+gei.isSignedIn());
		}

		public function doGPShowAchievements(e:MouseEvent=null):void {
			log("GoogleExtension.instance.showStandardAchievements()");
			gei.showStandardAchievements();
		}

		public function doGPLoadSnapshot(e:MouseEvent=null):void {
			log("GoogleExtension.instance.openSnapshot(\"test.txt\")");
			gei.openSnapshot("test.txt");
		}

		public function doGPSaveSnapshot(e:MouseEvent=null):void {
			log("GoogleExtension.instance.writeSnapshot(\"test.txt\", ...)");
			var binary:ByteArray = new ByteArray();
			binary.writeUTFBytes("hello boy");
			gei.writeSnapshot("test.txt", binary, 0);
		}

		public function doGPDeleteSnapshot(e:MouseEvent=null):void {
			log("GoogleExtension.instance.deleteSnapshot(\"test.txt\")");
			gei.deleteSnapshot("test.txt");
		}

		private function GPSonSignInSuccess(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignInSuccess: " + event.value);
			log("\nTrying to report achievement...");
			gei.reportAchievement("CgkIzaac840IEAIQAQ");

//			log("\nBilling init...");
//			gei.addEventListener(AirBillingEvent.ON_BILLING, OnBilling);
//			gei.billingInit();
		}
		private function GPSonSignOutSuccess(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignOutSuccess: " + event);
		}
		private function GPSonSignInFail(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignInFail: " + event.value);
		}

		private function GPSonSnapshotOpen(event:AirGooglePlayGamesEvent):void {
			var name:String = event.value;
			log("GPSonSnapshotOpen: " + name);

			var baCloud:ByteArray = gei.readSnapshot(name);
			if (baCloud) {
				log("Snapshot bytes: " + baCloud.length);
			} else {
				log("No data");
			}

		}



/*
		private function OnBilling(event:AirBillingEvent):void {
			log("OnBilling: " + event.value);
			switch(event.value) {
				case "setupFinished":
					log("Is billing ready? "+ gei.billingReady()+" <- should be true here.");

					
					//log("User canceled billing test: "
					//	+gei.doPayment("android.test.canceled")
					//);					

					var purchases:String = gei.doQueryPurchases("inapp"); //sync
					log("doQueryPurchases: " + purchases+"\n"); 					
					break;
				case "onPurchasesUpdated|0":
					log(".... OK (item purchased)");
					break;
				case "onPurchasesUpdated|1":
					log(".... USER_CANCELED");
					break;
				case "onPurchasesUpdated|2":
					log(".... SERVICE_UNAVAILABLE (Network connection is down)");
					break;
				case "onPurchasesUpdated|4":
					log(".... ITEM_UNAVAILABLE (Requested product is not available for purchase)");
					break;
				case "onPurchasesUpdated|6":
					log(".... ERROR (Fatal error during the API action)");
					break;
				case "onPurchasesUpdated|7":
					log(".... ITEM_ALREADY_OWNED");
					break;

			}
		} 


		public function purchaseTest(e:MouseEvent=null):void {
			log("Test billing purchase: "
				+gei.doPayment("android.test.purchased")
			);
			
		}
		public function consumeTest(e:MouseEvent=null):void {
			log("Consuming product: ..android.test.purchased.")
			gei.consumeProduct("inapp:com.amanitadesign.TestAmanitaAndroidANE:android.test.purchased");
		}

		public function unavailableTest(e:MouseEvent=null):void {
			log("Trying to buy unavailable item...");
			gei.doPayment("android.test.item_unavailable");
		}

		public function beerTest(e:MouseEvent=null):void {
			log("doPayment (chuchel.pivo): "+ gei.doPayment("chuchel.pivo") );
		}
		public function querySKUTest(e:MouseEvent=null):void {
			log("doQuerySKU (chuchel.pivo) async...")
			gei.doQuerySKU("chuchel.pivo"); //async
		}
*/

		public function GPErrorHandler(e:ErrorEvent):void{
			log("GPErrorHandler: " + e.toString());
		}

		public function GPLicenseResult(event:LicenseStatusEvent):void{
			var GPNLR:String;
			var path:String;
			var file:File;
			var res:String = "status: " + event.status + " statusReason: " + event.statusReason;
			log("GPLicenseResult: " + res);
			
			if(event.statusReason && event.statusReason.length > 0) GPNLR = event.statusReason;
			
			if (GPNLR == "followLastLicensingUrl") {
				log("Now there should be opened Google Play store page for the app");
				gei.followLastLicensingURL();
				//tmQuit = setTimeout(NativeApplication.nativeApplication.exit, 100);
				log("App should quit now as it is unlicensed!");
				return;
			}

			if (event.status) {
				log("App is supposed to be licensed.");
				log("getExternalStorageDirectory: "+ gei.getExternalStorageDirectory().nativePath);
				log("getObbDirectory: "+ gei.getObbDirectory().nativePath);

				if(!gei.hasEventListener(ExpansionStatusEvent.EXPANSION_STATUS)) {
					gei.addEventListener(ExpansionStatusEvent.EXPANSION_STATUS, expansionFilesStatusHandler);
				}
				try {
					//log("beginHandler getStatus "+MainOBBVersion);
					gei.getExpansionStatus(MainOBBVersion);
				} catch(e:Error) {
					log("beginHandler getStatus E!!!!! "+e.getStackTrace());
				}
				path = gei.getMainOBBPath();
				log("Main OBB should be: "+path);
				file = new File(path);
				log("Main OBB exists: "+ file.exists);

			}
		}

		private function expansionFilesStatusHandler(e:ExpansionStatusEvent):void {
			log("expansionFilesStatusHandler: "+" main: "+e.main+" patch: "+e.patch);
			gei.removeEventListener(ExpansionStatusEvent.EXPANSION_STATUS, expansionFilesStatusHandler);
			//log("getAPKMainURL:      "+ gei.getAPKMainURL());
			//log("getAPKMainFileName: "+ gei.getAPKMainFileName());
			//log("getAPKMainFileSize: "+ gei.getAPKMainFileSize());
			log("???" + (e.main == false));
			if (!e.main) {
				log("OBB main file not found.. needs download!");
				gei.addEventListener(ExpansionProgressEvent.EXPANSION_PROGRESS, expansionFilesProgressHandler);
				gei.addEventListener(ExpansionStateEvent.EXPANSION_STATE, expansionFilesStateHandler);

				//var canWrite:Boolean = AndroidPermissions.instance.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE");
				//log("can write obb? "+canWrite);

				gei.startExpansionDownload(GPPublicKey);
			}
		}

		private function expansionFilesStateHandler(e:ExpansionStateEvent):void {
			var state:int = e.state;
			var msg:String;
			switch(state) {
				case ExpansionStateEvent.STATE_CONNECTING:                   msg = "Connecting"; break;
				case ExpansionStateEvent.STATE_DOWNLOADING:                  msg = "Downloading"; break;
				
				case ExpansionStateEvent.STATE_FAILED_SDCARD_FULL:           msg = "SD Card full!"; break;
				case ExpansionStateEvent.STATE_FAILED_FETCHING_URL:          msg = "Failed fetching URL"; break;
				case ExpansionStateEvent.STATE_FAILED_UNLICENSED:            msg = "Unlicensed!"; break;
				
				case ExpansionStateEvent.STATE_PAUSED_NETWORK_SETUP_FAILURE: msg = "Network setup failure"; break;
				case ExpansionStateEvent.STATE_PAUSED_NETWORK_UNAVAILABLE:   msg = "Network unavailable"; break;
				
				case ExpansionStateEvent.STATE_PAUSED_ROAMING:
				case ExpansionStateEvent.STATE_PAUSED_BY_REQUEST:
				case ExpansionStateEvent.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
				case ExpansionStateEvent.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
				                                                             msg = "Paused"; break;
				case ExpansionStateEvent.STATE_PAUSED_WIFI_DISABLED:
				case ExpansionStateEvent.STATE_PAUSED_NEED_WIFI:             msg = "Need Wi-Fi"; break;
				case ExpansionStateEvent.STATE_COMPLETED:                    msg = "EXPANSION DOWNLOAD COMPLETED"; break;

				case ExpansionStateEvent.STATE_FAILED:                       msg = "Failed!"; break;
			}
			log("expansionFilesStateHandler state:" + state +" msg: "+ msg);
		}
		private function expansionFilesProgressHandler(e:ExpansionProgressEvent):void {
			log("expansionFilesProgressHandler percent:" + e.status);
		}
		
	}
	
}