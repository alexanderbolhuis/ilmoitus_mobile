package com.ilmoitus.adapter;

import java.util.ArrayList;

import com.example.ilmoitus.R;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeclarationOverviewAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<BaseDeclaration> declarations;
	
	public DeclarationOverviewAdapter(Activity activity, ArrayList<BaseDeclaration> declarations)
	{
		this.activity = activity;
		this.declarations = declarations;
		this.inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if(declarations.get(position).getClass() == OpenDeclaration.class){
			openDeclaration = (OpenDeclaration) declarations.get(position);
		}
		if(declarations.get(position).getClass() == ClosedDeclaration.class){
			closedDeclaration = (ClosedDeclaration) declarations.get(position);
		}
		View rowView = inflator.inflate(R.layout.row, null);
		TextView top = (TextView) rowView.findViewById(R.id.text1);
		TextView bottom = (TextView) rowView.findViewById(R.id.text2);
		if(openDeclaration != null){
			top.setText("Declaratie op " + openDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("open " + openDeclaration.getItemsTotalPrice());
		}
		if(closedDeclaration != null){
			top.setText("Declaratie op " + closedDeclaration.getCreatedAt().substring(0, 10));
			bottom.setText("in behandeling " + closedDeclaration.getItemsTotalPrice());
		}
		return rowView;
	}

}
