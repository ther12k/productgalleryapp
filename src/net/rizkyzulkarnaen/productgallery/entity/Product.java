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
	private String no="";
	private float price;
	private String image;
	private byte[] imageArray;
	private Date date;
	private String id;
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
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public byte[] getImageArray() {
		return imageArray;
	}

	public void setImageArray(byte[] imageArray) {
		this.imageArray = imageArray;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
