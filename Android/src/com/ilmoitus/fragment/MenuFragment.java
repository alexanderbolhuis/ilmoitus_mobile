package com.ilmoitus.fragment;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.activity.DeclareActivity;
import com.ilmoitus.activity.LoginActivity;
import com.ilmoitus.activity.MainActivity;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.Supervisor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;


public class MenuFragment extends Fragment implements OnClickListener{

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){
		View view =  inflater.inflate(R.layout.menu_fragment, container, false);
		Button logout = (Button) view.findViewById(R.id.buttonLogout);
		logout.setOnClickListener(this);
		Button overView = (Button) view.findViewById(R.id.buttonOvervieuw);
		overView.setOnClickListener(this);
		Button declareButton = (Button) view.findViewById(R.id.buttonDeclare);
		declareButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonLogout:
			new UserLogoutTask(getActivity()).execute();
			break;
		case R.id.buttonDeclare:
			onDeclareButtonClick();
			break;
		case R.id.buttonOvervieuw:
			onOverviewButtonClick();
			break;
		}
	}
	
	public void onDeclareButtonClick(){
		Intent intent = new Intent(getActivity(), DeclareActivity.class);
		startActivity(intent);
	}
	public void onOverviewButtonClick() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
	}
	
	private class UserLogoutTask extends AsyncTask<Void, Void, String>{
		private Activity activity;
		
		public UserLogoutTask(Activity activity){
			this.activity = activity;
		}
		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) + "/auth/logout");
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
		
		protected void onPostExecute(String result) {	
			Intent i = new Intent(activity, LoginActivity.class);
			startActivity(i);
		}
	}
}
