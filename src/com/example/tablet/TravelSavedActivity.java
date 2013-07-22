package com.example.tablet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.data.Travel;

public class TravelSavedActivity extends Activity {
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel_saved);
	
		TextView title = (TextView) findViewById(R.id.saved_title);
		TextView date = (TextView) findViewById(R.id.saved_data);
		EditText km = (EditText) findViewById(R.id.saved_myKM);
		
		final Travel travel = (Travel) getIntent().getExtras().getSerializable("travel");
		
		title.setText(travel.getName());
		
		String datastr = new SimpleDateFormat("dd/MM/yyyy").format(travel.getData());
		date.setText(datastr);
		DecimalFormat df = new DecimalFormat("#.##");
		km.setText(df.format((double)travel.getDistance()/1000.0));
		
		Button open_map = (Button) findViewById(R.id.saved_btn_view_map);
		open_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TravelSavedActivity.this, SavedMapActivity.class);
				intent.putExtra("travel", travel);
				
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_saved, menu);
		return true;
	}

}
