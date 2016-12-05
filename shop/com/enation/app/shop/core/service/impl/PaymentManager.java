package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.PaymentPluginBundle;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 支付方式管理
 * @author kingapex
 *2010-4-4下午02:26:19
 */
@SuppressWarnings("unchecked")
public class PaymentManager extends BaseSupport<PayCfg> implements IPaymentManager {
    private Log log = LogFactory.getLog(PaymentManager.class);
	//支付插件桩
	private PaymentPluginBundle paymentPluginBundle;
	
	public List<PayCfg> list() {
		String sql = "select * from payment_cfg where type!='alipayMobilePlugin' and type!='alipayWapPlugin'";
		return this.baseDaoSupport.queryForList(sql, PayCfg.class);
	}
	
	@Override
	public List<PayCfg> listAll() {
		String sql = "select * from payment_cfg order by id";
		return this.baseDaoSupport.queryForList(sql, PayCfg.class);
	}

	public PayCfg get(Integer id) {
	    log.info("手机端支付：支付方式" + id);
		String sql = "select * from payment_cfg where id=?";
		return this.baseDaoSupport.queryForObject(sql, PayCfg.class, id);
	}

	public PayCfg get(String pluginid) {
	    log.info("手机端支付：支付方式" + pluginid);
		String sql = "select * from payment_cfg where type=?";
		return this.baseDaoSupport.queryForObject(sql, PayCfg.class, pluginid);
	}
	
	public Double countPayPrice(Integer id) {
		return 0D;
	}
	
	public Integer add(String name, String type, String biref,
			Map<String, String> configParmas) {
		if(StringUtil.isEmpty(name)) throw new IllegalArgumentException("payment name is  null");
		if(StringUtil.isEmpty(type)) throw new IllegalArgumentException("payment type is  null");
		if(configParmas == null) throw new IllegalArgumentException("configParmas  is  null");
		
		PayCfg payCfg = new PayCfg();
		payCfg.setName(name);
		payCfg.setType(type);
		payCfg.setBiref(biref);
		payCfg.setConfig( JSONObject.fromObject(configParmas).toString());
		this.baseDaoSupport.insert("payment_cfg", payCfg);
		return this.baseDaoSupport.getLastId("payment_cfg");
	}

	public Map<String, String> getConfigParams(Integer paymentId) {
		PayCfg payment =this.get(paymentId);
		String config  = payment.getConfig();
		if(null == config ) return new HashMap<String,String>();
		return (Map<String, String>) JSONObject.toBean(JSONObject.fromObject(config), Map.class);
	}
	
	public Map<String, String> getConfigParams(String pluginid) {
		PayCfg payment =this.get(pluginid);
		String config  = payment.getConfig();
		if(null == config ) return new HashMap<String,String>();
		return (Map<String, String>) JSONObject.toBean(JSONObject.fromObject(config), Map.class);
	}	

	public void edit(Integer paymentId, String name,String type, String biref,
			Map<String, String> configParmas) {
		if(StringUtil.isEmpty(name)) throw new IllegalArgumentException("payment name is  null");
		if(configParmas == null) throw new IllegalArgumentException("configParmas  is  null");
		PayCfg payCfg = new PayCfg();
		payCfg.setName(name);
		payCfg.setBiref(biref);
		payCfg.setType(type);
		payCfg.setConfig( JSONObject.fromObject(configParmas).toString());	
		this.baseDaoSupport.update("payment_cfg", payCfg, "id="+ paymentId);
	}
	
	public void delete(Integer[] idArray) {
		if(idArray==null || idArray.length==0) return;
		String idStr = StringUtil.arrayToString(idArray, ",");
		String sql  ="delete from payment_cfg where id in("+idStr+")";
		this.baseDaoSupport.execute(sql);
	}
	
	public List<IPlugin> listAvailablePlugins() {
		return this.paymentPluginBundle.getPluginList();
	}
	
	public String getPluginInstallHtml(String pluginId,Integer paymentId) {
		IPlugin installPlugin =null;
		List<IPlugin>  plguinList =  this.listAvailablePlugins();
		for(IPlugin plugin :plguinList){
			if(plugin instanceof AbstractPaymentPlugin){
				if( ((AbstractPaymentPlugin) plugin).getId().equals(pluginId)){
					installPlugin = plugin;
					break;
				}
			}
		}
		
		if(installPlugin==null) throw new ObjectNotFoundException("plugin["+pluginId+"] not found!"); 
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		fp.setClz(installPlugin.getClass());
		if(paymentId!=null){
			Map<String,String> params = this.getConfigParams(paymentId);
			Iterator<String> keyIter  = params.keySet().iterator();
			while(keyIter.hasNext()) {
				 String key  = keyIter.next();
				 String value = params.get(key);
				 fp.putData(key, value);
			}
		}
		return fp.proessPageContent();
	}
	
	public PaymentPluginBundle getPaymentPluginBundle() {
		return paymentPluginBundle;
	}

	public void setPaymentPluginBundle(PaymentPluginBundle paymentPluginBundle) {
		this.paymentPluginBundle = paymentPluginBundle;
	}
}
