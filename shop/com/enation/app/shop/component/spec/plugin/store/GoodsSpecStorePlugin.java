package com.enation.app.shop.component.spec.plugin.store;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.component.spec.service.IGoodsSpecStoreManager;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.StoreLog;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsStorePlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.app.shop.core.plugin.goods.IStockSaveEvent;
import com.enation.app.shop.core.plugin.goods.IStoreSaveEvent;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IStoreLogManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 规格商品的库存维护插件
 * 
 * @author kingapex 2012-3-22下午5:07:03
 */
@Component
public class GoodsSpecStorePlugin extends AbstractGoodsStorePlugin implements IGoodsDeleteEvent,IStockSaveEvent,IStoreSaveEvent {

	private IGoodsSpecStoreManager goodsSpecStoreManager;
	private IProductManager productManager;
	private IStoreLogManager storeLogManager;
	private IAdminUserManager adminUserManager;
	private IPermissionManager permissionManager;

	@Override
	public void onGoodsDelete(Integer[] goodsid) {

	}

	@Override
	public String getStoreHtml(Map goods) {
		Integer goodsid = (Integer) goods.get("goods_id");
		List<Map> depotList = goodsSpecStoreManager.listGoodsStore(goodsid);
		List<String> specNameList = productManager.listSpecName(goodsid);

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("depotList", depotList);
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.setPageName("store_input");
		return freeMarkerPaser.proessPageContent();
	}

