package com.enation.app.shop.component.bonus.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.framework.database.Page;

/**
 * 红包管理
 * @author kingapex
 *2013-8-13下午2:55:25
 */
public interface IBonusManager {
	
	
	
	/**
	 * 按会员级别发送红包
	 * @param typeid
	 * @param lvid
	 * @param onlyEmailChecked
	 * @return 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForMemberLv(int typeid,int lvid,int onlyEmailChecked);
	
	
	
	/**
	 * 按会员发送红包
	 * @param memberids
	 * @return 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForMember(int typeid,Long[] memberids);
	
	
	
	/**
	 * 按商品发放红包
	 * @param goodsids
	 * @return 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForGoods(int typeid,Integer[] goodsids);
	
	
	/**
	 * 发送线下红包
	 * @param count 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForOffLine(int typeid,int count);
	
 
	
	
	/**
	 * 分页读取红包列表
	 * @param page
	 * @param pageSize
	 * @param typeid
	 * @return
	 */
	public Page list(int page ,int pageSize,int typeid);
	public Page pageList(int page ,int pageSize,long memberid);
	
	/**
	 * 删除某个红包
	 * @param bronusid
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(int bronusid);
	
	
	/**
	 * 获取红包已绑定的商品
	 * @param typeid
	 * @return 
	 * goods_id
	 * name
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public List<Map> getGoodsList(int typeid);
	
	
	/**
	 * 导出下线红包到excel
	 * @param typeid
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public String exportToExcel(int typeid);
	
	
	/**
	 * 读取某会员的可用红包
	 * @param memberid 会员id
	 * @param goodsprice 当前购物车中的商品金额，只有红包的商品金额小于等于此金额的红包才会被读取
	 * @param type 1.会员中心中读取红包，查看此会员所有的红包.0.购物车使用红包查看可用红包
	 * @return 红包列表
	 */
	public List<Map> getMemberBonusList(long memberid,Double goodsprice,Integer type);
	/**
	 * 读取某会员的可用红包
	 * @param memberid 会员id
	 * @param goodsprice 当前购物车中的商品金额，只有红包的商品金额小于等于此金额的红包才会被读取
	 * @param type 1.会员中心中读取红包，查看此会员所有的红包.0.购物车使用红包查看可用红包
	 * @return 红包列表
	 */
	
	public List<Map> getMemberBonusListForApp(long memberid, Double goodsprice,Integer type);
	
	
	/**
	 * 分页读取会员红包列表
	 * @param memberid
	 * @param goodsprice
	 * @param type
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page getMemberBonusList(long memberid,Double goodsprice,Integer type,int pageNo,int pageSize);
	
	
	
	/**
	 * 根据红包id获取一个红包详细
	 * @param bounusid
	 * @return
	 */
	public MemberBonus getBonus(int bounusid);
	
	
	
	/**
	 * 根据红包编号获取红包
	 * @param sn
	 * @return
	 */
	public MemberBonus  getBonus(String sn);
	
	
	
	
	/**
	 * 使用一个红包
	 * 并更改使用数量  xulipeng 2014年8月5日15:56:30 修改
	 * @param bonusid
	 * @param memberid
	 * @param orderid
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void use(int bonusid,long memberid,Integer orderid,String ordersn,int bonus_type_id);
	
	
	/**
	 * 退还红包
	 * @param orderid
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void returned(int orderid);

	/**
	 * 列表查询
	 * @param member_id
	 * @param type
	 * @param cat  1 : 优惠券  0 ： 红包 
	 * @return
	 */

    public Page getBonusListForApp(int pageNo, int PAGE_SIZE, long member_id, Integer type, int cat);


}
