package net.rizkyzulkarnaen.productgallery;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.DropboxProductSource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

public class MainActivity extends BasicActivity{
	private final int ADD = 1;
	private final int IMPORT = 2;
	private final int EXPORT = 3;
	GalleryAdapter adapter;
	private boolean opened = false;
	private LruCache<String, Bitmap> mMemoryCache;
	private MenuItem linkMenu;
	private DbxDatastore datastore;

    static final int REQUEST_LINK_TO_DBX = 0;
    Button linkUnlinkButton;
    EditText listInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
            // Open the right datastore (list).
            datastore = datastoreManager.openDefaultDatastore();
            datastore.sync();
        } catch (DbxException e) {
            e.printStackTrace();
        }
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
        
	}
	@Override
	protected void onResume(){
	    if(opened){
	    	dismissDialogWait();
	    	opened = false;
	    }
	    super.onResume();

        try {
            // Open the right datastore (list).
            datastore = datastoreManager.openDefaultDatastore();
            datastore.sync();
        } catch (DbxException e) {
            e.printStackTrace();
        }

        // Listen for changes to this list (datastore).
        datastore.addSyncStatusListener(new DbxDatastore.SyncStatusListener() {
            @Override
            public void onDatastoreStatusChange(DbxDatastore dbxDatastore) {
                updateList();
            }
        });
	    super.onResume();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        datastore.removeSyncStatusListener(new DbxDatastore.SyncStatusListener() {
            @Override
            public void onDatastoreStatusChange(DbxDatastore dbxDatastore) {
                
            }
        });
        datastore.close();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		linkMenu=menu.findItem(R.id.link_menu);
		if (!accountManager.hasLinkedAccount()) {
			linkMenu.setTitle(R.string.action_link);
        } else {
            linkMenu.setTitle(R.string.action_unlink);
        }
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
								DropboxProductSource productSource = new DropboxProductSource(MainActivity.this,datastore);
								try {
									productSource.deleteAll();
								} catch (DbxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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
				Intent intent = new Intent(MainActivity.this, ExportFileBrowserActivity.class);
	    		startActivityForResult(intent,EXPORT);
				
				return true;
			case R.id.link_menu:
				if (!accountManager.hasLinkedAccount()) {
                    // If we're not already linked, start the linking process.
                    accountManager.startLink(this, REQUEST_LINK_TO_DBX);
                } else {
                    // If we're linked, unlink and start using a local datastore manager again.
                    accountManager.unlink();
                    datastoreManager = DbxDatastoreManager.localManager(accountManager);
                    linkMenu.setTitle(R.string.action_unlink);
                }
				
				return true;
		}
		return super.onOptionsItemSelected(item);
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
				case EXPORT:
					 final String basepath = intent.getExtras().getString("file");
					 AlertDialog.Builder builder = new AlertDialog.Builder(this);
					 builder.setTitle("Input file name");

					 // Set up the input
					 final EditText input = new EditText(this);
					 // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
					 input.setInputType(InputType.TYPE_CLASS_TEXT);
					 builder.setView(input);

					 // Set up the buttons
					 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					     @Override
					     public void onClick(DialogInterface dialog, int which) {
					         String path = input.getText().toString();
					         ExportTask task = new ExportTask();
							 String[] params = {basepath+"/"+path+".zip"};
							 task.execute(params); 
					     }
					 });
					 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					     @Override
					     public void onClick(DialogInterface dialog, int which) {
					         dialog.cancel();
					     }
					 });

					 builder.show();
					 break;
				case REQUEST_LINK_TO_DBX:
					 DbxAccount account = accountManager.getLinkedAccount();
	                 try {
	                    // Migrate any local datastores.
	                    datastoreManager.migrateToAccount(account);
	                    // Start using the remote datastore manager.
	                    datastoreManager = DbxDatastoreManager.forAccount(account);
	                    setUpDropboxListeners();
	                 } catch (DbxException e) {
	                    e.printStackTrace();
	                 }
	                 // Swap the menu title.
	                 linkMenu.setTitle(R.string.action_unlink);
		             
			}
		}

		//finish();//back to main
		super.onActivityResult(requestCode, resultCode, intent);
		return;
	}

	
	private void setUpDropboxListeners() {
        datastoreManager.addListListener(new DbxDatastoreManager.ListListener() {
            @Override
            public void onDatastoreListChange(DbxDatastoreManager dbxDatastoreManager) {
                // Update the UI when the list of datastores changes.
                MainActivity.this.updateList();
            }
        });
        updateList();
    }
	
	// Update the UI based on the current list of datastores.
    private void updateList() {
    	final GridView gridView = (GridView) findViewById(R.id.gridview);
    	/*
        ArrayList<DbxDatastoreInfo> infos = new ArrayList<DbxDatastoreInfo>();
        try {
            infos.addAll(datastoreManager.listDatastores());
        } catch (DbxException e) {
            e.printStackTrace();
        }
        // Sort by the modified time.
        Collections.sort(infos,
            new Comparator<DbxDatastoreInfo>() {
                @Override
                public int compare(DbxDatastoreInfo a, DbxDatastoreInfo b) {
                    if (a.mtime != null && b.mtime != null) {
                        return a.mtime.compareTo(b.mtime);
                    } else {
                        return a.id.compareTo(b.id);
                    }
                }
            });
    	 */
	 	adapter = new GalleryAdapter(this,datastore,laySize,mMemoryCache);
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
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ImportTask extends AsyncTask<String, Void, Integer> {
		private int SUCCESS = 0;
		private int NO_CSV = 1;

		protected void onPreExecute() {
			showWaitDialog("Import Data","Wait");
			
		}

		@Override
		protected Integer doInBackground(String... params) {
			String path = params[0];
			String csv="";
			try {
				csv = (new Import(getApplicationContext(),datastoreManager.openDefaultDatastore(),laySize,path)).importFile();
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		private int SUCCESS = 0;
		protected void onPreExecute() {
			showWaitDialog("Export Data","Wait");
		}

		@Override
		protected Integer doInBackground(String... params) {
			Export export = new Export(getApplicationContext(),datastoreManager,params[0]);
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
