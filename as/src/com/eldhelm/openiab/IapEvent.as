package com.eldhelm.openiab {
	import flash.events.Event;
	/**
	 * ...
	 * @author Andrey Glavchev
	 */
	public class IapEvent extends Event {
		
		public static const INIT_SUCCESSFUL:String = "iapEvent_init_successful";
		public static const INIT_FAILED:String = "iapEvent_init_failed";
		public static const PURCHASE_SUCCESSFUL:String = "iapEvent_purchase_successful";
		public static const PURCHASE_FAILED:String = "iapEvent_purchase_failed";
		
		/**
		 * Fires when an error occures while interacting with the api
		 */
		public static const ON_ERROR:String = "iapEvent_error";
		
		/**
		 * Fires when an exception occures (and it is catched) in the native code
		 */
		public static const ON_EXCEPTION:String = "iapEvent_exception";
		
		public var sharedObject:Object;
		
		public function IapEvent(type:String, sharedObject:Object = null, bubbles:Boolean = false, cancelable:Boolean = false) {
			super(type, bubbles, cancelable);
			this.sharedObject = sharedObject;
		}
		
	}

}