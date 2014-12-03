package net.rizkyzulkarnaen.productgallery;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewActivity extends Activity {
	private Product item;
	private Point laySize = null;
	private ProgressDialog progressDialog;
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

		ProductSource productSource = new ProductSource(this);
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("id")){
				 productSource.open();
				 item = productSource.get(bundleExtra.getLong("id"));
				 TextView nameView = (TextView) findViewById(R.id.name);
				 ImageView imageView = (ImageView) findViewById(R.id.image);
				 nameView.setText(item.getNo());
				 
				 if(item.getImageBitmap()!=null){
					Bitmap bitmap = item.getImageBitmap();
		        	imageView.setImageBitmap(bitmap);
					actionBar.setLogo(new BitmapDrawable(getResources(), bitmap));
					item.setFields(productSource.getFields(item.getId()));
		        	//photo.recycle();
					actionBar.setTitle(item.getNo());
					imageView.invalidate();
				 }
				 productSource.close();
			 }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}
	protected void showWaitDialog(String title,String message) {
		
		try{
				progressDialog = ProgressDialog.show(ViewActivity.this, title, message,
					false, false);
				progressDialog.setIndeterminate(true);
				progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
