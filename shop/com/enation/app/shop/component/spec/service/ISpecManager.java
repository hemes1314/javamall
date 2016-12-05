package com.enation.app.shop.component.spec.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;

/**
 * 规格管理接口
 * @author kingapex
 *2010-3-6下午03:48:59
 */
public interface ISpecManager {
	
	
	/**
	 * 检测规格是否被使用
	 * @param ids
	 * @return
	 */
	public boolean checkUsed(Integer[] ids);
	
	
	
	/**
	 * 检测某个规格值 是否被使用
	 * @param valueid
	 * @return
	 */
	public boolean checkUsed(Integer valueid);
	
	
	
	/**
	 * 读取规格列表
	 * @return
	 */
	public List list();
	
	
	/**
	 * 读取规格及规格值列表
	 * 通过 {@link Specification#getValueList()} 获取值列表
	 * @return
	 */
	public List<Specification> listSpecAndValue();
	
	
	
	/**
	 * 添加一种规格，同时添加其规格值
	 * @param spec
	 * @param valueList
	 */
	public void add(Specification spec,List<SpecValue> valueList);
	
	
	/**
	 * 修改一个规格，同时修改其规格值
	 * @param spec
	 * @param valueList
	 */
	public void edit(Specification spec,List<SpecValue> valueList);
	
	
	/**
	 * 删除某组规格
	 */
	public void delete(Integer[] idArray);
	

	/**
	 * 读取一个规格详细
	 * @param spec_id
	 * @return
	 */
	public Map get(Integer spec_id);
	
	
	/**
	 * 获取某个货品的规格列表
	 * @param productid
	 * @return
	 */
	public List<Map<String,String>> getProSpecList(int productid); 
}
