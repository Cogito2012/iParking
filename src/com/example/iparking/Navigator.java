package com.example.iparking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
public class Navigator extends Activity {
	
	MapView mMapView;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	
	//��ͼ�����������ſؼ�
	private Button mZoomIn;
	private Button mZoomOut;
	private float maxZoomLevel;
	private float minZoomLevel;
	float level;
			
	BDLocation userLoc=new BDLocation();
	BDLocation destLoc=new BDLocation();
	
	public MyLocationListenner myListener = new MyLocationListenner();

	boolean hasEndpoint = false;
	private RoutePlanSearch routePlanSearch;// ·���滮�����ӿ�
	private int drivintResultIndex = 0;// �ݳ�·�߷���index
	private int totalLine = 0;// ��¼ĳ���������ķ�������
	private Button nextLineBtn;
	private int flag0 = 0;
	private int itemsChecked = 0;
	private DrivingPolicy STRATEGY_TYPE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.navigator);
		
		// ��ͼ��ʼ������λ
		initMap();
				
		//��ȡ�����Ŀ�ĵ�����
		getDestLocation();
		
		//���Ȳ���һ��·��
	//	drivingSearch(drivintResultIndex);
		
        //���ò���
        TextView SetStrategy = (TextView)findViewById(R.id.tvStrategy);
        SetStrategy.setOnClickListener(new OnClickListener(){

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(0);
			}
        
        });
        
        //�滮·��
        TextView CalcRoute = (TextView)findViewById(R.id.tvCalcRoutes);
        CalcRoute.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(hasEndpoint = true){
					drivintResultIndex = 0;
					nextLineBtn.setEnabled(false);
					drivingSearch(drivintResultIndex);  //�ݳ�·�߹滮
					//startCalcRoute(NL_Net_Mode.NL_Net_Mode_OnLine);
				}
				else{
					Toast.makeText(Navigator.this, "���������յ㣡", Toast.LENGTH_SHORT).show();
				}
			}
        	
        });
        routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
		
		//��ʼ����
		TextView StartNavigate = (TextView)findViewById(R.id.tvStartNavi);
		StartNavigate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		//		launchNavigator();
				startNaviByClient(); //���ÿͻ��˵ķ�ʽ���𵼺�
			}
        	
        });
        
		//��һ��·��
		nextLineBtn = (Button) findViewById(R.id.nextline_btn);
		nextLineBtn.setEnabled(false);
		nextLineBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					drivingSearch(++drivintResultIndex);
			}
			
		});
		
		
	}
	public void getDestLocation(){
		Intent intent = this.getIntent();        //��ȡ���е�intent����   
		Bundle bundle = intent.getExtras();    //��ȡintent�����bundle���� 
		destLoc.setLongitude(bundle.getDouble("lng"));
		destLoc.setLatitude(bundle.getDouble("lat"));
		bundle.clear();
		
		LatLng endpoint = new LatLng(destLoc.getLatitude(), destLoc.getLongitude());
		//����Markerͼ��  
    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
    	    .fromResource(R.drawable.icon_marka);  
    	//����MarkerOption�������ڵ�ͼ�����Marker  
    	OverlayOptions option = new MarkerOptions()  
    	    .position(endpoint)  
    	    .icon(bitmap); 
    	//�ڵ�ͼ�����Marker������ʾ  
    	mBaiduMap.addOverlay(option);
	} 
	
	public void initMap(){
		mMapView = (MapView) findViewById(R.id.mapview_navi);
		mBaiduMap = mMapView.getMap();
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		//��������
		mBaiduMap.getUiSettings().setCompassEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������Ϊ�ٶȾ�γ������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
		initZoomControl();
	}
	
	//�Ե�ǰλ�ý��м���
	public class MyLocationListenner implements BDLocationListener{
		
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
			double Lo = location.getLongitude();
			double La = location.getLatitude();
			userLoc.setLongitude(Lo); //�����û���ǰλ��
			userLoc.setLatitude(La);
			System.out.println("���");
	    	System.out.println("����:"+userLoc.getLongitude());
	    	System.out.println("γ��:"+userLoc.getLatitude());
	    	
			if (isFirstLoc) {
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
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
	  if (keyCode == KeyEvent.KEYCODE_MENU) {
		  return true;
	  }  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
	  {
         return super.onKeyDown(keyCode, event); 
	  }
	  return true; 
	 }
	
	/**
	 * ���ðٶȵ�ͼ�ͻ��˵���
	 */
	private void startNaviByClient(){
		double mLat1 = userLoc.getLatitude(); 
	   	double mLon1 = userLoc.getLongitude(); 
	   	double mLat2 = destLoc.getLatitude();
	   	double mLon2 = destLoc.getLongitude();
	   	
	   	LatLng p1 = new LatLng(mLat1, mLon1);
	   	LatLng p2 = new LatLng(mLat2, mLon2);
	    
	   	final NaviParaOption para = new NaviParaOption();
	   	para.startPoint(p1);
		para.startName("���");
		para.endPoint(p2);
		para.endName("�յ�");
		
		try{
		    BaiduMapNavigation.openBaiduMapNavi(para, this); 
		}
		catch(BaiduMapAppNotSupportNaviException e){
			e.printStackTrace();  
            AlertDialog.Builder builder = new AlertDialog.Builder(this);  
            builder.setMessage("����δ��װ�ٶȵ�ͼapp��app�汾���ͣ����ȷ�ϰ�װ��");  
            builder.setTitle("��ʾ"); 
            builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					BaiduMapNavigation.openWebBaiduMapNavi(para, Navigator.this);
				}
            });

            builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
            });  

            builder.create().show();  
		}
	}
	
	//�ٶ�����תgcj������Google���ߵ����꣩
    public static LatLng transformFromBDToGCJ(BDLocation bdLoc) 
    {
        double x = bdLoc.getLongitude() - 0.0065, y = bdLoc.getLatitude() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        return new LatLng(z * Math.sin(theta), z * Math.cos(theta));
    }
	
	/**
	 * ·���滮�������öԻ���
	 */
	@Override
	protected Dialog onCreateDialog(int id){
		Builder builder0 = new AlertDialog.Builder(this);
		final CharSequence[] items0 = {
				this.getResources().getString(R.string.STR_AVOID_JAM),
				this.getResources().getString(R.string.STR_DIS_FIRST),
				this.getResources().getString(R.string.STR_FEE_FIRST),
				this.getResources().getString(R.string.STR_TIME_FIRST) };

		builder0.setTitle(R.string.SetStrategy);
		builder0.setPositiveButton(R.string.strOK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// TODO Auto-generated method stub
						if(flag0==0)
							STRATEGY_TYPE = DrivingPolicy.ECAR_AVOID_JAM;
						if(flag0==1)
							STRATEGY_TYPE = DrivingPolicy.ECAR_DIS_FIRST;
						if(flag0==2)
							STRATEGY_TYPE = DrivingPolicy.ECAR_FEE_FIRST;
						if(flag0==3)
							STRATEGY_TYPE = DrivingPolicy.ECAR_TIME_FIRST;
						
					}
				});
		builder0.setNegativeButton(R.string.strcancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// TODO Auto-generated method stub
					}
				});

		builder0.setSingleChoiceItems(items0, itemsChecked,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						flag0 = which;
						Toast.makeText(Navigator.this,flag0+":"+items0[flag0], Toast.LENGTH_LONG).show();
					}
				});
		return builder0.create();
	}
	
	/**
	 * �ݳ�·���滮
	 */
	public void drivingSearch(int index){
		DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
		//policy - ECAR_TIME_FIRST:ʱ�����ȣ�ECAR_DIS_FIRST:������̣�ECAR_FEE_FIRST:��������
		drivingOption.policy(STRATEGY_TYPE);// ���üݳ�·�߲���
		LatLng sGCJ = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
		LatLng eGCJ =new LatLng(destLoc.getLatitude(), destLoc.getLongitude());
		drivingOption.from(PlanNode.withLocation(sGCJ));// �������
		drivingOption.to(PlanNode.withLocation(eGCJ));// �����յ�
		
		routePlanSearch.drivingSearch(drivingOption);// ����ݳ�·�߹滮
	}
	
	/**
	 * ·�߹滮����ص�
	 */
	OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();
			if (drivingRouteResult == null
					|| drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(Navigator.this, "(��o��)��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			}
			if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// drivingRouteResult.getSuggestAddrInfo()
				return;
			}
			if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
				drivingRouteOverlay.setData(drivingRouteResult.getRouteLines()
						.get(drivintResultIndex));// ����һ���ݳ�·�߷���
				mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				totalLine = drivingRouteResult.getRouteLines().size();
				Toast.makeText(Navigator.this,
						"����ѯ��" + totalLine + "��������������·", 1000).show();
				if (totalLine > 1) {
					nextLineBtn.setEnabled(true);
				}
				// ͨ��getTaxiInfo()���Եõ��ܶ���ڴ򳵵���Ϣ
				//Toast.makeText(NavigateActivity.this,"��·�ߴ���·��"+ 
				//    drivingRouteResult.getTaxiInfo().getDistance(), 1000).show();
			}
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
			// TODO Auto-generated method stub
			
		}
	
	};
	
	public void initZoomControl(){
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
	}
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
}
