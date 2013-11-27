package com.sirs.mobilecashandroid;

import java.io.IOException;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;

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
	private AsyncHttpClient client;
	
	private void buyProduct() throws IOException {
		client = new AsyncHttpClient();
		BoundRequestBuilder r = client.preparePost("http://mobilecashserver.herokuapp.com/api/buy");
		r.setBody("{\"username\" : \"samuelcoelho\", \"password\" : \"ist169350\", \"product\" : \"coca-cola\"}");
		r.addHeader("Content-type", "application/json");
		r.execute(new AsyncCompletionHandler<Response>() {

			@Override
			public Response onCompleted(Response response) throws Exception {
				responseTxt.setText(response.getResponseBody());
				return response;
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
