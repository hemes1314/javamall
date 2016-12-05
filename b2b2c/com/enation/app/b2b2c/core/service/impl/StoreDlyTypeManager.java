package com.enation.app.b2b2c.core.service.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreDlyType;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.mapper.TypeAreaMapper;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.app.shop.core.model.support.TypeArea;
import com.enation.app.shop.core.model.support.TypeAreaConfig;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 配送方式
 * @author xulipeng
 *
 */

@Component
public class StoreDlyTypeManager extends BaseSupport implements IStoreDlyTypeManager {
	
	@Override
	public List getDlyTypeById(String typeid) {
		String sql ="select * from es_dly_type where type_id in ("+typeid+")";
		List list =this.baseDaoSupport.queryForList(sql);
		return list;
	}
	
	@Override
	public List getDlyTypeList(Integer template_id) {
		String sql = "select * from es_dly_type where template_id=? order by type_id";
		List list = this.baseDaoSupport.queryForList(sql, template_id);
		return list;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(StoreDlyType storeDlyType, DlyTypeConfig config,
			TypeAreaConfig[] configArray) {
		
		storeDlyType = this.fillType(storeDlyType, config);
		// }
		this.baseDaoSupport.insert("es_dly_type", storeDlyType);
		Integer typeId = this.baseDaoSupport.getLastId("dly_type");
		this.addTypeArea(typeId, configArray);
		
	}

	private StoreDlyType fillType(StoreDlyType storeDlyType, DlyTypeConfig config) {
		Double firstprice = config.getFirstprice(); // 首重费用
		Double continueprice = config.getContinueprice();// 续重费用
		Integer firstunit = config.getFirstunit(); // 首重重量
		Integer continueunit = config.getContinueunit(); // 续重重量

		// 组合公式
		String expressions = null;

		if (config.getUseexp() == 0) {
			expressions = this.createExpression(firstprice, continueprice,
					firstunit, continueunit);
		} else {
			expressions = config.getExpression();
		}

		storeDlyType.setExpressions(expressions);
		storeDlyType.setConfig(JSONObject.fromObject(config).toString());
		return storeDlyType;
	}
	
	private void addTypeArea(Integer typeId, TypeAreaConfig[] configArray) {
		for (TypeAreaConfig areaConfig : configArray) {
			if (areaConfig != null) {

				TypeArea typeArea = new TypeArea();
				typeArea.setArea_id_group(areaConfig.getAreaId());
				typeArea.setArea_name_group(areaConfig.getAreaName());
				typeArea.setType_id(typeId);
				typeArea.setHas_cod(areaConfig.getHave_cod());

				typeArea.setConfig(JSONObject.fromObject(areaConfig).toString());
				String expressions = "";
				if (areaConfig.getUseexp().intValue() == 1) { // 启用公式
					expressions = areaConfig.getExpression();
				} else {
					// 组合公式
					expressions = createExpression(areaConfig);
				}
				typeArea.setExpressions(expressions);
				this.baseDaoSupport.insert("dly_type_area", typeArea);
			}
		}
	}
	
	/**
	 * 生成公式字串
	 * 
	 * @param areaConfig
	 * @return
	 */
	private String createExpression(TypeAreaConfig areaConfig) {

		return this.createExpression(areaConfig.getFirstprice(),
				areaConfig.getContinueprice(), areaConfig.getFirstunit(),
				areaConfig.getContinueunit());
	}
	
	/**
	 * 生成公式字串
	 * 
	 * @param firstprice
	 * @param continueprice
	 * @param firstunit
	 * @param continueunit
	 * @return
	 */
	private String createExpression(Double firstprice, Double continueprice,
			Integer firstunit, Integer continueunit) {
		//return firstprice + "+tint(w-" + firstunit + ")/" + continueunit + "*" + continueprice;
	    
	    // 2015/12/29 humaodong fix bug#417 首重基础上续重不足的按续重足量计费
        return firstprice + "+parseInt(tint(w-" + firstunit + "+" + continueunit + "-1)/" + continueunit + ")*" + continueprice;
	}

	@Override
	public Integer getLastId() {
		Integer type_id = this.baseDaoSupport.getLastId("es_dly_type");
		return type_id;
	}

	@Override
	public List getDlyTypeAreaList(Integer type_id) {
		String sql ="select * from es_dly_type_area where type_id=?";
		List list = this.baseDaoSupport.queryForList(sql, type_id);
		return list;
	}


	@Override
	public Integer getDefaultDlyId(Integer store_id) {
		String sql = "select type_id from es_dly_type where store_id=? and defaulte=1";
		Integer id = this.baseDaoSupport.queryForInt(sql, store_id);
		return id;
	}

	@Override
	public void del_dlyType(Integer tempid) {
		String sql  ="select * from es_dly_type where template_id=?";
		List<Map> list = this.baseDaoSupport.queryForList(sql, tempid);
		if(!list.isEmpty()){
			StringBuffer dlyids =new StringBuffer();
			for(Map  map : list) {
				Integer type_id = (Integer) map.get("type_id");
				dlyids.append(type_id+",");
			}
			String ids = dlyids.toString().substring(0, dlyids.toString().length()-1);
			
			String areadelsql = "delete from es_dly_type_area where type_id in ("+ids+")";
			String dlydelsql = "delete from es_dly_type where template_id=?";
			this.baseDaoSupport.execute(areadelsql);
			this.baseDaoSupport.execute(dlydelsql,tempid);
		}
	}

}
