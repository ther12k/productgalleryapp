package net.rizkyzulkarnaen.productgallery.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

import net.rizkyzulkarnaen.productgallery.Helper;

public class Product implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5153488021324082110L;
	private String no;
	private String ean;
	private String asin;
	private String desc;
	private float price;
	private String cat1;
	private String cat2;
	private String cat3;
	private String image;
	private Date date;
	private long id;
	private List<Field> fields;
	public String getImage() {
		return image;
	}
	
	public Bitmap getImageBitmap() {
		return Helper.stringToBitmap(image);
	}
	
	public void setImageBitmap(Bitmap img) {
		if(img!=null)
		this.image = Helper.bitmapToString(img);
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
	}
	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getCat1() {
		return cat1;
	}
	public void setCat1(String cat1) {
		this.cat1 = cat1;
	}
	public String getCat2() {
		return cat2;
	}
	public void setCat2(String cat2) {
		this.cat2 = cat2;
	}
	public String getCat3() {
		return cat3;
	}
	public void setCat3(String cat3) {
		this.cat3 = cat3;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}
