package com.example.iparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.SDKInitializer;

public class PanoramaActivity extends ActionBarActivity {

	public PanoramaView mPanoView;
    BMapManager mBMapManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        
		setContentView(R.layout.panorama);

		Intent intent = this.getIntent();        //获取已有的intent对象   
		Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象  

		mPanoView = (PanoramaView) findViewById(R.id.panorama_view);
		mPanoView.setPanorama(bundle.getDouble("lng"), bundle.getDouble("lat"));
		bundle.clear();
	}
	@Override  
	protected void onPause() {  
	    super.onPause();  
	    mPanoView.onPause();  
	}  
	 
	@Override  
	protected void onResume() {  
	    super.onResume();  
	    mPanoView.onResume();  
	}  
	 
	@Override  
	protected void onDestroy() {  
	    mPanoView.destroy();  
	    super.onDestroy();  
	}
	
}
