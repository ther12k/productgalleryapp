package net.rizkyzulkarnaen.productgallery;

import java.util.List;

import com.dropbox.sync.android.DbxException;

import net.rizkyzulkarnaen.productgallery.entity.Image;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.DropboxProductSource;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewActivity extends BasicActivity {
	private Product item;
	private Point laySize = null;
	private final int div = 4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		final Bundle bundleExtra = getIntent().getExtras();
        ActionBar actionBar = getActionBar();
		Display display = getWindowManager().getDefaultDisplay();
        laySize = new Point();
		display.getSize(laySize);
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		DropboxProductSource productSource=null;
		try {
			productSource = new DropboxProductSource(this,datastoreManager.openDefaultDatastore());
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("id")){
				 item = productSource.get(bundleExtra.getString("id"));
				 List<Image> images = productSource.getImagesByNo(item.getNo());
				 //TextView nameView = (TextView) findViewById(R.id.name);
				 final ZoomImage imageView = (ZoomImage) findViewById(R.id.image);
				 //nameView.setText(item.getNo());
				 LayoutInflater layoutInflater = LayoutInflater.from(this);
				 LinearLayout imagesLayout = (LinearLayout)findViewById(R.id.imagesLayout);
				 
				 if(item.getImageBitmap()!=null){
					final Bitmap photo = item.getImageBitmap();
					
					int width = photo.getWidth();
		            int height = photo.getHeight();
		            int maxSize = Math.max(laySize.x, laySize.y);
		            float scale = 1;
		            if(width<height){
		            	if(maxSize<width)
		            		scale = maxSize/width;
		            }else{
		            	if(maxSize<height)
		            		scale = maxSize/height;
		            }
		            /*
		            width = (int)(scale*width);
		            height = (int)(scale*height);
		            Bitmap bitmap = Bitmap.createScaledBitmap(photo,
		            		width,
							height, true);
							*/
		        	imageView.setBitmap(photo);
					actionBar.setLogo(new BitmapDrawable(getResources(), photo));
					item.setFields(productSource.getFields(item.getId()));
		        	//photo.recycle();
					actionBar.setTitle(item.getNo());
					imageView.invalidate();
					/*
					imageView.setOnClickListener(
							new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									try{
										Intent intent = new Intent();
					            		//bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
					            		intent.setAction(android.content.Intent.ACTION_VIEW);
					            		File outputDir = getApplicationContext().getExternalCacheDir(); // context being the Activity pointer
					            		File outputFile = File.createTempFile("img", ".jpg", outputDir);
					            		OutputStream outStream = new FileOutputStream(outputFile);
					            		photo.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
										outStream.flush();
										outStream.close();
					            		intent.setDataAndType(Uri.fromFile(outputFile), "image/*");
						        		startActivity(intent);
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
							});
							*/
					if(images!=null&&images.size()>0){
						final LinearLayout imageItem= (LinearLayout) layoutInflater.inflate(
								R.layout.image_item, null);
						ImageView imageOther = (ImageView) imageItem.findViewById(R.id.image_other);
						width = (int)(scale/div*width);
						height = (int)(scale/div*height);
				        Bitmap smallBitmap = Bitmap.createScaledBitmap(photo,
				            		width,
									height, true);
			            imageOther.setImageBitmap(smallBitmap);
			            imageOther.invalidate();
			            imageOther.setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View view) {
							        	imageView.setBitmap(photo);
							        	/*
										imageView.invalidate();
										imageView.setOnClickListener(
												new View.OnClickListener() {
													@Override
													public void onClick(View view) {
														try{
															Intent intent = new Intent();
										            		//bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
										            		intent.setAction(android.content.Intent.ACTION_VIEW);
										            		File outputDir = getApplicationContext().getExternalCacheDir(); // context being the Activity pointer
										            		File outputFile = File.createTempFile("img", ".jpg", outputDir);
										            		OutputStream outStream = new FileOutputStream(outputFile);
										            		photo.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
															outStream.flush();
															outStream.close();
										            		intent.setDataAndType(Uri.fromFile(outputFile), "image/*");
											        		startActivity(intent);
														}catch(Exception ex){
															ex.printStackTrace();
														}
													}
												});
												*/
									}
								});
			            imagesLayout.addView(imageItem);
					}

				 }
				 if(images!=null&&images.size()>0){
						for(Image image : images){
							final LinearLayout imageItem= (LinearLayout) layoutInflater.inflate(
									R.layout.image_item, null);
							final Bitmap pic = image.getImageBitmap();
							int width = pic.getWidth();
				            int height = pic.getHeight();
				            int maxSize = Math.max(laySize.x, laySize.y);
				            float scale = 1;
				            if(width<height){
				            	if(maxSize<width)
				            		scale = maxSize/width;
				            }else{
				            	if(maxSize<height)
				            		scale = maxSize/height;
				            }
				            width = (int)(scale/div*width);
				            height = (int)(scale/div*height);
				            Bitmap bitmap = Bitmap.createScaledBitmap(pic,
				            		width,
									height, true);

				            ImageView imageOther = (ImageView) imageItem.findViewById(R.id.image_other);
				            imageOther.setImageBitmap(bitmap);
				            imageOther.invalidate();
				            imageOther.setOnClickListener(
									new View.OnClickListener() {
										@Override
										public void onClick(View view) {
								        	imageView.setBitmap(pic);
											imageView.invalidate();
											/*
											imageView.setOnClickListener(
													new View.OnClickListener() {
														@Override
														public void onClick(View view) {
															try{
																Intent intent = new Intent();
											            		//bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
											            		intent.setAction(android.content.Intent.ACTION_VIEW);
											            		File outputDir = getApplicationContext().getExternalCacheDir(); // context being the Activity pointer
											            		File outputFile = File.createTempFile("img", ".jpg", outputDir);
											            		OutputStream outStream = new FileOutputStream(outputFile);
											            		pic.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
																outStream.flush();
																outStream.close();
											            		intent.setDataAndType(Uri.fromFile(outputFile), "image/*");
												        		startActivity(intent);
															}catch(Exception ex){
																ex.printStackTrace();
															}
														}
													});
													*/
										}
									});
				            imagesLayout.addView(imageItem);
						}
					 }
			 }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = menuItem.getItemId();
		if (id == R.id.action_info) {
			showWaitDialog("Open product info","Open : "+item.getNo());
    		Intent intent = new Intent(ViewActivity.this,InfoActivity.class);
    		intent.putExtra("id", item.getId());
    		startActivity(intent);
    		finish();
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
}
