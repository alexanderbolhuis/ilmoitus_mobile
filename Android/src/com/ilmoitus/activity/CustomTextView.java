package com.ilmoitus.activity;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public CustomTextView(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {

		Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/quadraat-sans-regular.ttf");
		 setTypeface(myTypeface);
	}

}