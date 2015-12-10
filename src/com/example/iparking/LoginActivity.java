package com.example.iparking;
import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
public class LoginActivity extends ActionBarActivity {

	
	private CheckBox savePasswordCB;
	private SharedPreferences sp;
	private String cardNumStr;
	private String passwordStr;

	private AutoCompleteTextView username;//�û���
	private EditText password;  //����
	
	public int a=0;
	AndroidTools at;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//ȫ��
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		at = (AndroidTools) getApplicationContext();
		setContentView(R.layout.login);
		//�����ļ���
//		File primaryfolder = new File(getString(R.string.primary_path));
		File usersfolder = new File(getString(R.string.user_path));
		if(usersfolder.exists()==false){
//			primaryfolder.mkdirs();
			usersfolder.mkdirs();
		}
		
		//����ؼ�id��ȡ
		username = (AutoCompleteTextView)findViewById(R.id.username_edit);  
        password = (EditText)findViewById(R.id.password_edit); 
	  	Button sign = (Button)findViewById(R.id.signin_button);
	  	savePasswordCB = (CheckBox) findViewById(R.id.savepasswordcb);
	 	Button logon = (Button)findViewById(R.id.logon_button);
	  	
	  	sp = this.getSharedPreferences("passwordFile", MODE_APPEND);
	  	savePasswordCB.setChecked(true);// Ĭ��Ϊ��ס����
	  	if(savePasswordCB.isChecked()){  //��ʾ����
	  		username.setText(sp.getString("name", ""));
	  		password.setText(sp.getString("passwords", ""));
	  	}

	  	username.setThreshold(1);// ����1����ĸ�Ϳ�ʼ�Զ���ʾ
	  	password.setInputType(InputType.TYPE_CLASS_TEXT
	  			| InputType.TYPE_TEXT_VARIATION_PASSWORD);


	  	username.addTextChangedListener(new TextWatcher(){
	  		@Override
	  		public void onTextChanged(CharSequence s, int start, int before, int count){
	  			 String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()���ص����ж��ٸ���ֵ��
	  			 allUserName = sp.getAll().keySet().toArray(new String[0]);
	  			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	  					LoginActivity.this,
	  					android.R.layout.simple_dropdown_item_1line,
	  					allUserName);
	  			username.setAdapter(adapter);// ��������������
	  		}
	  		
	  		@Override
	  		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
	  			// TODO Auto-generated method stub
	  		}
	  		
  			@Override
  			public void afterTextChanged(Editable s) {
  				// TODO Auto-generated method stub
  				password.setText(sp.getString(username.getText().toString(), ""));// �Զ���������
  			}
	  	});

	  	//��½
	 	sign.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				cardNumStr = username.getText().toString();
				passwordStr = password.getText().toString();
			
				String TXTname=at.readFileSdcard(getString(R.string.NAME_path));
	            String TXTpassword=at.readFileSdcard(getString(R.string.PASS_path));
	            
	           
            	if(TXTname.equals(cardNumStr)&&TXTpassword.equals(passwordStr)){  
            		if (savePasswordCB.isChecked()) {// ��½�ɹ��ű����˺�����
	   	        		sp.edit().putString("name", cardNumStr).commit();
	   	        		sp.edit().putString("passwords", passwordStr).commit();
	   	        		at.setAccount(cardNumStr, passwordStr);
	   	        		//��½�ɹ�����ͬʱ����APP״̬�����״ε�½�Լ�δע��
	   	 			    sp.edit().putBoolean("FirstStart", false).commit();
	   	 			    sp.edit().putBoolean("hasLogout", false).commit();
   	                }
   	               startActivity(new Intent(LoginActivity.this, MainActivity.class)); 
   	               LoginActivity.this.finish();
   	            }
            	else 
	                //��¼��Ϣ����ͨ��Toast��ʾ��ʾ��Ϣ  
	                Toast.makeText(LoginActivity.this,"�û���¼��Ϣ����" , Toast.LENGTH_SHORT).show();
		    }
		});  
	 	/////////////////////////////////////////////////////////////////////////
	 	logon.getBackground().setAlpha(0);///////////////////////////////////
	 	logon.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {				
              // intent = new Intent(LoginActivity.this, mainactivity.class);  
               startActivity(new Intent(LoginActivity.this, LogonActivity.class)); 
               LoginActivity.this.finish();
		}});  
	
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//�����ǰ��Login��������ע�������ģ���MainActivity���ڻ�������˳�ʱҪ���������
			if(sp.getBoolean("hasLogout", false)==true){ 
				MainActivity main = new MainActivity();
				main.MainActivity.finish();
			}
			System.exit(0);
		}
		return false;
	}
	
}
