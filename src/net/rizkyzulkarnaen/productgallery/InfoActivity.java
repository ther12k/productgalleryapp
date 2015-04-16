package net.rizkyzulkarnaen.productgallery;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.sync.android.DbxException;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.DropboxProductSource;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends BasicActivity {
	private LinearLayout fieldLayout;
	private LinearLayout tabsLayout;
	private List<String> tabs;
	private boolean edit = false;
	private DropboxProductSource productSource;
	private Product item;
	private String currentCat;

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
				 try {
					productSource = new DropboxProductSource(this,datastoreManager.openDefaultDatastore());
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 item = productSource.get(bundleExtra.getString("id"));
				 Bitmap photo = item.getImageBitmap();
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
	            width = (int)(scale*width);
	            height = (int)(scale*height);
	            Bitmap bitmap = Bitmap.createScaledBitmap(photo,
	            		width,
						height, true);
	            item.setImageBitmap(bitmap);
				 List<Field> fields = productSource.getFields(item.getId());
				 actionBar.setLogo(new BitmapDrawable(getResources(), item.getImageBitmap()));
				 actionBar.setTitle(item.getNo());
				 LayoutInflater layoutInflater = LayoutInflater.from(this);
				 LayoutInflater layoutInflater2 = LayoutInflater.from(this);
				 fieldLayout = (LinearLayout)findViewById(R.id.fieldLayout);
				 tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);

				 final Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
				 
				 if(edit){
		        	 Button addFieldBtn = (Button)findViewById(R.id.add_field);
		        	 addFieldBtn.setVisibility(View.VISIBLE);
		        	 addFieldBtn.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								 LayoutInflater layoutInflater = LayoutInflater.from(InfoActivity.this);
								 final LinearLayout fieldItem = (LinearLayout) layoutInflater.inflate(
										R.layout.add_field_item, null);
								 final EditText label = (EditText)fieldItem.findViewById(R.id.fieldLabel);
								 EditText value = (EditText)fieldItem.findViewById(R.id.fieldValue);

								 fieldItem.setTag(new TabId(currentCat,""));
								 fieldLayout.addView(fieldItem);
								 TextView delete = (TextView) fieldItem.findViewById(R.id.delete);
				                 //delete.setTypeface(font);
				                 //delete.setText(getString(R.string.ic_delete));
				                 delete.setOnClickListener(new OnClickListener() {
										public void onClick(View v) {
											 showYesNoMsg(
														R.string.delete_confirm_field_title,
														getString(R.string.delete_confirm_field_message)+" "+label.getText().toString()+"?",
														new YesNoMsgAction() {
															@Override
															public void onYes() {
																 TabId tag = (TabId)fieldItem.getTag();
																 tag.setDeleted(true);
																 fieldItem.setTag(tag);
																 fieldItem.setVisibility(View.GONE);
															}
			
															@Override
															public void onNo() {
																// TODO Auto-generated
																// method stub
																return;
															}
														});
										}
									 });
							}
						 });
		        	 
				 }
				 String firstTab = "";			 
				 int count=0;
				 for(final Field field:fields){
					 final LinearLayout fieldItem = (LinearLayout) layoutInflater.inflate(
								R.layout.field_item, null);
					 final TextView label = (TextView)fieldItem.findViewById(R.id.fieldLabel);
					 EditText value = (EditText)fieldItem.findViewById(R.id.fieldValue);

					 if(count++>0 && edit){
						 value.setEnabled(true);
						 TextView delete = (TextView) fieldItem.findViewById(R.id.delete);
		                 //delete.setTypeface(font);
		                 //delete.setText(getString(R.string.ic_delete));
		                 delete.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									 showYesNoMsg(
												R.string.delete_confirm_field_title,
												getString(R.string.delete_confirm_field_message)+" "+label.getText().toString()+"?",
												new YesNoMsgAction() {
													@Override
													public void onYes() {
														 TabId tag = (TabId)fieldItem.getTag();
														 tag.setDeleted(true);
														 fieldItem.setTag(tag);
														 fieldItem.setVisibility(View.GONE);
													}
	
													@Override
													public void onNo() {
														// TODO Auto-generated
														// method stub
														return;
													}
												});
								}
							 });
		                 delete.setVisibility(View.VISIBLE);
					 }
					 label.setText(field.getName());
					 value.setText(field.getValue());
					 fieldItem.setTag(new TabId(field.getCategory(),field.getId()));
					 fieldItem.setVisibility(View.VISIBLE);
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
								currentCat = field.getCategory();
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
			 showYesNoMsg(
						R.string.save_confirm_title,
						getString(R.string.save_confirm_message)+" "+item.getNo()+"?",
						new YesNoMsgAction() {
							@Override
							public void onYes() {
								for(int i=0;i<fieldLayout.getChildCount();i++){
									LinearLayout row = (LinearLayout)fieldLayout.getChildAt(i);
									EditText valueView = (EditText)row.findViewById(R.id.fieldValue);
									String value = valueView.getText().toString();
									TabId tabId = (TabId)row.getTag();
									if(tabId.isDeleted()){
										 productSource.deleteFieldById(tabId.getId());
									}else{
										if(tabId.getId()==""){
											EditText fieldView = (EditText)row.findViewById(R.id.fieldLabel);
											String field = fieldView.getText().toString();
											if(!field.isEmpty()&&!value.isEmpty())
											productSource.createFields(item.getId(), tabId.getTab(), fieldView.getText().toString(), value);
										}else{
											productSource.updateField(tabId.getId(),value);
										}
									}
								}
								Intent i = new Intent(InfoActivity.this, InfoActivity.class);
								i.putExtra("id",item.getId());
					    		startActivity(i);
								finish();
							}

							@Override
							public void onNo() {
								// TODO Auto-generated
								// method stub
								return;
							}
						});
				return true;
				
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	public class TabId{
		private String tab;
		private String field;
		private boolean deleted;
		private String id = "";
		
		public TabId(String tab, String id){
			this.id = id;
			this.setTab(tab);
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTab() {
			return tab;
		}
		public void setTab(String tab) {
			this.tab = tab;
		}
		public boolean isDeleted() {
			return deleted;
		}
		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
	}
}
