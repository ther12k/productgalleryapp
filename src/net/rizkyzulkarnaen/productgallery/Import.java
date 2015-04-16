package net.rizkyzulkarnaen.productgallery;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.DropboxProductSource;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;

public class Import {
	private final String FILENAME="import.csv";
    private DropboxProductSource productSource;
    private String path;
    private Point laySize;
    
    public Import(Context context,DbxDatastore datastore,Point laySize,String path){
    	productSource = new DropboxProductSource(context,datastore);
		this.path = path;
		this.laySize = laySize;
    }
    
    public String importFile() {
    	String csv = "";
    	String[] tabs=null;
		String[] fields=null;
		try{ 
			FileInputStream fin = new FileInputStream(path); 
			ZipInputStream zin = new ZipInputStream(fin); 
			ZipEntry ze = null; 
			while ((ze = zin.getNextEntry()) != null&& !ze.isDirectory()) { 
				Log.v("Decompress", "Unzipping " + ze.getName()); 
		        
				if (ze.getName().toString().equalsIgnoreCase(FILENAME)) { 
				   
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
		                		Product product = productSource.getByNo(columns[0].trim());
		                		String id = "";
			                    if(product!=null){
			                    	id = product.getId();
			                    }else{
			                    	product = new Product();
									product.setNo(columns[0].trim());
									id = productSource.createNoImage(product);
			                    }
								for(int j=0;j<columns.length;j++){
									if(columns[j].trim().isEmpty()) continue;
									productSource.createFields(id, tabs[j].trim(), fields[j].trim(), columns[j].trim());
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
				        //photo.recycle();
			            zin.closeEntry(); 
			            //photo.recycle();
						String name = "";
						if(ze.getName().toString().endsWith(".jpeg"))
							name = ze.getName().replace(".jpeg", "");
						else
							name = ze.getName().replace(".jpg", "");
						
						name = name.trim();
						if(name.contains("_")){
							String[] names = name.split("_");
							name = names[0];
							Product product = productSource.getByNo(name);
							
							if(product!=null){
								productSource.addImage(product.getNo(),Helper.bitmapToArray(bitmap));
							}else{
								productSource.createImage(name,Helper.bitmapToArray(bitmap));
							}
						}else{
							Product product = productSource.getByNo(name);
							
							if(product!=null){
								productSource.updateImage(product.getId(),Helper.bitmapToArray(bitmap));
							}else{
								product = new Product();
								product.setNo(name);
								product.setImage(Helper.bitmapToString(bitmap));
								productSource.create(product);
							}
						}
						bitmap.recycle();
					}catch(Exception e){
						Log.e("Decompress", "unzip "+ze.getName(), e); 
					}
				}
		    } 
			zin.close();
		}catch(Exception e) {
			Log.e("Decompress", "unzip", e); 
		}
		productSource.close();
		return csv;
    }
}
