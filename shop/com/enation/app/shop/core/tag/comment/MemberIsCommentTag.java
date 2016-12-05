package com.enation.app.shop.core.tag.comment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 会员是否可以评论标签
 * @author lina
 * 2014-2-19
 */
@Component
@Scope("prototype")
public class MemberIsCommentTag extends BaseFreeMarkerTag {
	private IMemberOrderItemManager memberOrderItemManager;
	
	/**
	 * 会员是否可以评论标签
	 * @param 无
	 * @return Map型，其中的key结构为：
	 * goodsid:商品id
	 * isLogin:是否登录
	 * isBuy:是否购买了
	 * isCommented:是否评论过
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap(5);
		Integer goods_id =(Integer) params.get("goods_id");
		result.put("goods_id",goods_id);
		Member member = UserConext.getCurrentMember();
		if(member == null){
			result.put("isLogin",false);
		}else{
			result.put("isLogin",true);
			int buyCount = memberOrderItemManager.count(member.getMember_id(), goods_id);
			int commentCount = memberOrderItemManager.count(member.getMember_id(), goods_id,1);
			result.put("isBuy", buyCount > 0);
			result.put("isCommented",commentCount >= buyCount);
		}
		return result;
	}
	
	public IMemberOrderItemManager getMemberOrderItemManager() {
		return memberOrderItemManager;
	}
	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}

}
