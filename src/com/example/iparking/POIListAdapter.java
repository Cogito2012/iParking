package com.example.iparking;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class POIListAdapter extends ArrayAdapter<POIListItem> {
	int resource;
	public Context context;
	public static ArrayList<Integer> ai = new ArrayList<Integer>();
	
	public POIListAdapter(Context context, int resource,
			List<POIListItem> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource=resource;
		this.context=context;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout POIListView = null;
		final POIListItem item = getItem(position);
		final String name=item.name;
		final String id=item.id;
		
		if(convertView==null){
			POIListView=new LinearLayout(getContext());
		    String inflater = Context.LAYOUT_INFLATER_SERVICE;
		    LayoutInflater li;
		    li = (LayoutInflater)getContext().getSystemService(inflater);
		    li.inflate(this.resource, POIListView, true);
			
		}else{
			POIListView = (LinearLayout) convertView;
		}
	    TextView nameview = (TextView)POIListView.findViewById(R.id.poiname);
	    final CheckBox ck=(CheckBox)POIListView.findViewById(R.id.checkBox1);
	    ck.setClickable(false);
	    if(item.ifcheck){ck.setChecked(true);}
	    else {ck.setChecked(false);}
	    if(POIListActivity.ifEdit==false){
	    	ck.setVisibility(View.INVISIBLE);
	    	ck.setChecked(false);
	    	item.ifcheck=false;
	    	POIListAdapter.ai.remove(Integer.valueOf(position));
	    }
	    else ck.setVisibility(View.VISIBLE);
	    if(POIListActivity.checkall==true){
	    	ck.setChecked(true);
	    	item.ifcheck=true;
	    	POIListAdapter.ai.add(Integer.valueOf(position));
	    }
	    else {
	    	ck.setChecked(false);
	    	item.ifcheck=false;
	    	POIListAdapter.ai.remove(Integer.valueOf(position));
	    }
	    nameview.setText(name);
	    final LinearLayout lay=(LinearLayout)POIListView.findViewById(R.id.lay); 
	    lay.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(POIListActivity.ifEdit==false){
					POIListActivity.checkall=false;
					POIListActivity.finishTheAct(id);
				}
				else {
					if(ck.isChecked()){
						ck.setChecked(false);
				    	item.ifcheck=false;
				    	POIListAdapter.ai.remove(Integer.valueOf(position));
				    	}
					else {
						ck.setChecked(true);
				    	item.ifcheck=true;
				    	POIListAdapter.ai.add(Integer.valueOf(position));
					}
				}
			}
	    	
	    });
	    ck.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
			    	item.ifcheck=true;
			    	POIListAdapter.ai.add(Integer.valueOf(position));
			    	}
				else {
			    	item.ifcheck=false;
			    	POIListAdapter.ai.remove(Integer.valueOf(position));
				}
			}});
	    if(POIListAdapter.ai.contains(Integer.valueOf(position)))
	    	ck.setChecked(true);
		return POIListView;
	}

}
