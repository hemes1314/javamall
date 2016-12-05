/**
 * 
 */
package com.enation.app.shop.component.goodsindex.action.backend;

import java.util.Map;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.ProgressContainer;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.component.goodsindex.service.impl.GoodsIndexManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.jms.EopJmsMessage;
import com.enation.framework.jms.EopProducer;

/**
 * 商品索引生成action
 * @author kingapex
 *2015-5-14
 */

@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	@Result(name="input", type="freemarker", location="/com/enation/app/shop/component/goodsindex/action/backend/create.html")
})
public class GoodsIndexAction extends WWAction {
	private EopProducer eopProducer;

	
	private IGoodsIndexManager goodsIndexManager;
	
	/**
	 * 转向生成页面
	 */
	public String execute(){
		
		return this.INPUT;
	}
	
	
	/**
	 * 生成索引
	 * @return
	 */
	public String create(){
		
		try {
			
			if (ProgressContainer.getProgress(GoodsIndexManager.PRGRESSID)!=null ){
				this.showErrorJson("有索引任务正在进行中，需等待本次任务完成后才能再次生成。");
			} else{
				
				EopJmsMessage jmsMessage = new EopJmsMessage();
				jmsMessage.setProcessorBeanId("goodsIndexManager");
				eopProducer.send(jmsMessage);
					this.showSuccessJson("索引任务下达成功");
			}
		} catch (Exception e) {
			this.logger.error("生成出错", e);
			 this.showErrorJson("生成出错");
		}
		
		
		return this.JSON_MESSAGE;
	}


	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}


	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}


	public EopProducer getEopProducer() {
		return eopProducer;
	}


	public void setEopProducer(EopProducer eopProducer) {
		this.eopProducer = eopProducer;
	}
	
	
	
	
	
}
