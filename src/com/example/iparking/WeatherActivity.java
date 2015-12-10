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
				Toast.makeText(getApplicationContext(), "��ȡ����ʧ��", duration)
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
		
		TextView view_today = getTextView("��������");
		view_today.setTextColor(Color.YELLOW);
		myLayout.addView(view_today);
		myLayout.addView(getTextView("ʱ��:" + p.getTime()));
		myLayout.addView(getTextView("�¶�:" + p.getTemperature1()));
		myLayout.addView(getTextView("����:" + p.getWeather1()));
		myLayout.addView(getTextView("���:" + p.getWind_Detail1()));

		TextView view_tomorrow = getTextView("��������");
		view_tomorrow.setTextColor(Color.YELLOW);
		myLayout.addView(view_tomorrow);
		myLayout.addView(getTextView("�¶�:" + p.getTemperature2()));
		myLayout.addView(getTextView("����:" + p.getWeather2()));
		myLayout.addView(getTextView("���:" + p.getWind_Detail2()));
		
		TextView view_aftertomorrow = getTextView("��������");
		view_aftertomorrow.setTextColor(Color.YELLOW);
		myLayout.addView(view_aftertomorrow);
		myLayout.addView(getTextView("�¶�:" + p.getTemperature3()));
		myLayout.addView(getTextView("����:" + p.getWeather3()));
		myLayout.addView(getTextView("���:" + p.getWind_Detail3()));
		myLayout.addView(getTextView(""));
		
	//	tvTips.setText(p.getTip_Detail());
		
		sctvTips = (VerticalScrollTextView)findViewById(R.id.sctvTips);
		sctvTips.setText(p.getTip_Detail());
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// ȫ��
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
					Toast.makeText(getApplicationContext(), "����дҪ��ѯ�ĳ���",
							duration).show();
					return;
				}
				AndroidTools tools = new AndroidTools();
				if (!tools.isWifiConnected(getApplication())) {
					Toast.makeText(getApplicationContext(), "��������Wifi���磡",
							duration).show();
					return;
				}
				progressDialog = new ProgressDialog(WeatherActivity.this);
				progressDialog.setMessage("���ڻ�ȡ��������...");
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
	 * ͨ��webservice��ȡ��������
	 * 
	 * @param theCityName
	 * @return
	 */
	public String getRemoteInfo(String theCityName) {
		// http://WebXml.com.cn/getWeatherbyCityName
		// �����ռ�
		String nameSpace = "http://WebXml.com.cn/";
		// ���õķ�������
		//String methodName = "getWeatherbyCityName";
		String methodName = "getWeatherbyCityNamePro"; // (�޸�1)
		
		// EndPointͨ���ǽ�WSDL��ַĩβ��"?WSDL"ȥ����ʣ��Ĳ���
		String endPoint = "http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx";
		// SOAP Actionͨ��Ϊ�����ռ� + ���õķ�������
		//String soapAction = "http://WebXml.com.cn/getWeatherbyCityName";
		String soapAction = "http://WebXml.com.cn/getWeatherbyCityNamePro";// (�޸�2)
		
		// ָ��WebService�������ռ�͵��õķ�����
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// ���������WebService�ӿ���Ҫ�������������mobileCode��userId
		rpc.addProperty("theCityName", theCityName);

		// ���޸�3��
		//ʹ��Webservice��Ա��userID�������ݣ���ȡ�����Ľӿڳ�ΪgetWeatherbyCityNamePro
		String theUserID = "9c150d697d4b4021b0477cd3a63de1df"; //iParking�˻�,����:iParking123
		rpc.addProperty("theUserID", theUserID);

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.bodyOut = rpc;
		// �����Ƿ���õ���dotNet������WebService
		envelope.dotNet = true;
		// �ȼ���envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// ����WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ��ȡ���ص�����
		SoapObject object = (SoapObject) envelope.bodyIn;

		return object.getProperty(0).toString();
	}

}
