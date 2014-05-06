package com.ilmoitus.activity;

import com.example.ilmoitus.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class DeclareLineActivity extends Activity {

	DialogFragment newFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);
		newFragment = new DatePickerFragment();
	}
	
	public void onCancelButtonClick(View view) {
	}
	public void onAddButtonClick(View view) {
	}
	
	public void showDatePickerDialog(View v) {
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	public void setDate(String date) {
		// TODO Auto-generated method stub
		EditText EditDate = (EditText) findViewById(R.id.editTextDate);
		EditDate.setText(date);

		
	}
	

}
