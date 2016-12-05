package com.enation.app.shop.mobile.action.articel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.shop.core.utils.ParseXml;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 文档Api 
 * 
 * @author lxl
 
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("article")
@Results({
    @Result(name="wine", type="freemarker", location="/shop/admin/article/wine.html"),
    @Result(name="winels", type="freemarker", location="/shop/admin/article/winels.html"),
    @Result(name="wdetail", type="freemarker", location="/shop/admin/article/winedetail.html"),
    @Result(name="help", location="/themes/default/server.html")
})
public class DataSelectorAction extends WWAction {
	
	private IDataManager dataManager;
	private IDataCatManager dataCatManager;
	private List<DataCat> catList;
	private List<DataField> fieldList;
	private IDataFieldManager dataFieldManager;
	private Integer catid;
	private Integer id;
	private String keyWords;
	private int PAGE_SIZE=10;
	
	// 文件列表数据集
	private Object data;
	
//	public String showDialog(){
//		catList =dataCatManager.listAllChildren(0);
//		return "dialog";
//	}
	
	/**
	 * 跳转到在线客服页.
	 * 
	 * @return 在线客服页视图
	 */
	public String help() {
		return "help";
	}
	
	/**
	 * 跳转到文章列表.
	 * 
	 * @return 文章列表视图
	 */
	public String wine() {
	    HttpServletRequest request = getRequest();
	    Map<String, Object> dataMap = new HashMap<String, Object>();
        try{
            String cat = request.getParameter("cat");
            //解析xml 
            String path  = StringUtil.getRootPath();
            ParseXml px = new ParseXml(path+"/mappingToArticle.xml");
            String elementPath = "body/appArticle/"+cat;

            String eleTxt = px.getElementText(elementPath);
            Integer catid = 0;
            if (StringUtils.isNotBlank(eleTxt)) {
                catid = NumberUtils.toInt(px.getElementText(elementPath).trim().toString());
            }
            int page = NumberUtils.toInt(request.getParameter("page"),1);
            this.fieldList = dataFieldManager.listByCatId(catid);
            this.webpage =dataManager.pageForWeb(catid, page, 20);
            List list = (List) webpage.getResult();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = (Map<String, Object>) list.get(i);
                if ( map != null) {
                    map.put("image", UploadUtil.replacePath((String)map.get("image")));
                }
            }
            
            dataMap.put("result", webpage.getResult());
        }catch ( Exception e){
            dataMap.put("message", "好文章总是来的有点慢，我们的小编正在努力... ");
        }
        this.data = dataMap;
        if (request.getParameterMap().containsKey("1")) {
            return "winels";
        } else {
            return "wine";
        }
    }
	
	/**
     * 跳转到文章详情.
     * 
     * @return 文章详情视图
     */
	public String wdetail(){
        HttpServletRequest request = getRequest();
        try {
            Integer catid = NumberUtils.toInt(request.getParameter("catid"));
            Integer id = NumberUtils.toInt(request.getParameter("id"));
            this.fieldList = dataFieldManager.listByCatId(catid);
            Map  list = dataManager.getForMobile(id, catid, false);
            this.data = JSONArray.fromObject(list);
        } catch (Exception e) {
        }
        return "wdetail";
    }
	
	public String list(){
	    try{
	        HttpServletRequest request = getRequest();
	        String cat = request.getParameter("cat");
	        //解析xml 
	        String path  = StringUtil.getRootPath();
	        ParseXml px = new ParseXml(path+"/mappingToArticle.xml");
	        String elementPath = "body/appArticle/"+cat;

            Integer catid = NumberUtils.toInt(px.getElementText(elementPath).trim().toString());
            int page = NumberUtils.toInt(request.getParameter("page"), 1);
            this.fieldList = dataFieldManager.listByCatId(catid);
	        this.webpage =dataManager.pageForWeb(catid, page, 20);
	        List list = (List) webpage.getResult();
	        for (int i = 0; i < list.size(); i++) {
	            Map<String, Object> map = (Map<String, Object>) list.get(i);
	            if ( map != null) {
	                map.put("image", UploadUtil.replacePath((String)map.get("image")));
	            }
	        }

	        this.json = JsonMessageUtil.getMobileObjectJson(webpage);
	    }catch ( Exception e){
	        this.showPlainErrorJson("运行异常"+e);
	    }
		
		return WWAction.JSON_MESSAGE;
	}
	public String detail(){
		HttpServletRequest request = getRequest();
        Integer catid = NumberUtils.toInt(request.getParameter("catid"));
        Integer id = NumberUtils.toInt(request.getParameter("id"));
        this.fieldList = dataFieldManager.listByCatId(catid);
        Map  list = dataManager.getForMobile(id, catid, false);
		this.json = JsonMessageUtil.getMobileListJson(list);
		return WWAction.JSON_MESSAGE;
		
	}
	public String search (){
	    
	    try{
	        HttpServletRequest request = getRequest();
	        String cat = request.getParameter("cat");
	        //解析xml 
	        String path  = StringUtil.getRootPath();
	        ParseXml px = new ParseXml(path+"/mappingToArticle.xml");
	        String elementPath = "body/appArticle/"+cat;
	        int page = NumberUtils.toInt(request.getParameter("page"),1);
            Integer catid = NumberUtils.toInt(px.getElementText(elementPath).trim().toString());

	        Page  art= dataManager.searchForApp(page,PAGE_SIZE,keyWords,catid);
	        this.json = JsonMessageUtil.getMobileObjectJson(art);
	    }catch(RuntimeException e){
	        this.showPlainErrorJson("数据库运行异常"+e);
	    }
	    return WWAction.JSON_MESSAGE;
	}
   public String searchWords (){
        
        try{
            HttpServletRequest request = getRequest();
            String keyword = request.getParameter("keyword");
            int page = NumberUtils.toInt(request.getParameter("page"),1);

            Page  art= dataManager.searchWordsForApp(page,PAGE_SIZE,keyword);
            List<Map> list = (List) art.getResult();
            for (Map map : list){
                String title = (String)map.get("title");
                title = title.replace("；", " ");
                title = title.replace(";", " ");
                map.put("title", title);
                map.put("image",UploadUtil.replacePath((String)map.get("image")));
            }
            this.json = JsonMessageUtil.getMobileObjectJson(art);
        }catch(RuntimeException e){
            this.showPlainErrorJson("数据库运行异常"+e);
        }
        return WWAction.JSON_MESSAGE;
    }
   /*
    * 模糊查寻时间
    */
   public String getYears (){
       
       try{
           HttpServletRequest request = getRequest();
           String keyword = request.getParameter("keyword");
           List list  = dataManager.getYears(keyword);
           
          
           this.json = JsonMessageUtil.getMobileListJson(list);
       }catch(RuntimeException e){
           this.showPlainErrorJson("数据库运行异常"+e);
       }
       return WWAction.JSON_MESSAGE;
   }
   /*
    * 查询酒评分
    * 
    */
 public String wineScore (){
       
       try{
           HttpServletRequest request = getRequest();
           String keyword = request.getParameter("keyword");
           String year = request.getParameter("year");
           int page = NumberUtils.toInt(request.getParameter("page"),1);
           
           Page webpage  = dataManager.getWineScore(page,PAGE_SIZE,keyword,year);
          
           this.json = JsonMessageUtil.getMobileObjectJson(webpage);
       }catch(RuntimeException e){
           this.showPlainErrorJson("数据库运行异常"+e);
       }
       return WWAction.JSON_MESSAGE;
   }
	/*
     * 点赞接口
     */
    public String click(){
        try{
            dataManager.toClick(id, catid);  
            this.showPlainSuccessJson("成功");
        }catch( RuntimeException e){
            this.showPlainErrorJson("数据库运行异常");
        }
        return   WWAction.JSON_MESSAGE;
    }
    
    public String searchArticle(){
         try{
             HttpServletRequest request = getRequest();
             int page = NumberUtils.toInt(request.getParameter("page"),1);
             this.webpage =dataManager.searchArticle(page, PAGE_SIZE ,keyWords);
             List list = (List) webpage.getResult();
             for (int i = 0; i < list.size(); i++) {
                 Map<String, Object> map = (Map<String, Object>) list.get(i);
                 if ( map != null) {
                     map.put("image", UploadUtil.replacePath((String)map.get("image")));
                 }
             }
             this.json = JsonMessageUtil.getMobileObjectJson(webpage);
         }catch(RuntimeException e){
             this.showPlainErrorJson("查询数据失败");
         }
        return WWAction.JSON_MESSAGE;
        
    }
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public IDataCatManager getDataCatManager() {
		return dataCatManager;
	}

	public void setDataCatManager(IDataCatManager dataCatManager) {
		this.dataCatManager = dataCatManager;
	}

	public List<DataCat> getCatList() {
		return catList;
	}

	public void setCatList(List<DataCat> catList) {
		this.catList = catList;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public IDataFieldManager getDataFieldManager() {
		return dataFieldManager;
	}

	public void setDataFieldManager(IDataFieldManager dataFieldManager) {
		this.dataFieldManager = dataFieldManager;
	}

	public List<DataField> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<DataField> fieldList) {
		this.fieldList = fieldList;
	}
    
    public String getKeyWords() {
        return keyWords;
    }
    
    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}
