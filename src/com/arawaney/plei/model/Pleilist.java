package com.arawaney.plei.model;

import java.util.Calendar;

public class Pleilist {

	private long id;
	private String system_id;
	private String name;
	private String image;
	private String coverImage;
	private Calendar updated_at;
	private int deleted;
	private int flaged;
	private int order;
	private int favorite;
	private  String categoryId;
		
	public final static int DELETED = 1;
	public final static int NOT_DELETED = 0;
	
	public final static int FLAGED = 1;
	public final static int NOT_FLAGED = 0;
	
	public final static int FAVORITE = 1;
	public final static int NOT_FAVORITE = 0;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSystem_id() {
		return system_id;
	}

	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Calendar getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Calendar updated_at) {
		this.updated_at = updated_at;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}



	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getFlaged() {
		return flaged;
	}

	public void setFlaged(int flaged) {
		this.flaged = flaged;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

}
