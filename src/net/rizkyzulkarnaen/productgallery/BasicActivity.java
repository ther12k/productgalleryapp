package net.rizkyzulkarnaen.productgallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

public class BasicActivity extends Activity {
	protected ProgressDialog progressDialog;
	protected Point laySize = null;
	public DbxDatastoreManager datastoreManager;
	DbxAccountManager accountManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Display display = getWindowManager().getDefaultDisplay();
        accountManager = DbxAccountManager.getInstance(getApplicationContext(),
        		getString(R.string.dropbox_app_key),
        		getString(R.string.dropbox_app_secret));
        if (accountManager.hasLinkedAccount()) {
            // If there's a linked account, use that.
            try {
                datastoreManager = DbxDatastoreManager.forAccount(accountManager.getLinkedAccount());
                //linkUnlinkButton.setText("Unlink from Dropbox");
            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }
        } else {
            // Otherwise, use a local datastore manager.
            datastoreManager = DbxDatastoreManager.localManager(accountManager);
        }
		laySize = new Point();
		display.getSize(laySize);
		super.onCreate(savedInstanceState);
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
	protected void showYesNoMsg(int idTitle,String idMessage,final YesNoMsgAction yesno ){
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
	protected void showYesNoMsg(String idTitle,String idMessage,final YesNoMsgAction yesno ){
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
	protected void showWaitDialog(String title,String message) {
		
		try{
				progressDialog = ProgressDialog.show(BasicActivity.this, title, message,
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
}
