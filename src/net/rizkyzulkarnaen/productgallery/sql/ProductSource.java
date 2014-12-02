package net.rizkyzulkarnaen.productgallery.sql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.rizkyzulkarnaen.productgallery.entity.Field;
import net.rizkyzulkarnaen.productgallery.entity.Product;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductSource {
	// Database fields
	private String tableName;
	private String tableFieldsName;
	private SQLiteDatabase database;
	private ProductSqlHelper dbHelper;

	private String[] allColumns = { 
			ProductSqlHelper.ID,
			ProductSqlHelper.NO,
			ProductSqlHelper.IMAGE,
			ProductSqlHelper.DATE};

	private String[] allFieldsColumns = { 
			ProductSqlHelper.ID,
			ProductSqlHelper.ARTICLE_ID,
			ProductSqlHelper.CATEGORY,
			ProductSqlHelper.NAME,
			ProductSqlHelper.VALUE};
	public ProductSource(Context context) {
		tableName = ProductSqlHelper.ARTICLE_TABLE;
		tableFieldsName = ProductSqlHelper.FIELDS_TABLE;
		dbHelper = new ProductSqlHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public long createFields(long id,String cat,String name,String val) {
		ContentValues values = new ContentValues();
		values.put(ProductSqlHelper.ARTICLE_ID, id);
		values.put(ProductSqlHelper.CATEGORY,cat);
		values.put(ProductSqlHelper.NAME,name);
		values.put(ProductSqlHelper.VALUE,val);
		try {
			long ret = database.insert(tableFieldsName, null, values);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public ContentValues createValues(Product product) {
		ContentValues values = new ContentValues();
		values.put(ProductSqlHelper.NO, product.getNo());
		values.put(ProductSqlHelper.IMAGE, product.getImage());
		long date = Calendar.getInstance().getTimeInMillis();
		values.put(ProductSqlHelper.DATE, String.valueOf(date));
		return values;
	}

	public long create(Product product) {
		ContentValues values = createValues(product);
		long ret = 0;
		try {
			ret = database.insert(tableName, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public int update(Product product) {
		String ids = Long.toString(product.getId());
		ContentValues values = createValues(product);
		return database.update(tableFieldsName, values,
				ProductSqlHelper.ID + " = " + ids, null);
	}
	
	public int updateImage(long id,String image) {
		ContentValues values = new ContentValues();
		values.put(ProductSqlHelper.IMAGE, image);
		return database.update(tableName, values,
				ProductSqlHelper.ID + " = " +  Long.toString(id), null);
	}

	public void delete(Product product) {
		long id = product.getId();
		database.delete(tableName, ProductSqlHelper.ID
				+ " = " + id, null);
	}
	
	public void deleteAll() {
		database.delete(tableName, "1", null);
		database.delete(tableFieldsName, "1", null);
	}

	public List<Product> getAll() {
		List<Product> products = new ArrayList<Product>();
		try {
			Cursor cursor = database.query(tableName,allColumns,null, null, null,null,ProductSqlHelper.ID+" DESC");
			cursor.moveToLast();
			while (!cursor.isBeforeFirst()) {
				Product product = cursorToProductNoImage(cursor);
				products.add(product);
				cursor.moveToPrevious();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return products;
	}
	
	public List<Field> getFields(long id) {
		List<Field> fields = new ArrayList<Field>();
		String ids = Long.toString(id);
		try {
			Cursor cursor = database.query(tableFieldsName, allFieldsColumns,
					ProductSqlHelper.ARTICLE_ID + " = ? ", new String[] {
							ids}, null, null,ProductSqlHelper.ID+" DESC");
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
	}

	public Product get(long id) {
		Product product = new Product();
		String ids = Long.toString(id);
		try {
			Cursor cursor = database.query(tableName, allColumns,
					ProductSqlHelper.ID + " = ? ", new String[] {
							ids}, null, null,
							ProductSqlHelper.DATE);

			// Cursor cursor =
			// database.rawQuery("SELECT * FROM "+tableName+" WHERE "+NAASqlHelper.COLUMN_ID
			// + "="+Long.toString(id),null);
			if (cursor != null) {
				cursor.moveToFirst();
				product = cursorToProduct(cursor);
			}
		} catch (Exception e) {
			Log.e("error get", e.getMessage());
		}
		return product;
	}
	public Product getByNo(String no) {
		Product product = null;
		try {
			Cursor cursor = database.query(tableName, allColumns,
					ProductSqlHelper.NO + " = ? ", new String[] {
							no}, null, null,null);

			// Cursor cursor =
			// database.rawQuery("SELECT * FROM "+tableName+" WHERE "+NAASqlHelper.COLUMN_ID
			// + "="+Long.toString(id),null);
			if (cursor != null) {
				cursor.moveToFirst();
				product = cursorToProduct(cursor);
			}
		} catch (Exception e) {
			Log.e("error get", e.getMessage());
		}
		return product;
	}
	private Product cursorToProductNoImage(Cursor cursor) {
		Product product = new Product();
		product.setId(cursor.getLong(0));
		product.setNo(cursor.getString(1));
		Date date = new Date(cursor.getLong(3));
		product.setDate(date);
		//List<Field> fields= getFields(product.getId());
		/*
		for(Field field:fields){
			if(field.getName()=="EAN") product.setEan(field.getValue());
			if(field.getName()=="ASIN") product.setAsin(field.getValue());
			if(field.getName()=="Description") product.setDesc(field.getValue());
			if(field.getName()=="Sale Price") product.setPrice(Float.parseFloat(field.getValue()));
			if(field.getName()=="Category 1") product.setCat1(field.getValue());
			if(field.getName()=="Category 2") product.setCat2(field.getValue());
			if(field.getName()=="Category 3") product.setCat3(field.getValue());
		}
		*/
		//product.setFields(fields);
		return product;
	}
	private Product cursorToProduct(Cursor cursor) {
		Product product = new Product();
		product.setId(cursor.getLong(0));
		product.setNo(cursor.getString(1));
		product.setImage(cursor.getString(2));
		Date date = new Date(cursor.getLong(3));
		product.setDate(date);
		//List<Field> fields= getFields(product.getId());
		/*
		for(Field field:fields){
			if(field.getName()=="EAN") product.setEan(field.getValue());
			if(field.getName()=="ASIN") product.setAsin(field.getValue());
			if(field.getName()=="Description") product.setDesc(field.getValue());
			if(field.getName()=="Sale Price") product.setPrice(Float.parseFloat(field.getValue()));
			if(field.getName()=="Category 1") product.setCat1(field.getValue());
			if(field.getName()=="Category 2") product.setCat2(field.getValue());
			if(field.getName()=="Category 3") product.setCat3(field.getValue());
		}
		*/
		//product.setFields(fields);
		return product;
	}
	
	private Field cursorToField(Cursor cursor) {
		Field field = new Field();
		field.setId(cursor.getLong(0));
		field.setArticle_id(cursor.getLong(1));
		field.setCategory(cursor.getString(2));
		field.setName(cursor.getString(3));
		field.setValue(cursor.getString(4));
		return field;
	}
}