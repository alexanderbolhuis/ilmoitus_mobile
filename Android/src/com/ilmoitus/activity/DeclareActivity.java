package com.ilmoitus.activity;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.Supervisor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class DeclareActivity extends Activity implements OnClickListener{

	private ArrayList<DeclarationLine> declarationLines;
	private Button declareButton;
	private Button mainButton;
	private Button addLineButton;
	private Spinner spinner1;
	private ArrayList<Supervisor> supervisors;
	
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
				DeclarationLine line = new DeclarationLine(b.getString("date"), b.getString("bedrag"), b.getString("declaratieSoort"),
						b.getString("declaratieSubSoort"), null);
				declarationLines.add(line);
				ArrayList<String> temp = new ArrayList<String>();
				for(int i = 0; i < declarationLines.size(); i++){
					temp.add(declarationLines.get(i).getDatum() + " - " + declarationLines.get(i).getDeclaratieSoort() + " - " 
							+ declarationLines.get(i).getBedrag());
				}
				ListView list = (ListView) findViewById(R.id.list);
				ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
				list.setAdapter(ad);
			}
		}
	}
	

	public void onAddLineButtonClick() {
		Intent intent = new Intent(this, DeclareLineActivity.class);
		startActivityForResult(intent, 1);
	}
	
	public void onOverviewButtonClick() {
		Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
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
					"http://2.sns-ilmoitus.appspot.com/supervisors/");
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
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(dataAdapter);
		}
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
		}
	}
}
