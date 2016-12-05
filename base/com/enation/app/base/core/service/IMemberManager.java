package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.framework.database.Page;

/**
 * 会员管理接口
 * @author kingapex
 *2010-4-30上午10:07:35
 */
public interface IMemberManager {
	
	/**
	 * 添加会员
	 * 
	 * @param member
	 * @return 0：用户名已存在，1：添加成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int add(Member member);
	
	
	/**
	 * 会员注册 
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int register(Member member);

	
	
	/**
	 * 某个会员邮件注册验证成功
	 * 此方法会更新为验证成功，并激发验证成功事件
	 * @param 会员实体
	 *  
	 */
	public void checkEmailSuccess(Member member);
	
	
	
	
	/**
	 * 检测用户名是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkname(String name);
	
	/**
	 * 检测邮箱是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkemail(String email);

	/**
	 * 修改会员信息
	 * 
	 * @param member
	 * @return
	 */
	public Member edit(Member member);
	
	 /**
     * 修改会员备注
     * add by linyang
     * @return
     */
    public boolean editRemark(long memid,String remark);

	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Member get(long member_id);

	/**
	 * 删除会员
	 * 
	 * @param id
	 */
	public void delete(Long[] id);

	/**
	 * 根据用户名称取用户信息
	 * 
	 * @param uname
	 * @return 如果没有找到返回null
	 */
	public Member getMemberByUname(String uname);
	
	/**
	 * 根据邮箱取用户信息
	 * @param email
	 * @return
	 */
	public Member getMemberByEmail(String email);

	/**
	 * 根据手机取用户信息
	 * @param mobile
	 * @return
	 */
	public Member getMemberByMobile(String mobile);
	
	
	/**
	 * 修改当前登录会员的密码
	 * 
	 * @param password
	 */
	public void updatePassword(String password);
	
	
	
	/**
	 * 更新某用户的密码
	 * @param memberid
	 * @param password
	 */
	public void updatePassword(long memberid,String password);
	
	/**
	 * 找回密码使用code
	 * @param code
	 */
	public void updateFindCode(long memberid,String code);
	
	
	/**
	 * 充值和支出 2015/10/11 humaodong
	 * @param memberid
	 * @param money
	 * @param isVirtual
	 * @param business
	 * @param note
	 */
	public AdvanceLogs topup(long memberid, Double advance, Double virtual, String business, String note,Integer orderid);
	public AdvanceLogs topup(long memberid, Double advance, Double virtual, String business, String note);
	public AdvanceLogs pay(long memberid, Double money, int virtualFirst, String business, String note);
	
	
	/**
	 * 减少预存款
	 * @param memberid
	 * @param num
	 */
	public void cutMoney(long memberid,Double num);
	
	
	
	
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int login(String username,String password);
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int loginWithCookie(String username, String password);
	
	/**
	 * 会员注销
	 */
	public void logout();
	
	
	
	/**
	 * 管理员以会员身份登录
	 * @param username 要登录的会员名称
	 * @return 0登录失败，无此会员
	 * @throws  RuntimeException 无权操作
	 */
	public int loginbysys(String username);
	
	
	/**
	 * 更新某个会员的等级
	 * @param memberid
	 * @param lvid
	 */
	public void updateLv(long memberid,int lvid);
	
	/**
	 * 会员搜索
	 * @param keyword
	 * @param lvid
	 * @return
	 */
	public List<Member> search(Map memberMap);
	
	/**
	 * 会员搜索 带分页
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMember(Map memberMap,Integer page,Integer pageSize,String other,String order);
	
	/**
	 * 检测手机号
	 * @param phone
	 * @return
	 */
	public int checkMobile(String phone);

	/**
	 * app修改密码
	 * @param mobile
	 * @param password
	 */
	public void findPassword(String mobile, String password);

	/**
	 * 获取优惠券数量
	 * @param member_id
	 * @return
	 */
	public int getCouponCount(long member_id);
	/**
	 * 获取红包数量
	 * @param member_id
	 * @return
	 */

	public int getBonusCount(long member_id);
	
	/**
     * 设置第三方登陆ID   add by Tension
     * @param memberId, thirdpartId
     * @return
     */
	public void setThirdpartId(String mobile, String thirdpartId);

	/**
     * 根据第三方登陆ID获取用户信息
     * @param thirdpartId
     * @return
     */
    public Member getMemberByThirdpartId(String thirdpartId);

    /**
     * 第三方登陆
     * @param thirdpartId
     * @return
     */
    public int thirdpartLogin(String thirdpartId);


    public void updateMobile(long member_id, String mobile);
    
    //add by Tension 增加后台修改用户方法
    public Member backendEdit(Member oldMember);
	
}