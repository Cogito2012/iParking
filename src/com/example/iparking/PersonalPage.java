package com.example.iparking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.iparking.utils.CircularImage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalPage extends Activity {

	/* 组件 */
	private ImageView switchAvatar;
	private CircularImage faceImage;
	private Button btnback;
	private Button btnLogout;
	private TextView tvName;
	SharedPreferences sp;
	AndroidTools at;

	int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@SuppressLint("CutPasteId")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题
		setContentView(R.layout.personalpage);

		sp = this.getSharedPreferences("passwordFile", MODE_APPEND);

		tvName = (TextView) findViewById(R.id.tv_Name);
		at = (AndroidTools) getApplicationContext();
		tvName.setText(at.getName());

		switchAvatar = (ImageView) findViewById(R.id.face);
		faceImage = (CircularImage) findViewById(R.id.face);
		faceImage.setImageBitmap(at.getHeadImg());

		btnback = (Button) findViewById(R.id.login_reback_btn);
		btnback.setOnClickListener(backListener);

		btnLogout = (Button) findViewById(R.id.Logout);
		btnLogout.setOnClickListener(logoutListener);
		// 设置事件监听
		switchAvatar.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							String state = Environment
									.getExternalStorageState();
							if (state.equals(Environment.MEDIA_MOUNTED)) {
								startActivityForResult(intentFromCapture,
										CAMERA_REQUEST_CODE);
							} else {
								Toast.makeText(getBaseContext(), "存储卡不可用！",
										Toast.LENGTH_LONG).show();
							}
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				FileOutputStream b = null;
				File file = new File("/sdcard/iParking/imgHead/");
				file.mkdirs();// 创建文件夹
				String fileName = "/sdcard/iParking/imgHead/head.jpg";
				try {
					b = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				at.setHeadimg(bitmap); // 头像信息保存到全局变量
				faceImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
				Toast.makeText(getBaseContext(),
						"图片已保存在/sdcard/iParking/imgHead/head.jpg",
						Toast.LENGTH_LONG).show();
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}

	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			at.setHeadimg(photo);
			faceImage.setImageBitmap(photo);
		}
	}

	private View.OnClickListener backListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startActivity(new Intent(PersonalPage.this, MainActivity.class));
			if (version > 5) {
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		}
	};

	/**
	 * 退出当前账号事件
	 */
	private View.OnClickListener logoutListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			sp.edit().putBoolean("hasLogout", true).commit();
			startActivity(new Intent(PersonalPage.this, LoginActivity.class));
			if (version > 5) {
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
			PersonalPage.this.finish();
		}
	};
	/*
	
*/
}