	/**
	 * 返回进货的HTML
	 */
	@Override
	public String getStockHtml(Map goods) {
		Integer goodsid = (Integer) goods.get("goods_id");
		List<Map> depotList = goodsSpecStoreManager.listGoodsStore(goodsid);
		List<String> specNameList = productManager.listSpecName(goodsid);

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("depotList", depotList);
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.setPageName("stock_input");
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public String getShipHtml(Map goods) {
		Integer goodsid = (Integer) goods.get("goods_id");
		List<Map> depotList = goodsSpecStoreManager.listGoodsStore(goodsid);
		List<String> specNameList = productManager.listSpecName(goodsid);

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("depotList", depotList);
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.setPageName("ship_input");
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public void onStoreSave(Map goods) {
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Integer goodsid = (Integer) goods.get("goods_id");
		String[] idAr = request.getParameterValues("id");
		String[] productidAr = request.getParameterValues("productid");
		String[] depotidAr = request.getParameterValues("depotid");
		String[] storeAr = request.getParameterValues("storeNum");

		int total = this.goodsSpecStoreManager.saveStore(goodsid, idAr,	productidAr, depotidAr, storeAr);

		// 库存日志
		AdminUser adminUser = UserConext.getCurrentAdminUser();

		if (isSuperAdmin) {
			for (String deptoid : depotidAr) {
				int did = StringUtil.toInt(deptoid, true);
				StoreLog storeLog = new StoreLog();
				storeLog.setGoodsid(goodsid);
				storeLog.setGoodsname(goods.get("name").toString());
				storeLog.setDepot_type(0);
				storeLog.setOp_type(1);
				storeLog.setDepotid(did);
				storeLog.setDateline(DateUtil.getDateline());
				storeLog.setNum(total);
				storeLog.setUserid(adminUser.getUserid());
				storeLog.setUsername(adminUser.getUsername());
				storeLogManager.add(storeLog);
			}
		} else if (isDepotAdmin) {
			DepotUser depotUser = (DepotUser) adminUser;
			StoreLog storeLog = new StoreLog();
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(goods.get("name").toString());
			storeLog.setDepot_type(1);
			storeLog.setOp_type(0);
			storeLog.setDepotid(depotUser.getDepotid());
			storeLog.setDateline(DateUtil.getDateline());
			storeLog.setNum(total);
			storeLog.setUserid(adminUser.getUserid());
			storeLog.setUsername(adminUser.getUsername());
			storeLogManager.add(storeLog);
		} else {
			throw new RuntimeException("没有操作库存的权限");
		}
	}

	/**
	 * 进货保存事件
	 */

	@Override
	public void onStockSave(Map goods) {
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Integer goodsid = (Integer) goods.get("goods_id");
		String[] idAr = request.getParameterValues("id");
		String[] productidAr = request.getParameterValues("productid");
		String[] depotidAr = request.getParameterValues("depotid");
		String[] storeAr = request.getParameterValues("storeNum");

		int total = this.goodsSpecStoreManager.stock(goodsid, idAr,	productidAr, depotidAr, storeAr);
		if(depotidAr==null){ //没有进货，直接返回
			return ;
		}
		// 库存日志
		AdminUser adminUser = UserConext.getCurrentAdminUser();

		if (isSuperAdmin) {
			for (String deptoid : depotidAr) {
				int did = StringUtil.toInt(deptoid, true);
				StoreLog storeLog = new StoreLog();
				storeLog.setGoodsid(goodsid);
				storeLog.setGoodsname(goods.get("name").toString());
				storeLog.setDepot_type(0);
				storeLog.setOp_type(1);
				storeLog.setDepotid(did);
				storeLog.setDateline(DateUtil.getDateline());
				storeLog.setNum(total);
				storeLog.setUserid(adminUser.getUserid());
				storeLog.setUsername(adminUser.getUsername());
				storeLogManager.add(storeLog);
			}
		} else if (isDepotAdmin) {
			DepotUser depotUser = (DepotUser) adminUser;
			StoreLog storeLog = new StoreLog();
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(goods.get("name").toString());
			storeLog.setDepot_type(1);
			storeLog.setOp_type(0);
			storeLog.setDepotid(depotUser.getDepotid());
			storeLog.setDateline(DateUtil.getDateline());
			storeLog.setNum(total);
			storeLog.setUserid(adminUser.getUserid());
			storeLog.setUsername(adminUser.getUsername());
			storeLogManager.add(storeLog);
		} else {
			throw new RuntimeException("没有操作库存的权限");
		}
	}

	@Override
	public void onShipSave(Map goods) {
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Integer goodsid = (Integer) goods.get("goods_id");
		String[] idAr = request.getParameterValues("id");
		String[] productidAr = request.getParameterValues("productid");
		String[] depotidAr = request.getParameterValues("depotid");
		String[] storeAr = request.getParameterValues("store");

		int total = this.goodsSpecStoreManager.ship(goodsid, idAr, productidAr,	depotidAr, storeAr);

		// 库存日志
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (isSuperAdmin) {
			for (String deptoid : depotidAr) {
				int did = StringUtil.toInt(deptoid, true);
				StoreLog storeLog = new StoreLog();
				storeLog.setGoodsid(goodsid);
				storeLog.setGoodsname(goods.get("name").toString());
				storeLog.setDepot_type(0);
				storeLog.setOp_type(1);
				storeLog.setDepotid(did);
				storeLog.setDateline(DateUtil.getDateline());
				storeLog.setNum(total);
				storeLog.setUserid(adminUser.getUserid());
				storeLog.setUsername(adminUser.getUsername());
				storeLogManager.add(storeLog);
			}
		} else if (isDepotAdmin) {
			DepotUser depotUser = (DepotUser) adminUser;
			StoreLog storeLog = new StoreLog();
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(goods.get("name").toString());
			storeLog.setDepot_type(1);
			storeLog.setOp_type(0);
			storeLog.setDepotid(depotUser.getDepotid());
			storeLog.setDateline(DateUtil.getDateline());
			storeLog.setNum(total);
			storeLog.setUserid(adminUser.getUserid());
			storeLog.setUsername(adminUser.getUsername());
			storeLogManager.add(storeLog);
		} else {
			throw new RuntimeException("没有操作库存的权限");
		}
	}

	@Override
	public boolean canBeExecute(Map goods) {
		return true;
	}

	@Override
	public String getWarnNumHtml(Map goods) {
		return null;
	}

	@Override
	public void onWarnSave(Map goods) {

	}

	public IGoodsSpecStoreManager getGoodsSpecStoreManager() {
		return goodsSpecStoreManager;
	}

	public void setGoodsSpecStoreManager(IGoodsSpecStoreManager goodsSpecStoreManager) {
		this.goodsSpecStoreManager = goodsSpecStoreManager;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IStoreLogManager getStoreLogManager() {
		return storeLogManager;
	}

	public void setStoreLogManager(IStoreLogManager storeLogManager) {
		this.storeLogManager = storeLogManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

}
