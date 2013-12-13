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

import com.loopj.android.http.AsyncHttpResponseHandler;

import comsirs.mobilecashandroid.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity<T> extends Activity {

	private Button button;
	private EditText userName;
	private EditText passwd;
	private EditText product;
	private TextView responseTxt;
	private MobileCashServerAPI api;
	
	private void buyProduct() throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		
		String username = userName.getText().toString();
		String password = passwd.getText().toString();
		String productCode = product.getText().toString();
        
		
        api.buy(getApplicationContext(), username, password, productCode, new AsyncHttpResponseHandler() {
        	public void onSuccess(String response) {
        		responseTxt.setText(response);
        	}
        	
        	public void onFailure(Throwable error) {
        		responseTxt.setText(error.getMessage());
        	}
        });
	}
	
	
	private void initViews() {
		button = (Button) findViewById(R.id.submit_button);
		userName = (EditText) findViewById(R.id.userName);
		passwd = (EditText) findViewById(R.id.passwd);
		product = (EditText) findViewById(R.id.product);
		responseTxt = (TextView) findViewById(R.id.responseTxt);
		
		button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
					buyProduct();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void sendPublicKey() {
		
		try {
			api.sendPublicKey(getApplicationContext(), new AsyncHttpResponseHandler() {
				public void onSuccess(String response) {
					responseTxt.setText(response);
				}
				
				public void onFailure(Throwable e) {
					responseTxt.setText(e.getMessage());
				}
			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
