package com.ilmoitus.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.apache.http.client.ClientProtocolException;
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
import com.ilmoitus.croscutting.ListViewUtility;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.Attachment;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.DeclarationSubTypes;
import com.ilmoitus.model.DeclarationTypes;
import com.ilmoitus.model.Supervisor;
import com.ilmoitus.adapter.AttachmentOverviewAdapter;
import com.ilmoitus.adapter.AttachmentOverviewDetialsAdapter;
import com.ilmoitus.adapter.DeclarationLineAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.DateTimeKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
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

public class DeclareActivity extends Activity {
	private ArrayList<DeclarationLine> declarationLines;
	private ArrayList<Supervisor> supervisors;
	private Button declareButton, mainButton, addLineButton, addDeclaration;
	private ListView decLinesView;
	private Spinner spinnerSupervisors;
	private ArrayList<JSONObject> attachmentsJSON;
	private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	private MultiAutoCompleteTextView commentTextView;
	private Boolean validation = true;
	private String errorMsg;
	private ListView attachmentListView;
	private ArrayList<String> attachmentsData = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare);
		new GetSupervisors(this).execute();
		declarationLines = new ArrayList<DeclarationLine>();
		new DecimalFormat("0.00");
		attachmentListView = (ListView) findViewById(R.id.attachmentDetailsList);
		decLinesView = (ListView) findViewById(R.id.list);
		decLinesView.setVerticalScrollBarEnabled(false);
		spinnerSupervisors = (Spinner) findViewById(R.id.spinnerSupervisors);
		commentTextView = (MultiAutoCompleteTextView) findViewById(R.id.commentTextView);
		declareButton = (Button) findViewById(R.id.buttonDeclare);
		declareButton.setEnabled(false);
		mainButton = (Button) findViewById(R.id.buttonOvervieuw);
		addLineButton = (Button) findViewById(R.id.onAddLineButton);
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
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
					if (attachmentsJSON == null) {
						attachmentsJSON = new ArrayList<JSONObject>();
					}
					Bundle b = data.getExtras();
					DeclarationLine line = new DeclarationLine(b.getLong("id"),b.getString("date"), new DeclarationTypes(b.getString("declaratieSoort"),
									b.getLong("declaratieSoortId")), new DeclarationSubTypes(b.getString("declaratieSubSoort"), b.getLong("declaratieSubSoortId")),
									b.getDouble("bedrag"));
					declarationLines.add(line);
					b.getDouble("bedrag");
					DeclarationLineAdapter ad = new DeclarationLineAdapter(this, declarationLines);
					decLinesView.setAdapter(ad);
					ListViewUtility.setListViewHeightBasedOnChildren(decLinesView);
				}
				if (requestCode == 2) {
					Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
					Attachment tempAttachment = new Attachment(imageBitmap, "image");
					attachments.add(tempAttachment);
					try {
						attachmentsJSON.add(new JSONObject(BitmapToBase64String(imageBitmap, tempAttachment.getAttachmentName())));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (requestCode == 3) {
					Uri selectedImage = data.getData();
					String[] filePath = { MediaStore.Images.Media.DATA };
					Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
					c.moveToFirst();
					int columnIndex = c.getColumnIndex(filePath[0]);
					String picturePath = c.getString(columnIndex);
					c.close();
					Bitmap imageBitmap = (BitmapFactory.decodeFile(picturePath));
					Attachment tempAttachment = new Attachment(imageBitmap, "image");
					attachments.add(tempAttachment);
					try {
						attachmentsJSON.add(new JSONObject(BitmapToBase64String(imageBitmap, tempAttachment.getAttachmentName())));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				AttachmentOverviewDetialsAdapter adapter = new AttachmentOverviewDetialsAdapter(this, attachments);
				attachmentListView.setAdapter(adapter);
				ListViewUtility.setListViewHeightBasedOnChildren(attachmentListView);
			}
	}

	public void addDeclaration(View v){
		if (!isValidDeclaration()) {
			validation = false;
		}
		if (!isValidComment(commentTextView.getText()
				.toString())) {
			validation = false;
			String message = getErrorMsg();
			commentTextView.setError(spanString(message));
		}
		if(!isValidAttachment()){
			validation = false;
		}
		if (validation == false) {
			Toast.makeText(getApplicationContext(),
					"Declaratie bevat fouten",
					Toast.LENGTH_LONG).show();
		} else {
			new AddDeclaration().execute();
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
			decl.put("attachments", new JSONArray(attachmentsJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return decl;
	}

	private boolean isValidAttachment() {
		if (attachments.size() <= 0) {
			Toast.makeText(this, "Minimaal ��n bijlage toevoegen!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
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

	public void onAddLineButtonClick(View v) {
		Intent intent = new Intent(this, DeclareLineActivity.class);
		startActivityForResult(intent, 1);
	}

	public void onAddImageButtonClick(View view) {
		selectImage();
	}

	private void selectImage() {
		final CharSequence[] options = { "Foto via Camera","Foto via bibliotheek", "Annuleer" };
		AlertDialog.Builder builder = new AlertDialog.Builder(DeclareActivity.this);
		builder.setTitle("Maak / Kies foto:");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if (options[item].equals("Foto via Camera")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 2);
				} else if (options[item].equals("Foto via bibliotheek")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 3);
				} else if (options[item].equals("Annuleer")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private String BitmapToBase64String(Bitmap bitmap, String name) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		JSONObject object = new JSONObject();
		try {
			object.put("name", name);
			object.put("file", String.format("data:%s;base64,%s", "image/jpeg", temp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
	private class AddDeclaration extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(getResources().getString(R.string.base_url) + "/declaration");
			httpPost.setHeader("Authorization", LoggedInPerson.token);
			try {
				JSONObject decl = createDeclaration();
				JSONObject totalDeclaration = new JSONObject();
				totalDeclaration.put("declaration", decl);
				httpPost.setEntity(new StringEntity(totalDeclaration.toString()));
				HttpResponse response = httpClient.execute(httpPost);
				InputStream inputStream = response.getEntity().getContent();
				if (inputStream != null) {
					result = InputStreamConverter.convertInputStreamToString(inputStream);
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

	// Form validation
	public SpannableStringBuilder spanString(String spanstring) {
		int textColor = Color.WHITE;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(textColor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(
				spanstring);
		ssbuilder.setSpan(fgcspan, 0, spanstring.length(), 0);
		return ssbuilder;
	}

	private boolean isValidComment(String inputComment) {
		if (inputComment.matches("")) {
			String error = "Opmerking verplicht!";
			String strDateErrorFormat = getResources().getString(R.string.label_date_hint);
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

}
