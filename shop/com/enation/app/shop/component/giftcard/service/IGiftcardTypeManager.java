package com.enation.app.shop.component.giftcard.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.giftcard.model.GiftcardType;
import com.enation.framework.database.Page;

/**
 * 礼品卡类型管理
 * @author humaodong
 *2015-10-11
 */
public interface IGiftcardTypeManager {
	
	/**
	 * 添加一个礼品卡
	 * @param bronusType
	 */
	public void add(GiftcardType giftcardType);
	
	
	/**
	 * 修改一个礼品卡
	 * @param bronusType
	 */
	public void update(GiftcardType giftcardType);
	
	
	/**
	 * 删除一个礼品卡
	 * @param bronusTypeId
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer [] giftcardTypeId);
	
	
	/**
	 * 分页读取礼品卡类型
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page, int pageSize);
	
	public List list();
	
	
	/**
	 * 获取一个礼品卡类型
	 * @param typeid
	 * @return
	 */
	public GiftcardType get(int typeid);
	
	
	
	
}
