package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Logi;
import com.enation.framework.database.Page;
/**
 * 物流公司管理类
 * @author fenlongli
 *
 */
public interface ILogiManager {
	/**
	 * 添加物流公司
	 * @param name
	 */
	public void saveAdd(Logi logi);
	
	/**
	 * 编辑物流公司
	 * @param id
	 * @param name
	 */
	public void saveEdit(Logi logi);
	
	/**
	 * 分页读取物流公司
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pageLogi(String order ,Integer page,Integer pageSize);
	
	/**
	 * 读取所有物流公司列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> list();
	
	
	/**
	 * 删除物流公司
	 * @param id
	 */
	public void delete(Integer[] logi_id);
	
	/**
	 * 获取物流公司
	 * @param id
	 * @return
	 */
	public Logi getLogiById(Integer id);
	
}
