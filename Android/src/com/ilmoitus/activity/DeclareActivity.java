package com.ilmoitus.activity;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
import com.ilmoitus.model.DeclarationSubTypes;
import com.ilmoitus.model.DeclarationTypes;
import com.ilmoitus.model.Supervisor;
import com.ilmoitus.adapter.DeclarationLineAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.DateTimeKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DeclareActivity extends Activity implements OnClickListener {
	private ArrayList<DeclarationLine> declarationLines;
	private ArrayList<Supervisor> supervisors;
	private Button declareButton, mainButton, addLineButton, addDeclaration;
	private ListView decLinesView;
	private Spinner spinnerSupervisors;
	private ArrayList<JSONObject> attachments;
	private MultiAutoCompleteTextView commentTextView;
	private Boolean validation = true;
	private String errorMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare);
		declarationLines = new ArrayList<DeclarationLine>();
		new DecimalFormat("0.00");
		decLinesView = (ListView) findViewById(R.id.list);
		decLinesView.setVerticalScrollBarEnabled(false);
		spinnerSupervisors = (Spinner) findViewById(R.id.spinnerSupervisors);
		commentTextView = (MultiAutoCompleteTextView) findViewById(R.id.commentTextView);
		declareButton = (Button) findViewById(R.id.buttonDeclare);
		declareButton.setEnabled(false);
		mainButton = (Button) findViewById(R.id.buttonOvervieuw);
		mainButton.setOnClickListener(this);
		addLineButton = (Button) findViewById(R.id.onAddLineButton);
		addLineButton.setOnClickListener(this);
		new GetSupervisors(this).execute();
		if (this.getIntent().getExtras() != null) {
			long x = getIntent().getExtras().getLong("decId");
			new GetSpecificDeclaration(this, x).execute();
		}
		findViewById(R.id.onAddDeclaration).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (!isValidDeclaration()) {
							validation = false;
						}
						;
						if (!isValidComment(commentTextView.getText()
								.toString())) {
							validation = false;
							String message = getErrorMsg();
							commentTextView.setError(spanString(message));
						}
						if (validation == false) {
							Toast.makeText(getApplicationContext(),
									"Declaratie bevat fouten",
									Toast.LENGTH_LONG).show();
						} else {
							new AddDeclaration().execute();
						}
					}
				});
	}

	// Setter & Getters
	public void setErrorMsg(String err) {
		this.errorMsg = err;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				if (attachments == null) {
					attachments = new ArrayList<JSONObject>();
				}
				Bundle b = data.getExtras();
				DeclarationLine line = new DeclarationLine(b.getLong("id"),
						b.getString("date"), new DeclarationTypes(
								b.getString("declaratieSoort"),
								b.getLong("declaratieSoortId")),
						new DeclarationSubTypes(b
								.getString("declaratieSubSoort"), b
								.getLong("declaratieSubSoortId")),
						b.getDouble("bedrag"));
				declarationLines.add(line);
				b.getDouble("bedrag");
				ArrayList<String> temp = b.getStringArrayList("attachments");
				for (int i = 0; i < temp.size(); i++) {
					try {
						attachments.add(new JSONObject(temp.get(i)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				DeclarationLineAdapter ad = new DeclarationLineAdapter(this,
						declarationLines);
				decLinesView.setAdapter(ad);
			}
		}
	}

	public JSONObject createDeclaration() {
		MultiAutoCompleteTextView comment = (MultiAutoCompleteTextView) findViewById(R.id.commentTextView);
		Supervisor temp = (Supervisor) spinnerSupervisors.getSelectedItem();
		JSONObject decl = new JSONObject();
		try {
			decl.put("state", "open");
			decl.put("created_at", new Date());
			decl.put("created_by", LoggedInPerson.id);
			decl.put("assigned_to", temp.getId());
			decl.put("supervisor", temp.getId());
			decl.put("comment", comment.getText());
			decl.put("items_total_price", getTotalPrice());
			decl.put("items_count", declarationLines.size());
			decl.put("lines", linesToJSONArray());
			decl.put("attachments", new JSONArray(attachments));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return decl;
	}

	private JSONArray linesToJSONArray() {
		JSONArray lines = new JSONArray();
		for (int i = 0; i < declarationLines.size(); i++) {
			JSONObject temp = new JSONObject();
			try {
				temp.put("receipt_date", declarationLines.get(i).getDatum());
				temp.put("cost", declarationLines.get(i).getBedrag());
				temp.put("declaration_sub_type", declarationLines.get(i)
						.getDeclaratieSubSoort().getId());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			lines.put(temp);
		}
		return lines;
	}

	private double getTotalPrice() {
		double x = 0;
		for (int i = 0; i < declarationLines.size(); i++) {
			x += declarationLines.get(i).getBedrag();
		}
		return x;
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
		switch (v.getId()) {
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
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(getResources().getString(
					R.string.base_url)
					+ "/declaration");
			httpPost.setHeader("Authorization", LoggedInPerson.token);
			try {
				JSONObject decl = createDeclaration();
				JSONObject totalDeclaration = new JSONObject();
				totalDeclaration.put("declaration", decl);
				httpPost.setEntity(new StringEntity(totalDeclaration.toString()));
				HttpResponse response = httpClient.execute(httpPost);
				InputStream inputStream = response.getEntity().getContent();
				if (inputStream != null) {
					// parse the inputStream to string
					result = InputStreamConverter
							.convertInputStreamToString(inputStream);
				} else {
					result = "Did not Work";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
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
			HttpGet httpGet = new HttpGet(getResources().getString(
					R.string.base_url)
					+ "/current_user/supervisors");
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
			try {
				supervisors = new ArrayList<Supervisor>();
				JSONArray supervisorArray = new JSONArray(result);
				for (int i = 0; i < supervisorArray.length(); i++) {
					JSONObject object = supervisorArray.getJSONObject(i);
					Supervisor supervisor = new Supervisor(object);
					supervisors.add(supervisor);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayAdapter<Supervisor> dataAdapter = new ArrayAdapter<Supervisor>(
					activity, android.R.layout.simple_spinner_item, supervisors);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerSupervisors.setAdapter(dataAdapter);
		}
	}

	private class GetSpecificDeclaration extends AsyncTask<Void, Void, String> {

		private Activity activity;
		private long decId;

		public GetSpecificDeclaration(Activity activity, Long decId) {
			this.activity = activity;
			this.decId = decId;
		}

		protected String doInBackground(Void... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(
					R.string.base_url)
					+ "/declaration/" + decId);
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
			try {
				JSONObject declarationDetails = new JSONObject(result);
				JSONObject supervisor = (JSONObject) declarationDetails
						.get("last_assigned_to");
				JSONArray lines = (JSONArray) declarationDetails.get("lines");
				for (int i = 0; i < lines.length(); i++) {
					JSONObject line = lines.getJSONObject(i);
					JSONObject declarationType = (JSONObject) line
							.get("declaration_type");
					JSONObject declarationSubType = (JSONObject) line
							.get("declaration_sub_type");
					DeclarationLine decLine = new DeclarationLine(
							line.getLong("id"), line.getString("receipt_date"),
							new DeclarationTypes(declarationType
									.getString("name"), declarationType
									.getLong("id")),
							new DeclarationSubTypes(declarationSubType
									.getString("name"), declarationSubType
									.getLong("id")), line.getInt("cost"));
					declarationLines.add(decLine);
				}
				DeclarationLineAdapter decLineAdapter = new DeclarationLineAdapter(
						activity, declarationLines);
				int x = 0;
				for (int i = 0; i < supervisors.size(); i++) {
					Supervisor temp = supervisors.get(i);
					if (temp.getFirstName().equals(
							supervisor.getString("first_name"))
							&& temp.getLastName().equals(
									supervisor.getString("last_name"))) {
						x = i;
					}
				}
				if (x != 0) {
					spinnerSupervisors.setSelection(x);
				}
				decLinesView.setAdapter(decLineAdapter);
				commentTextView
						.setText(declarationDetails.getString("comment"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// Form validation
	public SpannableStringBuilder spanString(String spanstring) {
		int textColor = Color.BLACK;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(textColor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(
				spanstring);
		ssbuilder.setSpan(fgcspan, 0, spanstring.length(), 0);
		return ssbuilder;
	}

	private boolean isValidComment(String inputComment) {
		if (inputComment.matches("")) {
			String error = "Opmerking verplicht!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			validation = false;
			return false;
		}
		setErrorMsg(null);
		commentTextView.setError(null);
		return true;
	}

	private boolean isValidDeclaration() {
		if (declarationLines.size() <= 0) {
			Toast.makeText(this, "Minimaal een declaratie toevoegen!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();

	}
}
