package com.arawaney.plei.model;

import java.util.Calendar;

public class Cover {

	private long id;
	private String system_id;
	private String name;	
	private Calendar updated_at;
	private String type;
	private String imageFile;
	private String categoryId;
	private String pleilistId;
	private int section;
	
	public final static String TYPE_CATEGORY = "Category";
	public final static String TYPE_PLEILIST = "Pleilist";

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
	public Calendar getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Calendar updated_at) {
		this.updated_at = updated_at;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getImageFile() {
		return imageFile;
	}
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getPleilistId() {
		return pleilistId;
	}
	public void setPleilistId(String pleilistId) {
		this.pleilistId = pleilistId;
	}
	public int getSection() {
		return section;
	}
	public void setSection(int section) {
		this.section = section;
	}

	
}
