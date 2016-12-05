package com.enation.framework.resource;

public class Resource {
	
	private String src;
	private int compress;//是否压缩
	private int merge; //是否合并
	private String type;//css\javascript\image
	private boolean iscommon; //是否是公用的
	
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public int getCompress() {
		return compress;
	}
	public void setCompress(int compress) {
		this.compress = compress;
	}
	public int getMerge() {
		return merge;
	}
	public void setMerge(int merge) {
		this.merge = merge;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isIscommon() {
		return iscommon;
	}
	public void setIscommon(boolean iscommon) {
		this.iscommon = iscommon;
	}
 
	
	
	
}
