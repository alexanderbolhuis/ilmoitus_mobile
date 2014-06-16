
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
import com.ilmoitus.adapter.AttachmentOverviewDetialsAdapter;
import com.ilmoitus.adapter.DeclarationLineAdapter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.Attachment;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.DeclarationSubTypes;
import com.ilmoitus.model.DeclarationTypes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class DeclarationDetailsActivity extends Activity{

	private long decId;
	private ArrayList<DeclarationLine> decLines;
	private LinearLayout attachmentList;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declaration_details);
		decLines = new ArrayList<DeclarationLine>();
		decId = getIntent().getExtras().getLong("decId");
		ListView list = (ListView) findViewById(R.id.list);
		new GetSpecificDeclaration(this, list).execute();
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DeclarationLine line = decLines.get(position);
				DeclarationSubTypes t = line.getDeclaratieSubSoort();
				Intent intent = new Intent(getApplicationContext(), DeclarationLineDetailsActivity.class);
				Bundle b = new Bundle();
				b.putString("datum", line.getDatum());
				b.putString("declaratieSoort", line.getDeclaratieSoort().getName());
				b.putString("declaratieSubSoort", line.getDeclaratieSubSoort().getName());
				b.putDouble("cost", line.getBedrag());
				b.putStringArrayList("attachments", line.getAttachmentArray());
				intent.putExtra("data", b);
				startActivity(intent);
			}
		});
	}
	
	private class GetSpecificDeclaration extends AsyncTask<Void, Void, String>{
		
		private Activity activity;
		private ListView context;
		
		public GetSpecificDeclaration(Activity activity, ListView context){
			this.activity = activity;
			this.context = context;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) + "/declaration/" + decId);
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
		
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject declarationDetails = new JSONObject(result);
				
				JSONArray attachments = new JSONArray(declarationDetails.getString("attachments"));
						
				JSONObject supervisor = (JSONObject) declarationDetails.get("last_assigned_to");				
				JSONArray lines = (JSONArray) declarationDetails.get("lines");
				for(int i = 0; i < lines.length(); i++){
					JSONObject line = lines.getJSONObject(i);		
					JSONObject declarationType = (JSONObject) line.get("declaration_type");
					JSONObject declarationSubType = (JSONObject) line.get("declaration_sub_type");
					DeclarationLine decLine = new DeclarationLine(line.getLong("id"), line.getString("receipt_date"),
							new DeclarationTypes(declarationType.getString("name"), declarationType.getLong("id")), 
							new DeclarationSubTypes(declarationSubType.getString("name"), declarationSubType.getLong("id")), line.getInt("cost"));
					decLines.add(decLine);
				}
				DeclarationLineAdapter decLineAdapter = new DeclarationLineAdapter(activity, decLines);
				context.setAdapter(decLineAdapter);
				TextView totalPrice = (TextView) findViewById(R.id.totalPrice);
				totalPrice.setText("\u20AC" + declarationDetails.getString("items_total_price"));
				
				TextView supervisorTextView = (TextView) findViewById(R.id.leidinggevende);
				supervisorTextView.setText(String.format("%s %s", supervisor.getString("first_name"), supervisor.getString("last_name")));
				
				MultiAutoCompleteTextView comment = (MultiAutoCompleteTextView) findViewById(R.id.comment);
				comment.setText(declarationDetails.getString("comment"));
				comment.setEnabled(false);
				
				ArrayList<Attachment> temp = new ArrayList<Attachment>();
				for(int i = 0; i < attachments.length(); i++)
				{
					JSONObject object = attachments.getJSONObject(i);
					
					String base64String = object.getString("data");
		            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
		            Bitmap bitmapObj = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
					//attachmentDetail.add(new Attachment(bitmapObj, "image "));
					
					temp.add(new Attachment(bitmapObj, object.getString("name")));
				}
				
				attachmentList = (LinearLayout) findViewById(R.id.attachmentDetailsList);	
				
				AttachmentOverviewDetialsAdapter adapter = new AttachmentOverviewDetialsAdapter(
						activity, temp);		
				final int adapterCount = adapter.getCount();
				View item = adapter.getView(adapterCount - 1, null, null);
				attachmentList.addView(item);	
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
