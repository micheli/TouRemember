package com.example.data;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.example.tablet.GPSToMeter;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="Travels")
public class Travel implements Serializable {

	private static final long serialVersionUID = 8432255958697785460L;

	@DatabaseField(generatedId=true)
	private long idTravel;

	@DatabaseField
	private String name;
	
	@DatabaseField
	private Date data;

	private int distance;
	private List<TravelPoint> points;

	
	public Travel() {
		this.points = new LinkedList<TravelPoint>();
		this.name = null;
		this.data = null;
		this.distance = 0;
		this.idTravel = 0;
	}
	
	public Travel(String name, Date data) {
		super();
		this.name = name;
		this.data = data;
		this.points = new LinkedList<TravelPoint>();
		this.distance = 0;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public long getIdTravel() {
		return idTravel;
	}

	public List<TravelPoint> getPoints() {
		return points;
	}

	public void addPoint(TravelPoint p) {
		points.add(p);
		recomputeDistance();
	}

	private void recomputeDistance() {
		if (points.size() > 1) {
			TravelPoint prec = null;
			distance = 0;
			for (TravelPoint p : points) {
				if (prec != null) {
					double trip = GPSToMeter
							.gps2m(prec.getLatitude(),
									prec.getLongitude(),
									p.getLatitude(),
									p.getLongitude());
					distance += trip;
				}
				prec = p;
			}
		} else {
			distance = 0;
		}
	}
	
	public int getNumberOfPointsSoFar() {
		return points.size();
	}

}
