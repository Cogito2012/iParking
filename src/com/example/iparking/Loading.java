package com.example.iparking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class Loading extends ActionBarActivity {

	private AndroidTools at;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// 全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loading);

		ImageView loadImage = (ImageView) findViewById(R.id.imageload);//设置图片
		at = (AndroidTools)getApplicationContext();
		sp = this.getSharedPreferences("passwordFile", MODE_APPEND);
		
		boolean isFisrtStart = sp.getBoolean("FirstStart", true);
		//依据用户是否登陆，未登陆则先进入下面的动画，否则直接启动主界面
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2000);
		loadImage.setAnimation(animation);

		//动画效果
		boolean hasLogout = sp.getBoolean("hasLogout", false);
		if (isFisrtStart||hasLogout) //APP状态：首次登陆，或者已经注销过
		{
			animation.setAnimationListener(aniListener1);  //动画进入LoginActivity
		} else {          
			animation.setAnimationListener(aniListener2);  //动画进入MainActivity
		}
	
	}
	//进入登陆界面的动画监听
	public AnimationListener aniListener1 = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			//动画结束
			startActivity(new Intent(Loading.this, LoginActivity.class));
			Loading.this.finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}
	};
	
	//进入主界面的动画监听
	public AnimationListener aniListener2 = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			//将SharedPreferences账户信息获取到APP
			String strName = sp.getString("name", "");
			String strPassWords = sp.getString("passwords", "");
			at.setAccount(strName, strPassWords);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			//动画结束
			startActivity(new Intent(Loading.this, MainActivity.class));
			Loading.this.finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}
	};
}