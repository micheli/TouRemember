package com.example.tablet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class ImageInfoWindowAdapter implements InfoWindowAdapter {
	private Context context;

	public ImageInfoWindowAdapter(Context context) {
		this.context = context;
	}

	@Override
	public View getInfoContents(Marker objmarker) {
		if (objmarker.getTitle() != null && objmarker.getTitle().startsWith("Photo:")) {
			String path = objmarker.getTitle().substring(6);
			ImageView res = new ImageView(context);

			// First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    Bitmap bm = BitmapFactory.decodeFile(path, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, 150, 150);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    bm = BitmapFactory.decodeFile(path, options);
			
			res.setImageBitmap(bm);

			return res;
		} else {
			return null;
		}
	}

	@Override
	public View getInfoWindow(Marker objmarker) {
		return null;
	}

	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
}
