package com.arawaney.plei.model;

import java.util.Calendar;

public class Category {

	private long id;
	private String system_id;
	private String name;
	private String imageFile;
	private Calendar updated_at;



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

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public Calendar getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Calendar updated_at) {
		this.updated_at = updated_at;
	}

}
