package com.sirs.mobilecashandroid;

import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import comsirs.mobilecashandroid.R;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
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
	
	private void buyProduct() throws IOException, JSONException {
		AsyncHttpClient client = new AsyncHttpClient();
		
		String username = userName.getText().toString();
		String password = passwd.getText().toString();
		String productCode = product.getText().toString();
		
		JSONObject jsonParams = new JSONObject();
        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("product", productCode);
        StringEntity entity = new StringEntity(jsonParams.toString());
        String url = "http://mobilecashserver.herokuapp.com/api/buy";
        client.post(getApplicationContext(), url, entity, "application/json", new AsyncHttpResponseHandler() {
        	@Override
        	public void onSuccess(String response) {
        		try {
					JSONObject object = new JSONObject(response);
					responseTxt.setText("You have bougth a " + object.getString("product") + " and you have " + object.getString("balance"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						JSONObject object = new JSONObject(response);
						responseTxt.setText(object.getString("message"));
					} catch (JSONException e1) {
						responseTxt.setText("FATAL ERROR!!");
					}
				}
        		
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
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
