package com.enation.app.b2b2c.core.tag.activity;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.service.impl.ActivityManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class ActivityListTag extends BaseFreeMarkerTag {
    
    ActivityManager activityManager;
    private IStoreMemberManager storeMemberManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        String ctx = this.getRequest().getContextPath();
        HttpServletResponse response = ThreadContextHolder.getHttpResponse();
        
        StoreMember member = storeMemberManager.getStoreMember();
        if(member == null || member.getIs_store() != 1)
        {
            try {
                response.sendRedirect(ctx+"/store/business_center.html?menu=store_index");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        int page = this.getPage();
        int pageSize = this.getPageSize();
        
        Page webPage = activityManager.getPage(null, null, null, null, null, page, pageSize,true);
        webPage.setPage(page);
        return webPage;
    }
    
    public ActivityManager getActivityManager() {
        return activityManager;
    }
    
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }
    
    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }
    
}
