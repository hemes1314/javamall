package com.enation.app.b2b2c.core.tag.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.net.aso.s;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.sun.jmx.snmp.ThreadContext;

import freemarker.template.TemplateModelException;
@Component
/**
 * 检查b2b2c会员
 * @author LiFenLong
 *
 */
public class CheckB2b2cMemberTag extends BaseFreeMarkerTag{
	private IStoreMemberManager storeMemberManager;
	
	private IStoreManager storeManager;
	
	@Override
	/**
	 * 获取当前登录会员
	 */
	protected Object exec(Map params) throws TemplateModelException {
		String ctx = this.getRequest().getContextPath();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		if("/".equals(ctx)){
			ctx="";
		}
		StoreMember member = storeMemberManager.getStoreMember();
		
		
		if (member == null) {
			
			try {
				response.sendRedirect(ctx+"/store/login.html");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else
		{
		   String uri = request.getRequestURI();
		   if (member.getIs_store() == null) member.setIs_store(0);
		   if(uri.indexOf("/store/business_center.html") != -1)
		   {
		       return member;
		   }
		    
		   //没有开店用户
		   if(member.getStore_id() == null) 
		   {
		       if(uri.indexOf("/store/apply/step1.html") == -1)
		       {
		           try {
	                   //如果当前用户不是商家用户，跳转商家中心页面
		               response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
	                   return null;
	                } catch (IOException e) {
	                   e.printStackTrace();
	                   return null;
	                }
		       }
		   }else
		   {
		       Store store = storeManager.getStoreByMember(member.getMember_id());
		       if(store.getDisabled() == 0 || store.getDisabled() == 2) //待审核(已关闭)
		       {
    		        try {
                       response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
                       return null;
                    } catch (IOException e) {
                       e.printStackTrace();
                       return null;
                    }
		       }else if(store.getDisabled() == 1) //已审核
		       {
		           if(uri.indexOf("/store/apply/step1.html") != -1 || uri.indexOf("/store/apply/reApply.html") != -1)
		           {
		               try {
	                       response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
	                       return null;
	                    } catch (IOException e) {
	                       e.printStackTrace();
	                       return null;
	                    }
		           }
               }else if(store.getDisabled() == -1) //审核未通过
               {
				   if (uri.indexOf("/store/apply/reApply.html") == -1) {
                       try {
                           response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
                           return null;
                        } catch (IOException e) {
                           e.printStackTrace();
                           return null;
                        }
                   }
               }
		       
		       
		   }
		  
		}
		
	    /*********** 2015/10/20 humaodong ****************/
	    // the member added by backend is_store=null
	    if (member.getIs_store() == null) member.setIs_store(0);
	    /*************************************************/
		return member;
		
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
    public IStoreManager getStoreManager() {
        return storeManager;
    }
    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }
	
}
