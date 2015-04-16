package net.rizkyzulkarnaen.productgallery.entity;

import java.io.Serializable;

public class Field implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5088553117808929770L;
	/**
	 * 
	 */
	private String name;
	private String category;
	private String value;
	private String id;
	private String article_id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getArticle_id() {
		return article_id;
	}
	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}
	
}
