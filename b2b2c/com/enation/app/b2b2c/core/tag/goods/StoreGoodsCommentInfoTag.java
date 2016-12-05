package com.enation.app.b2b2c.core.tag.goods;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品评论信息标签
 * @author fenlongli
 *
 */
@Component
public class StoreGoodsCommentInfoTag extends BaseFreeMarkerTag{
	private IStoreMemberCommentManager storeMemberCommentManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id = NumberUtils.toInt(params.get("goods_id").toString());
		return storeMemberCommentManager.getGoodsStore_desccredit(goods_id);
	}
	public IStoreMemberCommentManager getStoreMemberCommentManager() {
		return storeMemberCommentManager;
	}
	public void setStoreMemberCommentManager(
			IStoreMemberCommentManager storeMemberCommentManager) {
		this.storeMemberCommentManager = storeMemberCommentManager;
	}
}
