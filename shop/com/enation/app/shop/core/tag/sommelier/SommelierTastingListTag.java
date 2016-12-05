package com.enation.app.shop.core.tag.sommelier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师列表
 * @author lin
 *2013-8-20下午7:58:00
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
@Scope("prototype")
public class SommelierTastingListTag extends BaseFreeMarkerTag {
	private SommelierManager sommelierManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的详情
	 * {@link Sommelier}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
        HttpServletRequest request = getRequest();
        Integer pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
	    Long userId = NumberUtils.toLong(request.getParameter("user_id"), 0L);
	    Sommelier sommelier = sommelierManager.getByUserId(userId);
	    Page list_tast_note = this.sommelierManager.list_tast_note_tag("id", pageNo, 12, userId);
        Map result = new HashMap();
        sommelier.setImg_url(StringUtils.trimToEmpty(sommelier.getImg_url()));
		if (sommelier.getTasting_count() == null) sommelier.setTasting_count(0);
		if (sommelier.getBad_comment() == null) sommelier.setBad_comment(0);
		if (sommelier.getGood_comment() == null) sommelier.setGood_comment(0);
        
        // 替换图片路径
		String statis = SystemSetting.getStatic_server_domain();
        sommelier.setImg_url(sommelier.getImg_url().replaceAll("fs:",statis)); 
        
        List<Map> list = (List<Map>)list_tast_note.getResult();
        //List<Auction> list = apiAuctionManager.list();
        // 替换图片路径
        for(Map a :list) {
           if(a.get("image") != null) {
             a.put("image", ((String) a.get("image")).replaceAll("fs:",statis)); 
           }
        }
        result.put("sommelier", sommelier);
        result.put("sommelier_id", request.getParameter("sommelier_id"));
        result.put("tastingNoteList", list_tast_note);
        return result;
	}
    
    public SommelierManager getSommelierManager() {
        return sommelierManager;
    }
    
    public void setSommelierManager(SommelierManager sommelierManager) {
        this.sommelierManager = sommelierManager;
    }
	
}
