package com.example.ilmoitus;

import com.example.ilmoitus.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get ListView object from xml
		listView = (ListView) findViewById(R.id.list);

		// Create adapter
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.custom_row_view, new String[] { "title", "status" },
				new int[] { R.id.text1, R.id.text2 });

		populateList();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getApplicationContext(), "On item Clicked" , Toast.LENGTH_SHORT).show();
				 
			}
		});

		// Go to Login
		// TODO:
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	// TODO: replace by function that gets al the data from the web
	private void populateList() {
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("title", "Declaratie 11-11-2012");
		temp.put("status", "open");
		list.add(temp);
		HashMap<String, String> temp2 = new HashMap<String, String>();
		temp2.put("title", "Declaratie 11-11-2012");
		temp2.put("status", "open");
		list.add(temp2);
		HashMap<String, String> temp3 = new HashMap<String, String>();
		temp3.put("title", "Declaratie 11-11-2012");
		temp3.put("status", "open");
		list.add(temp3);
		HashMap<String, String> temp4 = new HashMap<String, String>();
		temp4.put("title", "Declaratie 11-11-2012");
		temp4.put("status", "open");
		list.add(temp4);
	}
	
	//TODO: give every button his own function
	public void onButtonClick(View view) {	
		Toast.makeText(getApplicationContext(), "On button Clicked" , Toast.LENGTH_SHORT).show();
		
	}
}
