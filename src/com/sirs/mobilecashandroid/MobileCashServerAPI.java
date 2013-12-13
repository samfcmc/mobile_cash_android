package com.sirs.mobilecashandroid;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * The Class MobileCashServerAPI.
 */
public class MobileCashServerAPI {

    /** The instance. */
    private static MobileCashServerAPI instance;

    /** The client. */
    private final AsyncHttpClient client = new AsyncHttpClient();

    /** The url. */
    private final String url = "https://mobilecashserver.herokuapp.com/api/";

    /** The buy endpoint. */
    private final String buyEndpoint = "buy";

    /** The public key endpoint. */
    private final String publicKeyEndpoint = "publicKey";

    /** The public key. */
    private PublicKey publicKey;

    /** The private key. */
    private PrivateKey privateKey;

    /**
     * Instantiates a new mobile cash server api.
     * 
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    private MobileCashServerAPI() throws NoSuchAlgorithmException {
        generateKeyPair();
    }

    /**
     * Gets the single instance of MobileCashServerAPI.
     * 
     * @return single instance of MobileCashServerAPI
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static MobileCashServerAPI getInstance() throws NoSuchAlgorithmException {
        if (instance == null) {
            instance = new MobileCashServerAPI();
        }
        return instance;
    }

    /**
     * Gets the buy url.
     * 
     * @return the buy url
     */
    private String getBuyURL() {
        return url + buyEndpoint + "/";
    }

    /**
     * Gets the public key url.
     * 
     * @return the public key url
     */
    private String getPublicKeyURL() {
        return url + publicKeyEndpoint + "/";
    }

    /**
     * Buy.
     * 
     * @param context the context
     * @param username the username
     * @param password the password
     * @param product the product
     * @param responseHandler the response handler
     * @throws JSONException the jSON exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws InvalidKeyException the invalid key exception
     * @throws NoSuchPaddingException the no such padding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws SignatureException the signature exception
     */
    public void buy(Context context, String username, String password, String product, AsyncHttpResponseHandler responseHandler)
            throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        JSONObject jsonParams = new JSONObject();
        DateTime time = new DateTime();

        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("product", product);
        jsonParams.put("timestamp", time.getMillis());

        String message = jsonParams.toString();

        Log.d("Send request", message);

        //String hashed = hash(message);
        String toSign = username + password + product + time.getMillis();

        Log.d("Message to sign", toSign);

        String signedHash = sign(toSign);

        //Add the hash
        jsonParams.put("hash", signedHash);

        message = jsonParams.toString();

        Log.d("message with hash", message);

        StringEntity entity = new StringEntity(jsonParams.toString());

        String url = getBuyURL();

        printTime(time);

        client.post(context, url, entity, "application/json", responseHandler);
    }

    /**
     * Generate key pair.
     * 
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);

        KeyPair keyPair = keyGen.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

    }

    /**
     * Sign.
     * 
     * @param plainText the plain text
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws NoSuchPaddingException the no such padding exception
     * @throws InvalidKeyException the invalid key exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws SignatureException the signature exception
     */
    private String sign(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, SignatureException {

        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(privateKey);
        signer.update(plainText.getBytes());
        byte[] signatureBytes = signer.sign();
        String signatureString = Base64.encodeToString(signatureBytes, Base64.NO_WRAP);
        return signatureString;
    }

    /**
     * Send public key.
     * 
     * @param context the context
     * @param responseHandler the response handler
     * @throws JSONException the jSON exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public void sendPublicKey(Context context, AsyncHttpResponseHandler responseHandler) throws JSONException,
            UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject jsonParams = new JSONObject();

        byte[] keyBytes = publicKey.getEncoded();

        String keyString = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

        Log.d("Sending public key", keyString);

        jsonParams.put("publicKey", keyString);

        StringEntity entity = new StringEntity(jsonParams.toString());

        String url = getPublicKeyURL();

        client.post(context, url, entity, "application/json", responseHandler);

    }

    /**
     * Prints the time.
     * 
     * @param time the time
     */
    public void printTime(DateTime time) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        String timeString = formatter.print(time);

        Log.d("Current time", timeString);
    }
}
