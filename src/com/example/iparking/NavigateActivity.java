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
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	
	BDLocation userLoc=new BDLocation();
	BDLocation destLoc=new BDLocation();
	
	public MyLocationListenner myListener = new MyLocationListenner();
	//����
	private final static String ACCESS_KEY = "uHtqDoUgVHKRDWj4G09xvpSo";
	private boolean mIsEngineInitSuccess = false; 
	
	private RoutePlanModel mRoutePlanModel = null;
	private MapGLSurfaceView mMapView1 = null;

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
		setContentView(R.layout.navigate);
		// ��ͼ��ʼ��
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
		
		mBaiduMap.setOnMapLongClickListener(MyLongClickListener);
	
		//��ʼ����������  
 //       BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(), 
 //       		mNaviEngineInitListener,ACCESS_KEY,mKeyVerifyListener);
		/*
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
        mNaviEngineInitListener, new LBSAuthManagerListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                String str = null;
                if (0 == status) {
                    str = "keyУ��ɹ�!";
                } else {
                    str = "keyУ��ʧ��, " + msg;
                }
                Toast.makeText(NavigateActivity.this, str,
                        Toast.LENGTH_LONG).show();
            }
        });
*/
		boolean isInitSuccess = BaiduNaviManager.getInstance().checkEngineStatus(getApplicationContext());
        Toast.makeText(NavigateActivity.this, "isInitSuccess"+isInitSuccess,Toast.LENGTH_LONG).show();

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
					Toast.makeText(NavigateActivity.this, "���������յ㣡", Toast.LENGTH_SHORT).show();
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
	
	private String getSdcardDir() {  
        if (Environment.getExternalStorageState().equalsIgnoreCase(  
                Environment.MEDIA_MOUNTED)) {  
            return Environment.getExternalStorageDirectory().toString();  
        }  
        return null;  
    }   
	
	//���������ʼ������
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {  
	        public void engineInitSuccess() {  
	            //������ʼ�����첽�ģ���ҪһС��ʱ�䣬�������־��ʶ�������Ƿ��ʼ���ɹ���Ϊtrueʱ����ܷ��𵼺�  
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
			Toast.makeText(NavigateActivity.this, "keyУ��ɹ�", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onVerifyFailed(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(NavigateActivity.this, "keyУ��ʧ��", Toast.LENGTH_LONG)
					.show();
		}
	};
	
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
	
	/**
	 * �����¼��ļ���
	 */
	public OnMapLongClickListener MyLongClickListener = new OnMapLongClickListener() {  
	    /** 
	    * ��ͼ�����¼������ص����� 
	    * @param point �����ĵ������� 
	    */  
	    public void onMapLongClick(LatLng point){  
	    	hasEndpoint = true;
	    	//����Markerͼ��  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(R.drawable.icon_marka);  
	    	//����MarkerOption�������ڵ�ͼ�����Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point)  
	    	    .icon(bitmap);  
	    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	    	 
	    	getSystemService(VIBRATOR_SERVICE);
	    	//��30ms
	    	vibrator.vibrate(30);
	    	//���Marker
	    	mBaiduMap.clear();
	    	//�ڵ�ͼ�����Marker������ʾ  
	    	mBaiduMap.addOverlay(option);
	    	Toast.makeText(NavigateActivity.this, "����Ϊ�յ�", Toast.LENGTH_SHORT).show();
	    	
	    	EditText mDest = (EditText)findViewById(R.id.et_destination);
	    	mDest.setText("���ȣ�"+point.longitude+" γ�ȣ�"+point.latitude);
	    	destLoc.setLongitude(point.longitude);
	    	destLoc.setLatitude(point.latitude);
	    	System.out.println("�յ�");
	    	System.out.println("���ȣ�"+destLoc.getLongitude());
	    	System.out.println("γ�ȣ�"+destLoc.getLatitude());
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
	 * ����GPS����. ǰ�����������������ʼ���ɹ�
	 */
	private void launchNavigator(){
		BaiduNaviManager.getInstance().launchNavigator(this,
				userLoc.getLatitude(), userLoc.getLongitude(),"�ҵ�λ��", 
		        destLoc.getLatitude(),destLoc.getLongitude(),"�յ�",
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //��·��ʽ
				true, 									   		 //��ʵ����
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //�����߲���
				new OnStartNavigationListener() {				 //��ת����
					
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
					BaiduMapNavigation.openWebBaiduMapNavi(para, NavigateActivity.this);
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
	
	/**
	 * ����·��
	 * @param NL_NET_MODE
	 */
	private void startCalcRoute(int NL_NET_MODE){
		LatLng sGCJ = transformFromBDToGCJ(userLoc);
		LatLng eGCJ = transformFromBDToGCJ(destLoc);
		int sX = (int) (sGCJ.latitude*1E5);
		int sY = (int) (sGCJ.longitude*1E5);
		int eX = (int) (eGCJ.latitude*1E5);
		int eY = (int) (eGCJ.longitude*1E5);
		
		//���
		RoutePlanNode startNode = new RoutePlanNode(sX, sY,
				RoutePlanNode.FROM_MAP_POINT, "�ҵ�λ��", "�ҵ�λ��");
		//�յ�
		RoutePlanNode endNode = new RoutePlanNode(eX, eY,
				RoutePlanNode.FROM_MAP_POINT, "�յ�", "�յ�");
		//�����յ���ӵ�nodeList
		ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
		nodeList.add(startNode);
		nodeList.add(endNode);
		BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));
		//������·��ʽ
		BNRoutePlaner.getInstance().setCalcMode(NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
		// ������·����ص�
		BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
		// �������յ㲢��·
		boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(nodeList,NL_Net_Mode.NL_Net_Mode_OnLine);
		if(!ret){
			Toast.makeText(this, "�滮ʧ��", Toast.LENGTH_SHORT).show();
		}
		
//		BNaviPoint startPoint = new BNaviPoint(userLoc.getLongitude(),userLoc.getLatitude(), "�ҵ�λ��", CoordinateType.BD09_MC);
//        BNaviPoint endPoint = new BNaviPoint(destLoc.getLongitude(),destLoc.getLatitude(), "�յ�", CoordinateType.BD09_MC);
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
	//�ٶ�����תgcj������Google���ߵ����꣩
    public static LatLng transformFromBDToGCJ(BDLocation bdLoc) 
    {
        double x = bdLoc.getLongitude() - 0.0065, y = bdLoc.getLatitude() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        return new LatLng(z * Math.sin(theta), z * Math.cos(theta));
    }
	
	/**
	 * ��·����ص�
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
						Toast.makeText(NavigateActivity.this,flag0+":"+items0[flag0], Toast.LENGTH_LONG).show();
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
		LatLng sGCJ = transformFromBDToGCJ(userLoc);
		LatLng eGCJ = transformFromBDToGCJ(destLoc);
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
				Toast.makeText(NavigateActivity.this, "(��o��)��δ�ҵ����",
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
				Toast.makeText(NavigateActivity.this,
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
