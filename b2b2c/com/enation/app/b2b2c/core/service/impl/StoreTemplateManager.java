package com.enation.app.b2b2c.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreTemlplate;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.eop.sdk.database.BaseSupport;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class StoreTemplateManager extends BaseSupport implements IStoreTemplateManager {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(StoreTemlplate storeTemlplate) {
		this.baseDaoSupport.insert("es_store_template", storeTemlplate);
		Integer templateId = this.baseDaoSupport.getLastId("es_store_template");
		storeTemlplate.setId(templateId);
		// 当前店铺只有一个运费模板时将此模板作为默认模板
		this.baseDaoSupport.execute("UPDATE es_store_template t SET def_temp=1 WHERE id=? AND 0=(SELECT COUNT(id) FROM es_store_template WHERE store_id=t.store_id AND id != t.id) ", templateId);
		return templateId;
	}

	@Override
	public List getTemplateList(Integer store_id) {
		String sql = "select * from es_store_template where store_id=?";
		List list = this.baseDaoSupport.queryForList(sql, store_id);
		return list;
	}

	@Override
	public Integer getLastId() {
		Integer id= this.baseDaoSupport.getLastId("es_store_template");
		return id;
	}

	@Override
	public Map getTemplae(Integer store_id, Integer tempid) {
		String sql = "select * from es_store_template where store_id=? and id=?";
		List list = this.baseDaoSupport.queryForList(sql, store_id,tempid);
		Map map = (Map) list.get(0);
		return map;
	}

	
	@Override
	public void edit(StoreTemlplate storeTemlplate) {
		this.baseDaoSupport.update("es_store_template", storeTemlplate, " id="+storeTemlplate.getId());
		
	}

	@Override
	public void delete(Integer tempid) {
		Integer def_temp = this.baseDaoSupport.queryForInt("select def_temp from es_store_template where id=?", tempid);
		if(def_temp==1){
			throw new RuntimeException("不能删除默认物流模板");
		}
		
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
		this.baseDaoSupport.execute("delete from es_store_template where id=?", tempid);
	}

	@Override
	public Integer getDefTempid(Integer storeid) {
		String sql = "select * from es_store_template where store_id=? and def_temp=1";
		List list = this.baseDaoSupport.queryForList(sql, storeid);
		if(list.isEmpty()){return null;}
		Map map = (Map) list.get(0);
		return (Integer) map.get("id");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setDefTemp(Integer tempid,Integer storeid) {
		this.baseDaoSupport.execute("update es_store_template set def_temp=0 where store_id=?", storeid);
		this.baseDaoSupport.execute("update es_store_template set def_temp=1 where id=?", tempid);
	}

	@Override
	public Integer getStoreTemlpateByName(String name,Integer storeid) {
		int i= this.baseDaoSupport.queryForInt("select count(0) from es_store_template where name=? and store_id=?", name,storeid);
		return i;
	}

    @Override
    public int checkIsDef(Integer tempid) {
        int result = this.daoSupport.queryForInt("select def_temp from es_store_template where id = ?", tempid);
        return result;
    }
	
	

	/*@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private List getList(List<Map> list){
		Integer tid=0;
		List templist = new ArrayList();
		List dlylist = null;
		
		for(Map map :list){
			Map tempmap = new HashMap();
			
			String tname = (String) map.get("tname");
			tempmap.put("tname", tname);
			
			Integer template_id = (Integer) map.get("template_id");
			if(tid!=template_id){
				tempmap.put("dlylist", dlylist);
				dlylist = new ArrayList();
				tid=template_id;
			}
			
			Map dlymap = new HashMap();
			Integer type_id = (Integer) map.get("type_id");
			dlymap.put("name", (String) map.get("name"));
			dlymap.put("config", (String) map.get("config"));
			dlymap.put("expressions", (String) map.get("expressions"));
			dlylist.add(dlymap);
		}
		
		return list;
	}*/
}
