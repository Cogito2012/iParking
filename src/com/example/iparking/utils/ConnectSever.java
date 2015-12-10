package com.example.iparking.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ConnectSever {
	
	public ConnectSever(){
		
	}

	//"id", "address", "type", "lng", "lat","username"
	public int syncPOI(int inde,String id,String address,String type,
			double lng,double lat,String username){
		if(inde==0){try {
			URL url=new URL("http://rssr.iparking.asia/iPr/iPr_poi.php?poi_delall=yes&"
					+ "poi_user=liuyaqi");
			HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
			InputStreamReader isr=new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br=new BufferedReader(isr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		String result="";
		int code=-1;
		address=Base64Util.getBASE64(address);
		 try {
			URL url=new URL("http://rssr.iparking.asia/iPr/iPr_poi.php?"
			 		+ "poi_id="+id
			 		+"&poi_addre="+address
			 		+"&poi_type="+type
			 		+"&poi_lng="+lng
			 		+"&poi_lat="+lat
			 		+"&poi_user="+username);
			Log.v("URL", url.toString());
			HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
            InputStreamReader isr=new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br=new BufferedReader(isr);
            result=br.readLine(); 
          //对获得的json数据进行解析
            try {
				JSONObject object=new JSONObject(result);
	            code=object.getInt("code");
	            System.out.print(code+"~!!!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}

	public JSONObject getJSON(String url,List<NameValuePair> params){
		InputStream is=null;
		String json = "";
		JSONObject jObj = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is=httpEntity.getContent();   
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
                
		return jObj;
	}

}
