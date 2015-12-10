package com.example.iparking;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.*;
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
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.verify.BNKeyVerifyListener;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
public class NavigateActivity extends Activity {
	
	MapView mMapView;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	
	BDLocation userLoc=new BDLocation();
	BDLocation destLoc=new BDLocation();
	
	public MyLocationListenner myListener = new MyLocationListenner();
	//导航
	private final static String ACCESS_KEY = "uHtqDoUgVHKRDWj4G09xvpSo";
	private boolean mIsEngineInitSuccess = false; 
	
	private RoutePlanModel mRoutePlanModel = null;
	private MapGLSurfaceView mMapView1 = null;

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
		setContentView(R.layout.navigate);
		// 地图初始化
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
		
		mBaiduMap.setOnMapLongClickListener(MyLongClickListener);
	
		//初始化导航引擎  
 //       BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(), 
 //       		mNaviEngineInitListener,ACCESS_KEY,mKeyVerifyListener);
		/*
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
        mNaviEngineInitListener, new LBSAuthManagerListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                String str = null;
                if (0 == status) {
                    str = "key校验成功!";
                } else {
                    str = "key校验失败, " + msg;
                }
                Toast.makeText(NavigateActivity.this, str,
                        Toast.LENGTH_LONG).show();
            }
        });
*/
		boolean isInitSuccess = BaiduNaviManager.getInstance().checkEngineStatus(getApplicationContext());
        Toast.makeText(NavigateActivity.this, "isInitSuccess"+isInitSuccess,Toast.LENGTH_LONG).show();

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
					Toast.makeText(NavigateActivity.this, "请先设置终点！", Toast.LENGTH_SHORT).show();
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
	
	private String getSdcardDir() {  
        if (Environment.getExternalStorageState().equalsIgnoreCase(  
                Environment.MEDIA_MOUNTED)) {  
            return Environment.getExternalStorageDirectory().toString();  
        }  
        return null;  
    }   
	
