package com.ilmoitus.activity;

import com.example.ilmoitus.R;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.ilmoitus.adapter.DeclarationOverviewAdapter;
import com.ilmoitus.croscutting.InputStreamConverter;
import com.ilmoitus.croscutting.LoggedInPerson;
import com.ilmoitus.model.BaseDeclaration;
import com.ilmoitus.model.ClosedDeclaration;
import com.ilmoitus.model.OpenDeclaration;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
	
	private ArrayList<BaseDeclaration> declarations;
	//ListView listView;
	SwipeListView swipelistview;
	DeclarationOverviewAdapter adapter;
	private Button overviewButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		declarations = new ArrayList<BaseDeclaration>();
		overviewButton = (Button) findViewById(R.id.buttonOvervieuw);
		overviewButton.setEnabled(false);
		//listView = (ListView) findViewById(R.id.list);
		swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list);
		adapter = new DeclarationOverviewAdapter(this,R.layout.custom_row, declarations);
		
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                
             
                swipelistview.openAnimate(position); //when you touch front view it will open
              
             
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
                
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            	
            }

        });
		
		//These are the swipe listview settings. you can change these
        //setting as your requirement 
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions 
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(80f)); // right side offset
        swipelistview.setAnimationTime(500); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
	
        //swipelistview.setAdapter(adapter);
		
		new GetDeclerationsTask(this, swipelistview).execute();
	}
	
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

	public void onButtonClick(View view) {
		Toast.makeText(getApplicationContext(), "On button Clicked",
				Toast.LENGTH_SHORT).show();
	}
	
	public void onDeclareButtonClick(View view) {
	    Intent intent = new Intent(this, DeclareActivity.class);
	    startActivity(intent);
	}

	private class GetDeclerationsTask extends AsyncTask<Void, Void, String> {
		private Activity activity;
		private ListView context;

		public GetDeclerationsTask(Activity activity, ListView context) {
			this.activity = activity;
			this.context = context;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.base_url) +
					"/declarations/employee");
			httpGet.setHeader("Authorization", LoggedInPerson.token);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				InputStream inputStream = response.getEntity().getContent();
				if (inputStream != null) {
					// parse the inputStream to string
					result = InputStreamConverter
							.convertInputStreamToString(inputStream);
				} else {
					result = "Did not Work";
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			try {
				JSONArray declaration = new JSONArray(result);
				for (int i = 0; i < declaration.length(); i++) {
					JSONObject object = declaration.getJSONObject(i);
					if (object.getString("class_name").equals(
							"open_declaration")) {
						declarations.add(new OpenDeclaration(object));
					}
					if (object.getString("class_name").equals(
							"locked_declaration")) {
						declarations.add(new ClosedDeclaration(object));
					}
				}
				TextView titleView = (TextView) findViewById(R.id.person_title);
				titleView.setText("Ingelogd als " + LoggedInPerson.firstName
						+ " " + LoggedInPerson.lastName + " ("
						+ LoggedInPerson.employeeNumber + ")");
				adapter = new DeclarationOverviewAdapter(activity, R.layout.custom_row, declarations);
				context.setAdapter(adapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
