/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：广告api  
 * 修改人：Sylow  
 * 修改时间：2015-08-22
 * 修改内容：增加获得广告列表api
 */
package com.enation.app.shop.mobile.action.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 广告位api
 * 提供广告相关api
 * @author Sylow
 * @version v1.0
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileAdvApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("adv")
public class AdvApiAction extends WWAction {

	private IAdvManager advManager;
	private IAdColumnManager adColumnManager;
	
	private long advid;

	/**
	 * @param acid
	 *            广告位id
	 * @return Map广告信息数据，其中key结构为 adDetails:广告位详细信息 {@link AdColumn}
	 *         advList:广告列表 {@link Adv}
	 */
	@SuppressWarnings("unchecked")
	public String advList() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String acid = request.getParameter("acid").toString();
		acid = acid == null ? "0" : acid;
		Map<String, Object> data = new HashMap<String,Object>();
		try {
			AdColumn adDetails = adColumnManager.getADcolumnDetail(Long
					.valueOf(acid));
			List<Adv> advList = null;

			if (adDetails != null) {
				advList = advManager.listAdv(Long.valueOf(acid));
			}

			advList = advList == null ? new ArrayList<Adv>() : advList;
			// if(!advList.isEmpty()){
			data.put("adDetails", adDetails);// 广告位详细信息
			data.put("advList", advList);// 广告列表
			// }
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 根据某个广告id获取广告信息
	 * 
	 * @param advid
	 * 
	 * @return result:1调用成功 0调用失败 data: Adv对象json
	 * 
	 */
	public String getOneAdv() {

		try {

			Adv adv = advManager.getAdvDetail(advid);
			this.json = JsonMessageUtil.getObjectJson(adv);

		} catch (Exception e) {
			this.logger.error("获取某个广告出错", e);
			this.showErrorJson(e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
	}
	
	
	
	public IAdvManager getAdvManager() {
		return advManager;
	}

	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

	public long getAdvid() {
		return advid;
	}

	public void setAdvid(long advid) {
		this.advid = advid;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
	}

}
