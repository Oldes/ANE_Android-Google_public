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
	
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	
	import com.amanitadesign.GoogleExtension;
	import com.amanitadesign.events.AirGooglePlayGamesEvent;
	import com.amanitadesign.events.AirBillingEvent;
	import com.amanitadesign.events.LicenseStatusEvent;
	
	/**
	 * ...
	 * @author Oldes
	 */
	public class Main extends Sprite 
	{
		
		public var tf:TextField;
		private var nextBtnX:Number = 20;

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

/*
			addButton("PURCHASE", purchaseTest);
			addButton("CONSUME", consumeTest);
			addButton("UNAVAILABLE", unavailableTest);
			addButton("BEER", beerTest)
			addButton("QuerySKU", querySKUTest)
*/
			
			log("Testing Amanita Android ANE...");
			log("GoogleExtension is supported: " + GoogleExtension.isSupported);
			//log(GoogleExtension.instance.hello());

			GoogleExtension.instance.addEventListener(ErrorEvent.ERROR, GPErrorHandler);
			GoogleExtension.instance.addEventListener(LicenseStatusEvent.STATUS, GPLicenseResult);
			GoogleExtension.instance.checkLicense(GPPublicKey);


//			log("Is billing ready? "+ GoogleExtension.instance.billingReady()+" <- should be false here.");
			
			log("\nGooglePlay signin test...");
			GoogleExtension.instance.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_IN_SUCCESS, GPSonSignInSuccess);
			GoogleExtension.instance.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_OUT_SUCCESS, GPSonSignOutSuccess);
			GoogleExtension.instance.addEventListener(AirGooglePlayGamesEvent.ON_SIGN_IN_FAIL, GPSonSignInFail);
			GoogleExtension.instance.signIn();

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
		
		private function GPSonSignInSuccess(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignInSuccess: " + event.value);

//			log("\nBilling init...");
//			GoogleExtension.instance.addEventListener(AirBillingEvent.ON_BILLING, OnBilling);
//			GoogleExtension.instance.billingInit();
		}
		private function GPSonSignOutSuccess(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignOutSuccess: " + event);
		}
		private function GPSonSignInFail(event:AirGooglePlayGamesEvent):void {
			log("GPSonSignInFail: " + event.value);
		}

/*
		private function OnBilling(event:AirBillingEvent):void {
			log("OnBilling: " + event.value);
			switch(event.value) {
				case "setupFinished":
					log("Is billing ready? "+ GoogleExtension.instance.billingReady()+" <- should be true here.");

					
					//log("User canceled billing test: "
					//	+GoogleExtension.instance.doPayment("android.test.canceled")
					//);					

					var purchases:String = GoogleExtension.instance.doQueryPurchases("inapp"); //sync
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
				+GoogleExtension.instance.doPayment("android.test.purchased")
			);
			
		}
		public function consumeTest(e:MouseEvent=null):void {
			log("Consuming product: ..android.test.purchased.")
			GoogleExtension.instance.consumeProduct("inapp:com.amanitadesign.TestAmanitaAndroidANE:android.test.purchased");
		}

		public function unavailableTest(e:MouseEvent=null):void {
			log("Trying to buy unavailable item...");
			GoogleExtension.instance.doPayment("android.test.item_unavailable");
		}

		public function beerTest(e:MouseEvent=null):void {
			log("doPayment (chuchel.pivo): "+ GoogleExtension.instance.doPayment("chuchel.pivo") );
		}
		public function querySKUTest(e:MouseEvent=null):void {
			log("doQuerySKU (chuchel.pivo) async...")
			GoogleExtension.instance.doQuerySKU("chuchel.pivo"); //async
		}
*/

		public function GPErrorHandler(e:ErrorEvent):void{
			log("GPErrorHandler: " + e.toString());
		}

		public function GPLicenseResult(event:LicenseStatusEvent):void{
			var res:String = "status: " + event.status + " statusReason: " + event.statusReason;
			log("GPLicenseResult: " + res);
			
			var GPNLR:String;
			if(event.statusReason && event.statusReason.length > 0) GPNLR = event.statusReason;
			
			if (GPNLR == "followLastLicensingUrl") {
				log("Now there should be opened Google Play store page for the app");
				GoogleExtension.instance.followLastLicensingURL();
				//tmQuit = setTimeout(NativeApplication.nativeApplication.exit, 100);
				log("App should quit now as it is unlicensed!");
			}
		}
		
	}
	
}