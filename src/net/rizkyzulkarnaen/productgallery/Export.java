package net.rizkyzulkarnaen.productgallery;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class Export {
	private ZipOutputStream out;
    private ProductSource productSource;
    private List<Product> items = new ArrayList<Product>();
    private String path;
    
    public Export(Context context,String path){
    	productSource = new ProductSource(context);
    	productSource.open();
		items = (ArrayList<Product>) productSource.getAll();
		productSource.close();
		this.path = path;
    }
    
	public void makeZip() {
		FileOutputStream dest=null;
        try {
            dest = new FileOutputStream(path);
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out = new ZipOutputStream(new BufferedOutputStream(dest));
        for(Product product:items){
	        ZipEntry entry = new ZipEntry(product.getNo()+".jpg");
	        try {
	            out.putNextEntry(entry);
	            out.write(Base64.decode(product.getImage(),Base64.DEFAULT));
	            Log.v("put", "Adding: ");
	        }
	        catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        }
        closeZip();
    }

    private void closeZip () {
        try {
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