	//导航引擎初始化监听
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {  
	        public void engineInitSuccess() {  
	            //导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航  
	            mIsEngineInitSuccess = true;  
	        }  
	 
	        public void engineInitStart() {  
	        }  
	 
	        public void engineInitFail() {  
	        }  
	    };  
    private BNKeyVerifyListener mKeyVerifyListener = new BNKeyVerifyListener() {

		@Override
		public void onVerifySucc() {
			// TODO Auto-generated method stub
			Toast.makeText(NavigateActivity.this, "key校验成功", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onVerifyFailed(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(NavigateActivity.this, "key校验失败", Toast.LENGTH_LONG)
					.show();
		}
	};
	
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
	
	/**
	 * 长按事件的监听
	 */
	public OnMapLongClickListener MyLongClickListener = new OnMapLongClickListener() {  
	    /** 
	    * 地图长按事件监听回调函数 
	    * @param point 长按的地理坐标 
	    */  
	    public void onMapLongClick(LatLng point){  
	    	hasEndpoint = true;
	    	//构建Marker图标  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(R.drawable.icon_marka);  
	    	//构建MarkerOption，用于在地图上添加Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point)  
	    	    .icon(bitmap);  
	    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
	    	getSystemService(VIBRATOR_SERVICE);
	    	//震动30ms
	    	vibrator.vibrate(30);
	    	//清除Marker
	    	mBaiduMap.clear();
	    	//在地图上添加Marker，并显示  
	    	mBaiduMap.addOverlay(option);
	    	Toast.makeText(NavigateActivity.this, "设置为终点", Toast.LENGTH_SHORT).show();
	    	
	    	EditText mDest = (EditText)findViewById(R.id.et_destination);
	    	mDest.setText("经度："+point.longitude+" 纬度："+point.latitude);
	    	destLoc.setLongitude(point.longitude);
	    	destLoc.setLatitude(point.latitude);
	    	System.out.println("终点");
	    	System.out.println("经度："+destLoc.getLongitude());
	    	System.out.println("纬度："+destLoc.getLatitude());
	    }  
	};
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
	 * 启动GPS导航. 前置条件：导航引擎初始化成功
	 */
	private void launchNavigator(){
		BaiduNaviManager.getInstance().launchNavigator(this,
				userLoc.getLatitude(), userLoc.getLongitude(),"我的位置", 
		        destLoc.getLatitude(),destLoc.getLongitude(),"终点",
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //算路方式
				true, 									   		 //真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
				new OnStartNavigationListener() {				 //跳转监听
					
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(NavigateActivity.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
				        startActivity(intent);
					}
					
					@Override
					public void onJumpToDownloader() {
					}
				});
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
					BaiduMapNavigation.openWebBaiduMapNavi(para, NavigateActivity.this);
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
	
	/**
	 * 计算路径
	 * @param NL_NET_MODE
	 */
	private void startCalcRoute(int NL_NET_MODE){
		LatLng sGCJ = transformFromBDToGCJ(userLoc);
		LatLng eGCJ = transformFromBDToGCJ(destLoc);
		int sX = (int) (sGCJ.latitude*1E5);
		int sY = (int) (sGCJ.longitude*1E5);
		int eX = (int) (eGCJ.latitude*1E5);
		int eY = (int) (eGCJ.longitude*1E5);
		
		//起点
		RoutePlanNode startNode = new RoutePlanNode(sX, sY,
				RoutePlanNode.FROM_MAP_POINT, "我的位置", "我的位置");
		//终点
		RoutePlanNode endNode = new RoutePlanNode(eX, eY,
				RoutePlanNode.FROM_MAP_POINT, "终点", "终点");
		//将起终点添加到nodeList
		ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
		nodeList.add(startNode);
		nodeList.add(endNode);
		BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));
		//设置算路方式
		BNRoutePlaner.getInstance().setCalcMode(NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
		// 设置算路结果回调
		BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
		// 设置起终点并算路
		boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(nodeList,NL_Net_Mode.NL_Net_Mode_OnLine);
		if(!ret){
			Toast.makeText(this, "规划失败", Toast.LENGTH_SHORT).show();
		}
		
//		BNaviPoint startPoint = new BNaviPoint(userLoc.getLongitude(),userLoc.getLatitude(), "我的位置", CoordinateType.BD09_MC);
//        BNaviPoint endPoint = new BNaviPoint(destLoc.getLongitude(),destLoc.getLatitude(), "终点", CoordinateType.BD09_MC);
//        BaiduNaviManager.getInstance().launchNavigator(this, startPoint, endPoint, NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,true,
//        		BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, new OnStartNavigationListener() {
//
//            @Override
//            public void onJumpToNavigator(Bundle configParams) {
//                // TODO Auto-generated method stub
//                BaiduNaviManager.getInstance().dismissWaitProgressDialog();
//                BNMapController.getInstance().setLayerMode(
//                        LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
//                mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()
//                        .getModel(ModelName.ROUTE_PLAN);
//            }
//
//            @Override
//            public void onJumpToDownloader() {
//                // TODO Auto-generated method stub
//
//            }
//        });

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
	 * 算路结果回调
	 */
	private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {

		@Override
		public void onRoutePlanYawingSuccess() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanYawingFail() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanSuccess() {
			// TODO Auto-generated method stub
			BNMapController.getInstance().setLayerMode(
					LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
			mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()
					.getModel(ModelName.ROUTE_PLAN);
		}

		@Override
		public void onRoutePlanFail() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanCanceled() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanStart() {
			// TODO Auto-generated method stub

		}

	};
	
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
						Toast.makeText(NavigateActivity.this,flag0+":"+items0[flag0], Toast.LENGTH_LONG).show();
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
		LatLng sGCJ = transformFromBDToGCJ(userLoc);
		LatLng eGCJ = transformFromBDToGCJ(destLoc);
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
				Toast.makeText(NavigateActivity.this, "(⊙o⊙)…未找到结果",
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
				Toast.makeText(NavigateActivity.this,
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
