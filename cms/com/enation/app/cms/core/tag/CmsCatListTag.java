package com.enation.app.cms.core.tag;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.shop.core.model.Article;
import com.enation.app.shop.core.utils.ParseXml;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 通过ID，读取该ID下分类
 * @author wanghongjun
 *
 */
@Component
@Scope("prototype")
public class CmsCatListTag  extends BaseFreeMarkerTag{
	private IDataManager dataManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = getRequest();
		String cat = request.getParameter("cat_id")==null?"":request.getParameter("cat_id");
		String pageNoStr = request.getParameter("page");
		Integer catid=0;
		//解析xml
		if(cat.matches("\\d*")){
	          catid= NumberUtils.toInt(cat);
	    }else{
              String path  = StringUtil.getRootPath();
              ParseXml px = new ParseXml(path+"/mappingToArticle.xml");
              String elementPath = "body/appArticle/"+cat;
              String elementTxt = px.getElementText(elementPath);
              if (elementTxt != null) {
            	  catid = NumberUtils.toInt(elementTxt, 0);
              }
       }
       
		if (pageNoStr == null || pageNoStr.isEmpty()) {
			pageNoStr = "1";
		}
		Integer pageNo = NumberUtils.toInt(pageNoStr);
		int pageSize = 6;
		Page page = new Page();
		try {
			dataManager.pageForWeb(catid, pageNo, pageSize);
			List list = (List) page.getResult();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) list.get(i);
				if ( map!= null) {
					map.put("image", UploadUtil.replacePath((String)map.get("image")));
				}
			}
			page.setCurrentPageNo(pageNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

}
