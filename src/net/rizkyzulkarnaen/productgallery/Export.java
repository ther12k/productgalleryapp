package net.rizkyzulkarnaen.productgallery;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.opencsv.CSVWriter;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class Export {
	private int ERROR = -1;
	private int SUCCESS = 0;
	private ZipOutputStream out;
    private ProductSource productSource;
    private List<Product> items = new ArrayList<Product>();
    private String path;
    
    protected HashMap<String, ArrayList<String>> tabs = new HashMap<String, ArrayList<String>>();
    
    public Export(Context context,String path){
    	productSource = new ProductSource(context);
    	productSource.open();
		items = (ArrayList<Product>) productSource.getAll();
		productSource.close();
		this.path = path;
    }
    
    public int makeZip() {
		FileOutputStream dest=null;
        try {
            dest = new FileOutputStream(path);
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ERROR;
        }
        out = new ZipOutputStream(new BufferedOutputStream(dest));
        List<List<Field>> fieldsArray= new ArrayList<List<Field>>();
        for(Product product:items){
	        ZipEntry entry = new ZipEntry(product.getNo()+".jpg");
	        try {
	            out.putNextEntry(entry);
	        	productSource.open();
	        	product = productSource.get(product.getId());
	    		productSource.close();
	    		if(product.getImage()!=null)
	            out.write(Base64.decode(product.getImage(),Base64.DEFAULT));
	    		out.flush();
	            Log.v("put", "Adding: ");
	            productSource.open();
	            List<Field> productFields = productSource.getFieldsSortByTab(product.getId());
	            fieldsArray.add(productFields);
	            for(Field field:productFields){
	            	if(!tabs.containsKey(field.getCategory())) {
	            		ArrayList<String> fields = new ArrayList<String>();
	            		fields.add(field.getName());
	            		tabs.put(field.getCategory(), fields);
	            	}else{
	            		ArrayList<String> fields = tabs.get(field.getCategory());
	            		if(!fields.contains(field.getName())){
		            		fields.add(field.getName());
		            		tabs.put(field.getCategory(), fields);
	            		}
	            	}
	            }
	            productSource.close();
	            out.closeEntry();
	        }
	        catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        }
        for(Map.Entry<String,ArrayList<String>> field:tabs.entrySet()){
        	ArrayList<String> fields = field.getValue();
        	Collections.sort(fields);
        	tabs.put(field.getKey(), fields);
        }
        ZipEntry entry = new ZipEntry("import.csv"); // create a zip entry and add it to ZipOutputStream
        try {
			out.putNextEntry(entry);
			List<String> tabCols = new ArrayList<String>();
			List<String> fieldCols = new ArrayList<String>();
			
	        for(Map.Entry<String,ArrayList<String>> field:tabs.entrySet()){
	        	for(String f:field.getValue()){
	        		tabCols.add(field.getKey());
	        		fieldCols.add(f);
	        	}
	        }
	        List<String[]> rowList = new ArrayList<String[]>();
	        rowList.add(tabCols.toArray(new String[tabCols.size()]));
	        rowList.add(fieldCols.toArray(new String[fieldCols.size()]));
	        for(List<Field> fields:fieldsArray){
	        	List<String> row = new ArrayList<String>();
		        for(Map.Entry<String,ArrayList<String>> field:tabs.entrySet()){
		        	for(String f:field.getValue()){
		        		String col="";
		        		for(Field fieldProduct:fields){
		        			if(fieldProduct.getCategory().equals(field.getKey())&&fieldProduct.getName().equals(f)){
		        				col = fieldProduct.getValue();
		        				break;
		        			}
		        		}
		        		row.add(col);
		        	}
		        }

		        rowList.add(row.toArray(new String[row.size()]));
	        }
        	CSVWriter writer = new CSVWriter(new OutputStreamWriter(out),';',CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        	writer.writeAll(rowList);  
            writer.close();
            out.closeEntry();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
            out.close();
            return SUCCESS;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ERROR;
        }
    }
}
