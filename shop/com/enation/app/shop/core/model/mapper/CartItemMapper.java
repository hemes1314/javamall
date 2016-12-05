package com.enation.app.shop.core.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.model.support.CartItem;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.util.StringUtil;

public class CartItemMapper implements RowMapper {

	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CartItem cartItem = new CartItem();
		cartItem.setId(rs.getInt("cart_id"));
		cartItem.setProduct_id(rs.getInt("product_id"));
		cartItem.setGoods_id(rs.getInt("goods_id"));
		cartItem.setName(rs.getString("name"));
		cartItem.setMktprice(rs.getDouble("mktprice"));
		cartItem.setPrice(rs.getDouble("price"));
		double price  = rs.getDouble("price");
		cartItem.setCoupPrice(price); //优惠价格默认为销售价，则优惠规则去改变
		cartItem.setCatid(rs.getInt("catid"));
		//lxl add 
		cartItem.setStore_id(rs.getInt("store_id"));
		cartItem.setStore_name(rs.getString("store_name"));
//		String image_default =  rs.getString("image_default");
//		
//		if(image_default!=null ){
//			image_default  =UploadUtil.replacePath(image_default);
//		}
//		cartItem.setImage_default(image_default);
		
		String thumbnail =  rs.getString("thumbnail");
		
		if(thumbnail!=null ){
			thumbnail  =UploadUtil.replacePath(thumbnail);
		}
		cartItem.setImage_default(thumbnail);
		
		cartItem.setNum(rs.getInt("num"));
		cartItem.setPoint(rs.getInt("point"));
		cartItem.setItemtype(rs.getInt("itemtype"));
		//if( cartItem.getItemtype().intValue() ==  0){
			cartItem.setAddon(rs.getString("addon"));
	//	}
		//赠品设置其限购数量
		if( cartItem.getItemtype().intValue() ==  2){
			cartItem.setLimitnum(rs.getInt("limitnum"));
		}
		 
		cartItem.setSn(rs.getString("sn"));
		
		if( cartItem.getItemtype().intValue() ==  0){
			String specs = rs.getString("specs");
			cartItem.setSpecs(specs);
//			if(StringUtil.isEmpty(specs)) 
//				cartItem.setName(  cartItem.getName() );
//			else
//				cartItem.setName(  cartItem.getName() +"("+ specs +")" );
		}
		
		cartItem.setUnit(rs.getString("unit"));
		cartItem.setWeight(rs.getDouble("weight"));
		cartItem.setCurrent_activity(rs.getInt("current_activity"));
		cartItem.setCurrent_activity_name(rs.getString("current_activity_name"));
		return cartItem;
	}

}
