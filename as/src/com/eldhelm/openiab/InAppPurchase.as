package com.eldhelm.openiab {
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	/**
	 * ...
	 * @author Andrey Glavchev
	 */
	public class InAppPurchase extends EventDispatcher {
		
		public static const YANDEX:String = "yandex";
		public static const AMAZON:String = "amazon";
		public static const TSTORE:String = "tstore";
		public static const SAMSUNG:String = "samsung";
		public static const GOOGLE:String = "google";
		
		public static const CONSUMABLE:String = "consumable";
		public static const NON_CONSUMABLE:String = "non-consumable";
		public static const SUBSCRIPTION:String = "subscription";
		
		private var extContext:ExtensionContext;
		
		public var initialized:Boolean;
		public var develompentMode:Boolean;
		public var base64EncodedPublicKey:String;
		public var yandexPublicKey:String;
		
		/**
		 * The constructor accepts an optional configuration object for example:
		 * @param	config
		 */
		public function InAppPurchase(config:Object = null) {
			if (config != null) {
				if (config.develompentMode) develompentMode = config.develompentMode;
				if (config.base64EncodedPublicKey) base64EncodedPublicKey = config.base64EncodedPublicKey;
				if (config.yandexPublicKey) yandexPublicKey = config.yandexPublicKey;
			}
			extContext = ExtensionContext.createExtensionContext("com.eldhelm.openiab.InAppPurchase", "");
			if (extContext != null) {
				trace("IAP: context created");
				extContext.addEventListener(StatusEvent.STATUS, onStatus);
			} else {
				trace("IAP: context creation failed");
			}
		}
		
		private function onStatus(event:StatusEvent):void {		
			if (event.level == "init_successful" || event.level == "init_failed" || event.level == "purchase_failed" || event.level == "purchase_successful") {
				trace("IAP Extension: " + event.level + ": " + event.code);
				if (event.level == "init_successful") initialized = true;
				dispatchEvent(new IapEvent("iapEvent_" + event.level, JSON.parse(event.code) ));
				return;
				
			} else if (event.level == "exception") {
				trace("================ " + event.code + " ===================");
				trace(extContext.actionScriptData);
				dispatchEvent(new IapEvent(IapEvent.ON_EXCEPTION, { code: event.code, data: extContext.actionScriptData } ));
				
			} else if (event.level == "warning") {
				trace("IAP Extension: " + event.code);
				return;
				
			} else if (event.level == "error") {
				trace("================ Extension error ===================");
				trace("IAP Extension: " + event.code);
				dispatchEvent(new IapEvent(IapEvent.ON_ERROR, { code: event.code }));
			}
		}
		
		/**
		 * Initializes the extension
		 */
		public function initialize():void {
			if (!extContext) return;
			
			trace("IAP: execute initialize");
			extContext.call("initializeExtension", develompentMode, base64EncodedPublicKey, yandexPublicKey);
		}
		
		/**
		 *  Initiate a mapign of an item
		 * @param	sku
		 * @param	vendor
		 * @param	vendorSku
		 * @param	type
		 */
		public function mapSku(sku:String, vendor:String, vendorSku:String, type:String):void {
			if (!extContext|| !initialized) return;
			
			trace("IAP: execute mapSku");
			extContext.call("mapSku", sku, vendor, vendorSku, type);
		}
		
		/**
		 * Initiate a purchase of an item
		 * @param	sku
		 */
		public function purchase(sku:String, payload:String):void {
			if (!extContext|| !initialized) return;
			
			trace("IAP: execute purchase");
			extContext.call("purchase", sku, payload);
		}
		
		/**
		 * Disposes the iap and related objects
		 */
		public function dispose():void {
			if (extContext != null) {
				extContext.removeEventListener(StatusEvent.STATUS, onStatus);	
				extContext.dispose();
				extContext = null;
			}
		}
		
	}

}