package com.ilmoitus.activity;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.adapter.DeclarationLineAdapter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.DeclarationLine;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class DeclarationLineDetailsActivity extends Activity {

	private long lineId;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declarationline_details);
		lineId = getIntent().getExtras().getLong("lineId");
		new GetDeclarationLineDetails().execute();
	}
	
	private class GetDeclarationLineDetails extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			System.out.println(lineId);
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) + "/declaration/lines/" + lineId);
			httpGet.setHeader("Authorization", LoggedInPerson.token);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				InputStream inputStream = response.getEntity().getContent();
				if (inputStream != null) {
					// parse the inputStream to string
					result = InputStreamConverter.convertInputStreamToString(inputStream);
				} else {
					result = "Did not Work";
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject declarationLineDetails = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
