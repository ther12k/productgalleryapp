package net.rizkyzulkarnaen.productgallery;

import java.util.ArrayList;
import java.util.List;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends Activity {
	private LinearLayout fieldLayout;
	private LinearLayout tabsLayout;
	private List<String> tabs;
	private boolean edit = false;
	private ProductSource productSource;
	private Product item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final Bundle bundleExtra = getIntent().getExtras();
        ActionBar actionBar = getActionBar();
        tabs = new ArrayList<String>();
		// Enabling Back navigation on Action Bar icon
		//actionBar.setDisplayHomeAsUpEnabled(true);
        if (bundleExtra != null) {
			 if(bundleExtra.containsKey("edit")) edit = true;
			 if(bundleExtra.containsKey("id")){
				 productSource = new ProductSource(this);
				 productSource.open();
				 item = productSource.get(bundleExtra.getLong("id"));
				 List<Field> fields = productSource.getFields(item.getId());
				 productSource.close();
				 actionBar.setLogo(new BitmapDrawable(getResources(), item.getImageBitmap()));
				 actionBar.setTitle(item.getNo());
				 LayoutInflater layoutInflater = LayoutInflater.from(this);
				 LayoutInflater layoutInflater2 = LayoutInflater.from(this);
				 fieldLayout = (LinearLayout)findViewById(R.id.fieldLayout);
				 tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);

				 String firstTab = "";
				 int count=0;
				 for(final Field field:fields){
					 final LinearLayout fieldItem = (LinearLayout) layoutInflater.inflate(
								R.layout.field_item, null);
					 TextView label = (TextView)fieldItem.findViewById(R.id.fieldLabel);
					 EditText value = (EditText)fieldItem.findViewById(R.id.fieldValue);
					 if(count++>1)
						 if(edit) value.setEnabled(true);
					 label.setText(field.getName());
					 value.setText(field.getValue());
					 fieldItem.setTag(new TabId(field.getCategory(),field.getId()));
					 fieldLayout.addView(fieldItem);
					 if(!tabs.contains(field.getCategory())){
						 if(firstTab.length()==0) firstTab = field.getCategory();
						 //tab = field.getCategory();

						 final LinearLayout tabItem = (LinearLayout) layoutInflater2.inflate(
									R.layout.tab_item, null);
						 final Button btn = (Button)tabItem.findViewById(R.id.tabBtn);
						 btn.setText(field.getCategory());
						 btn.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								for(int i=0;i<fieldLayout.getChildCount();i++){
									LinearLayout row = (LinearLayout)fieldLayout.getChildAt(i);
									TabId tab = (TabId)row.getTag();
									if(tab.getTab().equals(field.getCategory())){
										row.setVisibility(View.VISIBLE);
									}else{
										row.setVisibility(View.GONE);
									}
								}
								clearAllBtn();
								btn.setTextColor(Color.BLACK);
							}
						 });
						 tabs.add(field.getCategory());
						 tabsLayout.addView(tabItem);
					 }
					 
				 }
				 /*
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
				 */
				 if(tabsLayout.getChildCount()>0){
					 LinearLayout row = (LinearLayout)tabsLayout.getChildAt(0);
					 Button btn = (Button)row.findViewById(R.id.tabBtn);
					 btn.callOnClick();
				 }
			 }

			 
        }
        
	}
	
	private void clearAllBtn(){
		for(int i=0;i<tabsLayout.getChildCount();i++){
			LinearLayout row = (LinearLayout)tabsLayout.getChildAt(i);
			Button btn = (Button)row.findViewById(R.id.tabBtn);
			btn.setTextColor(Color.WHITE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(edit)
			getMenuInflater().inflate(R.menu.editinfo, menu);
		else
			getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = menuItem.getItemId();
		if (id == R.id.action_edit) {
			Intent i = new Intent(InfoActivity.this, InfoActivity.class);
			i.putExtra("id",item.getId());
			i.putExtra("edit", true);
    		startActivity(i);
			finish();
			return true;
		}
		if (id == R.id.action_close) {
			finish();
			return true;
		}
		if (id == R.id.action_save) {
			for(int i=0;i<fieldLayout.getChildCount();i++){
				LinearLayout row = (LinearLayout)fieldLayout.getChildAt(i);
				EditText valueView = (EditText)row.findViewById(R.id.fieldValue);
				String value = valueView.getText().toString();
				TabId tabId = (TabId)row.getTag();
				productSource.open();
				productSource.updateField(tabId.getId(),value);
				productSource.close();
			}
			Intent i = new Intent(InfoActivity.this, InfoActivity.class);
			i.putExtra("id",item.getId());
    		startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	public class TabId{
		private String tab;
		private long id = 0;
		
		public TabId(String tab, long id){
			this.id = id;
			this.setTab(tab);
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getTab() {
			return tab;
		}
		public void setTab(String tab) {
			this.tab = tab;
		}
	}
}
