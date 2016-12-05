package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class VirtualProductManager extends BaseSupport<VirtualProduct> {

	public void add(VirtualProduct virtualProduct) {
		this.baseDaoSupport.insert("es_virtual_product", virtualProduct);
	}
	
	public void edit(VirtualProduct virtualProduct) {
		this.baseDaoSupport.update("es_virtual_product", virtualProduct, "id=" + virtualProduct.getId());
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_virtual_product where id in (" + id_str + ")";
        this.baseDaoSupport.execute(sql);
	}
	
	
    public VirtualProduct get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_virtual_product where id=?";
	            VirtualProduct ym = this.baseDaoSupport.queryForObject(sql, VirtualProduct.class,id);
	            return ym;
	        }else{
	            return null;
	        }
	 }
	
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_virtual_product";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize,VirtualProduct.class);
        return webpage;
    }
    
    public List list() {
        String sql = "select * from es_virtual_product order by price";
        List yuemolist = this.baseDaoSupport.queryForList(sql, VirtualProduct.class);
        return yuemolist;
    }
	
	
	
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_virtual_product order by id", pageNo, pageSize);
	}

    public Page listForApp(int page, int pageSize) {
        String sql = "select * from es_virtual_product order by price";
        return this.baseDaoSupport.queryForPage(sql, page, pageSize, VirtualProduct.class);
    }

	
}
