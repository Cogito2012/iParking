package com.example.iparking.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
//创建数据库
public class POIDatabaseHelper extends SQLiteOpenHelper {

	private static int  VERSON = 1;//默认的数据库版本

	/**
	 * DatabaseHelper必须有自己的构造函数
	 * @param context第一个参数为该类本身
	 * @param name第二个参数为数据库的名字
	 * @param factory第三个参数是用来设置游标对象的，这里一般设置为null
	 * @param version第四个参数为数据库版本号
	 */
	public POIDatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * DatabaseHelper构造函数简化版
	 * 数据库默认为poi.db
	 * @param context第一个参数为该类本身
	 */
    public POIDatabaseHelper(Context context){
        this(context, "poi.db", null, VERSON);
    }

	//该函数在数据库第一次被建立时调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        System.out.println("create a sqlite database");
        //execSQL()为执行参数里面的SQL语句，因此参数中的语句需要符合SQL语法,这里是创建一个表
        //varchar(20)表示可放20个字符
        db.execSQL("create table poi("
        		+ "id varchar(25), "//poi的编号，用户id(6)+时间(14)
        		+ "address varchar(50), "//poi类型
        		+ "type char(12),"//poi描述
        		+ "lng double, "//经度
        		+ "lat double, "//纬度
        		+ "username varchar(8)"
        		+ ")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        System.out.println("update a sqlite database");
		
	}
	

}
