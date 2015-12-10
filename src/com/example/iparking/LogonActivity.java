package com.example.iparking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogonActivity extends ActionBarActivity {

	private EditText username;//�û���
	private EditText password1;//����
	private EditText password2;//ȷ������
	private AndroidTools at;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// ȫ��
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		at = (AndroidTools) getApplicationContext();
		setContentView(R.layout.logon);

		sp = this.getSharedPreferences("passwordFile", MODE_APPEND);
		
		Button sign = (Button) findViewById(R.id.signin_button);
		sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				username = (EditText) findViewById(R.id.username_edit);
				password1 = (EditText) findViewById(R.id.password_edit);
				password2 = (EditText) findViewById(R.id.password_edit2);

				String Name = username.getText().toString();
				String Password1 = password1.getText().toString();
				String Password2 = password2.getText().toString();

				if (Name.length() == 0 || Password1.length() ==0 ) { //��Ϣ�ǿ�
					Toast.makeText(LogonActivity.this, "�û��������벻��Ϊ�գ�",
							Toast.LENGTH_LONG).show();
					return;
				}
				else if(Password1.length() < 6)  //���볤�ȿ���
				{
					Toast.makeText(LogonActivity.this, "���볤������6λ��",
							Toast.LENGTH_LONG).show();
					return;
				}
				else if (Password1.equals(Password2)) {
					at.writeFileSdcard(getString(R.string.NAME_path), Name);
					at.writeFileSdcard(getString(R.string.PASS_path), Password1);
					//ע��ɹ���Ҫ���õ�ǰAPPΪ��ע��
					sp.edit().putBoolean("hasLogout", true).commit();
					Toast.makeText(LogonActivity.this, "ע��ɹ������¼",
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(LogonActivity.this,
							LoginActivity.class));
					LogonActivity.this.finish();
				} else {
					Toast.makeText(LogonActivity.this, "��������������",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
