package com.amanitadesign.events
{
	import flash.events.Event;

	public class ExpansionStatusEvent extends Event
	{
		public static const EXPANSION_STATUS:String = "EXPANSION_STATUS";
		
		public var main:Boolean;
		public var patch:Boolean;
		
		public function ExpansionStatusEvent( type:String, status:String )
		{
			super(type);
			trace("ExpansionStatusEvent... " + status);
			if (status != "missing") {
				var parts:Array = status.split(",");
				if (parts && parts.length == 3 && parts[0]=="found") {
					main  = (parts[1] == "true");
					patch = (parts[2] == "true");
				}
			}
		}
	}
}

	
