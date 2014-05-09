package com.ilmoitus.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ManagerOnItemSelectedListener implements OnItemClickListener {

	@SuppressWarnings("rawtypes")
	public void onItemSelected(AdapterView parent, View view, int pos, long id) {
		Toast.makeText(parent.getContext(), "Selected Country : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("rawtypes")
	public void onNothingSelected(AdapterView parent) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
