package com.example.iparking;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import android.app.AlertDialog.Builder;

public class MainActivity extends ActionBarActivity {

    static Activity MainActivity;
	private static final String LTAG = MainActivity.class.getSimpleName();
	private SDKReceiver mReceiver;
	String sosNumber;

	int flag0 = 0;
	int flag1 = 0;
	int itemsChecked = 0;
	EditText etNumber;
	
	public ImageView mapbutton_lg;
	public ImageButton buttonWeather;
	public ImageButton buttonFind;
	public ImageButton buttonNavigate;
	
	private Locale locale = Locale.SIMPLIFIED_CHINESE;
	private AndroidTools at;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.main);
		
		MainActivity = this;
		at = (AndroidTools)getApplicationContext();
		
		changeBuootnLanguage(getResources().getString(
				R.string.SimplifiedChinese));
		
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		
		//地图收藏入口
		ImageButton Button_poi=(ImageButton)findViewById(R.id.button_poi);
		Button_poi.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AddPOIActivity.class);
				MainActivity.this.startActivity(intent);
			}});
		
		//地图导航入口
		ImageButton Button_navi=(ImageButton)findViewById(R.id.button_navigate);
		Button_navi.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,NavigateActivity.class);
				MainActivity.this.startActivity(intent);
			}});
		
		// 天气
		ImageButton buttonWeather = (ImageButton) findViewById(R.id.button_weather);
		buttonWeather.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,WeatherActivity.class);
				MainActivity.this.startActivity(intent);
			}

		});
		// SOS一键呼救
		ImageButton buttonSOS = (ImageButton) findViewById(R.id.button_sos);
		buttonSOS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent_call = new Intent();
				intent_call.setAction("android.intent.action.CALL");
				intent_call.addCategory("android.intent.category.DEFAULT");
				intent_call.setData(android.net.Uri.parse("tel:" + sosNumber));
				MainActivity.this.startActivity(intent_call);
			}
		});
		// 设置
		ImageButton buttonSet = (ImageButton) findViewById(R.id.button_set);
		buttonSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChooseSetItems();
			}

		});
		// 个人主页
		ImageButton buttonPersonal = (ImageButton) findViewById(R.id.personalpage);
		buttonPersonal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,PersonalPage.class);
				MainActivity.this.startActivity(intent);
			}

		});
		ImageButton Button_dl=(ImageButton)findViewById(R.id.downloadmap);
		Button_dl.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,POIListActivity.class);
				MainActivity.this.startActivity(intent);
			}});
		LinearLayout Button_se=(LinearLayout)findViewById(R.id.button_place);
		Button_se.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,CloudSearchActivity.class);
				MainActivity.this.startActivity(intent);
			}});

	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(MainActivity.this,
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置", 
						Toast.LENGTH_LONG).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(MainActivity.this,
						"网络出错", 
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	/**
	 * 对不同的设置项，分别作出相应的响应
	 */
	@SuppressWarnings("deprecation")
	private void ChooseSetItems() {
		String[] items = new String[] {
				this.getText(R.string.language).toString(),
				this.getText(R.string.sosNumber).toString() };

		new AlertDialog.Builder(this)
				.setTitle(R.string.setChoice)
				.setItems(items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							showDialog(0);  //选择语言设置
							break;
						case 1:
							showDialog(1);  //选择SOS号码设置
							break;
						}
					}
				})
				.setNegativeButton(R.string.strcancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:                //语言设置部分
			Builder builder0 = new AlertDialog.Builder(this);
			CharSequence[] items0 = {
					this.getResources().getString(R.string.SimplifiedChinese),
					this.getResources().getString(R.string.TraditionalChinese),
					this.getResources().getString(R.string.English),
					this.getResources().getString(R.string.Arabic) };

			builder0.setTitle(R.string.language);
			builder0.setPositiveButton(R.string.strOK,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// TODO Auto-generated method stub
							SetLanguage(flag0);
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
						}
					});
			return builder0.create();
		case 1:              //SOS号码设置部分
			final CharSequence[] items1 = { "110", "120", "119",
					this.getResources().getString(R.string.selfDefine) };
			Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle(R.string.sosNumber);
			builder1.setPositiveButton(R.string.strOK,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// TODO Auto-generated method stub
							// 根据items1的不同选项（flag1即为选项id），用字符串变量sosNumber保存设置好的号码items1[flag1]
							if (flag1 == 3) {
								showDialog(2);
							} else {
								sosNumber = items1[flag1].toString();
							}
						}
					});
			builder1.setNegativeButton(R.string.strcancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder1.setSingleChoiceItems(items1, itemsChecked,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							flag1 = which;
						}
					});
			return builder1.create();
		case 2:
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle(R.string.selfDefine);
			View view = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.editnumber, null);
			builder2.setView(view);
			etNumber = (EditText) view.findViewById(R.id.editNumber);
			builder2.setPositiveButton(R.string.strOK,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							sosNumber = etNumber.getText().toString();
						}
					});
			builder2.setNegativeButton(R.string.strcancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder2.create();
		}
		return null;
	}
	/**
	 * 根据不同的语言，设置
	 * @param flag
	 */
	public void SetLanguage(int flag){
		String strlocale;
		switch (flag) {
		case 0:
			strlocale = "zh_CN";
			changeLanguage(strlocale);
			break;
		case 1:
			strlocale = "zh_HK";
			changeLanguage(strlocale);
			break;
		case 2:
			strlocale = "en_US";
			changeLanguage(strlocale);
			break;
		case 3:
			strlocale = "ar";
			changeLanguage(strlocale);
			break;
		default:
			strlocale = "zh_CN";
			changeLanguage(strlocale);
			break;
		}
	}
	
	/**
	 * 更换软件的语言
	 * 
	 * @param strlocale
	 */
	private void changeLanguage(String strlocale) {

		Locale locales[] = Locale.getAvailableLocales();
		for (Locale l : locales) {
			if (l.toString().equalsIgnoreCase(strlocale)) {
				this.locale = l;
			}
		}

		Locale.setDefault(locale);// 设置选定的语言
		Configuration config = new Configuration();
		config.locale = locale;

		this.getResources().updateConfiguration(config,
				this.getResources().getDisplayMetrics());
		this.finish();
		this.startActivity(this.getIntent());

		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"com.iparking", Context.MODE_PRIVATE);
		sharedPreferences.edit().putString("locale", locale.toString())
				.commit();

	}

	/**
	 * 改变按钮语言
	 */
	private void changeBuootnLanguage(String strlocale) {
		// R.string.English
		// weatherbutton_lg=(ImageView) findViewById(R.id.imageView4);
		buttonWeather = (ImageButton) findViewById(R.id.button_weather);
		mapbutton_lg = (ImageView) findViewById(R.id.imageView2);
		buttonFind = (ImageButton) findViewById(R.id.button_poi);
		buttonNavigate = (ImageButton) findViewById(R.id.button_navigate);

		if (strlocale.equals("简体中文")) {

			buttonWeather.setImageResource(R.drawable.weatherbutton);
			buttonFind.setImageResource(R.drawable.collectbutton);
			mapbutton_lg.setImageResource(R.drawable.mapbutton_cn);
			buttonNavigate.setImageResource(R.drawable.navibutton);
		}
		if (strlocale.equals("簡體中文")) {

			buttonWeather.setImageResource(R.drawable.weatherbutton_cf);
			mapbutton_lg.setImageResource(R.drawable.mapbutton_cf);
			buttonFind.setImageResource(R.drawable.collectbutton);
			buttonNavigate.setImageResource(R.drawable.navibutton_cf);
		}
		if (strlocale.equals("Simplified Chinese")) {

			buttonWeather.setImageResource(R.drawable.weatherbutton_en);
			mapbutton_lg.setImageResource(R.drawable.mapbutton_en);
			buttonFind.setImageResource(R.drawable.collectbutton_en);
			buttonNavigate.setImageResource(R.drawable.navibutton_en);
		}
		if (strlocale.equals("الصينية المبسطة")) {

			buttonWeather.setImageResource(R.drawable.weatherbutton_ar);
			mapbutton_lg.setImageResource(R.drawable.mapbutton_ar);
			buttonFind.setImageResource(R.drawable.collectbutton_ar);
			buttonNavigate.setImageResource(R.drawable.navibutton_ar);
		}

	}
	
	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isExit = false; // 取消退出
				}
				
			}, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}

}
