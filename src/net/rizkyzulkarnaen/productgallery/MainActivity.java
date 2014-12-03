package net.rizkyzulkarnaen.productgallery;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final int ADD = 1;
	private final int IMPORT = 2;
	GalleryAdapter adapter;
	private ProgressDialog progressDialog;
	private Point laySize = null;
	private boolean opened = false;
	private LruCache<String, Bitmap> mMemoryCache;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 4;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
		Display display = getWindowManager().getDefaultDisplay();
		laySize = new Point();
		display.getSize(laySize);
		final GridView gridView = (GridView) findViewById(R.id.gridview);
	 	adapter = new GalleryAdapter(this,laySize,mMemoryCache);
	 	gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
        		Product item = adapter.getItem(position);
            	showWaitDialog("Open a product","Open : "+item.getNo());
        		Intent intent = new Intent(MainActivity.this, ViewActivity.class);
        		intent.putExtra("id", item.getId());
        		startActivity(intent);
        		opened = true;
        		//finish();
            }
        });
		gridView.invalidate();
	}
	@Override
	protected void onResume(){
	    if(opened){
	    	dismissDialogWait();
	    	opened = false;
	    }
	    super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
			/*
			case R.id.add_menu:
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
	    		startActivityForResult(intent,ADD);
				return true;
				*/
			case R.id.clear_menu:
				showYesNoMsg(
						R.string.clear_confirm_title,
						R.string.clear_confirm_message,
						new YesNoMsgAction() {

							@Override
							public void onYes() {
								ProductSource productSource = new ProductSource(MainActivity.this);
								productSource.open();
								productSource.deleteAll();
								productSource.close();
								final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

							    // Use 1/8th of the available memory for this memory cache.
							    final int cacheSize = maxMemory / 4;
								mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
							        @Override
							        protected int sizeOf(String key, Bitmap bitmap) {
							            // The cache size will be measured in kilobytes rather than
							            // number of items.
							            return bitmap.getByteCount() / 1024;
							        }
							    };
								Intent intent = new Intent(MainActivity.this, MainActivity.class);
								startActivity(intent);
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
			case R.id.import_menu:
				Intent i = new Intent(MainActivity.this, ImportFileBrowserActivity.class);
	    		startActivityForResult(i,IMPORT);
				return true;
			case R.id.export_menu:
				ExportTask task = new ExportTask();
				String[] params = {""};
				task.execute(params); 
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void showYesNoMsg(int idTitle,int idMessage,final YesNoMsgAction yesno ){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(idTitle);
		alert.setMessage(idMessage);
		alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // I do not need any action here you might
		    	yesno.onNo();
		        dialog.dismiss();
		    }
		});
		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				yesno.onYes();
				return;
			}
		});
		alert.show();
	}
	
	public void onActivityResult(int requestCode, int resultCode,Intent intent){

		if (resultCode == RESULT_OK) {
			switch(requestCode){
				case ADD:
					Intent i = new Intent(MainActivity.this, MainActivity.class);
		    		startActivity(i);
					finish();
			        break;
				case IMPORT:
					final String path = intent.getExtras().getString("file");
					ImportTask task = new ImportTask();
					String[] params = {path};
					task.execute(params); 
					break;
			}
		}
		//finish();//back to main
		super.onActivityResult(requestCode, resultCode, intent);
		return;
	}
	protected void showWaitDialog(String title,String message) {
		
		try{
				progressDialog = ProgressDialog.show(this, title, message,
					false, false);
				progressDialog.setIndeterminate(true);
				progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Close progress dialog if still show
	 */
	protected void dismissDialogWait() {
		try{
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ImportTask extends AsyncTask<String, Void, Integer> {
		private int ERROR = -1;
		private int SUCCESS = 0;
		private int NO_CSV = 1;

		protected void onPreExecute() {
			showWaitDialog("Import Data","Wait");
			
		}

		@Override
		protected Integer doInBackground(String... params) {

			ProductSource productSource = new ProductSource(MainActivity.this);
			String csv = "";
			String[] tabs=null;
			String[] fields=null;
			String path = params[0];
			try{ 
				FileInputStream fin = new FileInputStream(path); 
				ZipInputStream zin = new ZipInputStream(fin); 
				ZipEntry ze = null; 
				while ((ze = zin.getNextEntry()) != null&& !ze.isDirectory()) { 
					Log.v("Decompress", "Unzipping " + ze.getName()); 
			        
					if (ze.getName().toString().equalsIgnoreCase("import.csv")) { 
					   
						StringBuilder sb = new StringBuilder();
						for (int c = zin.read(); c != -1; c = zin.read()) {
						    sb.append((char)c);
						}
						csv = sb.toString();
						
						zin.closeEntry(); 
						if(csv.length()>0){
							final String[] lines = csv.toString().split("\n");  
							int row=0;
			                for(String line : lines)
			                {
			                	row++;
			                    
			                	if(row==1){
			                		tabs = line.split(";");
			                	}else if(row==2){
			                		fields = line.split(";");
			                	}else{
			                		if(tabs==null||fields==null) break;
				                    String[] columns = line.split(";");
									productSource.open();
			                		Product product = productSource.getByNo(columns[0].trim());
			                		long id = 0;
				                    if(product!=null){
				                    	id = product.getId();
				                    }else{
				                    	product = new Product();
										product.setNo(columns[0].trim());
										id = productSource.create(product);
				                    }
									productSource.close();
									for(int j=0;j<columns.length;j++){

										productSource.open();
										long ret = productSource.createFields(id, tabs[j].trim(), fields[j].trim(), columns[j].trim());

										productSource.close();
										ret++;
									}
			                	}
			                }   
						}
					}else if (ze.getName().toString().endsWith(".jpeg")||
							ze.getName().toString().endsWith(".jpg")) { 
						try{
							Log.v("Decompress", "Unzipping " + ze.getName());
							/*
							ByteArrayOutputStream sb = new ByteArrayOutputStream(); 
					        long iSize   = 0;
					        int iReaded = 0;
							byte buffer[]  = new byte[BLOCKSIZE];
				            long iTotal  = ze.getSize();
	
				            while ((iReaded = zin.read(buffer,0,BLOCKSIZE)) > 0 && ((iSize+=iReaded) <= iTotal) ) 
				            {   
				            	sb.write(buffer,0,iReaded);
				            }
				            */
				            Bitmap photo = BitmapFactory.decodeStream(zin);
				            int width = photo.getWidth();
				            int height = photo.getHeight();
				            int maxSize = Math.max(laySize.x, laySize.y);
				            float scale = 1;
				            if(width>height){
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
					        //photo.recycle();
				            zin.closeEntry(); 
				            //photo.recycle();
							productSource.open();
							String name = "";
							if(ze.getName().toString().endsWith(".jpeg"))
								name = ze.getName().replace(".jpeg", "");
							else
								name = ze.getName().replace(".jpg", "");
							name = name.trim();
							Product product = productSource.getByNo(name);
							
							if(product!=null){
								productSource.updateImage(product.getId(),Helper.bitmapToString(bitmap));
							}else{
								product = new Product();
								product.setNo(name);
								product.setImage(Helper.bitmapToString(bitmap));
								productSource.create(product);
							}
							productSource.close();
							bitmap.recycle();
						}catch(Exception e){
							Log.e("Decompress", "unzip "+ze.getName(), e); 
						}
					}
			    } 
				zin.close();
			}catch(Exception e) {
				Log.e("Decompress", "unzip", e); 
				return ERROR;
			}
			
			if(csv.length()==0){
		        return NO_CSV;
			}
			return SUCCESS;
		}

		@Override
		protected void onPostExecute(final Integer status) {
			dismissDialogWait();
			if(status==NO_CSV){
				Toast.makeText(MainActivity.this, "No import.csv", Toast.LENGTH_LONG).show();
			}else{
				Intent i = new Intent(MainActivity.this, MainActivity.class);
	    		startActivity(i);
				finish();
			}
		}

		@Override
		protected void onCancelled() {
			dismissDialogWait();
		}
	}

	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ExportTask extends AsyncTask<String, Void, Integer> {
		private int ERROR = -1;
		private int SUCCESS = 0;
		protected void onPreExecute() {
			showWaitDialog("Export Data","Wait");
		}

		@Override
		protected Integer doInBackground(String... params) {
			Export export = new Export(MainActivity.this,Environment.getExternalStorageDirectory() + File.separator+"Download"+File.separator+"data.zip");
			return export.makeZip();
		}

		@Override
		protected void onPostExecute(final Integer status) {
			dismissDialogWait();
			if(status==SUCCESS){
				//Intent i = new Intent(MainActivity.this, MainActivity.class);
	    		//startActivity(i);
				//finish();
			}
		}

		@Override
		protected void onCancelled() {
			dismissDialogWait();
		}
	}
}
