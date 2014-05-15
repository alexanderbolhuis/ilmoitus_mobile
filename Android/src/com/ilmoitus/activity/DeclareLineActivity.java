package com.ilmoitus.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import com.ilmoitus.model.DeclarationTypes;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	private EditText currency;
	private EditText dateField;

	int spinnerDeclarationTypesPosition, spinnerDeclarationSubTypesPosition;

	ArrayAdapter<DeclarationTypes> spinnerDeclarationTypesListAdapter;
	ArrayAdapter<String> spinnerDeclarationSubTypesListAdapter;
	ArrayList<DeclarationTypes> declarationTypesList;
	ArrayList<String> declarationSubTypesList;

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
		declarationSubTypesList = new ArrayList<String>();

		spinnerDeclarationSubTypesListAdapter = new ArrayAdapter<String>(this,
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
		b.putString("bedrag", currency.getText().toString());

		Log.d("myapp",
				"save: "
						+ declarationTypesList.get(
								spinnerDeclarationTypesPosition).toString());
		b.putString("declaratieSoort",
				declarationTypesList.get(spinnerDeclarationTypesPosition)
						.toString());
		b.putString("declaratieSubSoort", spinnerDeclarationSubTypes
				.getSelectedItem().toString());

		Intent i = new Intent(this, DeclareActivity.class);
		i.putExtras(b);
		setResult(RESULT_OK, i);
		finish();
	}

	// TODO/UNDER CONSTRUCTION Foto maken
	public void onAddImageButtonClick(View view) {
		selectImage();
	}

	private void selectImage() {
		final CharSequence[] options = { "Maak Foto",
				"Kies uit fotobibliotheek", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				DeclareLineActivity.this);
		builder.setTitle("Voeg foto toe!");
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if (options[item].equals("Maak Foto")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Kies uit fotobibliotheek")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);
				} else if (options[item].equals("Cancel")) {
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
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
							bitmapOptions);
					// viewImage.setImageBitmap(bitmap);
					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;

					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");

					try {

						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.w("path of image from gallery......******************.........",
						picturePath + "");
				// viewImage.setImageBitmap(thumbnail);
			}
		}
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

		public GetDeclerationSubTypesTask() {
			Log.d("myapp", "start: ");

		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;

			String declarationType = declarationTypesList.get(
					spinnerDeclarationTypesPosition).getId();

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(
					R.string.base_url)
					+ "/declarationsubtypes/" + declarationType);
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
				JSONArray declarationSubTypes = new JSONArray(result);

				declarationSubTypesList.clear();

				for (int i = 0; i < declarationSubTypes.length(); i++) {
					JSONObject object = declarationSubTypes.getJSONObject(i);

					String name = object.getString("name");

					declarationSubTypesList.add(name);
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
			Log.d("myapp", "niks: ");

		}
	}
}
