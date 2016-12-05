package com.enation.app.shop.core.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.model.support.GoodsView;
import com.enation.app.shop.core.utils.GoodsUtils;
import com.enation.eop.sdk.utils.UploadUtil;

public class GoodsDetailMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		
		GoodsView  goods = new GoodsView();
		goods.setName(rs.getString("name"));
		goods.setSn(rs.getString("sn"));
		goods.setUnit(rs.getString("unit"));
		goods.setWeight(rs.getDouble("weight"));
		goods.setGoods_id(rs.getInt("goods_id"));
		goods.setMktprice(rs.getDouble("mktprice"));
		goods.setMarket_enable(rs.getInt("market_enable"));
		goods.setPrice(rs.getDouble("price"));
		goods.setCreate_time(rs.getLong("create_time"));
		goods.setLast_modify(rs.getLong("last_modify"));
		goods.setBrand_name(rs.getString("brand_name"));
		goods.setParams(rs.getString("params"));
		goods.setIntro(rs.getString("intro"));
		goods.setBrief(rs.getString("brief"));
		goods.setPage_title(rs.getString("page_title"));
		goods.setMeta_keywords(rs.getString("meta_keywords"));
		goods.setMeta_description(rs.getString("meta_description"));
		goods.setSpecs(rs.getString("specs"));
		List specList = GoodsUtils.getSpecList(goods.getSpecs());
 		goods.setSpecList(specList);
		goods.setType_id(rs.getInt("type_id"));
		goods.setBrand_id(rs.getInt("brand_id"));
		goods.setCat_id(rs.getInt("cat_id"));
		goods.setAdjuncts(rs.getString("adjuncts"));
		if(goods.getAdjuncts()!=null && goods.getAdjuncts().equals("")){//由于update的时候，不能保存null故保存为空串。但是原有程序之对null做了判断。
			goods.setAdjuncts(null);
		}
		goods.setStore(rs.getInt("store"));
		goods.setDisabled(rs.getInt("disabled"));
		goods.setMarket_enable(rs.getInt("market_enable"));
		
		goods.setOriginal(UploadUtil.replacePath(rs.getString("original")));
		goods.setThumbnail(UploadUtil.replacePath(rs.getString("thumbnail")));
		goods.setSmall(UploadUtil.replacePath(rs.getString("small")));
		goods.setBig(UploadUtil.replacePath(rs.getString("big")));
 
		
		Map propMap = new HashMap();
		
		for(int i=0;i<20;i++){
			String value = rs.getString("p" + (i+1));
			propMap.put("p"+i,value);
		}
		goods.setPropMap(propMap);
		return goods;
	}
	
 


	
	
} 
