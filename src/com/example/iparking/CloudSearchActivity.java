package com.example.iparking;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.example.iparking.utils.ConnectSever;
import com.example.iparking.utils.DialogUtil;

public class CloudSearchActivity extends Activity implements CloudListener {
	private static final String LTAG = CloudSearchActivity.class
			.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private EditText et;
	public LatLng ll;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	public MyLocationListenner myListener = new MyLocationListenner();

	//��ͼ�����������ſؼ�
	private Button mZoomIn;
	private Button mZoomOut;
	private float maxZoomLevel;
	private float minZoomLevel;
	float level;
		
	@Override
	protected void onCreate(Bundle icicle) {
	    // ȥ��������    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		super.onCreate(icicle);
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_lbssearch);
		CloudManager.getInstance().init(CloudSearchActivity.this);
		et=(EditText)findViewById(R.id.textView1);
		mMapView = (MapView) findViewById(R.id.bmapView1);
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);
		mBaiduMap = mMapView.getMap();
		//���Ű�ť
		mZoomIn = (Button) findViewById(R.id.zoomin);
		mZoomOut = (Button) findViewById(R.id.zoomout);
		// ��ȡ�������ż���
		maxZoomLevel = mMapView.getMap().getMaxZoomLevel();
		// ��ȡ�������ż���
		minZoomLevel = mMapView.getMap().getMinZoomLevel();
		mZoomIn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
				level = mBaiduMap.getMapStatus().zoom;
				refreshZoomButtonStatus(level);
			}
			
		});
		mZoomOut.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
				level = mBaiduMap.getMapStatus().zoom;
				refreshZoomButtonStatus(level);
			}
			
		});
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mBaiduMap.setMyLocationEnabled(true);
		//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback(){

			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				Point p=new Point(50, 360);
				mMapView.setScaleControlPosition(p); 
				Point p1=new Point(100, 270);
				mBaiduMap.getUiSettings().setCompassPosition(p1);
				int overlookAngle = -1;
				MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(overlookAngle).build();
				MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
				mBaiduMap.animateMapStatus(u);
			}});

		//����
		mBaiduMap.setOnMarkerClickListener(listenerPOI);
		findViewById(R.id.regionSearch).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

				    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
				    	getSystemService(VIBRATOR_SERVICE);
				    	vibrator.vibrate(50);
				    	
						if (et.getText().toString().length()<1) {
							mBaiduMap.clear();
							Toast.makeText(CloudSearchActivity.this, "�ǵ�����ؼ���Ӵ", Toast.LENGTH_SHORT).show();
						}
						else{
							LocalSearchInfo info = new LocalSearchInfo();
							info.ak = "FeeT0DjvfCoVsGiFlpWwx75c";
							info.geoTableId = 59368;
							info.tags = "";
							info.q = et.getText().toString();
							info.pageSize = 50;
							info.region = "�人��";
							CloudManager.getInstance().localSearch(info);
							
							((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(CloudSearchActivity.this.getCurrentFocus()
									.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
							
						}
					}
				});
		//�ܱ�
		findViewById(R.id.nearbySearch).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						NearbySearchInfo info = new NearbySearchInfo();
						info.ak = "FeeT0DjvfCoVsGiFlpWwx75c";
						info.geoTableId = 59368;
						info.radius = 1000;
						info.pageSize = 50;						
						info.location = ll.longitude+","+ll.latitude;
						CloudManager.getInstance().nearbySearch(info);
					}
				});
		//��λ
		findViewById(R.id.button_location).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
			    		MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(17).build();
			    		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
						mBaiduMap.animateMapStatus(u);
					}
				});
		//ͼ��
		findViewById(R.id.button_layer).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mBaiduMap.getMapType()==BaiduMap.MAP_TYPE_SATELLITE){
							mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); 
						}else if(mBaiduMap.getMapType()==BaiduMap.MAP_TYPE_NORMAL){
							mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
						}
					}
				});
		//·��
		final ImageButton tr=(ImageButton)findViewById(R.id.button_traff);
		tr.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mBaiduMap.isTrafficEnabled()){
							//�رս�ͨͼ   
							mBaiduMap.setTrafficEnabled(false);		
							tr.setImageResource(R.drawable.traff);	
					    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
					    	getSystemService(VIBRATOR_SERVICE);
					    	vibrator.vibrate(30);			
						}
						else
						{
							//������ͨͼ   
							mBaiduMap.setTrafficEnabled(true);
							tr.setImageResource(R.drawable.traff1);
					    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
					    	getSystemService(VIBRATOR_SERVICE);
					    	vibrator.vibrate(30);
						}
					}
				});
	}
