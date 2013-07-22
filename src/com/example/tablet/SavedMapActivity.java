package com.example.tablet;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.example.data.Travel;
import com.example.data.TravelPoint;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class SavedMapActivity extends Activity {

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_map);
		
		final Travel travel = (Travel) getIntent().getExtras().getSerializable("travel");

		// Prendo il fragment della mappa
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view))
				.getMap();

		// Setto il tipo della mappa
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		mMap.setInfoWindowAdapter(new ImageInfoWindowAdapter(this));
		
		int count = 0;
		int total = travel.getNumberOfPointsSoFar();
		
		LatLng prevPos = null;
		for(TravelPoint p : travel.getPoints()) {
			LatLng pos = p.getPosition();
			
			if (count == 0) {
				// Partenza
				mMap.addMarker(new MarkerOptions().position((pos)).title("Partenza"));
				
				// Zoom e posizione iniziale
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 12);
				mMap.animateCamera(cameraUpdate);
			}
			else if (count == total - 1) {
				// Arrivo
				mMap.addMarker(new MarkerOptions().position((pos)).title("Fine"));
			}
			else {
				if (p.getPhoto() != null) {
					// Photo
					mMap.addMarker(new MarkerOptions().position((pos)).title("Photo:" + p.getPhoto()));
				}
				else {
					// Normal point
					mMap.addMarker(new MarkerOptions()
					.position(pos)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.dotlocation)));
				}
			}
			
			if (prevPos != null) {
				PolylineOptions path = new PolylineOptions();
				path.add(prevPos);
				path.add(pos);
				
				path.color(Color.BLUE);
				path.width(2);

				mMap.addPolyline(path);
			}
			
			prevPos = pos;
			count ++;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_map, menu);
		return true;
	}

}
