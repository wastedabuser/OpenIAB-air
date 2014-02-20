package com.eldhelm.openiab.function;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.eldhelm.openiab.InAppExtensionContext;

public class InitializeExtensionFunction implements FREFunction {

	@Override
	public FREObject call(FREContext arg0, FREObject[] arg1) {
		InAppExtensionContext frecontext = (InAppExtensionContext) arg0;
		
		try {
			frecontext.developmentMode = arg1[0].getAsBool();
			frecontext.base64EncodedPublicKey = arg1[1].getAsString();
			frecontext.YANDEX_PUBLIC_KEY = arg1[2].getAsString();
		} catch(Exception e) {
			frecontext.sendException(e);
		}
		
		frecontext.sendWarning("Initializing");
		frecontext.init();
		
		return null;
	}

}
