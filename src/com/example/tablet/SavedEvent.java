package com.example.tablet;

import java.util.Date;


public class SavedEvent {
	private Date date;
	private String name;
	
	
	public SavedEvent(Date date, String name) {
		super();
		this.date = date;
		this.name = name;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
