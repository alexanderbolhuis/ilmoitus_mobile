package com.ilmoitus.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.ilmoitus.R;
import com.ilmoitus.fragment.DatePickerFragment;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;

public class DeclareLineActivity extends Activity implements DatePickerFragment.OnDateSelectedListener,
	OnClickListener{
	
	private Spinner spinnerDeclarationTypes, spinnerDeclarationSubTypes;
	private Calendar date;
	private TextView title;
	private EditText currency;
	private EditText dateField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_line);
		title = (TextView) findViewById(R.id.person_title);
		Button addButton = (Button) findViewById(R.id.buttonAdd);
		addButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.id.buttonCancel);
		cancelButton.setOnClickListener(this);
		dateField = (EditText) findViewById(R.id.editTextDate);
		currency = (EditText) findViewById(R.id.editCurrency);
		currency.setFilters(new InputFilter[] { new CurrencyFormatInputFilter() });
		spinnerDeclarationTypes = (Spinner) findViewById(R.id.spinnerDeclarationType);
		spinnerDeclarationSubTypes = (Spinner)findViewById(R.id.spinnerDeclarationSubType);
		if (savedInstanceState == null) {
			date = Calendar.getInstance();		
			String[] values = new String[] {"Android List View"};			
			setDeclarationTypes(values);
			setDeclarationSubTypes(values);
		}
	}

	public void setDeclarationTypes(String[] values) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, values);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationTypes.setAdapter(adapter);
	}
	
	public void setDeclarationSubTypes(String[] values) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, values);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationSubTypes.setAdapter(adapter);
	}


	public void onAddButtonClick() {
		Bundle b = new Bundle();
		b.putString("date", dateField.getText().toString());
		b.putString("bedrag", currency.getText().toString());
		b.putString("declaratieSoort", spinnerDeclarationTypes.getSelectedItem().toString());
		b.putString("declaratieSubSoort", spinnerDeclarationSubTypes.getSelectedItem().toString());
		Intent i = new Intent(this, DeclareActivity.class);
		i.putExtras(b);
		setResult(RESULT_OK, i);
		finish();
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
		EditText editDate = (EditText) this.findViewById(
				R.id.editTextDate);
		editDate.setText(formattedDate);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonAdd:
			onAddButtonClick();
			break;
		case R.id.buttonCancel:
			super.onBackPressed();
			break;
		}
		
	}
}
