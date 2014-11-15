package com.example.productgallery;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		final Bundle bundleExtra = getIntent().getExtras();
        ActionBar actionBar = getActionBar();

		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("item")){
				 Item item = (Item)bundleExtra.getSerializable("item");
				 TextView nameView = (TextView) findViewById(R.id.name);
				 nameView.setText(item.name);
				 actionBar.setIcon(item.drawable);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_close) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
