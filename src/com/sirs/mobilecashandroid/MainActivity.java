package com.sirs.mobilecashandroid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import comsirs.mobilecashandroid.R;

/**
 * The Class MainActivity.
 * 
 */
public class MainActivity extends Activity {

    /** The button. */
    private Button button;

    /** The user name. */
    private EditText userName;

    /** The passwd. */
    private EditText passwd;

    /** The product. */
    private EditText product;

    /** The response txt. */
    private TextView responseTxt;

    /** The api. */
    private MobileCashServerAPI api;

    /**
     * Buy product.
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JSONException the jSON exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeyException the invalid key exception
     * @throws NoSuchPaddingException the no such padding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws SignatureException the signature exception
     */
    private void buyProduct() throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {

        String username = userName.getText().toString();
        String password = passwd.getText().toString();
        String productCode = product.getText().toString();

        api.buy(getApplicationContext(), username, password, productCode, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                responseTxt.setText(response);
            }

            @Override
            public void onFailure(Throwable error) {
                responseTxt.setText(error.getMessage());
            }
        });
    }

    /**
     * Inits the views.
     */
    private void initViews() {
        button = (Button) findViewById(R.id.submit_button);
        userName = (EditText) findViewById(R.id.userName);
        passwd = (EditText) findViewById(R.id.passwd);
        product = (EditText) findViewById(R.id.product);
        responseTxt = (TextView) findViewById(R.id.responseTxt);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    buyProduct();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        try {
            api = MobileCashServerAPI.getInstance();
            sendPublicKey();
        } catch (NoSuchAlgorithmException e) {
            responseTxt.setText("Error generating keys");
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Send public key.
     */
    private void sendPublicKey() {

        try {
            api.sendPublicKey(getApplicationContext(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    responseTxt.setText(response);
                }

                @Override
                public void onFailure(Throwable e) {
                    responseTxt.setText(e.getMessage());
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

}
