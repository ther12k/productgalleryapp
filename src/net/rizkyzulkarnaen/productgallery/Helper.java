package net.rizkyzulkarnaen.productgallery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Helper {
	

	public static String bitmapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] ba = baos.toByteArray();
		return Base64.encodeToString(ba, Base64.DEFAULT);
	}

	public static Bitmap stringToBitmap(String photo) {
		return stringToBitmap(photo, 0);
	}

	public static Bitmap stringToBitmap(String photo, int quality) {
		if (photo == null)
			return null;
		byte[] image = Base64.decode(photo, quality);
		InputStream is = new ByteArrayInputStream(image);
		return BitmapFactory.decodeStream(is);
	}
	
}
