package com.eldhelm.openiab.function;

import org.onepf.oms.OpenIabHelper;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.eldhelm.openiab.InAppExtensionContext;

public class MapSkuFunction implements FREFunction {

	@Override
	public FREObject call(FREContext arg0, FREObject[] arg1) {
		InAppExtensionContext frecontext = (InAppExtensionContext) arg0;
		
		String sku = "";
		String vendor = "";
		String externalSku = "";
		String type = "";
		
		try {
			sku = arg1[0].getAsString();
			vendor = arg1[1].getAsString();
			externalSku = arg1[2].getAsString();
			type = arg1[3].getAsString();
		} catch(Exception e) {
			frecontext.sendException(e);
		}		
		frecontext.sendWarning("Maping sku:" + sku + "; vendor:"+vendor+"; externalSku:"+externalSku+"; type:"+type);
		
		String vndr = null;
		if (vendor == "amazon") vndr = OpenIabHelper.NAME_AMAZON;
		else if (vendor == "tstore") vndr = OpenIabHelper.NAME_TSTORE;
		else if (vendor == "samsung") vndr = OpenIabHelper.NAME_SAMSUNG;
		else if (vendor == "google") vndr = OpenIabHelper.NAME_GOOGLE;
		else if (vendor == "yandex") vndr = "com.yandex.store";
		
		if (vendor == null) {
			frecontext.sendError("No vendor specified for sku mapping");
			return null;
		}
		
        OpenIabHelper.mapSku(sku, vndr, externalSku);
        if (type.equals(InAppExtensionContext.CONSUMABLE)) frecontext.productTypes.put(sku, type);
        
		return null;
	}

}
