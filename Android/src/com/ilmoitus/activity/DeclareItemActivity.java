package com.ilmoitus.activity;

import com.example.ilmoitus.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class DeclareItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare_item);
		
		Spinner spinnerKind = (Spinner)findViewById(R.id.spinnerKind);
		String[] items = new String[]{"1", "2", "three"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		spinnerKind.setAdapter(adapter);
		
		Spinner spinnerSubKind = (Spinner)findViewById(R.id.spinnerSubKind);
		String[] items2 = new String[]{"4", "5", "six"};
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
		spinnerSubKind.setAdapter(adapter2);
	}
	
	
	public void onButtonClick(View view) {	
		Toast.makeText(getApplicationContext(), "Back to declaration" , Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(DeclareItemActivity.this, DeclareActivity.class);
		startActivity(intent);
	}

}
