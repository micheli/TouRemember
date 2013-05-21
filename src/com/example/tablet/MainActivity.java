package com.example.tablet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	// Dichiaro la mappa
	private GoogleMap mMap;
	private Boolean LocationChange = false;
	LatLng myPosition;
	LocationManager mlocManager;
	public LatLng lastPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Prendo il fragment della mappa
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// Setto il tipo della mappa
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		// Uso il LocationManager per ottenere la posizione gps
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
		// Prendo il nome del miglior provider
		String provider = mlocManager.getBestProvider(criteria, true);

		// Ottengo l'ultima posizione conosciuta
		Location location = mlocManager.getLastKnownLocation(provider);

		// Latitudine
		double latitude = location.getLatitude();
		// Longitudine
		double longitude = location.getLongitude();
		// setto la mia posizione
		myPosition = new LatLng(latitude, longitude);

		// Ricavo l'ultima posizione nota e mi sposto in quella posizione
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				(myPosition), 12);
		// con un'animazione
		mMap.animateCamera(cameraUpdate);
		mMap.addMarker(new MarkerOptions().position(myPosition).title(
				"Ultima posizione conosciuta"));

		findViewById(R.id.start_travel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Gps Disabled",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			LocationChange = true;
			loc.getLatitude();
			loc.getLongitude();
			lastPos = new LatLng(loc.getLatitude(), loc.getLongitude());
			LatLng MyPos = new LatLng(loc.getLatitude(), loc.getLongitude());
			mMap.addMarker(new MarkerOptions()
					.position(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.title("Sono qui!")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.dotlocation)));

			// appena trovo la posizione da gps mi sposto in quella posizione
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(MyPos);
			// con un'animazione
			mMap.animateCamera(cameraUpdate);

			// bottone

			findViewById(R.id.start_travel).setOnClickListener(
					new OnClickListener() {
						private Button b;

						@Override
						public void onClick(View v) {
							int cont = 1;
							if (cont == 1) {
								mMap.addMarker(new MarkerOptions().position(
										(lastPos)).title("Partenza"));
								cont++;
							} else {
								b = (Button) findViewById(R.id.start_travel);
								b.setText("fine");

							}
						}
					});
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(),
					"Gps Disabled, turn on for start a travel",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(),
					"Attendi mentre ottengo la tua posizione",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (LocationChange) {
			switch (item.getItemId()) {
			case R.id.take_photo:
				Uri fileUri;
				final int MEDIA_TYPE_IMAGE = 1;
				final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

				
				
				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //
				// create a file to save the image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				// set the image file name

				// start the image capture Intent
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

				mMap.addMarker(new MarkerOptions().position((lastPos)).title(
						"foto"));

				return true;
			}

		}

		return false;
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;


	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");}
//	    } else if(type == MEDIA_TYPE_VIDEO) {
//	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//	        "VID_"+ timeStamp + ".mp4");
//	    } 
	        else {
	        return null;
	    }

	    return mediaFile;
	}
}
