package com.example.tablet;

import java.text.SimpleDateFormat;

public class EventItem extends TitledItem {

	private SavedEvent object;
		
	public EventItem(SavedEvent obj) {
		super();
		
		this.object = obj;
	}
	
	@Override
	public String getTitle() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(object.getDate());
	}
	
	@Override
	public String getContent() {
		return object.getName();
	}

}
