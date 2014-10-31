package com.iumol.kanmeizi.entity;

import java.io.Serializable;

/**
 * get data failed reason
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-11-25
 */
public class MzituUrl implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String url;
	private String image_url;

	public MzituUrl(String url, String image_url) {

		this.url = url;
		this.image_url = image_url;
	}

	public MzituUrl(String title, String url, String image_url) {
		this.title = title;
		this.url = url;
		this.image_url = image_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return image_url;
	}

	public void setImageUrl(String image_url) {
		this.image_url = image_url;
	}

}
