package com.ilmoitus.croscutting;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.example.ilmoitus.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class LoggedInPerson {
	public static String token;
	public static Long id;
	public static String firstName;
	public static String lastName;
	public static Long supervisorId;
	public static int employeeNumber;
	public static String email;
	public static Long departmentId;
	
	public static void createPerson(){
		new InitPerson().execute();
	}
	
	private static class InitPerson extends AsyncTask<Void, Void, String>{
	
		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
		    HttpGet httpGet = new HttpGet("http://8.sns-ilmoitus.appspot.com/" + id);
		    httpGet.setHeader("Authorization",LoggedInPerson.token);
		    try {
		        HttpResponse response = httpClient.execute(httpGet);
		        InputStream inputStream = response.getEntity().getContent();
				if(inputStream != null){
					result = InputStreamConverter.convertInputStreamToString(inputStream);
				}
				else{
					result = "Did not Work";
				}
			} 
			catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}
		
		protected void onPostExecute(String result) {
			try{
				JSONObject person = new JSONObject(result);
				firstName = person.getString("first_name");
				lastName = person.getString("last_name");
				supervisorId = person.getLong("supervisor");
				employeeNumber = person.getInt("employee_number");
				email = person.getString("email");
				departmentId = person.getLong("department");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
