package com.ilmoitus.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.ilmoitus.R;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.ItemRow;
import com.ilmoitus.model.OpenDeclaration;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeclarationOverviewAdapter extends ArrayAdapter<BaseDeclaration> {

	private LayoutInflater inflator;
	private Activity activity;
	private List<BaseDeclaration> declarations;
	// private List<ItemRow> data;
	private int layoutResID;
	private DecimalFormat currencyFormat;

	public DeclarationOverviewAdapter(Activity activity, int layoutResourceId,
			List<BaseDeclaration> declarations) {
		super(activity, layoutResourceId, declarations);

		this.activity = activity;
		this.declarations = declarations;
		this.layoutResID = layoutResourceId;
		// this.inflator = (LayoutInflater) activity
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		currencyFormat = new DecimalFormat("0.00");
	}

	public int getCount() {
		return declarations.size();
	}

	public BaseDeclaration getItem(int position) {
		return declarations.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		OpenDeclaration openDeclaration = null;
		ClosedDeclaration closedDeclaration = null;
		NewsHolder holder = null;
		View rowView = convertView;
		holder = null;

		if (rowView == null) {
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			rowView = inflater.inflate(layoutResID, parent, false);

			holder = new NewsHolder();

			holder.itemName = (TextView) rowView
					.findViewById(R.id.example_itemname);
			holder.itemNameTwo = (TextView) rowView
					.findViewById(R.id.example_itemname_two);
			holder.itemNameThree = (TextView) rowView
					.findViewById(R.id.example_itemname_three);
			holder.icon = (ImageView) rowView.findViewById(R.id.example_image);
			holder.button1 = (Button) rowView.findViewById(R.id.bekijk);
			holder.button2 = (Button) rowView.findViewById(R.id.bewerk);
//			holder.button3 = (Button) rowView.findViewById(R.id.swipe_button3);
			rowView.setTag(holder);
		} else {
			holder = (NewsHolder) rowView.getTag();
		}

		if (declarations.get(position).getClass() == OpenDeclaration.class) {
			openDeclaration = (OpenDeclaration) declarations.get(position);
		}
		if (declarations.get(position).getClass() == ClosedDeclaration.class) {
			closedDeclaration = (ClosedDeclaration) declarations.get(position);
		}

		// BaseDeclaration bd = declarations.get(position);
		// holder.itemName.setText(openDeclaration.getCreatedAt());

		// rowView = inflator.inflate(R.layout.row, null);
		// TextView top = (TextView) rowView.findViewById(R.id.text1);
		// TextView bottom = (TextView) rowView.findViewById(R.id.text2);
		if (openDeclaration != null) {
			// top.setText("Declaratie op "
			// + openDeclaration.getCreatedAt().substring(0, 10));
			// bottom.setText("open "
			// + "\u20AC"
			// + currencyFormat.format(openDeclaration
			// .getItemsTotalPrice()));
			holder.itemName.setText("Declaratie op "
					+ openDeclaration.getCreatedAt().substring(0, 10));
			holder.itemNameTwo.setText("open");
			holder.itemNameThree.setText("\u20AC"
					+ currencyFormat.format(openDeclaration
					.getItemsTotalPrice()));
			holder.icon.setBackgroundResource(R.drawable.open_declaration);
			
			//buttons zichtbaar
			holder.button1.setEnabled(true);
			holder.button2.setEnabled(true);
		}
		if (closedDeclaration != null) {
			// top.setText("Declaratie op "
			// + closedDeclaration.getCreatedAt().substring(0, 10));
			// bottom.setText("in behandeling "
			// + "\u20AC"
			// + currencyFormat.format(closedDeclaration
			// .getItemsTotalPrice()));
			holder.itemName.setText("Declaratie op "
					+ closedDeclaration.getCreatedAt().substring(0, 10));
			holder.itemNameTwo.setText("in behandeling");
			holder.itemNameThree.setText("\u20AC"
					+ currencyFormat.format(closedDeclaration
					.getItemsTotalPrice()));
			holder.icon.setBackgroundResource(R.drawable.closed_declaration);
			
			//buttons zichtbaar
			holder.button1.setEnabled(false);
			holder.button2.setEnabled(true);
		}

		holder.button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Button 1 Clicked", Toast.LENGTH_SHORT)
						.show();
			}
		});

		holder.button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Button 2 Clicked", Toast.LENGTH_SHORT)
						.show();
			}
		});

//		holder.button3.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Toast.makeText(activity, "Button 3 Clicked", Toast.LENGTH_SHORT)
//						.show();
//			}
//		});

		return rowView;
	}

	static class NewsHolder {

		TextView itemName;
		TextView itemNameTwo;
		TextView itemNameThree;
		ImageView icon;
		Button button1;
		Button button2;
		Button button3;
	}

}
