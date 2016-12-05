package com.enation.eop.resource.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.enation.framework.database.NotDbField;

/**
 * @author lzf
 *         <p>
 *         created_time 2009-11-19 下午03:20:40
 *         </p>
 * @version 1.0
 */
public class ThemeUri extends Resource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3537303897321923063L;
	private Integer themeid;
	private String uri;
	private String path;
	private String pagename;
	private int point;
	private int sitemaptype;
	private String keywords;
	private String description;
	private int httpcache;

	private Pattern pattern;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getThemeid() {
		return themeid;
	}

	public void setThemeid(Integer themeid) {
		this.themeid = themeid;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPagename() {
		return pagename;
	}

	public void setPagename(String pagename) {
		this.pagename = pagename;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getSitemaptype() {
		return sitemaptype;
	}

	public void setSitemaptype(int sitemaptype) {
		this.sitemaptype = sitemaptype;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHttpcache() {
		return httpcache;
	}

	public void setHttpcache(int httpcache) {
		this.httpcache = httpcache;
	}

	@NotDbField
	public Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile("^" + this.uri + "$", 2 | Pattern.DOTALL);
		}
		return pattern;
	}

}