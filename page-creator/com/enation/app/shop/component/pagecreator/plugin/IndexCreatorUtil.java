package com.enation.app.shop.component.pagecreator.plugin;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.component.pagecreator.service.impl.GeneralPageCreator;
import com.enation.eop.processor.HttpCopyWrapper;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;


public class IndexCreatorUtil {

    public static void createIndexPage(){ 
        HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
        HttpCopyWrapper newrequest = new HttpCopyWrapper(request); 
        
        String root_path = StringUtil.getRootPath(); 
        String pagename=("/index.html");
        newrequest.setServletPath(pagename);
        ThreadContextHolder.setHttpRequest(newrequest);
        
        String pagePath =root_path+"/html/"+pagename;
        System.out.println("... ..."+pagePath);
        GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
        pageCreator.parse(pagename);
        
    }
}
