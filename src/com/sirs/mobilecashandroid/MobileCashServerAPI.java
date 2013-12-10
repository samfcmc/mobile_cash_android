package com.sirs.mobilecashandroid;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MobileCashServerAPI {
	private static MobileCashServerAPI instance;
	
	private AsyncHttpClient client = new AsyncHttpClient();
	private final String url = "http://mobilecashserver.herokuapp.com/api/";
	private final String buyEndpoint = "buy";
	
	private MobileCashServerAPI() {
	}
	
	public static MobileCashServerAPI getInstance() {
		if(instance == null) {
			instance = new MobileCashServerAPI();
		}
		return instance;
	}
	
	private String getBuyURL() {
		return url + buyEndpoint + "/";
	}
	
	public void buy(Context context, String username, String password, String product, AsyncHttpResponseHandler responseHandler) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
		JSONObject jsonParams = new JSONObject();
		DateTime time = new DateTime();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("product", product);
        jsonParams.put("timestamp", time.getMillis());
        
        String message = jsonParams.toString();
        
        //Hash the message
        digest.update(message.getBytes());
        String hash = new String(digest.digest());
        
        //Add the hash
        jsonParams.put("hash", hash);
        
        message = jsonParams.toString();	
        
        Log.d("message", message);
        
        StringEntity entity = new StringEntity(jsonParams.toString());
        
        String url = getBuyURL();
        
        client.post(context, url, entity, "application/json", responseHandler);       
	}
}
