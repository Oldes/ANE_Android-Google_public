package com.amanitadesign.events
{
	import flash.events.Event;

	public class ExpansionProgressEvent extends Event
	{
		public static const EXPANSION_PROGRESS:String = "EXPANSION_PROGRESS";
		
		public var status:String;
		
		public function ExpansionProgressEvent( type:String, status:String )
		{
			super(type);
			//trace("ExpansionProgressEvent... " + status);
			this.status = status;
		}
	}
}