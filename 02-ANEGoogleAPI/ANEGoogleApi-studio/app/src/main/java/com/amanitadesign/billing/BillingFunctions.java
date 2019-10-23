package com.amanitadesign.billing;

import com.adobe.fre.FREObject;
import com.amanitadesign.GoogleExtension;
import com.amanitadesign.GoogleExtensionContext;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;

import java.util.List;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import android.util.Log;

/**
 * Created by Oldes on 17/9/2018.
 */

public class BillingFunctions {
	/*
    static public class BillingInit implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "BillingInit -> call()");
            
            if(GoogleExtension.billingClient == null) {
            	GoogleExtension.billingClient = BillingClient.newBuilder(GoogleExtensionContext.getMainActivity()).setListener(GoogleExtension.extensionContext).build();
            }
            if(!GoogleExtension.billingClient.isReady())
            	GoogleExtension.billingClient.startConnection(GoogleExtension.extensionContext);
            return null;
        }
    }
    
    static public class BillingEnd implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "BillingEnd -> call()");
            GoogleExtension.billingClient.endConnection();
            GoogleExtension.billingClient = null;
            return null;
        }
    }
    
    static public class BillingReady implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "BillingReady -> call()");
            FREObject result = null;
            try {
            	result = FREObject.newObject(
            			GoogleExtension.billingClient != null &&
            			GoogleExtension.billingClient.isReady()
            	);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    static public class DoPayment implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "DoPayment -> call()");
            FREObject result = null;
            try {
            	String sku  = args[0].getAsString();
            	String type = args[1].getAsString();
            
            	int resultCode = GoogleExtension.extensionContext.doPayment(sku, type);
            	result = FREObject.newObject(resultCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    static public class ConsumeProduct implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "ConsumeProduct -> call()");
            try {
            	String token  = args[0].getAsString();
            	if(token == null) return null;
            	GoogleExtension.billingClient.consumeAsync(token, GoogleExtension.extensionContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    static public class DoQuerySKU implements FREFunction  {
    	// test pouze na jednu jednotku... type je defaultne: inapp
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "DoQuerySKU -> call()");
            try {
                String sku  = args[0].getAsString();
                String type = args[1].getAsString();
                GoogleExtension.extensionContext.doQuerySKU(sku, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    static public class DoQueryPurchases implements FREFunction  {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            if(GoogleExtension.VERBOSE > 0) Log.i(GoogleExtension.TAG, "DoQueryResponses -> call()");
            StringBuilder str = new StringBuilder();
            FREObject result = null;
            try {
                String type = args[0].getAsString();
                PurchasesResult pResult = GoogleExtension.extensionContext.doQueryPurchases(type);
                if(pResult != null) {
                	List<Purchase> purchases  = pResult.getPurchasesList();
                	str.append(purchases.size());
        			for (Purchase purchase : purchases) {
                        str.append('|');
                        str.append(purchase.getOriginalJson());
        			}
                }
                result = FREObject.newObject(str.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return result;
        }
    }
*/
  
}
