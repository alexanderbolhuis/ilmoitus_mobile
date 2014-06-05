package com.ilmoitus.adapter;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DeclarationOverviewAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<BaseDeclaration> declarations;
	private DecimalFormat currencyFormat;

	public DeclarationOverviewAdapter(Activity activity,
			ArrayList<BaseDeclaration> declarations) {
		this.activity = activity;
		this.declarations = declarations;
		this.inflator = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		currencyFormat = new DecimalFormat("0.00");
	}

	public int getCount() {
		return declarations.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		OpenDeclaration openDeclaration = null;
		ClosedDeclaration closedDeclaration = null;
		if (declarations.get(position).getClass() == OpenDeclaration.class) {
			openDeclaration = (OpenDeclaration) declarations.get(position);
		}
		if (declarations.get(position).getClass() == ClosedDeclaration.class) {
			closedDeclaration = (ClosedDeclaration) declarations.get(position);
		}
		View rowView = inflator.inflate(R.layout.row, null);
		TextView top = (TextView) rowView.findViewById(R.id.text1);
		TextView bottom = (TextView) rowView.findViewById(R.id.text2);
		if (openDeclaration != null) {
			top.setText("Declaratie op "
					+ openDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("open "
					+ "\u20AC"
					+ currencyFormat.format(openDeclaration
							.getItemsTotalPrice()));
			LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.layout);
			Button btnDelete = new Button(activity);
			btnDelete.setFocusable(false);
			btnDelete.setTag(position);
			btnDelete.setLayoutParams(new LayoutParams(200, LayoutParams.WRAP_CONTENT));
			btnDelete.setText("Annuleren");
			btnDelete.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					String pos = v.getTag().toString();
					int position = Integer.parseInt(pos);
					new RemoveDeclerationTask(position).execute();
				}
			});
			layout.addView(btnDelete);
		}
		if (closedDeclaration != null) {
			top.setText("Declaratie op "
					+ closedDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("in behandeling "
					+ "\u20AC"
					+ currencyFormat.format(closedDeclaration
							.getItemsTotalPrice()));
		}
		return rowView;
	}
	
	private class RemoveDeclerationTask extends AsyncTask<Void, Void, String> {

		private int position;

		public RemoveDeclerationTask(int position) {
			this.position = position;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpDelete httpDelete = new HttpDelete("http://10.0.3.2:8080/declaration/" + declarations.get(position).getId());
			httpDelete.setHeader("Authorization", LoggedInPerson.token);
			try {
				HttpResponse response = httpClient.execute(httpDelete);
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
			declarations.remove(position);
			notifyDataSetChanged();
		}
	}

}