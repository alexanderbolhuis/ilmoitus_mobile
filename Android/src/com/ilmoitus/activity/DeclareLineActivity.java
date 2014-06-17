package com.ilmoitus.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.adapter.AttachmentOverviewAdapter;
import com.ilmoitus.adapter.DeclarationOverviewAdapter;
import com.ilmoitus.croscutting.CurrencyFormatInputFilter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.fragment.DatePickerFragment;
import com.ilmoitus.model.Attachment;
import com.ilmoitus.model.DeclarationSubTypes;
import com.ilmoitus.model.DeclarationTypes;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DeclareLineActivity extends Activity implements
		DatePickerFragment.OnDateSelectedListener {

	private Spinner spinnerDeclarationTypes, spinnerDeclarationSubTypes;
	private Calendar date;
	private TextView title;
	private MultiAutoCompleteTextView commentTextView;
	private EditText currencyEditText, dateEditText;
	private ArrayList<String> attachmentsData = new ArrayList<String>();
	private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	private String errorMsg;
	private Boolean validation = true;
	private int spinnerDeclarationTypesPosition;
	private ArrayAdapter<DeclarationTypes> spinnerDeclarationTypesListAdapter;
	private ArrayAdapter<DeclarationSubTypes> spinnerDeclarationSubTypesListAdapter;
	private ArrayList<DeclarationTypes> declarationTypesList;
	private ArrayList<DeclarationSubTypes> declarationSubTypesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);
		title = (TextView) findViewById(R.id.person_title);
		dateEditText = (EditText) findViewById(R.id.editTextDate);
		spinnerDeclarationTypes = (Spinner) findViewById(R.id.spinnerDeclarationType);
		spinnerDeclarationTypes.setOnItemSelectedListener(new spinnerListener());
		spinnerDeclarationTypesPosition = 0;
		currencyEditText = (EditText) findViewById(R.id.editCurrency);
		currencyEditText.setFilters(new InputFilter[] { new CurrencyFormatInputFilter() });
		commentTextView = (MultiAutoCompleteTextView) findViewById(R.id.commentTextView);
		spinnerDeclarationSubTypes = (Spinner) findViewById(R.id.spinnerDeclarationSubType);
		if (savedInstanceState == null) {
			date = Calendar.getInstance();
			setDeclarationTypes();
			setDeclarationSubTypes();
		}
	}

	public void onAddButtonClick(View v){
		final String date = dateEditText.getText().toString();
		if (!isValidDate(date)) {
			validation = false;
			String message = getErrorMsg();
			dateEditText.setError(spanString(message));
		}
		if(!isValidType()){
			validation = false;
		}
		final String currency = currencyEditText.getText().toString();
		if (!isValidCurrency(currency)) {
			validation = false;
			String message = getErrorMsg();
			currencyEditText.setError(spanString(message));
		}
		final String comment = commentTextView.getText().toString();
		if (!isValidComment(comment)) {
			validation = false;
			String message = getErrorMsg();
			commentTextView.setError(spanString(message));
		}
		if (validation == false) {
			Toast.makeText(getApplicationContext(),
					"Declaratie regel bevat fouten", Toast.LENGTH_LONG)
					.show();
			validation = true;
		} else {
			bundleDeclaration();
		}
	}
	
	public void onCancelButtonClick(View v){
		super.onBackPressed();
	}
	// Setter & Getters
	public void setErrorMsg(String err) {
		this.errorMsg = err;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setDeclarationTypes() {
		declarationTypesList = new ArrayList<DeclarationTypes>();

		spinnerDeclarationTypesListAdapter = new ArrayAdapter<DeclarationTypes>(
				this, android.R.layout.simple_spinner_item,
				declarationTypesList);
		spinnerDeclarationTypesListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationTypesListAdapter.setNotifyOnChange(true);

		new GetDeclerationTypesTask(this).execute();
	}

	public void setDeclarationSubTypes() {
		declarationSubTypesList = new ArrayList<DeclarationSubTypes>();

		spinnerDeclarationSubTypesListAdapter = new ArrayAdapter<DeclarationSubTypes>(
				this, android.R.layout.simple_spinner_item,
				declarationSubTypesList);
		spinnerDeclarationSubTypesListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationSubTypesListAdapter.setNotifyOnChange(true);
		spinnerDeclarationSubTypes
				.setAdapter(spinnerDeclarationSubTypesListAdapter);
	}

	public void bundleDeclaration() {
		Bundle b = new Bundle();
		b.putString("date", dateEditText.getText().toString());
		String bedrag = currencyEditText.getText().toString().replace(",", ".");

		b.putDouble("bedrag", Double.parseDouble(bedrag));
		b.putStringArrayList("attachments", attachmentsData);
		b.putString("declaratieSoort",
				declarationTypesList.get(spinnerDeclarationTypesPosition)
						.getName());
		b.putLong("declaratieSoortId",
				declarationTypesList.get(spinnerDeclarationTypesPosition)
						.getId());
		b.putString("declaratieSubSoort",
				((DeclarationSubTypes) spinnerDeclarationSubTypes
						.getSelectedItem()).getName());
		b.putLong("declaratieSubSoortId",
				((DeclarationSubTypes) spinnerDeclarationSubTypes
						.getSelectedItem()).getId());
		Intent i = new Intent(this, DeclareActivity.class);
		i.putExtras(b);
		setResult(RESULT_OK, i);
		finish();
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("year", date.get(Calendar.YEAR));
		args.putInt("month", date.get(Calendar.MONTH));
		args.putInt("day", date.get(Calendar.DAY_OF_MONTH));
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSelected(int year, int month, int day) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.YEAR, year);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = df.format(date.getTime());
		isValidDate(formattedDate);
		dateEditText.setText(formattedDate);
	}

	private class GetDeclerationTypesTask extends AsyncTask<Void, Void, String> {
		public GetDeclerationTypesTask(Activity activity) {
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(
					R.string.base_url)
					+ "/declarationtypes");
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
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			try {
				JSONArray declarationTypes = new JSONArray(result);

				for (int i = 0; i < declarationTypes.length(); i++) {
					JSONObject object = declarationTypes.getJSONObject(i);

					String name = object.getString("name");
					Long id = object.getLong("id");

					declarationTypesList.add(new DeclarationTypes(name, id));
				}
				spinnerDeclarationTypes
						.setAdapter(spinnerDeclarationTypesListAdapter);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class GetDeclerationSubTypesTask extends
			AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = null;

			Long declarationType = declarationTypesList.get(
					spinnerDeclarationTypesPosition).getId();

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(
					R.string.base_url)
					+ "/declarationtype/" + declarationType);
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
				// Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			try {
				JSONArray declarationSubTypes = new JSONArray(result);
				declarationSubTypesList.clear();
				for (int i = 0; i < declarationSubTypes.length(); i++) {
					JSONObject object = declarationSubTypes.getJSONObject(i);
					String name = object.getString("name");
					Long id = object.getLong("id");
					DeclarationSubTypes temp = new DeclarationSubTypes(name, id);
					if (object.has("max_cost")) {
						temp.setMaxCost(object.getDouble("max_cost"));
					}
					declarationSubTypesList.add(temp);
				}
				spinnerDeclarationSubTypes
						.setAdapter(spinnerDeclarationSubTypesListAdapter);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class spinnerListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			spinnerDeclarationTypesPosition = pos;
			new GetDeclerationSubTypesTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	// Form validation & Error messaging
	public SpannableStringBuilder spanString(String spanstring) {
		int textColor = Color.WHITE;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(textColor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(
				spanstring);
		ssbuilder.setSpan(fgcspan, 0, spanstring.length(), 0);
		return ssbuilder;
	}

	private boolean isValidDate(String inputDate) {

		if (inputDate.matches("")) {
			String error = "Datum verplicht!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.setLenient(false);
		Date dateOfDeclarationLine;
		try {
			// if not valid, it will throw ParseException
			dateOfDeclarationLine = sdf
					.parse(dateEditText.getText().toString());

		} catch (ParseException e) {
			String error = "Geen geldige datum!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}

		Date datNow = new Date();
		if (dateOfDeclarationLine.after(datNow)) {
			String error = "Datum ligt niet in het verleden!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}

		setErrorMsg(null);
		dateEditText.setError(null);
		return true;
	}

	private boolean isValidType() {

		if (declarationTypesList.get(spinnerDeclarationTypesPosition)
				.toString().matches("")) {
			Toast.makeText(this, "Geen declaratie type geselecteerd!",
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (((DeclarationSubTypes) spinnerDeclarationSubTypes.getSelectedItem())
				.getId().toString().matches("")) {
			Toast.makeText(this, "Geen declaratie subtype geselecteerd!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean isValidCurrency(String inputTotal) {
		if (inputTotal.matches("")) {
			String error = "Bedrag verplicht!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}
		if (!inputTotal.matches("(0|[1-9]+[0-9]*)?(\\,[0-9]{0,2})?")) {
			String error = "Geen geldig bedrag!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}

		double maxCost = ((DeclarationSubTypes) spinnerDeclarationSubTypes
				.getSelectedItem()).getMaxCost();
		if (maxCost != 0) {
			if (Double.parseDouble(inputTotal) > maxCost) {
				String error = "Bedrag is hoger dan maximaal in te dienen";
				String strDateErrorFormat = getResources().getString(
						R.string.label_date_hint);
				setErrorMsg(String.format(strDateErrorFormat, error));
				return false;
			}
		}
		setErrorMsg(null);
		dateEditText.setError(null);
		return true;
	}

	private boolean isValidAttachment() {
		if (attachments.size() <= 0) {
			Toast.makeText(this, "Minimaal ��n bijlage toevoegen!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean isValidComment(String inputComment) {
		if (inputComment.matches("")) {
			String error = "Opmerking verplicht!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			return false;
		}
		setErrorMsg(null);
		commentTextView.setError(null);
		return true;
	}
}
