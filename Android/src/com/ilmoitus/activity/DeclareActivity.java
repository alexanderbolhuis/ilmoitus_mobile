package com.ilmoitus.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.Supervisor;
import com.ilmoitus.adapter.DeclarationLineAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class DeclareActivity extends Activity implements OnClickListener{

	private ArrayList<DeclarationLine> declarationLines;
	private Button declareButton;
	private Button mainButton;
	private Button addLineButton;
	private Button addDeclaration;
	private Spinner spinner1;
	private ArrayList<Supervisor> supervisors;
	private int totalPrice;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare);
		declareButton = (Button) findViewById(R.id.buttonDeclare);
		declareButton.setEnabled(false);
		mainButton = (Button) findViewById(R.id.buttonOvervieuw);
		mainButton.setOnClickListener(this);
		addLineButton = (Button) findViewById(R.id.onAddLineButton);
		addLineButton.setOnClickListener(this);
		addDeclaration = (Button) findViewById(R.id.onAddDeclaration);
		addDeclaration.setOnClickListener(this);
		new GetSupervisors(this).execute();
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				if(declarationLines == null){
					declarationLines = new ArrayList<DeclarationLine>();
				}
				Bundle b = data.getExtras();
				DeclarationLine line = new DeclarationLine(b.getString("date"), b.getString("declaratieSoort"), b.getLong("declaratieSubSoort"),
						b.getInt("bedrag"));
				declarationLines.add(line);
				totalPrice += b.getInt("bedrag");
				DeclarationLineAdapter ad = new DeclarationLineAdapter(this, declarationLines);
				
				TextView price = (TextView) findViewById(R.id.totalPrice);
				price.setText("\u20AC" + totalPrice);
				
				LinearLayout layout = (LinearLayout) findViewById(R.id.list);
				layout.removeAllViews();
				final int adapterCount = ad.getCount();
				for (int i = 0; i < adapterCount; i++) {
				  View item = ad.getView(i, null, null);
				  layout.addView(item);
				}
			}
		}
	}
	
	public JSONObject createDeclaration()
	{
		MultiAutoCompleteTextView comment = (MultiAutoCompleteTextView) findViewById(R.id.comment);
		Supervisor temp = (Supervisor) spinner1.getSelectedItem();
		JSONObject decl = new JSONObject();
		try {
			decl.put("state", "open");
			decl.put("created_at", new Date());
			decl.put("created_by", LoggedInPerson.id);
			decl.put("assigned_to", temp.getId());
			decl.put("comment", comment.getText());
			decl.put("items_total_price", totalPrice);
			decl.put("items_count", declarationLines.size());
			decl.put("lines", linesToJSONArray());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return decl;
	}
	
	public JSONArray linesToJSONArray()
	{
		JSONArray lines = new JSONArray();
		for(int i = 0; i < declarationLines.size(); i++){
			JSONObject temp = new JSONObject();
			try {
				temp.put("receipt_date", declarationLines.get(i).getDatum());
				temp.put("cost", declarationLines.get(i).getBedrag());
				temp.put("declaration_sub_type", declarationLines.get(i).getDeclaratieSubSoort());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			lines.put(temp);
		}
		return lines;
	}
	public void onAddLineButtonClick() {
		Intent intent = new Intent(this, DeclareLineActivity.class);
		startActivityForResult(intent, 1);
	}
	
	public void onOverviewButtonClick() {
		Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.onAddLineButton:
			onAddLineButtonClick();
			break;
		case R.id.buttonOvervieuw:
			onOverviewButtonClick();
			break;
		case R.id.onAddDeclaration:
			new AddDeclaration().execute();
			break;
		}
	}
	
	private class AddDeclaration extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(getResources().getString(R.string.base_url) + "/declaration");
			httpPost.setHeader("Authorization", LoggedInPerson.token);
			try{
				JSONArray lines = linesToJSONArray();
				JSONObject decl = createDeclaration();
				JSONObject totalDeclaration = new JSONObject();
				totalDeclaration.put("declaration", decl);
				totalDeclaration.put("lines", lines);
				totalDeclaration.put("attachments", "");
				httpPost.setEntity(new StringEntity(totalDeclaration.toString()));
				HttpResponse response = httpClient.execute(httpPost);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}		
	}
	
	private class GetSupervisors extends AsyncTask<Void, Void, String> {
		private Activity activity;

		public GetSupervisors(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					getResources().getString(R.string.base_url)  + "/supervisors/");
			httpGet.setHeader("Authorization", LoggedInPerson.token);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				InputStream inputStream = response.getEntity().getContent();
				if (inputStream != null) {
					// parse the inputStream to string
					result = InputStreamConverter
							.convertInputStreamToString(inputStream);
				} else {
					result = "Did not Work";
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			try{
				supervisors = new ArrayList<Supervisor>();
				JSONArray supervisorArray = new JSONArray(result);
				for (int i = 0; i < supervisorArray.length(); i++) {
					JSONObject object = supervisorArray.getJSONObject(i);
					Supervisor supervisor = new Supervisor(object);
					supervisors.add(supervisor);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			spinner1 = (Spinner) activity.findViewById(R.id.spinner1);
			ArrayAdapter<Supervisor> dataAdapter = new ArrayAdapter<Supervisor>(activity, android.R.layout.simple_spinner_item, supervisors);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(dataAdapter);
		}
	}
}
