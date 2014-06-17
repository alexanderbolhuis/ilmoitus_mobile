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
import com.ilmoitus.activity.DeclarationDetailsActivity;
import com.ilmoitus.activity.DeclareActivity;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.ApprovedDeclaration;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.DeclinedDeclaration;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
		ApprovedDeclaration approvedDeclaration = null;
		DeclinedDeclaration declinedDeclaration = null;
		if (declarations.get(position).getClass() == OpenDeclaration.class) {
			openDeclaration = (OpenDeclaration) declarations.get(position);
		}
		if (declarations.get(position).getClass() == ClosedDeclaration.class) {
			closedDeclaration = (ClosedDeclaration) declarations.get(position);
		}
		if (declarations.get(position).getClass() == ApprovedDeclaration.class) {
			approvedDeclaration = (ApprovedDeclaration) declarations.get(position);
		}
		if (declarations.get(position).getClass() == DeclinedDeclaration.class) {
			declinedDeclaration = (DeclinedDeclaration) declarations.get(position);
		}
		View rowView = inflator.inflate(R.layout.row_main, null);
		ImageView icon = (ImageView) rowView.findViewById(R.id.item_statusicon);
		TextView top = (TextView) rowView.findViewById(R.id.item_title);
		TextView bottom = (TextView) rowView.findViewById(R.id.item_subtitle);
		TextView total = (TextView) rowView.findViewById(R.id.item_total);
		Button delete = (Button) rowView.findViewById(R.id.item_delete);
		if (openDeclaration != null) {
			icon.setBackgroundResource(R.drawable.icon_open_declaratie);
			top.setText("Declaratie op "
					+ openDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("open");
			total.setText("\u20AC" + currencyFormat.format(openDeclaration.getItemsTotalPrice()));
			delete.setFocusable(false);
			delete.setTag(position);
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String pos = v.getTag().toString();
					int position = Integer.parseInt(pos);
					new RemoveDeclerationTask(position).execute();
				}
			});
			delete.setVisibility(View.VISIBLE);
		}
		if (closedDeclaration != null) {
			icon.setBackgroundResource(R.drawable.icon_gesloten_declaratie);
			top.setText("Declaratie op "
					+ closedDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("in behandeling");
			total.setText("\u20AC" + currencyFormat.format(closedDeclaration.getItemsTotalPrice()));
			delete.setVisibility(View.INVISIBLE);
		}
		if (approvedDeclaration != null){
			icon.setBackgroundResource(R.drawable.icon_gesloten_declaratie);
			top.setText("Declaratie op "
					+ approvedDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("Goedgekeurd");
			total.setText("\u20AC" + currencyFormat.format(approvedDeclaration.getItemsTotalPrice()));
			delete.setVisibility(View.INVISIBLE);
		}
		if (declinedDeclaration != null){
			icon.setBackgroundResource(R.drawable.icon_gesloten_declaratie);
			top.setText("Declaratie op "
					+ declinedDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("Afgekeurd");
			total.setText("\u20AC" + currencyFormat.format(declinedDeclaration.getItemsTotalPrice()));
			delete.setVisibility(View.INVISIBLE);
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