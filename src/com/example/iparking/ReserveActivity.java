package com.example.iparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ReserveActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);// »•µÙ±ÍÃ‚¿∏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reserve);
	}
}
