package com.example.iparking;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidTools extends Application {
	private String name;
	private String passwords;
	public Bitmap headImg;
	public int lineNumber = 0;

	/**
	 * ��ȡ�û���
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getPasswords() {
		return passwords;
	}

	/**
	 * ��ȡͷ��
	 * 
	 * @return
	 */
	public Bitmap getHeadImg() {
		return headImg;
	}

	/**
	 * �����˻���Ϣ
	 * 
	 * @param str
	 */
	public void setAccount(String strName, String strPasswords) {
		this.name = strName;
		this.passwords = strPasswords;
	}

	/**
	 * ����ͷ��
	 * 
	 * @param image
	 */
	public void setHeadimg(Bitmap bmp) {
		this.headImg = bmp;
	}
	
	/**
	 * �ж�WiFi�Ƿ���������
	 * 
	 * @param context
	 * @return
	 */
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * �ж����������Ƿ�����
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * �ж�GPS�Ƿ���
	 * 
	 * @param context
	 * @return true ��ʾ����
	 */
	public boolean isGPSOpen(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}

	/**
	 * ��WGS84��γ��ͨ��MercatorͶӰת����Web�ٶȵ�ͼƽ������
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public double[] LonLat2Mercator(double longitude, double latitude) {
		double PI = Math.PI;
		double[] result = new double[2];
		double x = longitude * 20037508.342789 / 180;
		double y = Math.log(Math.tan((90 + latitude) * PI / 360)) / (PI / 180);
		y = y * 20037508.342789 / 180;
		result[0] = x;
		result[1] = y;
		return result;
	}

	/**
	 * ���ı��ļ��ж�ȡ�ַ���
	 * 
	 * @param fileName
	 * @return String
	 */
	public String readFileSdcard(String fileName) {
		String res = "/";
		try {

			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return res;
	}

	/**
	 * ������д���ı��ļ�
	 * 
	 * @param fileName
	 * @param message
	 */
	public void writeFileSdcard(String fileName, String message) {
		try {
			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setAccount("_Default", "_Default");
	}

}
