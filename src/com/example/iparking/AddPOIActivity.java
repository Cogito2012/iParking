package com.example.iparking;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.iparking.utils.Base64Util;
import com.example.iparking.utils.POIDatabaseHelper;

//ʵ��poi��ǡ��ϴ����༭��������ɾ����
public class AddPOIActivity extends ActionBarActivity implements OnGetGeoCoderResultListener{

	public static boolean showone=false;
	String strSymbolName="ͣ����";
	public Spinner sp;
	String id="-1";
	ContentValues value ;
	GeoCoder mSearch = null;
	String result="δ֪";
	MapView mMapView;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	//��Ȥ������
	private static final String[] types = { "ͣ����", "ҽԺ", "С��", "����", "�̳�",
		"��԰", "ҩ��", "С��", "����" };

	//��ͼ�����������ſؼ�
	private Button mZoomIn;
	private Button mZoomOut;
	private float maxZoomLevel;
	private float minZoomLevel;
	float level;
			
	public MyLocationListenner myListener = new MyLocationListenner();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		super.onCreate(savedInstanceState);
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.addpoi);

		sp=(Spinner)findViewById(R.id.spn_poi_type);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (sp.getSelectedItem() != null) {
					strSymbolName = sp.getSelectedItem().toString();
				}
				strSymbolName = sp.getSelectedItem().toString();
				if (strSymbolName.equals("")) {
					return;
				} else {
					Toast.makeText(AddPOIActivity.this, strSymbolName, Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});


		TypedValue tv = new TypedValue();  
		if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {  
		   int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
		   RelativeLayout rt=(RelativeLayout)findViewById(R.id.relativeLayout1);
		   android.view.ViewGroup.LayoutParams pp =rt.getLayoutParams();
		   pp.height =actionBarHeight; 
		   rt.setLayoutParams(pp);
		}
		if(AddPOIActivity.showone){
			Bundle extra=getIntent().getExtras();
	        if(extra!=null){
	        	this.id=extra.getString("_id");
	        	extra.clear();
	        }	
			
		}
		//���ߵ�ͼ
		//MKOfflineMap om = new MKOfflineMap();
		//om.importOfflineData();
		
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mMapView.removeViewAt(1);//ȥ��LOGO
		mMapView.getChildAt(1).setVisibility(View.INVISIBLE); //�����Դ����ſؼ�
		
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
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		//��������
		mBaiduMap.getUiSettings().setCompassEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		MapStatus mapStatus = new MapStatus.Builder().zoom(17).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		mBaiduMap.setMapStatus(mapStatusUpdate);
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		mBaiduMap.setOnMapLongClickListener(listenerMap);
		mBaiduMap.setOnMarkerClickListener(listenerPOI);

		LoadPOIFromSQLite(mBaiduMap);
		
	}

	private void LoadPOIFromSQLite(BaiduMap mBaiduMap2) {
		// TODO Auto-generated method stub
    	POIDatabaseHelper database_helper = new POIDatabaseHelper(AddPOIActivity.this);
        SQLiteDatabase db = database_helper.getWritableDatabase();//�����ǻ�ÿ�д�����ݿ� 
        Cursor cursor = db.query("poi", new String[]{"id","type", "lng", "lat"}, null, null,  null, null, null);
    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
    	    .fromResource(R.drawable.icon_marka); 
        while(cursor.moveToNext()) {
	    	LatLng point = new LatLng(Double.valueOf(cursor.getString(cursor.getColumnIndex("lat"))),
	    			Double.valueOf(cursor.getString(cursor.getColumnIndex("lng"))));
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point)  
	    	    .icon(bitmap)
	    	    .title(cursor.getString(cursor.getColumnIndex("id"))+"/"+Base64Util.getFromBASE64(cursor.getString(cursor.getColumnIndex("type"))));  
	    	//�ڵ�ͼ�����Marker������ʾ 
	    	if((this.id).equals(cursor.getString(cursor.getColumnIndex("id")))){
	    		BitmapDescriptor bitmap1 = BitmapDescriptorFactory  
	    	    	    .fromResource(R.drawable.show); 
	    		OverlayOptions option1 = new MarkerOptions()  
	    	    	.position(point)  
	    	    	.icon(bitmap1)
	    	    	.title(cursor.getString(cursor.getColumnIndex("id"))+"/"+Base64Util.getFromBASE64(cursor.getString(cursor.getColumnIndex("type"))));  
	    		this.id="-1";
	    		mBaiduMap2.addOverlay(option1);
	    		LatLng cenpt = new LatLng(point.latitude,point.longitude);
	    		MapStatus mapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
	    		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
	    		mBaiduMap2.setMapStatus(mapStatusUpdate);

	    	}else{
	    		mBaiduMap2.addOverlay(option);
	    		}
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
			String type1=arg0.getTitle().split("/")[1];
			Toast.makeText(AddPOIActivity.this, "��ǣ�"+type1, Toast.LENGTH_SHORT).show();
			return true;
		}  
	};
	OnMapLongClickListener listenerMap = new OnMapLongClickListener() {  
	    /** 
	    * ��ͼ�����¼������ص����� 
	    * @param point �����ĵ������� 
	    */  
	    @SuppressLint("SimpleDateFormat")
		public void onMapLongClick(LatLng point){            
	    	SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddhhmmss");
	    	String id=sDateFormat.format(new java.util.Date());
	    	//����Markerͼ��  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(R.drawable.icon_marka);  
	    	//����MarkerOption�������ڵ�ͼ�����Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point)  
	    	    .icon(bitmap)
	    	    .title(id+"/"+strSymbolName);  
	    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
	    	getSystemService(VIBRATOR_SERVICE);
	    	//��0.05��
	    	vibrator.vibrate(50);
	    	//�ڵ�ͼ�����Marker������ʾ  
	    	mBaiduMap.addOverlay(option);
	    	value = new ContentValues();
	    	value.put("id", "liuyaqi"+id);
	    	value.put("type", Base64Util.getBASE64(strSymbolName));
	    	value.put("lng", point.longitude);
	    	value.put("lat", point.latitude);
	    	value.put("username", "liuyaqi");
	    	mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point)); 
	    	//Toast.makeText(AddPOIActivity.this, "��ǳɹ�"+result, Toast.LENGTH_SHORT).show();
	    }  
	};
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
	  if (keyCode == KeyEvent.KEYCODE_MENU) {
		  return true;
	  }  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
	  {
		  AddPOIActivity.showone=false;
          return super.onKeyDown(keyCode, event); 
	  }
	  return true; 
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
			if (isFirstLoc&&AddPOIActivity.showone==false) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			result="δ֪�ص�";
			Toast.makeText(AddPOIActivity.this, result, Toast.LENGTH_SHORT).show();
	    	value.put("addre", result);
	    	//���ݿ����
	    	POIDatabaseHelper database_helper = new POIDatabaseHelper(AddPOIActivity.this);
            SQLiteDatabase db = database_helper.getWritableDatabase();//�����ǻ�ÿ�д�����ݿ�
            db.insert("poi", null, value);
		}
		else{
			result=arg0.getAddress();
			Toast.makeText(AddPOIActivity.this,strSymbolName+":"+ result, Toast.LENGTH_SHORT).show();
	    	value.put("address", result);
	    	//���ݿ����
	    	POIDatabaseHelper database_helper = new POIDatabaseHelper(AddPOIActivity.this);
            SQLiteDatabase db = database_helper.getWritableDatabase();//�����ǻ�ÿ�д�����ݿ�
            db.insert("poi", null, value);
		}
		value.clear();
		
	}

	/**
	 * ����MapView�����ż���������Ű�ť��״̬�����ﵽ������ż�������mButtonZoomin
	 * Ϊ���ܵ������֮����mButtonZoomout
	 * @param level
	 */
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

	 @Override  
	 public boolean onCreateOptionsMenu(Menu menu) {  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.menu2, menu);  
	    return super.onCreateOptionsMenu(menu); 
	    } 
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	      Intent intent = new Intent(this, MainActivity.class);
	      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      startActivity(intent);
	      break;
	    case R.id.action_edit:  
	    	Toast.makeText(getApplicationContext(), "do not touch me", Toast.LENGTH_SHORT).show();
	    	    	
	    	break;
	    	
	    default:
	      break;
	    }
	    return super.onOptionsItemSelected(item);
	  }
}
