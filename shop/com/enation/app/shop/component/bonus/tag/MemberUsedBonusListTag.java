package com.enation.app.shop.component.bonus.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取会员已使用的线下发放优惠卷列表
 * @author kingapex
 *2013-9-27上午11:51:06
 */

@Component
@Scope("prototype")
public class MemberUsedBonusListTag extends BaseFreeMarkerTag {
	/**
	 * 获取会员可用红包列表
	 * @param 无
	 * @return 红包列表，List<Map>型
	 * map内容
	 * 所有MemberBonus的属性{@link MemberBonus }以及：
	 * 
	 */
	@Override
	protected Object exec(Map arg0) throws TemplateModelException {
		List<MemberBonus> bonusList  =BonusSession.get();
		bonusList=bonusList==null?new ArrayList<MemberBonus>():bonusList;
		
		return bonusList;
	}

}
