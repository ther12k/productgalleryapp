package net.rizkyzulkarnaen.productgallery.entity;

import java.io.Serializable;
import java.util.Date;

import net.rizkyzulkarnaen.productgallery.Helper;
import android.graphics.Bitmap;

public class Image implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5153488021324082110L;
	private String no;
	private String image;
	private byte[] imageArray;
	private Date date;
	private long article_id;
	private String id;
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
	
	public long getArticle_id() {
		return article_id;
	}

	public void setArticle_id(long article_id) {
		this.article_id = article_id;
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
