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

	private DeclarationLine line;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declarationline_details);
		Bundle data = (Bundle) getIntent().getExtras().get("data");
	}	
}
