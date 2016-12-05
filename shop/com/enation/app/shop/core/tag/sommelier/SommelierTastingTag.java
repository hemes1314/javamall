package com.enation.app.shop.core.tag.sommelier;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.TastingNote;
import com.enation.app.shop.core.service.impl.StastingNoteManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师列表
 * @author lin
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierTastingTag extends BaseFreeMarkerTag {
	private StastingNoteManager stastingNoteManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的详情
	 * {@link TastingNote}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    HttpServletRequest request = getRequest();
	    Integer tastingId = NumberUtils.toInt(request.getParameter("tasting_id"), 0);
	    TastingNote tast_note = this.stastingNoteManager.getTastingNote(tastingId);
	    if (tast_note != null) {
	    	String imageurl = tast_note.getImage();
	    	if (imageurl == null) {
	    		tast_note.setImage("");
	    	} else {
	    		String statis = SystemSetting.getStatic_server_domain();
	            imageurl = imageurl.replaceAll("fs:",statis);  
	            tast_note.setImage(imageurl);
	    	}
	    }
        return tast_note;
	}
    
    public StastingNoteManager getStastingNoteManager() {
        return stastingNoteManager;
    }
    
    public void setStastingNoteManager(StastingNoteManager stastingNoteManager) {
        this.stastingNoteManager = stastingNoteManager;
    }
      
	
}
