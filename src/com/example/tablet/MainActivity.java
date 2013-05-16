package com.example.tablet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	LatLng myPosition;

	// Setto le coordinate base
	public final double lat = 46.0702531;
	public final double lon = 11.1216386;
	LocationManager mlocManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Prendo il fragment della mappa
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// Setto il tipo della mappa
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// Setto le coordinate all' apertura della mappa su Trento
		LatLng myPos = new LatLng(lat, lon);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 12));

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
		// if (location != null) {
		// Latitudine
		double latitude = location.getLatitude();

		// Longitudine
		double longitude = location.getLongitude();
		// setto la mia posizione
		myPosition = new LatLng(latitude, longitude);

		// Ricavo l'ultima posizione nota e mi sposto in quella posizione
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(myPosition);
		// con un'animazione
		mMap.animateCamera(cameraUpdate);
		mMap.addMarker(new MarkerOptions().position(myPosition).title(
				"Ultima posizione conosciuta"));

	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {
		public LatLng lastPos;

		@Override
		public void onLocationChanged(Location loc) {

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
						@Override
						public void onClick(View v) {

							mMap.addMarker(new MarkerOptions().position(
									(lastPos)).title("Partenza"));
							findViewById(R.id.start_travel).setClickable(false);
						}
					});
			// Foto
			findViewById(R.id.foto).setOnClickListener(new OnClickListener() {
				private Uri fileUri;
				// public static final int MEDIA_TYPE_IMAGE = 1;
				private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

				@Override
				public void onClick(View v) {
					// create Intent to take a picture and return control to the
					// calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					// fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //
					// create a file to save the image
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					// set the image file name

					// start the image capture Intent
					startActivityForResult(intent,
							CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					mMap.addMarker(new MarkerOptions().position((lastPos))
							.title("foto"));

				}
			});
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
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

	private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
		float pk = (float) (180 / 3.14169);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1)
				* FloatMath.cos(b2);
		float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1)
				* FloatMath.sin(b2);
		float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}
}
