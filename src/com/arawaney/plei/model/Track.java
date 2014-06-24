package com.arawaney.plei.model;

import java.util.Calendar;

public class Track {

	private long id;
	private String system_id;
	private String name;
	private Calendar updated_at;
	private  String url;
	private  String youtubeUrl;
	private  String artist;
		
	


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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getYoutubeUrl() {
		return youtubeUrl;
	}

	public void setYoutubeUrl(String youtubeUrl) {
		this.youtubeUrl = youtubeUrl;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}


}
