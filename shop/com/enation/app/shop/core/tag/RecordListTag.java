package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 成交记录标签
 * @author LiFenLong
 *
 */
public class RecordListTag extends BaseFreeMarkerTag{
	private IDaoSupport daoSupport;
	/**
	 * @param goods_id,商品Id
	 * @return
	 * uname,会员名
	 * create_time,购买时间
	 * order_item_num,购买数量
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String sql="select * from  es_transaction_record where goods_id=? order by rog_time desc";
		int pageNo = this.getPage();
		int pageSize = this.getPageSize();
		Integer goods_id =  (Integer)params.get("goods_id");
		List list= daoSupport.queryForListPage(sql, pageNo, pageSize, goods_id);
		
		Page page=new Page(0, list.size(), pageSize, list); 
		return page;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
