package net.rizkyzulkarnaen.productgallery;

import java.util.List;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends Activity {
	private LinearLayout fieldLayout;
	private LinearLayout tabsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		final Bundle bundleExtra = getIntent().getExtras();
        ActionBar actionBar = getActionBar();

		// Enabling Back navigation on Action Bar icon
		//actionBar.setDisplayHomeAsUpEnabled(true);
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("id")){
				 ProductSource productSource = new ProductSource(this);
				 productSource.open();
				 Product item = productSource.get(bundleExtra.getLong("id"));
				 List<Field> fields = productSource.getFields(item.getId());
				 productSource.close();
				 actionBar.setLogo(new BitmapDrawable(getResources(), item.getImageBitmap()));
				 actionBar.setTitle(item.getNo());
				 LayoutInflater layoutInflater = LayoutInflater.from(this);
				 LayoutInflater layoutInflater2 = LayoutInflater.from(this);
				 fieldLayout = (LinearLayout)findViewById(R.id.fieldLayout);
				 tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);
				 String tab = "";
				 String firstTab = "";
				 for(final Field field:fields){
					 final LinearLayout fieldItem = (LinearLayout) layoutInflater.inflate(
								R.layout.field_item, null);
					 TextView label = (TextView)fieldItem.findViewById(R.id.fieldLabel);
					 EditText value = (EditText)fieldItem.findViewById(R.id.fieldValue);
					 label.setText(field.getName());
					 value.setText(field.getValue());
					 fieldItem.setTag(field.getCategory());
					 fieldLayout.addView(fieldItem);
					 if(tab.length()==0||!tab.equals(field.getCategory())){
						 if(tab.length()==0) firstTab = tab;
						 tab = field.getCategory();

						 final LinearLayout tabItem = (LinearLayout) layoutInflater2.inflate(
									R.layout.tab_item, null);
						 final Button btn = (Button)tabItem.findViewById(R.id.tabBtn);
						 btn.setText(tab);
						 btn.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								for(int i=0;i<fieldLayout.getChildCount();i++){
									LinearLayout row = (LinearLayout)fieldLayout.getChildAt(i);
									String tab = (String)row.getTag();
									if(tab.equals(field.getCategory())){
										row.setVisibility(View.VISIBLE);
									}else{
										row.setVisibility(View.GONE);
									}
								}
								hideAllBtn();
								btn.setTextColor(Color.BLACK);
							}
						 });

						 tabsLayout.addView(tabItem);
					 }
					 
				 }
				 for(int i=0;i<fieldLayout.getChildCount();i++){
					LinearLayout row = (LinearLayout)fieldLayout.getChildAt(i);
					String tabTag = (String)row.getTag();
					if(tabTag.equals(firstTab)){
						row.setVisibility(View.VISIBLE);
					}else{
						row.setVisibility(View.GONE);
					}
				 }
				 if(tabsLayout.getChildCount()>0){
					 LinearLayout row = (LinearLayout)tabsLayout.getChildAt(0);
					 Button btn = (Button)row.findViewById(R.id.tabBtn);
					 btn.setTextColor(Color.BLACK);
					 fieldLayout.invalidate();
					 tabsLayout.invalidate();
				 }
			 }

			 
        }
        
	}
	
	private void hideAllBtn(){
		for(int i=0;i<tabsLayout.getChildCount();i++){
			LinearLayout row = (LinearLayout)tabsLayout.getChildAt(i);
			Button btn = (Button)row.findViewById(R.id.tabBtn);
			btn.setTextColor(Color.WHITE);
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
