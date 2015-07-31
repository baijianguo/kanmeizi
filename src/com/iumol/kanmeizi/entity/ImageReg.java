package com.iumol.kanmeizi.entity;

import java.io.Serializable;

public class ImageReg implements Serializable {

	private int id;
	private String url;
	private String title;
	private String logo;
	private String thumReg;
	private String imgsReg;
	private String attriOrder;
	private String detailsLoopParam;
	private int detailsLoopCount;
	private String detailsUrlPre;
	private boolean agentPc;

	private static final long serialVersionUID = 1L;

	public ImageReg() {
	};

	public ImageReg(int id, String url, String title) {
		this.id = id;
		this.url = url;
		this.title = title;
	}

	public boolean isAgentPc() {
		return agentPc;
	}

	public void setAgentPc(boolean agentPc) {
		this.agentPc = agentPc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getThumReg() {
		return thumReg;
	}

	public void setThumReg(String thumReg) {
		this.thumReg = thumReg;
	}

	public String getImgsReg() {
		return imgsReg;
	}

	public void setImgsReg(String imgsReg) {
		this.imgsReg = imgsReg;
	}

	public String getAttriOrder() {
		return attriOrder;
	}

	public void setAttriOrder(String attriOrder) {
		this.attriOrder = attriOrder;
	}

	public String getDetailsLoopParam() {
		return detailsLoopParam;
	}

	public void setDetailsLoopParam(String detailsLoopParam) {
		this.detailsLoopParam = detailsLoopParam;
	}

	public int getDetailsLoopCount() {
		return detailsLoopCount;
	}

	public void setDetailsLoopCount(int detailsLoopCount) {
		this.detailsLoopCount = detailsLoopCount;
	}

	public String getDetailsUrlPre() {
		return detailsUrlPre;
	}

	public void setDetailsUrlPre(String detailsUrlPre) {
		this.detailsUrlPre = detailsUrlPre;
	}

}
