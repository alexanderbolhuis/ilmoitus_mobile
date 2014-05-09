package com.ilmoitus.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.adapter.DeclarationOverviewAdapter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.listener.ManagerOnItemSelectedListener;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.OpenDeclaration;
import com.ilmoitus.model.Supervisor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class DeclareActivity extends Activity {

	private ArrayList<Supervisor> supervisors;
	private Spinner spinner1;
	private Button declareButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare);
		declareButton = (Button) findViewById(R.id.buttonDeclare);
		declareButton.setEnabled(false);
		new GetSupervisors(this).execute();
		//addListenerOnSpinnerItemSelection();
	}
	
	public void onButtonClick(View view) {
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
			spinner1 = (Spinner) findViewById(R.id.spinner1);
			ArrayAdapter<Supervisor> dataAdapter = new ArrayAdapter<Supervisor>(activity, android.R.layout.simple_spinner_item, supervisors);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(dataAdapter);
		}
	}

//	public void addListenerOnSpinnerItemSelection() {
//		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		spinner1.setOnItemSelectedListener((OnItemSelectedListener) new ManagerOnItemSelectedListener());
//	}
}