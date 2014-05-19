package com.ilmoitus.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.ilmoitus.R;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.DeclarationLine;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeclarationLineAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<DeclarationLine> declarationLines;
	private DecimalFormat currencyFormat;

	public DeclarationLineAdapter(Activity activity,
			ArrayList<DeclarationLine> declarationLines) {
		this.activity = activity;
		this.declarationLines = declarationLines;
		this.inflator = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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
		TextView top = (TextView) rowView.findViewById(R.id.text1);
		top.setText(line.getDatum() + " - " + line.getDeclaratieSoort() + " - "
				+ "\u20AC" + currencyFormat.format(line.getBedrag()));

		return rowView;
	}

}
