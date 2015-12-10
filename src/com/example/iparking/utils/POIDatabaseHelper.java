package com.example.iparking.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
//�������ݿ�
public class POIDatabaseHelper extends SQLiteOpenHelper {

	private static int  VERSON = 1;//Ĭ�ϵ����ݿ�汾

	/**
	 * DatabaseHelper�������Լ��Ĺ��캯��
	 * @param context��һ������Ϊ���౾��
	 * @param name�ڶ�������Ϊ���ݿ������
	 * @param factory���������������������α����ģ�����һ������Ϊnull
	 * @param version���ĸ�����Ϊ���ݿ�汾��
	 */
	public POIDatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * DatabaseHelper���캯���򻯰�
	 * ���ݿ�Ĭ��Ϊpoi.db
	 * @param context��һ������Ϊ���౾��
	 */
    public POIDatabaseHelper(Context context){
        this(context, "poi.db", null, VERSON);
    }

	//�ú��������ݿ��һ�α�����ʱ����
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        System.out.println("create a sqlite database");
        //execSQL()Ϊִ�в��������SQL��䣬��˲����е������Ҫ����SQL�﷨,�����Ǵ���һ����
        //varchar(20)��ʾ�ɷ�20���ַ�
        db.execSQL("create table poi("
        		+ "id varchar(25), "//poi�ı�ţ��û�id(6)+ʱ��(14)
        		+ "address varchar(50), "//poi����
        		+ "type char(12),"//poi����
        		+ "lng double, "//����
        		+ "lat double, "//γ��
        		+ "username varchar(8)"
        		+ ")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        System.out.println("update a sqlite database");
		
	}
	

}
