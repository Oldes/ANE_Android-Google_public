package com.amanitadesign.events
{
	import flash.events.Event;
	
	public class AirBillingEvent extends Event
	{
		
		public static const ON_BILLING:String = "ON_BILLING";
		
		public var value:String;
		
		public function AirBillingEvent(type:String, value:String="", bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);

			this.value = value;
		}
	}
}