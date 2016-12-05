package com.enation.app.shop.component.spec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.model.mapper.SpecValueMapper;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;

/**
 * 规格管理
 * @author kingapex
 *2010-3-7上午11:19:20
 */
@Component
public class SpecManager extends BaseSupport<Specification>  implements ISpecManager {
	private ISpecValueManager specValueManager;
	
	/**
	 * 此版本未实现规格值排序功能
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	
	public void add(Specification spec, List<SpecValue> valueList) {
		
		this.baseDaoSupport.insert("specification", spec);
		Integer specId= this.baseDaoSupport.getLastId("specification");
		for(SpecValue value : valueList){
			value.setSpec_id(specId);
			value.setSpec_type(spec.getSpec_type());
			String path  = value.getSpec_image();
			if(path!=null){
				String static_server_domain= SystemSetting.getStatic_server_domain();
				path = path.replaceAll(static_server_domain, EopSetting.FILE_STORE_PREFIX);
			}
			value.setSpec_image(path);
			specValueManager.add(value);
		}
		
	}

	
	
	/**
	 * 检测某个规格是否被使用
	 */
	public boolean checkUsed(Integer[] idArray){
		if(idArray==null) return false;
		
		String idStr = StringUtil.arrayToString( idArray,",");
		String sql  ="select count(0)  from  goods_spec where spec_id in (-1,"+idStr+")";
		
		int count  = this.baseDaoSupport.queryForInt(sql);
		if(count>0)
			return true;
		else
			return false;
	} 
	
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.component.spec.service.ISpecManager#checkUsed(java.lang.Integer)
	 */
	@Override
	public boolean checkUsed(Integer valueid) {
		 String sql  ="select count(0) from goods_spec where spec_value_id=?";
		 
		return this.baseDaoSupport.queryForInt(sql, valueid)>0;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] idArray) {
		
		String idStr = StringUtil.arrayToString( idArray,",");
		String sql ="delete from specification where spec_id in (-1,"+idStr+")";
		this.baseDaoSupport.execute(sql);
		
		sql="delete from spec_values where spec_id in (-1,"+idStr+")";
		this.baseDaoSupport.execute(sql);
		
		sql="delete from goods_spec where spec_id in (-1,"+idStr+")";
		this.baseDaoSupport.execute(sql);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void edit(Specification spec, List<SpecValue> valueList) {
		
		//不存在规格值 ，直接全部删除
		if(valueList==null || valueList.isEmpty()){
			
			String sql ="delete from spec_values where spec_id=?";
			this.baseDaoSupport.execute(sql, spec.getSpec_id());
			this.baseDaoSupport.update("specification", spec, "spec_id="+spec.getSpec_id());
			
		}else{ //删除该删除的，添加该添加的，更新该更新的
			
			
			String valuidstr="";
			for(SpecValue value:valueList){
				int valueid = value.getSpec_value_id();
				if(!StringUtil.isEmpty(valuidstr)){
					valuidstr+=",";
				}
				valuidstr+=valueid;
				
			}
			String sql ="delete from spec_values where  spec_id=? and spec_value_id not in("+valuidstr+")"; //删除不存在的规格值id
			this.baseDaoSupport.execute(sql, spec.getSpec_id());
			this.baseDaoSupport.update("specification", spec, "spec_id="+spec.getSpec_id());
			for(SpecValue value : valueList){
				value.setSpec_id(spec.getSpec_id());
				value.setSpec_type(spec.getSpec_type());
				String path  = value.getSpec_image();
				if(path!=null){
					String static_server_domain= SystemSetting.getStatic_server_domain();
					path = path.replaceAll(static_server_domain, EopSetting.FILE_STORE_PREFIX);
				}
				value.setSpec_image(path);			
				if(value.getSpec_value_id().intValue()==0){ //id为0，新增的，添加
					specValueManager.add(value);
				}else{
					specValueManager.update(value);
				}
				
			}	
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)  
	public List list() {
		String sql ="select * from specification order by spec_id desc";
		return this.baseDaoSupport.queryForList(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#listSpecAndValue()
	 */
	public List<Specification> listSpecAndValue(){
		
		String sql ="select * from specification";
		List<Specification> specList= this.baseDaoSupport.queryForList(sql,Specification.class);
		
		sql ="select * from spec_values order by spec_id";
		List valueList=   this.baseDaoSupport.queryForList(sql, new SpecValueMapper() );
		for(Specification spec :specList){
			List<SpecValue> newList =  new ArrayList<SpecValue>();
			for(SpecValue value:(List<SpecValue>)valueList){
				if(value.getSpec_id().intValue() == spec.getSpec_id().intValue()){
					newList.add(value);
				}
			}
			spec.setValueList(newList);
		}
		return specList;
	}
	
	
	
	public Map get(Integer spec_id){
		String sql ="select * from specification where spec_id=?";
		return this.baseDaoSupport.queryForMap(sql, spec_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.component.spec.service.ISpecManager#getProSpecList(int)
	 */
	@Override
	public List getProSpecList(int productid) {
		String sql ="select s.spec_name name ,sv.spec_value value  from   "+this.getTableName("specification")+" s ,"+this.getTableName("spec_values")+" sv ,"+this.getTableName("goods_spec")+" gs where gs.product_id=? and gs.spec_id=s.spec_id and gs.spec_value_id = sv.spec_value_id";
		return  this.daoSupport.queryForList(sql, productid);
	}
	
	
	public ISpecValueManager getSpecValueManager() {
		return specValueManager;
	}

	public void setSpecValueManager(ISpecValueManager specValueManager) {
		this.specValueManager = specValueManager;
	}








}
