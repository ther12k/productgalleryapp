package net.rizkyzulkarnaen.productgallery.sql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Image;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class DropboxProductSource {
	// Database fields
	private String tableName;
	private String tableFieldsName;
	private String tableImageName;

	private DbxDatastoreManager datastoreManager;
	private DbxDatastore datastore;
	public DropboxProductSource(Context context,DbxDatastore datastore) {
		tableName = ProductSqlHelper.ARTICLE_TABLE;
		tableImageName = ProductSqlHelper.IMAGE_TABLE;
		tableFieldsName = ProductSqlHelper.FIELDS_TABLE;
		this.datastore = datastore;
	}
	
	public void close(){
		try{
			datastore.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String createFields(String id,String cat,String name,String val){
		try{		
			DbxTable fieldTbl = datastore.getTable(tableFieldsName);
			DbxRecord ret = fieldTbl.insert()
			.set(ProductSqlHelper.ARTICLE_ID, id)
			.set(ProductSqlHelper.CATEGORY,cat)
			.set(ProductSqlHelper.NAME,name)
			.set(ProductSqlHelper.VALUE,val);
			datastore.sync();
			
			return ret.getId();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public String create(Product product) {
		long date = Calendar.getInstance().getTimeInMillis();
		try{
			DbxTable productTbl = datastore.getTable(tableName);
			DbxRecord ret = productTbl.insert()
			.set(ProductSqlHelper.NO, product.getNo())
			.set(ProductSqlHelper.IMAGE, product.getImageArray())
			.set(ProductSqlHelper.DATE, date);
			datastore.sync();
			return ret.getId();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public String createNoImage(Product product) {
		long date = Calendar.getInstance().getTimeInMillis();
		try{
			DbxTable productTbl = datastore.getTable(tableName);
			DbxRecord ret = productTbl.insert()
			.set(ProductSqlHelper.NO, product.getNo())
			.set(ProductSqlHelper.DATE, date);
			datastore.sync();
			return ret.getId();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public DbxRecord update(Product product) throws DbxException {
		DbxRecord firstResult = getProductRecordById(product.getId());
		long date = Calendar.getInstance().getTimeInMillis();
		DbxRecord ret = firstResult
				.set(ProductSqlHelper.NO, product.getNo())
				.set(ProductSqlHelper.DATE, date);
		datastore.sync();
		return ret;
	}
	public String updateField(String id,String value){
		try{
			DbxRecord firstResult = getFieldRecordById(id);
			if(firstResult!=null){
				DbxRecord ret = firstResult.set(ProductSqlHelper.VALUE, value);
				datastore.sync();
				return ret.getId();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public DbxRecord updateImage(String id,byte[] image) throws DbxException {
		DbxRecord firstResult = getProductRecordById(id);
		if(firstResult!=null){
			DbxRecord ret = firstResult.set(ProductSqlHelper.IMAGE, image);
			datastore.sync();
			return ret;
		}
		return null;
	}
	
	public DbxRecord addImage(String no,byte[] image) throws DbxException {
		long date = Calendar.getInstance().getTimeInMillis();
		DbxTable imageTbl = datastore.getTable(tableImageName);
		DbxRecord ret = imageTbl.insert()
				.set(ProductSqlHelper.IMAGE, image)
				.set(ProductSqlHelper.NO, no)
				.set(ProductSqlHelper.DATE, date);
		datastore.sync();
		return ret;
	}
	
	public DbxRecord createImage(String no,byte[] image) throws DbxException {
		return addImage(no,image);
	}

	public void delete(Product product) throws DbxException {
		DbxRecord firstResult = getProductRecordById(product.getId());
		if(firstResult!=null){
			firstResult.deleteRecord();
			datastore.sync();
		}
	}
	
	public void deleteFieldById(String id){
		DbxRecord firstResult = getFieldRecordById(id);
		try {
			if(firstResult!=null){
				firstResult.deleteRecord();
				datastore.sync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAll() throws DbxException {
		datastoreManager.deleteDatastore("default");
	}

	public List<Product> getAll() {
		List<Product> products = new ArrayList<Product>();
		try{
			DbxTable productTbl = datastore.getTable(tableName);
			try {
				DbxTable.QueryResult results = productTbl.query();
				if(results.count()>0){
					while (results.iterator().hasNext()) {
						DbxRecord record = results.iterator().next();
						Product product = recordToProductNoImage(record);
						products.add(product);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return products;
	}
	
	public List<Field> getFields(String id) {
		List<Field> fields = new ArrayList<Field>();
		try {
			DbxTable fieldTbl = datastore.getTable(tableFieldsName);
			DbxFields queryParams = new DbxFields().set(ProductSqlHelper.ARTICLE_ID,id);
			DbxTable.QueryResult results = fieldTbl.query(queryParams);
			if(results.count()>0){
				while (results.iterator().hasNext()) {
					DbxRecord record = results.iterator().next();
					Field field = recordToField(record);
					fields.add(field);
				}
			}
		} catch (Exception e) {
			Log.e("error get", e.getMessage());
		}
		return fields;
	}
	
	public List<Field> getFieldsSortByTab(String id) {
		/*
		List<Field> fields = new ArrayList<Field>();
		String ids = Long.toString(id);
		try {
			Cursor cursor = database.query(tableFieldsName, allFieldsColumns,
					ProductSqlHelper.ARTICLE_ID + " = ? ", new String[] {
							ids}, null, null,ProductSqlHelper.CATEGORY+" DESC,"+ProductSqlHelper.NAME+" DESC");
			cursor.moveToLast(); //gara2 lupa ini jadi ngebug semalaman
			while (!cursor.isBeforeFirst()) {
				Field field = cursorToField(cursor);
				fields.add(field);
				cursor.moveToPrevious();
			}
		} catch (Exception e) {
			Log.e("error get", e.getMessage());
		}
		return fields;
		*/
		return getFields(id);
	}
	
	public DbxRecord getProductRecordById(String id){
		try{
			DbxTable productTbl = datastore.getTable(tableName);
			DbxRecord record = productTbl.getOrInsert(id);
			return record;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public DbxRecord getProductRecordByNo(String no){
		try{
			DbxTable productTbl = datastore.getTable(tableName);
			DbxFields queryParams = new DbxFields().set(ProductSqlHelper.NO,no);
			DbxTable.QueryResult results = productTbl.query(queryParams);
			if(results.count()>0)
				return results.iterator().next();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public DbxRecord getFieldRecordById(String id){
		try{
			DbxTable fieldTbl = datastore.getTable(tableFieldsName);
			DbxRecord record = fieldTbl.getOrInsert(id);
			return record;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public Product get(String id){
		Product product = null;
		DbxRecord record = getProductRecordById(id);
		if(record!=null){
			product = recordToProduct(record);
		}
		return product;
	}
	public Product getByNo(String no) {
		Product product = null;
		DbxRecord record = getProductRecordByNo(no);
		if(record!=null){
			product = recordToProduct(record);
		}
		return product;
	}
	
	public List<Image> getImagesByNo(String no) {
		List<Image> images= new ArrayList<Image>();
		DbxTable imageTbl = datastore.getTable(tableImageName);
		try {
			DbxFields queryParams = new DbxFields().set(ProductSqlHelper.NO,no);
			DbxTable.QueryResult results = imageTbl.query(queryParams);
			if(results.count()>0){
				while (results.iterator().hasNext()) {
					DbxRecord record = results.iterator().next();
					Image image = recordToImage(record);
					images.add(image);
				}
			}
		} catch (Exception e) {
			Log.e("error get", e.getMessage());
		}
		return images;
	}
	
	private Product recordToProductNoImage(DbxRecord record) {
		Product product = new Product();
		product.setId(record.getId());
		product.setNo(record.getString(ProductSqlHelper.NO));
		Date date = new Date(record.getLong(ProductSqlHelper.DATE));
		product.setDate(date);
		return product;
	}
	private Product recordToProduct(DbxRecord record) {
		Product product = new Product();
		product.setId(record.getId());
		if(record.hasField(ProductSqlHelper.NO))
			product.setNo(record.getString(ProductSqlHelper.NO));
		if(record.hasField(ProductSqlHelper.IMAGE))
			product.setImageArray(record.getBytes(ProductSqlHelper.IMAGE));
		Date date = new Date(record.getLong(ProductSqlHelper.DATE));
		product.setDate(date);
		return product;
	}
	private Image recordToImage(DbxRecord record) {
		Image image = new Image();
		image.setId(record.getId());
		if(record.hasField(ProductSqlHelper.NO))
			image.setNo(record.getString(ProductSqlHelper.NO));
		if(record.hasField(ProductSqlHelper.IMAGE))
			image.setImageArray(record.getBytes(ProductSqlHelper.IMAGE));
		Date date = new Date(record.getLong(ProductSqlHelper.DATE));
		image.setDate(date);
		return image;
	}
	private Field recordToField(DbxRecord record) {
		Field field = new Field();
		field.setId(record.getId());
		field.setArticle_id(record.getString(ProductSqlHelper.ARTICLE_ID));
		field.setCategory(record.getString(ProductSqlHelper.CATEGORY));
		field.setName(record.getString(ProductSqlHelper.NAME));
		field.setValue(record.getString(ProductSqlHelper.VALUE));
		return field;
	}
}