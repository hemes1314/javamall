package com.enation.app.shop.component.bonus.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.framework.database.Page;

/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午2:38:47
 */
public interface IBonusTypeManager {
	
	/**
	 * 添加一个红包
	 * @param bronusType
	 */
	public void add(BonusType bronusType);
	
	
	/**
	 * 修改一个红包
	 * @param bronusType
	 */
	public void update(BonusType bonusType);
	
	
	/**
	 * 删除一个红包
	 * @param bronusTypeId
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer [] bonusTypeId);
	
	
	/**
	 * 分页读取红包类型
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page, int pageSize);
	
	
	/**
	 * 获取一个红包类型
	 * @param typeid
	 * @return
	 */
	public StoreBonus get(int typeid);
	
	
    /**
     * 是否存在相同的识别码
     * 
     * @author chenzhongwei
     * @param recognition
     *            识别码
     * @return true 存在 false 不存在
     */
    public boolean isExistRecognition(String recognition);
	
}
