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
	boolean isFirstLoc = true;// 是否首次定位
	
	//地图比例尺与缩放控件
	private Button mZoomIn;
	private Button mZoomOut;
	private float maxZoomLevel;
	private float minZoomLevel;
	float level;
			
	BDLocation userLoc=new BDLocation();
	BDLocation destLoc=new BDLocation();
	
	public MyLocationListenner myListener = new MyLocationListenner();

	boolean hasEndpoint = false;
	private RoutePlanSearch routePlanSearch;// 路径规划搜索接口
	private int drivintResultIndex = 0;// 驾车路线方案index
	private int totalLine = 0;// 记录某种搜索出的方案数量
	private Button nextLineBtn;
	private int flag0 = 0;
	private int itemsChecked = 0;
	private DrivingPolicy STRATEGY_TYPE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.navigator);
		
		// 地图初始化及定位
		initMap();
				
		//获取传入的目的点坐标
		getDestLocation();
		
		//首先产生一条路径
	//	drivingSearch(drivintResultIndex);
		
        //设置策略
        TextView SetStrategy = (TextView)findViewById(R.id.tvStrategy);
        SetStrategy.setOnClickListener(new OnClickListener(){

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(0);
			}
        
        });
        
        //规划路径
        TextView CalcRoute = (TextView)findViewById(R.id.tvCalcRoutes);
        CalcRoute.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(hasEndpoint = true){
					drivintResultIndex = 0;
					nextLineBtn.setEnabled(false);
					drivingSearch(drivintResultIndex);  //驾车路线规划
					//startCalcRoute(NL_Net_Mode.NL_Net_Mode_OnLine);
				}
				else{
					Toast.makeText(Navigator.this, "请先设置终点！", Toast.LENGTH_SHORT).show();
				}
			}
        	
        });
        routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
		
		//开始导航
		TextView StartNavigate = (TextView)findViewById(R.id.tvStartNavi);
		StartNavigate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		//		launchNavigator();
				startNaviByClient(); //调用客户端的方式发起导航
			}
        	
        });
        
		//下一条路径
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
		Intent intent = this.getIntent();        //获取已有的intent对象   
		Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象 
		destLoc.setLongitude(bundle.getDouble("lng"));
		destLoc.setLatitude(bundle.getDouble("lat"));
		bundle.clear();
		
		LatLng endpoint = new LatLng(destLoc.getLatitude(), destLoc.getLongitude());
		//构建Marker图标  
    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
    	    .fromResource(R.drawable.icon_marka);  
    	//构建MarkerOption，用于在地图上添加Marker  
    	OverlayOptions option = new MarkerOptions()  
    	    .position(endpoint)  
    	    .icon(bitmap); 
    	//在地图上添加Marker，并显示  
    	mBaiduMap.addOverlay(option);
	} 
	
	public void initMap(){
		mMapView = (MapView) findViewById(R.id.mapview_navi);
		mBaiduMap = mMapView.getMap();
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		//开启罗盘
		mBaiduMap.getUiSettings().setCompassEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型为百度经纬度坐标
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
		initZoomControl();
	}
	
	//对当前位置进行监听
	public class MyLocationListenner implements BDLocationListener{
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			double Lo = location.getLongitude();
			double La = location.getLatitude();
			userLoc.setLongitude(Lo); //保存用户当前位置
			userLoc.setLatitude(La);
			System.out.println("起点");
	    	System.out.println("经度:"+userLoc.getLongitude());
	    	System.out.println("纬度:"+userLoc.getLatitude());
	    	
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
	 * 调用百度地图客户端导航
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
		para.startName("起点");
		para.endPoint(p2);
		para.endName("终点");
		
		try{
		    BaiduMapNavigation.openBaiduMapNavi(para, this); 
		}
		catch(BaiduMapAppNotSupportNaviException e){
			e.printStackTrace();  
            AlertDialog.Builder builder = new AlertDialog.Builder(this);  
            builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");  
            builder.setTitle("提示"); 
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					BaiduMapNavigation.openWebBaiduMapNavi(para, Navigator.this);
				}
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
            });  

            builder.create().show();  
		}
	}
	
	//百度坐标转gcj（例如Google、高德坐标）
    public static LatLng transformFromBDToGCJ(BDLocation bdLoc) 
    {
        double x = bdLoc.getLongitude() - 0.0065, y = bdLoc.getLatitude() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        return new LatLng(z * Math.sin(theta), z * Math.cos(theta));
    }
	
	/**
	 * 路径规划策略设置对话框
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
	 * 驾车路径规划
	 */
	public void drivingSearch(int index){
		DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
		//policy - ECAR_TIME_FIRST:时间优先；ECAR_DIS_FIRST:距离最短；ECAR_FEE_FIRST:费用最少
		drivingOption.policy(STRATEGY_TYPE);// 设置驾车路线策略
		LatLng sGCJ = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
		LatLng eGCJ =new LatLng(destLoc.getLatitude(), destLoc.getLongitude());
		drivingOption.from(PlanNode.withLocation(sGCJ));// 设置起点
		drivingOption.to(PlanNode.withLocation(eGCJ));// 设置终点
		
		routePlanSearch.drivingSearch(drivingOption);// 发起驾车路线规划
	}
	
	/**
	 * 路线规划结果回调
	 */
	OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();
			if (drivingRouteResult == null
					|| drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(Navigator.this, "(⊙o⊙)…未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// drivingRouteResult.getSuggestAddrInfo()
				return;
			}
			if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
				drivingRouteOverlay.setData(drivingRouteResult.getRouteLines()
						.get(drivintResultIndex));// 设置一条驾车路线方案
				mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				totalLine = drivingRouteResult.getRouteLines().size();
				Toast.makeText(Navigator.this,
						"共查询出" + totalLine + "条符合条件的线路", 1000).show();
				if (totalLine > 1) {
					nextLineBtn.setEnabled(true);
				}
				// 通过getTaxiInfo()可以得到很多关于打车的信息
				//Toast.makeText(NavigateActivity.this,"该路线打车总路程"+ 
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
		//缩放按钮
		mZoomIn = (Button) findViewById(R.id.zoomin);
		mZoomOut = (Button) findViewById(R.id.zoomout);
		// 获取最大的缩放级别
		maxZoomLevel = mMapView.getMap().getMaxZoomLevel();
		// 获取最大的缩放级别
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
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}
