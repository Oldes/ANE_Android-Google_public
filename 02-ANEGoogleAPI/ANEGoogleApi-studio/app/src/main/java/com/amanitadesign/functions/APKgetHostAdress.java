package com.amanitadesign.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import java.net.InetAddress;


public class APKgetHostAdress implements FREFunction  {

	@Override
	public FREObject call(FREContext ctx, FREObject[] passedArgs) {
		FREObject result = null;

		try{
			String host = passedArgs[0].getAsString();
			result = FREObject.newObject(InetAddress.getByName(host).getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
