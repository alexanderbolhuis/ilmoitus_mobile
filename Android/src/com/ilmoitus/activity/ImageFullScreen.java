package com.ilmoitus.activity;
import com.example.ilmoitus.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class ImageFullScreen extends Activity{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_image);
		String base64 = this.getIntent().getExtras().getString("base64");
		byte[] imageAsBytes = Base64.decode(base64, Base64.DEFAULT);
		Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		ImageView imgView = (ImageView) findViewById(R.id.imageview);
		imgView.setImageBitmap(bm);
		imgView.setScaleType(ScaleType.CENTER_INSIDE);
	}
}
