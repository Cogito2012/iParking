package com.example.iparking;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	private EditText cityname;
	private Button btn;
	private TextView tvTips;
	VerticalScrollTextView sctvTips;
	private final int duration = Toast.LENGTH_LONG;
	private String theCityName;
	private String result;
	private ProgressDialog progressDialog;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 1:
				SetView();
				break;
			case -1:
				Toast.makeText(getApplicationContext(), "获取数据失败", duration)
						.show();
				break;
			}
		}
	};

	private void SetView() {
		LinearLayout myLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
		myLayout.removeAllViews();
		ParseWeatherUtil p = new ParseWeatherUtil(result.substring(8,
				result.length() - 2));
		TextView view_province = getTextView(p.getProvince()+"/"+p.getCity());
		view_province.setTextColor(Color.YELLOW);
		view_province.setGravity(Gravity.CENTER);
		myLayout.addView(view_province);
		
		TextView view_today = getTextView("今日天气");
		view_today.setTextColor(Color.YELLOW);
		myLayout.addView(view_today);
		myLayout.addView(getTextView("时间:" + p.getTime()));
		myLayout.addView(getTextView("温度:" + p.getTemperature1()));
		myLayout.addView(getTextView("天气:" + p.getWeather1()));
		myLayout.addView(getTextView("风况:" + p.getWind_Detail1()));

		TextView view_tomorrow = getTextView("明日天气");
		view_tomorrow.setTextColor(Color.YELLOW);
		myLayout.addView(view_tomorrow);
		myLayout.addView(getTextView("温度:" + p.getTemperature2()));
		myLayout.addView(getTextView("天气:" + p.getWeather2()));
		myLayout.addView(getTextView("风况:" + p.getWind_Detail2()));
		
		TextView view_aftertomorrow = getTextView("后天天气");
		view_aftertomorrow.setTextColor(Color.YELLOW);
		myLayout.addView(view_aftertomorrow);
		myLayout.addView(getTextView("温度:" + p.getTemperature3()));
		myLayout.addView(getTextView("天气:" + p.getWeather3()));
		myLayout.addView(getTextView("风况:" + p.getWind_Detail3()));
		myLayout.addView(getTextView(""));
		
	//	tvTips.setText(p.getTip_Detail());
		
		sctvTips = (VerticalScrollTextView)findViewById(R.id.sctvTips);
		sctvTips.setText(p.getTip_Detail());
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// 全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.weather);

	//	tvTips = (TextView) findViewById(R.id.tvTips);
		cityname = (EditText) findViewById(R.id.cityname);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				theCityName = cityname.getText().toString();
				if (null == theCityName || "".equals(theCityName)) {
					Toast.makeText(getApplicationContext(), "请填写要查询的城市",
							duration).show();
					return;
				}
				AndroidTools tools = new AndroidTools();
				if (!tools.isWifiConnected(getApplication())) {
					Toast.makeText(getApplicationContext(), "请先连接Wifi网络！",
							duration).show();
					return;
				}
				progressDialog = new ProgressDialog(WeatherActivity.this);
				progressDialog.setMessage("正在获取天气数据...");
				progressDialog.show();
				new Thread(new GetWeatherTask(theCityName)).start();
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(WeatherActivity.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private class GetWeatherTask implements Runnable {

		String theCityName = "";

		public GetWeatherTask(String theCityName) {
			super();
			this.theCityName = theCityName;
		}

		@Override
		public void run() {
			try {
				result = getRemoteInfo(theCityName);
				handler.obtainMessage(1).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				handler.obtainMessage(-1).sendToTarget();
			}
		}

	}

	private TextView getTextView(String content) {
		TextView tv = new TextView(this);
		tv.setTextSize(20);
		tv.setTextColor(Color.parseColor("#ffffff"));
		tv.setText(content);
		return tv;
	}

	/**
	 * 通过webservice获取城市天气
	 * 
	 * @param theCityName
	 * @return
	 */
	public String getRemoteInfo(String theCityName) {
		// http://WebXml.com.cn/getWeatherbyCityName
		// 命名空间
		String nameSpace = "http://WebXml.com.cn/";
		// 调用的方法名称
		//String methodName = "getWeatherbyCityName";
		String methodName = "getWeatherbyCityNamePro"; // (修改1)
		
		// EndPoint通常是将WSDL地址末尾的"?WSDL"去除后剩余的部分
		String endPoint = "http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx";
		// SOAP Action通常为命名空间 + 调用的方法名称
		//String soapAction = "http://WebXml.com.cn/getWeatherbyCityName";
		String soapAction = "http://WebXml.com.cn/getWeatherbyCityNamePro";// (修改2)
		
		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// 设置需调用WebService接口需要传入的两个参数mobileCode、userId
		rpc.addProperty("theCityName", theCityName);

		// （修改3）
		//使用Webservice会员的userID访问数据，获取天气的接口成为getWeatherbyCityNamePro
		String theUserID = "9c150d697d4b4021b0477cd3a63de1df"; //iParking账户,密码:iParking123
		rpc.addProperty("theUserID", theUserID);

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.bodyOut = rpc;
		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;
		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;

		return object.getProperty(0).toString();
	}

}
