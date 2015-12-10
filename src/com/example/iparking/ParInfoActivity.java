package com.example.iparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ParInfoActivity extends ActionBarActivity{
	public Button back;
	public Button map;
	public TextView na;
	public TextView pr;
	public TextView re;
	public TextView outsceen;
	public TextView reserve;
	public TextView navigate;
	
	public double lng;
	public double lat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parinfo);
		
		Intent intent = this.getIntent();        //��ȡ���е�intent����   
		Bundle bundle = intent.getExtras();    //��ȡintent�����bundle����   
		final String Name = bundle.getString("Name");  
		final String Price = bundle.getString("Price");  
		String All = bundle.getString("All");  
		final String Remained = bundle.getString("Remained");
		lng=bundle.getDouble("lng");
		lat=bundle.getDouble("lat");
		bundle.clear();

		back=(Button)findViewById(R.id.btnBack);
		map=(Button)findViewById(R.id.btnMap);
		na=(TextView)findViewById(R.id.tvName);
		pr=(TextView)findViewById(R.id.tvPrice);
		re=(TextView)findViewById(R.id.tvRemain);
		outsceen=(TextView)findViewById(R.id.btnOutsceen);
		reserve=(TextView)findViewById(R.id.btnReserve);
		navigate=(TextView)findViewById(R.id.btnGothere);

		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ParInfoActivity.this,CloudSearchActivity.class); 
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
				ParInfoActivity.this.finish();
			}
			
		});
		map.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ParInfoActivity.this,CloudSearchActivity.class); 
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
				ParInfoActivity.this.finish();
			}
			
		});
		na.setText(Name);
		pr.setText("ͣ���۸�Ϊ��"+Price+"Ԫ/Сʱ");
		re.setText("ʣ�೵λ����"+Remained+"/"+All);
		
		//ͣ�����⾰
		outsceen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				Intent intent=new Intent(ParInfoActivity.this,PanoramaActivity.class); 
				Bundle bundle = new Bundle();  //����Bundle����   
				bundle.putDouble("lat", lat); //װ������ 
				bundle.putDouble("lng", lng); //װ������   
				intent.putExtras(bundle);    
				startActivity(intent);
				*/
				ImageView ivOutsceen = (ImageView)findViewById(R.id.outsceen);
				ivOutsceen.setBackgroundResource(R.drawable.outsceen);
			}
		});
		reserve.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ParInfoActivity.this,ReserveActivity.class); 
				Bundle bundle = new Bundle();  //����Bundle����   
				bundle.putString("Name", Name); //װ������ 
				bundle.putString("Price", Price); //װ������   
				bundle.putString("Remained", Remained); //װ������   
				intent.putExtras(bundle);    
				startActivity(intent);
			}
			
		});
		navigate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ParInfoActivity.this,Navigator.class); 
				Bundle bundle = new Bundle();  //����Bundle����   
				bundle.putDouble("lat", lat); //װ������ 
				bundle.putDouble("lng", lng); //װ������   
				intent.putExtras(bundle);    
				startActivity(intent);
			}
			
		});
		
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
}
