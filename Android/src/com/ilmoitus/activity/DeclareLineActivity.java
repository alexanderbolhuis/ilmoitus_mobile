package com.ilmoitus.activity;

import com.example.ilmoitus.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class DeclareLineActivity extends Activity implements DatePickerFragment.OnDateSelectedListener {

	int dateDay, dateMonth, dateYear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);
		
		dateYear = 0;
		dateMonth = 0;
		dateDay = 0;
	}
	
	
	public void onCancelButtonClick(View view) {
	}
	public void onAddButtonClick(View view) {
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		
		Bundle args = new Bundle();
	    args.putInt("year", dateYear);
	    args.putInt("month", dateMonth);
	    args.putInt("day", dateDay);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSelected(int year, int month, int day) {
		dateDay = day;
		dateMonth = month;
		dateYear = year;
		
		EditText EditDate = (EditText) findViewById(R.id.editTextDate);
		EditDate.setText(day + "-" + month + "-" + year);
		
	}
	

}
