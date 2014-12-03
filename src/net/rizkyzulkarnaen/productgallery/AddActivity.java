package net.rizkyzulkarnaen.productgallery;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AddActivity extends Activity {
	private static final int PICK_IMAGE = 1;
	private static final int TAKE_IMAGE = 2;
	private Product product;
	private String picturePath;
	private Point laySize = null;
	private ImageView image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		ActionBar actionBar = getActionBar();
		Display display = getWindowManager().getDefaultDisplay();
		laySize = new Point();
		display.getSize(laySize);
		image = (ImageView)findViewById(R.id.image);
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
		product = new Product();
		findViewById(R.id.pick).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						pickPhoto();
					}
				});
		findViewById(R.id.take).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						takePhoto();
					}
				});
	}
	
	protected void pickPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);	
        i.setType("image/*");
    	startActivityForResult(i, PICK_IMAGE);
	}
	protected void takePhoto() {
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_IMAGE);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save) {
			ProductSource productSource = new ProductSource(this);
			EditText noView = (EditText) findViewById(R.id.productNo);
			boolean error = false;
			
			if (TextUtils.isEmpty(noView.getText().toString())){
				noView.setError(getString(R.string.empty_no));
				error = true;
			}
			if(!error){
				product.setNo(noView.getText().toString());
				
				productSource.open();
				productSource.create(product);
				productSource.close();
				setResult(RESULT_OK);
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode,Intent intent){
		Bitmap bitmap = null;
		if (resultCode == RESULT_OK) {
			switch(requestCode){
				case PICK_IMAGE:
					Uri selectedImage = intent.getData();
			        String[] filePathColumn = { MediaStore.Images.Media.DATA };

			        Cursor cursor = getContentResolver().query(selectedImage,
			                filePathColumn, null, null, null);
			        cursor.moveToFirst();

			        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			        picturePath = cursor.getString(columnIndex);
			        cursor.close();

			        Bitmap photo = BitmapFactory.decodeFile(picturePath);
			        bitmap = Bitmap.createScaledBitmap(photo,
							(int) (laySize.x/2),
							(int) (laySize.y/2), true);
			        photo.recycle();
			        product.setImageBitmap(bitmap);
			        image.setImageBitmap(bitmap);

			        break;
				case TAKE_IMAGE:
			        if (resultCode == RESULT_OK) {
			            bitmap = Bitmap.createScaledBitmap( (Bitmap) intent.getExtras().get("data"),
								(int) (laySize.x/2),
								(int) (laySize.y/2), true);
			            product.setImageBitmap(bitmap);
				        image.setImageBitmap(bitmap);
			        }
			}
		}
		//finish();//back to main
		super.onActivityResult(requestCode, resultCode, intent);
		return;
	}
}