/*
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		CloudManager.getInstance().destroy();
	}
*/
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(CloudSearchActivity.this, result.poiInfo.title,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CloudSearchActivity.this,
						"status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onGetSearchResult(CloudSearchResult result, int error) {
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(LTAG, "onGetSearchResult, result length: " + result.poiList.size());
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
				
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).title(info.title+"/"+info.extras.get("theid"));
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		}else{
			Toast.makeText(CloudSearchActivity.this, "Сiȥ�˻��Ƕ�û�ҵ���", Toast.LENGTH_SHORT).show();
			}
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			ll = new LatLng(location.getLatitude(),location.getLongitude());
			if (isFirstLoc) {
				isFirstLoc = false;
	    		MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(17).build();
	    		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
				mBaiduMap.animateMapStatus(u);
			}
		}
			public void onReceivePoi(BDLocation poiLocation) {
			}
		}

	OnMarkerClickListener listenerPOI = new OnMarkerClickListener() {  
	    /** 
	    * ��ͼ Marker ���������¼��������� 
	    * @param marker ������� marker
	    */  
		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			final Marker ar= arg0;
			Toast.makeText(CloudSearchActivity.this, ar.getTitle(), Toast.LENGTH_SHORT).show();			
			Thread t = new Thread(new Runnable() {
	        	@Override
	        	public void run() {
	        		// TODO Auto-generated method stub
	        		ConnectSever cs = new ConnectSever();
	    			JSONObject jb=null;
	    			List<NameValuePair> params=new ArrayList<NameValuePair>();
	    			NameValuePair code   = new BasicNameValuePair("code", "-1");
	    			NameValuePair price   = new BasicNameValuePair("Price", "-1");
	    			NameValuePair all   = new BasicNameValuePair("All", "-1");
	    			NameValuePair remained   = new BasicNameValuePair("Remained", "-1");
	    			params.add(code);
	    			params.add(price);
	    			params.add(all);
	    			params.add(remained);
	    			String url="http://rssr.iparking.asia/iPr/iPr_par.php?par_id="+ar.getTitle().split("/")[1];
	    			jb=cs.getJSON(url, params);
	    			
	    			try {
	    				//Toast.makeText(CloudSearchActivity.this,"r"+jb.getString("Remained") , Toast.LENGTH_SHORT).show();
	    				Log.v("click_mark", "r"+jb.getString("Remained"));
	    				Intent intent=new Intent(CloudSearchActivity.this,ParInfoActivity.class); 
	    				Bundle bundle = new Bundle();  //����Bundle����   
	    				bundle.putString("Price", jb.getString("Price")); //װ������ 
	    				bundle.putString("All", jb.getString("All")); //װ������ 
	    				bundle.putString("Remained", jb.getString("Remained")); //װ������ 
	    				bundle.putString("Name", ar.getTitle().split("/")[0]); //װ������ 
	    				bundle.putDouble("lat", ar.getPosition().latitude); //װ������ 
	    				bundle.putDouble("lng", ar.getPosition().longitude); //װ������   
	    				intent.putExtras(bundle);    
	    				startActivity(intent);
	    			} catch (JSONException e) {
	    				// TODO Auto-generated catch block
	    				Log.v("click_mark", "error");
	    				e.printStackTrace();
	    			}
					}				 
	        	});
	        t.start();
			//
			return true;
			
		}  
	};
	
	public void refreshZoomButtonStatus(float level){
		if(mMapView == null){
			throw new NullPointerException("you can call setMapView(MapView mapView) at first");
		}
		if(level > minZoomLevel && level < maxZoomLevel){
			if(!mZoomOut.isEnabled()){
				mZoomOut.setEnabled(true);
			}
			if(!mZoomIn.isEnabled()){ 
				mZoomIn.setEnabled(true);
			}
		}
		else if(level == minZoomLevel ){
			mZoomOut.setEnabled(false);
		}
		else if(level == maxZoomLevel){
			mZoomIn.setEnabled(false);
		}
	}
}

