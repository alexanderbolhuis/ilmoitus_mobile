package com.ilmoitus.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
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
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.MediaStore;
import android.text.InputFilter;
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

public class DeclareLineActivity extends Activity implements
		DatePickerFragment.OnDateSelectedListener, OnClickListener {

	private Spinner spinnerDeclarationTypes, spinnerDeclarationSubTypes;
	private Calendar date;
	private TextView title;
	private EditText currency, dateField;
	private ImageView photo;
	private List<String> base64Array = new ArrayList<String>();

	int spinnerDeclarationTypesPosition, spinnerDeclarationSubTypesPosition;

	ArrayAdapter<DeclarationTypes> spinnerDeclarationTypesListAdapter;
	ArrayAdapter<DeclarationSubTypes> spinnerDeclarationSubTypesListAdapter;
	ArrayList<DeclarationTypes> declarationTypesList;
	ArrayList<DeclarationSubTypes> declarationSubTypesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);
		title = (TextView) findViewById(R.id.person_title);
		Button addButton = (Button) findViewById(R.id.buttonAdd);
		addButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.id.buttonCancel);
		cancelButton.setOnClickListener(this);
		dateField = (EditText) findViewById(R.id.editTextDate);
		currency = (EditText) findViewById(R.id.editCurrency);
		currency.setFilters(new InputFilter[] { new CurrencyFormatInputFilter() });

		spinnerDeclarationTypes = (Spinner) findViewById(R.id.spinnerDeclarationType);
		spinnerDeclarationTypes
				.setOnItemSelectedListener(new spinnerListener());
		spinnerDeclarationTypesPosition = 0;

		spinnerDeclarationSubTypes = (Spinner) findViewById(R.id.spinnerDeclarationSubType);
		spinnerDeclarationSubTypesPosition = 0;
		
		photo = (ImageView) findViewById(R.id.mImageView);

		if (savedInstanceState == null) {
			date = Calendar.getInstance();

			setDeclarationTypes();
			setDeclarationSubTypes();
		}
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

		spinnerDeclarationSubTypesListAdapter = new ArrayAdapter<DeclarationSubTypes>(this,
				android.R.layout.simple_spinner_item, declarationSubTypesList);
		spinnerDeclarationSubTypesListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationSubTypesListAdapter.setNotifyOnChange(true);
		spinnerDeclarationSubTypes
				.setAdapter(spinnerDeclarationSubTypesListAdapter);
	}

	public void onAddButtonClick() {
		Bundle b = new Bundle();
		b.putString("date", dateField.getText().toString());
		b.putInt("bedrag", Integer.parseInt(currency.getText().toString()));
		b.putString("declaratieSoort",declarationTypesList.get(spinnerDeclarationTypesPosition).toString());
		b.putLong("declaratieSubSoort", ((DeclarationSubTypes) spinnerDeclarationSubTypes.getSelectedItem()).getId());
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
				photo.setImageBitmap(imageBitmap);		
				
				//ArrayList base64 string		
				base64Array.add(BitmapToBase64String(null));
				
				
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
				photo.setImageBitmap(imageBitmap);
				
				//ArrayList base64 string
				base64Array.add(BitmapToBase64String(null));
			}
		}
	}
	
	private String BitmapToBase64String(Bitmap bitmap)
	{
		ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return "data:image/jpeg;base64," + temp;
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
		EditText editDate = (EditText) this.findViewById(R.id.editTextDate);
		editDate.setText(formattedDate);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonAdd:
			onAddButtonClick();
			break;
		case R.id.buttonCancel:
			super.onBackPressed();
			break;
		}
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
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			try {
				JSONArray declarationTypes = new JSONArray(result);

				for (int i = 0; i < declarationTypes.length(); i++) {
					JSONObject object = declarationTypes.getJSONObject(i);

					String name = object.getString("name");
					String id = object.getString("id");

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

			String declarationType = declarationTypesList.get(spinnerDeclarationTypesPosition).getId();

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) + "/declarationsubtypes/" + declarationType);
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

			try {
				JSONArray declarationSubTypes = new JSONArray(result);
				declarationSubTypesList.clear();

				for (int i = 0; i < declarationSubTypes.length(); i++) {
					JSONObject object = declarationSubTypes.getJSONObject(i);
					String name = object.getString("name");
					Long id = object.getLong("id");
					declarationSubTypesList.add(new DeclarationSubTypes(name, id));
				}
				spinnerDeclarationSubTypes.setAdapter(spinnerDeclarationSubTypesListAdapter);

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
			Log.d("myapp", "niks: ");

		}
	}
}
