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
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.fragment.DatePickerFragment;
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
		DatePickerFragment.OnDateSelectedListener, OnClickListener {

	private Spinner spinnerDeclarationTypes, spinnerDeclarationSubTypes;
	private Calendar date;
	private TextView title;
	private MultiAutoCompleteTextView commentTextView;
	private EditText currencyEditText, dateEditText;
	private ImageView photo;
	private ArrayList<String> attachmentsData = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> attachmentItem = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> map;
	private String errorMsg;
	private Boolean validation = false;
	private double maxCost;
	private int spinnerDeclarationTypesPosition,
			spinnerDeclarationSubTypesPosition;
	private LinearLayout attachmentListView;

	ArrayAdapter<DeclarationTypes> spinnerDeclarationTypesListAdapter;
	ArrayAdapter<DeclarationSubTypes> spinnerDeclarationSubTypesListAdapter;
	ArrayList<DeclarationTypes> declarationTypesList;
	ArrayList<DeclarationSubTypes> declarationSubTypesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);

		title = (TextView) findViewById(R.id.person_title);
		dateEditText = (EditText) findViewById(R.id.editTextDate);
		spinnerDeclarationTypes = (Spinner) findViewById(R.id.spinnerDeclarationType);
		spinnerDeclarationTypes
				.setOnItemSelectedListener(new spinnerListener());
		spinnerDeclarationTypesPosition = 0;
		currencyEditText = (EditText) findViewById(R.id.editCurrency);
		currencyEditText
				.setFilters(new InputFilter[] { new CurrencyFormatInputFilter() });
		attachmentListView = (LinearLayout) findViewById(R.id.attachmentList);
		attachmentListView.removeAllViews();
		commentTextView = (MultiAutoCompleteTextView) findViewById(R.id.commentTextView);

		// SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(),
		// attachmentItem, R.layout.attachment_view_list,
		// new String[] {"img", "title", "description"}, new int[]
		// {R.id.attachmentImage, R.id.attachmentTitle,
		// R.id.attachmentDescription});
		// attachmentListView.setAdapter(mSchedule);

		findViewById(R.id.buttonAdd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (validation == false) {
					// Check Date
					final String date = dateEditText.getText().toString();
					if (!isValidDate(date)) {
						String message = getErrorMsg();
						dateEditText.setError(spanString(message));
					}

					// Check Declaration Type & SubType
					isValidType();

					// Check Currency
					final String currency = currencyEditText.getText()
							.toString();
					if (!isValidCurrency(currency)) {
						String message = getErrorMsg();
						currencyEditText.setError(spanString(message));
					}

					// Check Attachments
					isValidAttachment();

					// Check Comment
					final String comment = commentTextView.getText().toString();
					if (!isValidComment(comment)) {
						String message = getErrorMsg();
						commentTextView.setError(spanString(message));
					}

					// General Error Message
					Toast.makeText(getApplicationContext(),
							"Declaratie regel bevat fouten", Toast.LENGTH_LONG)
							.show();
				} else {
					final String currency = currencyEditText.getText()
							.toString();
					if (isValidCurrency(currency)) {
						bundleDeclaration();
					}
				}
			}
		});

		findViewById(R.id.buttonCancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						onBackPressed();
					}
				});

		spinnerDeclarationSubTypes = (Spinner) findViewById(R.id.spinnerDeclarationSubType);
		spinnerDeclarationSubTypesPosition = 0;

		// photo = (ImageView) findViewById(R.id.mImageView);

		if (savedInstanceState == null) {
			date = Calendar.getInstance();

			setDeclarationTypes();
			setDeclarationSubTypes();
		}
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

	// TODO Make Photo / Select from library and post + display by Murat Aydin
	public void onAddImageButtonClick(View view) {
		selectImage();
	}

	private void selectImage() {
		final CharSequence[] options = { "Foto via Camera",
				"Foto via bibliotheek", "Annuleer" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				DeclareLineActivity.this);
		builder.setTitle("Maak / Kies foto:");
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if (options[item].equals("Foto via Camera")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Foto via bibliotheek")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);
				} else if (options[item].equals("Annuleer")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

				// test voor het ophalen om het te zien
				// photo.setImageBitmap(imageBitmap);

				map = new HashMap<String, String>();
				map.put("title", "Excel");
				map.put("description", "Tableur");
				map.put("img", String.valueOf(new BitmapDrawable(getResources(), imageBitmap)));
				attachmentItem.add(map);

				// ArrayList base64 string
				attachmentsData.add(BitmapToBase64String(imageBitmap));

			} else if (requestCode == 2) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap imageBitmap = (BitmapFactory.decodeFile(picturePath));
				// photo.setImageBitmap(imageBitmap);

				map = new HashMap<String, String>();
				map.put("title", "Excel");
				map.put("description", "test");
				map.put("img", String.valueOf(new BitmapDrawable(getResources(), imageBitmap)));
				attachmentItem.add(map);

				// ArrayList base64 string
				attachmentsData.add(BitmapToBase64String(imageBitmap));
			}

			SimpleAdapter sa = new SimpleAdapter(this.getBaseContext(),
					attachmentItem, R.layout.attachment_view_list,
					new String[] { "img", "title", "description" }, new int[] {
							R.id.attachmentImage, R.id.attachmentTitle,
							R.id.attachmentDescription });
			final int adapterCount = sa.getCount();
			// for (int i = 0; i < adapterCount; i++) {
			View item = sa.getView(adapterCount - 1, null, null);
			attachmentListView.addView(item);
			// }
		}
	}

	private String BitmapToBase64String(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		JSONObject object = new JSONObject();
		try {
			object.put("name", bitmap.toString());
			object.put("file",
					String.format("data:%s;base64,%s", "image/jpeg", temp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object.toString();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonAdd:
			// onAddButtonClick();
			break;
		case R.id.buttonCancel:
			super.onBackPressed();
			break;
		}
	}

	public void onBackPressed() {
		super.onBackPressed();
	}

	private class GetDeclerationTypesTask extends AsyncTask<Void, Void, String> {
		private Activity activity;

		public GetDeclerationTypesTask(Activity activity) {
			this.activity = activity;
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
		int textColor = Color.BLACK;
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
			validation = false;
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
			validation = false;
			return false;
		}

		Date datNow = new Date();
		if (dateOfDeclarationLine.after(datNow)) {
			String error = "Datum ligt niet in het verleden!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			validation = false;
			return false;
		}

		setErrorMsg(null);
		dateEditText.setError(null);
		validation = true;
		return true;
	}

	private boolean isValidType() {

		if (declarationTypesList.get(spinnerDeclarationTypesPosition)
				.toString().matches("")) {
			Toast.makeText(this, "Geen declaratie type geselecteerd!",
					Toast.LENGTH_LONG).show();
			validation = false;
			return false;
		}

		if (((DeclarationSubTypes) spinnerDeclarationSubTypes.getSelectedItem())
				.getId().toString().matches("")) {
			Toast.makeText(this, "Geen declaratie subtype geselecteerd!",
					Toast.LENGTH_LONG).show();
			validation = false;
			return false;
		}
		validation = true;
		return true;
	}

	private boolean isValidCurrency(String inputTotal) {
		if (inputTotal.matches("")) {
			String error = "Bedrag verplicht!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			validation = false;
			return false;
		}
		if (!inputTotal.matches("(0|[1-9]+[0-9]*)?(\\,[0-9]{0,2})?")) {
			String error = "Geen geldig bedrag!";
			String strDateErrorFormat = getResources().getString(
					R.string.label_date_hint);
			setErrorMsg(String.format(strDateErrorFormat, error));
			validation = false;
			return false;
		}

		double maxCost = ((DeclarationSubTypes) spinnerDeclarationSubTypes.getSelectedItem()).getMaxCost();
		if (maxCost != 0) {
			if (Double.parseDouble(inputTotal) > maxCost) {
				String error = "Bedrag is hoger dan maximaal in te dienen";
				String strDateErrorFormat = getResources().getString(
						R.string.label_date_hint);
				setErrorMsg(String.format(strDateErrorFormat, error));
				validation = false;
				return false;
			}
		}
		setErrorMsg(null);
		dateEditText.setError(null);
		validation = true;
		return true;
	}

	private boolean isValidAttachment() {
		if (attachmentItem.size() <= 0) {
			Toast.makeText(this, "Minimaal ��n bijlage toevoegen!",
					Toast.LENGTH_LONG).show();
			validation = false;
			validation = false;
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
			validation = false;
			return false;
		}
		setErrorMsg(null);
		commentTextView.setError(null);
		validation = true;
		return true;
	}
}
