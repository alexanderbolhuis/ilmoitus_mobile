package com.ilmoitus.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.ilmoitus.R;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.DialogFragment;

public class DeclareLineActivity extends Activity implements
		DatePickerFragment.OnDateSelectedListener,
		DeclareLineFragment.DeclareLineListener {

	Calendar date;
	DeclareLineFragment dlFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);

		EditText editCurrency = (EditText) findViewById(R.id.editCurrency);
		editCurrency
				.setFilters(new InputFilter[] { new CurrencyFormatInputFilter() });

		if (savedInstanceState == null) {

			dlFragment = (DeclareLineFragment) getFragmentManager()
					.findFragmentById(R.id.declare_line_fragment);

			date = Calendar.getInstance();
			
			 String[] values = new String[] { "Android List View", 
                     "Adapter implementation",
                     "Simple List View In Android",
                     "Create List View Android", 
                     "Android Example", 
                     "List View Source Code", 
                     "List View Array Adapter", 
                     "Android Example List View" 
                    };
			
			dlFragment.setDeclarationTypes(values);
			dlFragment.setDeclarationSubTypes(values);
		}
	}

	// TODO Terug naar overzicht
	public void onCancelButtonClick(View view) {
		Toast.makeText(getBaseContext(), "ToDo: Terug naar overzicht",
				Toast.LENGTH_LONG).show();
	}

	// TODO Opslaan en Terug naar overzicht
	public void onAddButtonClick(View view) {

		String bedrag = dlFragment.getCurrency();

		Toast.makeText(getBaseContext(),
				"ToDo: Opslaan en Terug naar overzicht", Toast.LENGTH_LONG)
				.show();
	}

	// TODO Foto maken
	public void onAddImageButtonClick(View view) {
		Toast.makeText(getBaseContext(), "ToDo: Photo maken", Toast.LENGTH_LONG)
				.show();
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();

		Bundle args = new Bundle();
		args.putInt("year", date.get(Calendar.YEAR));
		args.putInt("month", date.get(Calendar.MONTH));
		args.putInt("day", date.get(Calendar.DAY_OF_MONTH));
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSelected(int year, int month, int day) {

		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.YEAR, year);

		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = df.format(date.getTime());

		dlFragment.setDate(formattedDate);
	}

	@Override
	public void onDeclareLineChanged(String value) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "ToDo: Update sub list", Toast.LENGTH_LONG)
		.show();
	}

}
