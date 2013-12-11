package com.sirs.mobilecashandroid;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MobileCashServerAPI {
	private static MobileCashServerAPI instance;
	
	private AsyncHttpClient client = new AsyncHttpClient();
	private final String url = "https://mobilecashserver.herokuapp.com/api/";
	private final String buyEndpoint = "buy";
	private final String publicKeyEndpoint = "publicKey";
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	private MobileCashServerAPI() throws NoSuchAlgorithmException {
		generateKeyPair();
	}
	
	public static MobileCashServerAPI getInstance() throws NoSuchAlgorithmException {
		if(instance == null) {
			instance = new MobileCashServerAPI();
		}
		return instance;
	}
	
	private String getBuyURL() {
		return url + buyEndpoint + "/";
	}
	
	private String getPublicKeyURL() {
		return url + publicKeyEndpoint + "/";
	}
	
	public void buy(Context context, String username, String password, String product, AsyncHttpResponseHandler responseHandler) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		JSONObject jsonParams = new JSONObject();
		DateTime time = new DateTime();

        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("product", product);
        jsonParams.put("timestamp", time.getMillis());
        
        String message = jsonParams.toString();
        
        String hashed = hash(message);
        
        String signedHash = cipher(hashed);
                
        //Add the hash
        jsonParams.put("hash", signedHash);
        
        message = jsonParams.toString();	
        
        Log.d("message", message);
        
        StringEntity entity = new StringEntity(jsonParams.toString());
        
        String url = getBuyURL();
        
        client.post(context, url, entity, "application/json", responseHandler);       
	}
	
	private String hash(String stringToHash) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		
		//Hash the message
        digest.update(stringToHash.getBytes());
        String hash = new String(digest.digest());
        
        return hash;
	}
	
	private void generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		
		KeyPair keyPair = keyGen.generateKeyPair();
		
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		
		//Log.d("public key", new String(publicKey.getEncoded()));
		//Log.d("private key", new String(privateKey.getEncoded()));
	}
	
	public String cipher(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipheredBytes = cipher.doFinal(plainText.getBytes());
		String cipheredString = Base64.encodeToString(cipheredBytes, Base64.NO_WRAP);
		
		return cipheredString;
	}
	
	public void sendPublicKey(Context context, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		JSONObject jsonParams = new JSONObject();
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class);
		
		byte[] keyBytes = spec.getEncoded();
		
		String keyString = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

		Log.d("Sending public key", keyString);
		
		jsonParams.put("publicKey", keyString);

		StringEntity entity = new StringEntity(jsonParams.toString());

		String url = getPublicKeyURL();

		client.post(context, url, entity, "application/json", responseHandler);

	}
}
