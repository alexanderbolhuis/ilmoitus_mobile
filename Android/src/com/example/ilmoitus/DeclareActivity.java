package com.example.ilmoitus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class DeclareActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_declare);
		
		Spinner dropdown = (Spinner)findViewById(R.id.spinner);
		String[] items = new String[]{"1", "2", "three"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);
	
	}

	public void onButtonClickAddItem(View view) {	
		Toast.makeText(getApplicationContext(), "add Item" , Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(DeclareActivity.this, DeclareItemActivity.class);
		startActivity(intent);
	}
	
	public void onButtonClickSubmit(View view) {	
		Toast.makeText(getApplicationContext(), "On Submit button Clicked" , Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(DeclareActivity.this, MainActivity.class);
		startActivity(intent);
	}
}

