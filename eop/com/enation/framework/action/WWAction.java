package com.enation.framework.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings({ "rawtypes", "serial" })
public class WWAction extends ActionSupport implements SessionAware {
	protected final Logger logger = Logger.getLogger(getClass());
	protected Page webpage;

	protected Map session = null; 

	protected List msgs = new ArrayList();

	protected Map urls = new HashMap();
	
	//需要新打开页面的url
	protected Map blankUrls= new HashMap();
	
	protected static final String MESSAGE = "message";

	protected static final String JSON_MESSAGE = "json_message";

	

	protected String script = "";

	protected String json;
	
	protected int isJsonp = 0;	//是否使用jsonp  0 或 null=否  1=是
	protected String callback = "callback";	//jsonp自定义回调函数名
 

	protected int page;

	protected int pageSize;
	protected int rows;
	
	// 页面id,加载页面资源所用
	protected String pageId;

	private String sort;
	private String order;
 

	public String list_ajax() {
	
		return "list";
	}

	public String add_input() {

		return "add";
	}

	public String edit_input() {

		return "edit";
	}

	public int getPageSize() {
		if( this.rows==0) this.rows=SystemSetting.getBackend_pagesize();
		return rows;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		page = page < 1 ? 1 : page;
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

 

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	protected HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	protected HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * 直接输出.
	 * 
	 * @param contentType
	 *            内容的类型.html,text,xml的值见后，json为"text/x-json;charset=UTF-8"
	 */
	protected void render(String text, String contentType) {
		try {
			HttpServletResponse response = getResponse();
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * 直接输出纯字符串.
	 */
	protected void renderText(String text) {
		render(text, "text/plain;charset=UTF-8");
	}

	/**
	 * 直接输出纯HTML.
	 */
	protected void renderHtml(String text) {
		render(text, "text/html;charset=UTF-8");
	}

	/**
	 * 直接输出纯XML.
	 */
	protected void renderXML(String text) {
		render(text, "text/xml;charset=UTF-8");
	}
	
	
	protected void showSuccessJson(String message){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\"}";
	}
	
	protected void showSuccessJson(String message, String addon){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1,\"addon\":\"" + addon + "\"}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\",\"addon\":\"" + addon + "\"}";
	}
	
	protected void showPlainSuccessJson(String message){
	  ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
    if(StringUtil.isEmpty(message))
      this.json="{\"result\":1}";
    else
      this.json="{\"result\":1,\"message\":\""+message+"\"}";
  }
	
	protected void showPlainSuccessJsonZyh(String message){
	      ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
	    if(StringUtil.isEmpty(message))
	      this.json="{\"result\":1}";
	    else
	      this.json="{\"result\":1,\"message\":"+message+"}";
	  }
	
	protected void showSuccessJson(String message,Integer id){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\",\"id\":\""+id+"\"}";
	}
	
	protected void showSuccessJson(String message,Long id){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\",\"id\":\""+id+"\"}";
	}
	
	protected void showPlainSuccessJson(String message,Long id){
	  ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
    if(StringUtil.isEmpty(message))
      this.json="{\"result\":1}";
    else
      this.json="{\"result\":1,\"message\":\""+message+"\",\"id\":\""+id+"\"}";
  }
	
	protected void showErrorJson(String message){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":0}";
		else
			this.json="{\"result\":0,\"message\":\""+message+"\"}";
	}
	
	protected void showPlainErrorJson(String message){
	  ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
    if(StringUtil.isEmpty(message))
      this.json="{\"result\":0}";
    else
      this.json="{\"result\":0,\"message\":\""+message+"\"}";
  }
	
	/**
	 * wabpage转gropjson
	 */
	public void showGridJson(Page page){
		this.json= "{\"total\":"+page.getTotalCount()+",\"rows\":"+JSONArray.fromObject(page.getResult()).toString()+"}";
	}

	/**
	 * list转gropjson
	 * @param list
	 */
	public void showGridJson(List list){
		this.json= "{\"total\":"+list.size()+",\"rows\":"+JSONArray.fromObject(list).toString()+"}";
	}
	
	/**
	 * JSONP输出处理.
	 * 
	 * @author baoxiufeng
	 */
	public void jsonp(JSONObject result) {
		String callback = getRequest().getParameter("callback");
        if (StringUtils.isNotBlank(callback)) {
            PrintWriter writer = null;
            try {
            	JSONObject jsonObject = JSONObject.parseObject(json);
            	if (jsonObject.getInteger("result") == 0 || jsonObject.getInteger("result") == -1) {
            		result.put("errMsg", jsonObject.getString("message"));
            	} else {
            		result.put("success", true);
            		result.put("errCode", 0);
            	}
                writer = ThreadContextHolder.getHttpResponse().getWriter();
                writer.write(HttpUtils.jsonp(callback, result.toJSONString()));
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }
        }
	}
	
	public List getMsgs() {
		return msgs;
	}

	public void setMsgs(List msgs) {
		this.msgs = msgs;
	}

	public Map getUrls() {
		return urls;
	}

	public void setUrls(Map urls) {
		this.urls = urls;
	}

	public Page getWebpage() {
		return webpage;
	}

	public void setWebpage(Page webpage) {
		this.webpage = webpage;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Map getBlankUrls() {
		return blankUrls;
	}

	public void setBlankUrls(Map blankUrls) {
		this.blankUrls = blankUrls;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getCtx(){
		HttpServletRequest req = this.getRequest();
		if(req!=null){
			return req.getContextPath();
		}else{
			return null;
		}
	}

	public int getIsJsonp() {
		return isJsonp;
	}

	public void setIsJsonp(int isJsonp) {
		this.isJsonp = isJsonp;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	
}
