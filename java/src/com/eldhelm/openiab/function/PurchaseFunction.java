package com.eldhelm.openiab.function;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.eldhelm.openiab.InAppExtensionContext;

public class PurchaseFunction implements FREFunction {

	@Override
	public FREObject call(FREContext arg0, FREObject[] arg1) {
		InAppExtensionContext frecontext = (InAppExtensionContext) arg0;
		
		String sku = "";
		String payload = "";
		try {
			sku = arg1[0].getAsString();
			payload = arg1[1].getAsString();
		} catch(Exception e) {
			frecontext.sendException(e);
		}
		
		frecontext.sendWarning("Purchase sku:" + sku + "; payload:" + payload);
		frecontext.purchase(sku, payload);
		
		return null;
	}

}
