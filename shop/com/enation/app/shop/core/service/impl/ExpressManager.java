package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.enation.app.base.core.model.ExpressPlatform;
import com.enation.app.base.core.model.SmsPlatform;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.app.base.core.plugin.sms.ISmsSendEvent;
import com.enation.app.shop.core.service.IExpressManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.plugin.IPlugin;

public class ExpressManager extends BaseSupport implements IExpressManager {

	@Override
	public List getList() {
		List list = this.daoSupport.queryForList("select * from es_express_platform");
		return list;
	}

	@Override
	public void add(ExpressPlatform platform) {
		this.daoSupport.insert("es_express_platform", platform);
	}

	@Override
	public ExpressPlatform getPlateform(Integer id) {
		String sql ="select * from es_express_platform where id=?";
		ExpressPlatform platform = (ExpressPlatform) this.daoSupport.queryForObject(sql, ExpressPlatform.class, id);
		return platform;
	}

	@Override
	public String getPlateformHtml(String code,Integer id) {
		
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		IPlugin installPlugin = null;
		installPlugin = SpringContextHolder.getBean(code);
		fp.setClz(installPlugin.getClass());
		
		Map<String,String> params = this.getConfigParams(id);
		fp.putData(params);
		
		return fp.proessPageContent();
	}
	
	public Map<String, String> getConfigParams(Integer id) {
		ExpressPlatform platform = this.getPlateform(id);
		String config  = platform.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}

	@Override
	public void setParam(Integer id, Map<String, String> param) {
		String sql ="update es_express_platform set config=? where id=?";
		this.daoSupport.execute(sql, JSONObject.fromObject(param).toString(),id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void open(Integer id) {
		this.daoSupport.execute("update es_express_platform set is_open=0");
		this.daoSupport.execute("update es_express_platform set is_open=1 where id=?", id);
	}

	@Override
	public Map getDefPlatform(String com, String nu) {
		try {
		    List<ExpressPlatform> list = this.daoSupport.queryForList("select * from es_express_platform where is_open=1",ExpressPlatform.class);
			if(list!=null && list.size()==1){
				ExpressPlatform platform = list.get(0);
				String config = platform.getConfig();
				JSONObject jsonObject = JSONObject.fromObject( config );  
				Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
				IExpressEvent expressEvent = SpringContextHolder.getBean(platform.getCode());
				Map kuaidiresult =  expressEvent.getExpressDetail(com, nu, itemMap);
				return kuaidiresult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("查询快递错误"+e);
		}
		return null;
	}

	@Override
	public int getPlateform(String code) {
		String sql ="select id from es_express_platform where code=?";
		List list = this.daoSupport.queryForList(sql, code);
		if(!list.isEmpty()){
			return 1;
		}
		return 0;
	}

}
