package com.example.tablet;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.data.DatabaseHelper;
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

public class MainActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	// Dichiaro la mappa
	private GoogleMap mMap;
	public Boolean locationAvailable;
	public LatLng lastPos;
	public LatLng prevPos;

	private boolean started;
	public ProgressDialog pd;

	private Travel currentTravel;

	private File mediaFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationAvailable = false;
		started = false;

		// The travel is not started until the button is pressed
		currentTravel = null;
		// Prendo il fragment della mappa
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// Setto il tipo della mappa
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// Setto il custom info window per disegnare le foto
		mMap.setInfoWindowAdapter(new ImageInfoWindowAdapter(this));
		// Uso il LocationManager per ottenere la posizione gps
		final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);

		// Creo un criteria Obj per il provider
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

		// Prendo il nome del miglior provider
		String provider = mlocManager.getBestProvider(criteria, true);

		// Ottengo l'ultima posizione conosciuta
		Location location = mlocManager.getLastKnownLocation(provider);

		if ((location != null)||(locationAvailable)) {
			// Latitudine
			double latitude = location.getLatitude();
			// Longitudine
			double longitude = location.getLongitude();
			// setto la mia posizione
			LatLng lastKnownPosition = new LatLng(latitude, longitude);

			// Ricavo l'ultima posizione nota e mi sposto in quella posizione
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					(lastKnownPosition), 12);
			// con un'animazione
			mMap.animateCamera(cameraUpdate);
			mMap.addMarker(new MarkerOptions().position(lastKnownPosition)
					.title("Ultima posizione conosciuta"));
		}

		prevPos = null;

		findViewById(R.id.start_travel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!started) {
							if (mlocManager
									.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
								if (!locationAvailable) {
									pd = ProgressDialog
											.show(MainActivity.this,
													"Aspetto il segnale GPS",
													"Attendi mentre ottengo la tua posizione...",
													true, true);
								}
								currentTravel = new Travel("new travel",
										new Date());
							} else {
								Toast.makeText(getApplicationContext(),
										"Gps Disabilitato", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							AlertDialog.Builder alert = new AlertDialog.Builder(
									MainActivity.this);
							final EditText input = new EditText(
									MainActivity.this);
							alert.setView(input);
							alert.setTitle("Inserisci il nome con cui salvare questo viaggio");
							alert.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											final Travel tosave = currentTravel;
											currentTravel = null;

											AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
												private ProgressDialog pd;

												@Override
												protected void onPreExecute() {
													Editable value = input
															.getText();
													tosave.setName(value
															.toString());

													pd = new ProgressDialog(
															MainActivity.this);
													pd.setTitle("Salvataggio...");
													pd.setMessage("Sto salvando il tuo viaggio...");
													pd.setCancelable(false);
													pd.setIndeterminate(true);
													pd.show();
												}

												@Override
												protected Void doInBackground(
														Void... arg0) {
													DatabaseHelper db = DatabaseHelper
															.getInstance(getApplicationContext());
													try {
														db.createTravel(tosave);
													} catch (SQLException e) {
														Toast.makeText(
																getApplicationContext(),
																"Ops, c'è stato un problema...",
																Toast.LENGTH_SHORT)
																.show();
														e.printStackTrace();
													}
													return null;
												}

												@Override
												protected void onPostExecute(
														Void result) {
													pd.dismiss();

													Button b = (Button) findViewById(R.id.start_travel);
													b.setText("Parti");

													// Reset variables
													locationAvailable = false;
													started = false;
													currentTravel = null;

													finish();
												}
											};
											task.execute((Void[]) null);

										}
									});
							alert.show();
						}
					}
				});
	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {
		// private LatLng newPos;

		@Override
		public void onLocationChanged(Location loc) {
			lastPos = new LatLng(loc.getLatitude(), loc.getLongitude());
			locationAvailable = true;

			if (currentTravel != null) {
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}

				TravelPoint point = new TravelPoint(currentTravel, lastPos,
						currentTravel.getNumberOfPointsSoFar());
				currentTravel.addPoint(point);

				// Add a polyline to trace the path
				if (prevPos != null) {
					PolylineOptions path = new PolylineOptions();
					path.add(prevPos);
					path.add(lastPos);

					path.color(Color.BLUE);
					path.width(2);

					mMap.addPolyline(path);
				}
				prevPos = lastPos;

				if (!started) {
					mMap.addMarker(new MarkerOptions().position((lastPos))
							.title("Partenza"));
					Button b = (Button) findViewById(R.id.start_travel);
					b.setText("Fine");

					started = true;
				} else {

					mMap.addMarker(new MarkerOptions().position(lastPos).icon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.dotlocation)));

				}
			}

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(lastPos);
			// con un'animazione
			mMap.animateCamera(cameraUpdate);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Il GPS non è attivo...",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(),
					"GPS abilitato, attendi mentre ottengo la tua posizione",
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
		if ((locationAvailable) && (started)) {
			switch (item.getItemId()) {
			case R.id.take_photo:
				Uri fileUri;
				final int MEDIA_TYPE_IMAGE = 1;

				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// create a file to save the image
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				// set the image file name
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

				// start the image capture Intent
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

				return true;
			}

		}
		else{
			Toast.makeText(getApplicationContext(),
					"Non puoi fare foto se non hai iniziato un viaggio!", Toast.LENGTH_SHORT)
					.show();
			
		}

		return false;
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	// Create a file Uri for saving an image
	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	// Create a File for saving an image
	@SuppressLint("SimpleDateFormat")
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"TouRemember Photos");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("TouRemember Photos", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getAbsolutePath()
					+ File.separator + "IMG_" + timeStamp + ".jpg");

			return mediaFile;
		} else {
			return null;
		}
	}

	@Override
	public void onBackPressed() {
		if (started) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Chiusura Mappa")
					.setMessage(
							"Uscendo perderai il percorso effettuato fin'ora, sei sicuro di voler uscire?")
					.setPositiveButton("Esci",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();

								}

							}).setNegativeButton("No", null).show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// Update MediaScanner to show new photos in albums
				MediaScannerConnection.scanFile(getBaseContext(),
						new String[] { mediaFile.getAbsolutePath() },
						new String[] { "image/jpg" }, null);

				// Create a new point with the picture
				TravelPoint point = new TravelPoint(currentTravel, lastPos,
						mediaFile.getAbsolutePath(),
						currentTravel.getNumberOfPointsSoFar());
				currentTravel.addPoint(point);

				// Add marker
				mMap.addMarker(new MarkerOptions().position(lastPos).title(
						"Photo:" + mediaFile.getAbsolutePath()));
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				Toast.makeText(getApplicationContext(),
						"Ops, c'è stato un problema...", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
