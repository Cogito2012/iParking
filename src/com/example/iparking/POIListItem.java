package com.example.iparking;

public class POIListItem {
	public String name;
	public double lng;
	public double lat;
	public String id;
	public boolean ifcheck=false;
	

	public POIListItem(String _name,double _lng,double _lat){
		this.name=_name;
		this.lng=_lng;
		this.lat=_lat;
	}
	public POIListItem(String _name,String _id){
		this.name=_name;
		this.id=_id;
	}
	public POIListItem(String _name){
		this.name=_name;
	}

}
