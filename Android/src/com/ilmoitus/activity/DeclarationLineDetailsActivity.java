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
import com.ilmoitus.adapter.AttachmentOverviewAdapter;
import com.ilmoitus.adapter.AttachmentOverviewDetialsAdapter;
import com.ilmoitus.adapter.DeclarationLineAdapter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.Attachment;
import com.ilmoitus.model.DeclarationLine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class DeclarationLineDetailsActivity extends Activity {

	private DeclarationLine line;
	private LinearLayout attachmentList;
	private ArrayList<Attachment> attachmentDetail = new ArrayList<Attachment>();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declarationline_details);
		Bundle data = (Bundle) getIntent().getExtras().get("data");
		TextView date = (TextView) findViewById(R.id.date);
		date.setText(data.getString("datum"));
		
		TextView declarationType = (TextView) findViewById(R.id.declarationtype);
		declarationType.setText(data.getString("declaratieSoort"));
		
		TextView declarationSubType = (TextView) findViewById(R.id.declarationsubtype);
		declarationSubType.setText(data.getString("declaratieSubSoort"));
		
		TextView cost = (TextView) findViewById(R.id.cost);
		cost.setText("\u20AC" + data.getDouble("cost"));
	}	
}
