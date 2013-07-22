package com.example.data;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="TravelPoints")

public class TravelPoint implements Serializable {

	private static final long serialVersionUID = 935316564383950173L;

	@DatabaseField(generatedId=true)
	private long idTravelPoint;
	
	@DatabaseField
	private double latitude;
	
	@DatabaseField
	private double longitude;
	
	@DatabaseField
	private int order;
	
	@DatabaseField(canBeNull=true)
	private String photo;
	
	@DatabaseField(canBeNull = false, foreign=true)
	private Travel travel;
	
	
	public TravelPoint() {
		this.latitude = 0;
		this.longitude = 0;
		this.photo = null;
		this.setTravel(null);
		idTravelPoint = 0;
	}
	
	public TravelPoint(Travel travel, LatLng position, int order) {
		super();
		this.latitude = position.latitude;
		this.longitude = position.longitude;
		this.photo = null;
		this.order = order;
		this.setTravel(travel);
		idTravelPoint = 0;
	}
	
	public TravelPoint(Travel travel, LatLng position, String photo, int order) {
		super();
		this.latitude = position.latitude;
		this.longitude = position.longitude;
		this.photo = photo;
		this.order = order;
		this.setTravel(travel);
		idTravelPoint = 0;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Travel getTravel() {
		return travel;
	}

	public void setTravel(Travel travel) {
		this.travel = travel;
	}

	public long getIdTravelPoint() {
		return idTravelPoint;
	}

	public void setIdTravelPoint(long idTravelPoint) {
		this.idTravelPoint = idTravelPoint;
	}

	public LatLng getPosition() {
		LatLng res = new LatLng(latitude, longitude);
		return res;
	}
	
}
