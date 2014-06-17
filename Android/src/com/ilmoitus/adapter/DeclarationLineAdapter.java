package com.ilmoitus.adapter;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ilmoitus.R;
import com.ilmoitus.activity.DeclarationDetailsActivity;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeclarationLineAdapter extends BaseAdapter{

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<DeclarationLine> declarationLines;
	private DecimalFormat currencyFormat;

	public DeclarationLineAdapter(Activity activity,
			ArrayList<DeclarationLine> declarationLines) {
		this.activity = activity;
		this.declarationLines = declarationLines;
		this.inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		currencyFormat = new DecimalFormat("0.00");
	}

	public int getCount() {
		return declarationLines.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DeclarationLine line = null;

		line = (DeclarationLine) declarationLines.get(position);

		View rowView = inflator.inflate(R.layout.row_declaration_line, null);
		TextView title = (TextView) rowView.findViewById(R.id.item_title);
		TextView sub_title = (TextView) rowView.findViewById(R.id.item_subtitle);
		TextView total = (TextView) rowView.findViewById(R.id.item_total);
			
		title.setText(line.getDatum());
		sub_title.setText(line.getDeclaratieSoort().getName());
		total.setText("\u20AC" + currencyFormat.format(line.getBedrag()));
		
		RelativeLayout layout = (RelativeLayout) rowView.findViewById(R.id.layout);
		Button btnDelete = (Button) rowView.findViewById(R.id.item_delete);
		if(activity.getClass() == DeclarationDetailsActivity.class){
			layout.removeView(btnDelete);
		}
		else{
			btnDelete.setTag(position);
			btnDelete.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					String pos = v.getTag().toString();
					int position = Integer.parseInt(pos);
					declarationLines.remove(position);
					notifyDataSetChanged();
				}
			});
		}
		return rowView;
	}
}
