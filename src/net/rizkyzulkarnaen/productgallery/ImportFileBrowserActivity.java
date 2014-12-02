package net.rizkyzulkarnaen.productgallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rizkyzulkarnaen.productgallery.iconified.IconifiedText;
import net.rizkyzulkarnaen.productgallery.iconified.IconifiedTextListAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


public class ImportFileBrowserActivity extends ListActivity {
	
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
	
    protected static final int SUB_ACTIVITY_REQUEST_CODE = 1337;

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private List<IconifiedText> fileEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = new File("/");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
		this.setSelection(0);
	}

	/**
	 * This function browses to the 
	 * root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
    }
	
	/**
	 * This function browses up one level 
	 * according to the field: currentDirectory
	 */
	private void upOneLevel(){
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	
	private void browseTo(final File aDirectory){
		// On relative we display the full path in the title.
		if(this.displayMode == DISPLAYMODE.RELATIVE)
			this.setTitle(aDirectory.getAbsolutePath() + " :: " + 
					getString(R.string.app_name));
		if (aDirectory.isDirectory()){
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		}else{
			
			showYesNoMsg(
					R.string.open_confirm_title,
					R.string.open_confirm_message,
					new YesNoMsgAction() {

						@Override
						public void onYes() {
							openFile(aDirectory);
						}

						@Override
						public void onNo() {
							// TODO Auto-generated
							// method stub
							return;
						}
					});
		}
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
	
	private void openFile(File aFile){
		try {
			Intent i = new Intent();
			i.putExtra("file", aFile.getAbsolutePath());
			setResult(RESULT_OK,i);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();	
		this.fileEntries.clear();	
		// and the ".." == 'Up one level'
		
		Drawable currentIcon = null;
		if(files!=null)
		for (File currentFile : files){
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);

				switch (this.displayMode) {
					case ABSOLUTE:
						/* On absolute Mode, we show the full path */
						this.directoryEntries.add(new IconifiedText(currentFile
								.getPath(), currentIcon));
						break;
					case RELATIVE: 
						/* On relative Mode, we have to cut the
						 * current-path at the beginning */
						int currentPathStringLenght = this.currentDirectory.
														getAbsolutePath().length();
						this.directoryEntries.add(new IconifiedText(
								currentFile.getAbsolutePath().
								substring(currentPathStringLenght),
								currentIcon));

						break;
				}

			}else{
				String fileName = currentFile.getName();
				/* Determine the Icon to be used, 
				 * depending on the FileEndings defined in:
				 * res/values/fileendings.xml. */
				if(checkEndsWithInStringArray(fileName, getResources().
								getStringArray(R.array.fileEndingZip))){
					currentIcon = getResources().getDrawable(R.drawable.ic_launcher); 

					switch (this.displayMode) {
						case ABSOLUTE:
							/* On absolute Mode, we show the full path */
							this.fileEntries.add(new IconifiedText(currentFile
									.getPath(), currentIcon));
							break;
						case RELATIVE: 
							/* On relative Mode, we have to cut the
							 * current-path at the beginning */
							int currentPathStringLenght = this.currentDirectory.
															getAbsolutePath().length();
							this.fileEntries.add(new IconifiedText(
									currentFile.getAbsolutePath().
									substring(currentPathStringLenght),
									currentIcon));

							break;
					}
				}else continue;		

			}		
		}
		Collections.sort(this.directoryEntries);
		Collections.sort(this.fileEntries);
		
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		for(IconifiedText icon:fileEntries){
			directoryEntries.add(0, icon);
		}
		if(this.currentDirectory.getParent() != null){
			directoryEntries.add(0,new IconifiedText(getString(R.string.up_one_level), 
					getResources().getDrawable(R.drawable.back)));
		}
		itla.setListItems(directoryEntries);
		this.setListAdapter(itla);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectionRowID = position;
		String selectedFileString = this.directoryEntries.get(selectionRowID).getText();
		if (selectedFileString.equals(getString(R.string.current_dir))) {
			this.openFile(this.currentDirectory);
		} else if(selectedFileString.equals(getString(R.string.up_one_level))){
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch(this.displayMode){
				case RELATIVE:
					clickedFile = new File(this.currentDirectory.getAbsolutePath() 
												+ this.directoryEntries.get(selectionRowID).getText());
					break;
				case ABSOLUTE:
					clickedFile = new File(this.directoryEntries.get(selectionRowID).getText());
					break;
			}
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
	
	/** Checks whether checkItsEnd ends with 
	 * one of the Strings from fileEndings */
	private boolean checkEndsWithInStringArray(String checkItsEnd, 
					String[] fileEndings){
		for(String aEnd : fileEndings){
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
}