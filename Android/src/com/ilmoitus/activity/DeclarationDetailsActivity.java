
package com.ilmoitus.activity;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class DeclarationDetailsActivity extends Activity{

	private long decId;
	private ArrayList<DeclarationLine> decLines;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declaration_details);
		decLines = new ArrayList<DeclarationLine>();
		decId = getIntent().getExtras().getLong("decId");
		ListView list = (ListView) findViewById(R.id.list);
		new GetSpecificDeclaration(this, list).execute();
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DeclarationLine line = decLines.get(position);
				Intent intent = new Intent(getApplicationContext(), DeclarationLineDetailsActivity.class);
				intent.putExtra("lineId", line.getId());
				startActivity(intent);
			}
			
		});
	}
	
	private class GetSpecificDeclaration extends AsyncTask<Void, Void, String>{
		
		private Activity activity;
		private ListView context;
		
		public GetSpecificDeclaration(Activity activity, ListView context){
			this.activity = activity;
			this.context = context;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) + "/declaration/" + decId);
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
				JSONObject declarationDetails = new JSONObject(result);
				
				TextView totalPrice = (TextView) findViewById(R.id.totalPrice);
				totalPrice.setText("\u20AC" + declarationDetails.getString("items_total_price"));
				
				JSONArray lines = (JSONArray) declarationDetails.get("lines");
				for(int i = 0; i < lines.length(); i++){
					JSONObject line = lines.getJSONObject(i);
					DeclarationLine declLine = new DeclarationLine(line.getLong("id"), line.getString("receipt_date"), null,
							line.getLong("declaration_sub_type"), line.getInt("cost"));
					decLines.add(declLine);
				}
				DeclarationLineAdapter decLineAdapter = new DeclarationLineAdapter(activity, decLines);
				context.setAdapter(decLineAdapter);
				MultiAutoCompleteTextView comment = (MultiAutoCompleteTextView) findViewById(R.id.comment);
				comment.setText(declarationDetails.getString("comment"));
				comment.setEnabled(false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
