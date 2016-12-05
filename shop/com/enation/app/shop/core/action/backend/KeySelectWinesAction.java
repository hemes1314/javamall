package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.dom4j.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.impl.KeySelectWinesManager;
import com.enation.app.shop.core.service.impl.YuemoManager;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonUtil;
import com.gomecellar.workflow.utils.Constants;
import com.gomecellar.workflow.utils.HttpClientUtils;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("keySelectWines")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/keySelectWinesTags/keySelect_list.html"),
	@Result(name="add_ksw", type="freemarker",location="/shop/admin/keySelectWinesTags/keySelect_add.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class KeySelectWinesAction extends WWAction {
    private Integer[] tag_id;
    private Integer[] id;
    Tag tag;
    private List<Tag> keySelectWinesTags;
    KeySelectWinesManager keySelectWinesManager;


	public String edit_keySelectWines() {
	    try{ 
	       tag.setIs_key_select("1");
	       this.keySelectWinesManager.edit(tag);
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        this.showSuccessJson("添加成功");
        return JSON_MESSAGE;
    }
	
	public String add_ksw()
	{
	    return "add_ksw";
	}
	
   public String delete() {
       HttpServletRequest request = ServletActionContext.getRequest(); 
       try {
           this.keySelectWinesManager.delete(tag_id);
           //this.keySelectWinesManager.delete(id);
           this.showSuccessJson("删除成功");
       } catch (RuntimeException e) {
           this.showErrorJson("删除失败");
       }
       return this.JSON_MESSAGE;
   }
	
	
	public String listKeySelectWinesTag() {
	        try{
	            this.webpage = this.keySelectWinesManager.list(this.getSort(), this.getPage(), this.getPageSize());
	        }catch (RuntimeException e) {
	            this.logger.error("数据库运行异常", e);
	            this.showPlainErrorJson(e.getMessage());
	        }
	        this.showGridJson(webpage);
	        return JSON_MESSAGE;
	}

	   public String list() {
	       return "list";
   }
    
    public KeySelectWinesManager getKeySelectWinesManager() {
        return keySelectWinesManager;
    }

    
    public void setKeySelectWinesManager(KeySelectWinesManager keySelectWinesManager) {
        this.keySelectWinesManager = keySelectWinesManager;
    }

    
    public Tag getTag() {
        return tag;
    }

    
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    
    public List<Tag> getKeySelectWinesTags() {
        return keySelectWinesTags;
    }

    
    public void setKeySelectWinesTags(List<Tag> keySelectWinesTags) {
        keySelectWinesTags = keySelectWinesTags;
    }



    
    
    public Integer[] getTag_id() {
        return tag_id;
    }

    
    public void setTag_id(Integer[] tag_id) {
        this.tag_id = tag_id;
    }

    public Integer[] getId() {
        return id;
    }

    
    public void setId(Integer[] id) {
        this.id = id;
    }

	
}
