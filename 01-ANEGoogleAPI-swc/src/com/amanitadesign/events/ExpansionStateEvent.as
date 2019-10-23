package com.amanitadesign.events
{
	import flash.events.Event;

	public class ExpansionStateEvent extends Event
	{
		public static const EXPANSION_STATE:String = "EXPANSION_STATE";
		
		public static const STATE_IDLE:int = 1;
		public static const STATE_FETCHING_URL:int = 2;
		public static const STATE_CONNECTING:int = 3;
		public static const STATE_DOWNLOADING:int = 4;
		public static const STATE_COMPLETED:int = 5;

		public static const STATE_PAUSED_NETWORK_UNAVAILABLE:int = 6;
		public static const STATE_PAUSED_BY_REQUEST:int = 7;

		/**
		 * Both STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION and
		 * STATE_PAUSED_NEED_CELLULAR_PERMISSION imply that Wi-Fi is unavailable and
		 * cellular permission will restart the service. Wi-Fi disabled means that
		 * the Wi-Fi manager is returning that Wi-Fi is not enabled, while in the
		 * other case Wi-Fi is enabled but not available.
		 */
		public static const STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:int = 8;
		public static const STATE_PAUSED_NEED_CELLULAR_PERMISSION:int = 9;

		/**
		 * Both STATE_PAUSED_WIFI_DISABLED and STATE_PAUSED_NEED_WIFI imply that
		 * Wi-Fi is unavailable and cellular permission will NOT restart the
		 * service. Wi-Fi disabled means that the Wi-Fi manager is returning that
		 * Wi-Fi is not enabled, while in the other case Wi-Fi is enabled but not
		 * available.
		 * <p>
		 * The service does not return these values. We recommend that app
		 * developers with very large payloads do not allow these payloads to be
		 * downloaded over cellular connections.
		 */
		public static const STATE_PAUSED_WIFI_DISABLED:int = 10;
		public static const STATE_PAUSED_NEED_WIFI:int = 11;

		public static const STATE_PAUSED_ROAMING:int = 12;

		/**
		 * Scary case. We were on a network that redirected us to another website
		 * that delivered us the wrong file.
		 */
		public static const STATE_PAUSED_NETWORK_SETUP_FAILURE:int = 13;

		public static const STATE_PAUSED_SDCARD_UNAVAILABLE:int = 14;

		public static const STATE_FAILED_UNLICENSED:int = 15;
		public static const STATE_FAILED_FETCHING_URL:int = 16;
		public static const STATE_FAILED_SDCARD_FULL:int = 17;
		public static const STATE_FAILED_CANCELED:int = 18;

		public static const STATE_FAILED:int = 19;
		
		public var state:int;
		
		public function ExpansionStateEvent( type:String, state:String )
		{
			super(type);
			this.state = parseInt(state);
			//trace("ExpansionStateEvent... " + state);
			
		}
	}
}