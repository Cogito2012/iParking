package com.example.iparking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.iparking.utils.ConnectSever;
import com.example.iparking.utils.DialogUtil;
import com.example.iparking.utils.POIDatabaseHelper;

public class POIListActivity extends ActionBarActivity {

	public static Activity temp;
	public static boolean ifEdit=false;
	public static boolean checkall=false;
	public static ArrayList<POIListItem> poilist_item;
	private static POIListAdapter pa;
	public Button sync_Button;
	public Button check_Button;
	public Button del_Button;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    // 去除标题栏    
	    //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		temp=this;

		setContentView(R.layout.poilist);
		
		ActionBar ab = getActionBar();
		ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.background6));
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setTitle("返回");

        FragmentManager fm = getFragmentManager();
        POIListFragment poilistFragment =
        		(POIListFragment)fm.findFragmentById(R.id.POIList_Fragment);
        //poilistFragment.getListView().setDivider(null);
        poilist_item=new ArrayList<POIListItem>();
        int resID = R.layout.poilistitem;
        pa=new POIListAdapter(POIListActivity.this, resID, poilist_item);
        poilistFragment.setListAdapter(pa);
        
        LoadItemFromSQLite();

        check_Button=(Button)findViewById(R.id.check_button);
        del_Button=(Button)findViewById(R.id.del_button);
        check_Button.setVisibility(View.INVISIBLE);
        del_Button.setVisibility(View.INVISIBLE);
        sync_Button=(Button)findViewById(R.id.sync_button);
        check_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(POIListActivity.checkall==false){
					POIListActivity.checkall=true;
					pa.notifyDataSetChanged();
				}
				else {
					POIListActivity.checkall=false;
					pa.notifyDataSetChanged();
				}

			}
        	
        });
        sync_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogUtil.showDialog(POIListActivity.this, "真的要进行同步吗？", false);

			}
        	
        });
        del_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DialogUtil.showDialog(POIListActivity.this, "真的要进行同步吗？", false);
				pa.notifyDataSetChanged();
				ArrayList<Integer> ai = new ArrayList<Integer>();
				for(POIListItem p : poilist_item){
					CheckBox ck1=(CheckBox)pa.getView(pa.getPosition(p),null , null).
							findViewById(R.id.checkBox1);
					//if(ck1.isChecked()){
					if(p.ifcheck){
						Integer it=Integer.valueOf(pa.getPosition(p));
						ai.add(it);
					}
				}

				int inde=0;
				if(ai!=null)for(Integer i:ai){
					//Toast.makeText(POIListActivity.this, i.intValue()+"!", Toast.LENGTH_SHORT).show();
					String _id=poilist_item.get(i.intValue()-inde).id;
					POIDatabaseHelper database_helper = new POIDatabaseHelper(POIListActivity.this);
			        SQLiteDatabase db = database_helper.getWritableDatabase();
			        String sql = "delete from poi where id='"+_id+"'";//删除操作的SQL语句
			        db.execSQL(sql);//执行删除操作
			        
					poilist_item.remove(i.intValue()-inde);
					POIListAdapter.ai.remove(i);
					inde++;
					
				}
				POIListAdapter.ai.clear();
				pa.notifyDataSetChanged();

			}
        	
        });
        

	}

	private void LoadItemFromSQLite() {
		// TODO Auto-generated method stub
		POIDatabaseHelper database_helper = new POIDatabaseHelper(POIListActivity.this);
        SQLiteDatabase db = database_helper.getWritableDatabase();//这里是获得可写的数据库 
        Cursor cursor = db.query("poi", new String[]{"id", "address", "lng","lat"}, null, null,  null, null, null);
        while(cursor.moveToNext()) {
        	onNewItemAdded(cursor.getString(cursor.getColumnIndex("address")),
        			cursor.getString(cursor.getColumnIndex("id")));
        }
	}
	//上传
	public static void uploadall(Context ctx){
		POIDatabaseHelper database_helper = new POIDatabaseHelper(ctx);
        SQLiteDatabase db = database_helper.getWritableDatabase();//这里是获得可写的数据库 
        final Cursor cursor = db.query("poi", new String[]{
        		"id", "address", "type", "lng", "lat","username"}, null, null,  null, null, null);
        Thread t = new Thread(new Runnable() {
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
        		ConnectSever cs=new ConnectSever();
        		int i=0;
				while(cursor.moveToNext()) {
				int re=cs.syncPOI(i,cursor.getString(cursor.getColumnIndex("id")), 
						cursor.getString(cursor.getColumnIndex("address")),
						cursor.getString(cursor.getColumnIndex("type")),
						cursor.getDouble(cursor.getColumnIndex("lng")),
						cursor.getDouble(cursor.getColumnIndex("lat")),
						cursor.getString(cursor.getColumnIndex("username")));
				if(cursor.isLast()){
					DialogUtil.MyDialog.dismiss();
					}
				i++;
				}
			  }
        	});
        t.start();
        //}
	}

	//新增
	public static void onNewItemAdded(String name,String id) {
		// TODO Auto-generated method stub
			  POIListItem newTodoItem = new POIListItem(name,id);
			  poilist_item.add(0, newTodoItem);
			  pa.notifyDataSetChanged();
		
	}

	//清除所有
	public static void clearItem() {
		// TODO Auto-generated method stub
			poilist_item.clear();
			pa.notifyDataSetChanged();
		
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
	  if (keyCode == KeyEvent.KEYCODE_MENU) {
		  return true;
	  }  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
	  {
		 POIListActivity.ifEdit=false;
		 POIListActivity.checkall=false;
		 pa.notifyDataSetChanged();
         return super.onKeyDown(keyCode, event); 
	  }
	  return true; 
	 }
	 @Override  
	 public boolean onCreateOptionsMenu(Menu menu) {  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.main1, menu);  
	    return super.onCreateOptionsMenu(menu); 
	    } 
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	      Intent intent = new Intent(this, MainActivity.class);
	      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      startActivity(intent);
	      break;
	    case R.id.action_edit:  
	    	//Toast.makeText(getApplicationContext(), "do not touch me", Toast.LENGTH_SHORT).show();
	    	if(POIListActivity.ifEdit==false){
	    		POIListActivity.ifEdit=true;
	            check_Button.setVisibility(View.VISIBLE);
	            del_Button.setVisibility(View.VISIBLE);
	    	}
	    	else{
	    		POIListActivity.ifEdit=false;
	    		POIListActivity.checkall=false;
	            check_Button.setVisibility(View.INVISIBLE);
	            del_Button.setVisibility(View.INVISIBLE);
	    	}
	    	pa.notifyDataSetChanged();
	    	break;
	    	
	    default:
	      break;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	  public static void finishTheAct(String id){
		  Intent intent=new Intent(temp,AddPOIActivity.class);
		  AddPOIActivity.showone=true;
		  intent.putExtra("_id", id);
		  temp.startActivity(intent);
		  temp.finish();
	  }
}
