package com.example.productgallery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewActivity extends Activity {
	private Item item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		final Bundle bundleExtra = getIntent().getExtras();
        ActionBar actionBar = getActionBar();

		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("item")){
				 item = (Item)bundleExtra.getSerializable("item");
				 TextView nameView = (TextView) findViewById(R.id.name);
				 ImageView imageView = (ImageView) findViewById(R.id.image);
				 nameView.setText(item.name);
				 imageView.setImageResource(item.drawable);
				 actionBar.setTitle(item.name);
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
    		Intent intent = new Intent(ViewActivity.this,InfoActivity.class);
    		intent.putExtra("item", item);
    		startActivity(intent);
    		finish();
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
}
