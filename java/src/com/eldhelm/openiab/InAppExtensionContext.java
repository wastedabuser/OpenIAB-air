package com.eldhelm.openiab;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.onepf.oms.Appstore;
import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.OpenIabHelper.Options;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Inventory;
import org.onepf.oms.appstore.googleUtils.Purchase;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.eldhelm.openiab.function.InitializeExtensionFunction;
import com.eldhelm.openiab.function.MapSkuFunction;
import com.eldhelm.openiab.function.PurchaseFunction;

public class InAppExtensionContext extends FREContext {
	
	public static final String CONSUMABLE = "consumable";
	static final int RC_REQUEST = 10001;
	
	public boolean developmentMode;
	public String base64EncodedPublicKey;
    public String YANDEX_PUBLIC_KEY;
	public Map<String, String> productTypes = new HashMap<String, String>();
    
    private OpenIabHelper mHelper;
	
	public void init() {
        sendWarning("init.");
        
        Map<String, String> storeKeys = new HashMap<String, String>();
        if (base64EncodedPublicKey != null) storeKeys.put(OpenIabHelper.NAME_GOOGLE, base64EncodedPublicKey);
        // storeKeys.put(OpenIabHelper.NAME_AMAZON, "Unavailable. Amazon doesn't support RSA verification. So this mapping is not needed");
        // storeKeys.put(OpenIabHelper.NAME_SAMSUNG,"Unavailable. SamsungApps doesn't support RSA verification. So this mapping is not needed");
        if (YANDEX_PUBLIC_KEY != null) storeKeys.put("com.yandex.store", YANDEX_PUBLIC_KEY);
        
        mHelper = new OpenIabHelper(getActivity(), storeKeys);
        sendWarning("Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
            	sendWarning("Setup finished.");

                if (!result.isSuccess()) {
                	initFailed("Problem setting up in-app billing: " + result);
                    return;
                }

                sendWarning("Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
	}
	
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        	sendWarning("Query inventory finished.");
            if (result.isFailure()) {
            	initFailed("Failed to query inventory: " + result);
                return;
            }

            sendWarning("Query inventory was successful.");
            /*
             * TODO: Implement some stuff for non-consumables here 
            */
            
            initSuccessful();
        }
    };

    public void purchase(String sku, String payload) {   	
    	mHelper.launchPurchaseFlow(getActivity(), sku, RC_REQUEST, 
                mPurchaseFinishedListener, payload);
    }
    
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            sendWarning("Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
            	purchaseFailed(purchase.getSku(), "Error purchasing: " + result);
                return;
            }

            sendWarning("Purchase successful.");
            String type = productTypes.get(purchase.getSku());
           
            if (type != null && type.equals(CONSUMABLE)) mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            else purchaseSuccessful(purchase.getSku(), purchase.getDeveloperPayload());
        }
    };
    
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        	sendWarning("Consumption finished. Purchase: " + purchase + ", result: " + result);

        	purchaseSuccessful(purchase.getSku(), purchase.getDeveloperPayload());
        	
            if (result.isSuccess()) {
            	// ok ...
            } else {
            	sendError("Error while consuming: " + result);
            }

        }
    };
    
    public void initSuccessful() {
    	dispatchStatusEventAsync("{}", "init_successful");
    }
    
    public void initFailed(String message) {
    	JSONObject json = new JSONObject();
    	try {
	    	json.put("message", message);
    	} catch(Exception e) {
    		sendException(e);
    	}
    	dispatchStatusEventAsync(json.toString(), "init_failed");
    }
    
    public void purchaseFailed(String sku, String message) {
    	JSONObject json = new JSONObject();
    	try {
	    	json.put("sku", sku);
	    	json.put("message", message);
    	} catch(Exception e) {
    		sendException(e);
    	}
    	dispatchStatusEventAsync(json.toString(), "purchase_failed");
    }
    
    public void purchaseSuccessful(String sku, String payload) {
    	JSONObject json = new JSONObject();
    	try {
	    	json.put("sku", sku);
	    	json.put("payload", payload);
    	} catch(Exception e) {
    		sendException(e);
    	}
    	dispatchStatusEventAsync(json.toString(), "purchase_successful");
    }
    
	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		functions.put("initializeExtension", new InitializeExtensionFunction());
		functions.put("mapSku", new MapSkuFunction());
		functions.put("purchase", new PurchaseFunction());
		return functions;
	}
	
	@Override
	public void dispose() {
		
	}
	
	public void sendException(Exception e) {
		sendException(e, "");
	}

	public void sendException(Exception e, String id) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String err = sw.toString();
		try {
			setActionScriptData(FREObject.newObject(err));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		dispatchStatusEventAsync("Extension exception: " + id, "exception");
	}

	public void sendWarning(String msg) {
		dispatchStatusEventAsync(msg, "warning");
	}

	public void sendError(String msg) {
		dispatchStatusEventAsync(msg, "error");
	}

}
