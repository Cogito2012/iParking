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
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// ȫ��
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loading);

		ImageView loadImage = (ImageView) findViewById(R.id.imageload);//����ͼƬ
		at = (AndroidTools)getApplicationContext();
		sp = this.getSharedPreferences("passwordFile", MODE_APPEND);
		
		boolean isFisrtStart = sp.getBoolean("FirstStart", true);
		//�����û��Ƿ��½��δ��½���Ƚ�������Ķ���������ֱ������������
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2000);
		loadImage.setAnimation(animation);

		//����Ч��
		boolean hasLogout = sp.getBoolean("hasLogout", false);
		if (isFisrtStart||hasLogout) //APP״̬���״ε�½�������Ѿ�ע����
		{
			animation.setAnimationListener(aniListener1);  //��������LoginActivity
		} else {          
			animation.setAnimationListener(aniListener2);  //��������MainActivity
		}
	
	}
	//�����½����Ķ�������
	public AnimationListener aniListener1 = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			//��������
			startActivity(new Intent(Loading.this, LoginActivity.class));
			Loading.this.finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}
	};
	
	//����������Ķ�������
	public AnimationListener aniListener2 = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			//��SharedPreferences�˻���Ϣ��ȡ��APP
			String strName = sp.getString("name", "");
			String strPassWords = sp.getString("passwords", "");
			at.setAccount(strName, strPassWords);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			//��������
			startActivity(new Intent(Loading.this, MainActivity.class));
			Loading.this.finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}
	};
}