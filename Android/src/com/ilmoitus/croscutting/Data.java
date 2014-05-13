package com.ilmoitus.croscutting;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import com.ilmoitus.model.DeclarationLine;

public class Data {

	private static String URL = "LINES";
	
	private static SharedPreferences getPrefs(Context context){
		return context.getSharedPreferences("LINES", 0);
	}
}
