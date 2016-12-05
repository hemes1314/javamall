package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.enation.app.shop.core.model.Cart;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.member.impl.StoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.mobile.model.LiaomoFriend;
import com.enation.app.shop.mobile.service.impl.LiaomoFriendsManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员管理
 * 
 * @author kingapex 2010-4-30上午10:07:24
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class MemberManager extends BaseSupport<Member> implements IMemberManager {

	protected IMemberLvManager memberLvManager;
	private IMemberPointManger memberPointManger;
	@Autowired
	private IAdvanceLogsManager advanceLogsManager;
	private MemberPluginBundle memberPluginBundle;
	private LiaomoFriendsManager liaomoFriendsManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public void logout() {
		//this.daoSupport.insert(table, fields);
		//this.daoSupport.update(table, fields, where);
		//this.daoSupport.queryForInt(sql, args)
		
		//this.daoSupport.execute(sql, args);  es_member
		//this.baseDaoSupport				   member
		
		Member member = UserConext.getCurrentMember();
//		member = this.get(member.getMember_id());
		ThreadContextHolder.getSessionContext().removeAttribute(UserConext.CURRENT_MEMBER_KEY);
		// remove current store
		ThreadContextHolder.getSessionContext().removeAttribute(StoreMemberManager.CURRENT_STORE_MEMBER_KEY);
		this.memberPluginBundle.onLogout(member);
		
		// 2015/10/30 humaodong
        HttpSession sess = ThreadContextHolder.getSessionContext().getSession();
        if (sess != null) sess.invalidate();
        if (member != null) {
        	System.out.println("------- member "+member.getName()+" logout -------");
        }
        
        /*
        try {    
            ThreadContextHolder.getHttpRequest().logout();
        } catch(ServletException e) {
            e.printStackTrace();
        }
        */
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int register(Member member) {
		int result = add(member);
		try {
			this.memberPluginBundle.onRegister(member);
			
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int add(Member member) {
		if (member == null) throw new IllegalArgumentException("member is null");
		
		List<String> unames = Arrays.asList(
				"波波酱酒",
				"集美酒业",
				"jiupinhui",	
				"13940206103",
				"13701199275",
				"13311062337",
				"18080426961",
				"13068977019",
				"brigewine",
				"15210328773",
				"13701129348",
				"gm_whzhouzifei",
				"伍壹玖酒类专营店",
				"99美酒旗舰店",
				"博酒汇酒",	
				"cyiwine",
				"13822263399",
				"519酒业",
				"18611132308",
				"13810823065",
				"18001661709",
				"ziyingdian"
		);
		boolean updateFlag = false;
		Member dbMember = null;
		if (this.checkname(member.getUname()) == 1) {
			if (!unames.contains(member.getUname())) return 0;
			dbMember = this.getMemberByUname(member.getUname());
			if (StringUtils.isNotBlank(member.getNickname())) {
				dbMember.setNickname(member.getNickname());
			} else {
				dbMember.setNickname(member.getUname());
			}
			if (StringUtils.isNotBlank(member.getMobile())) {
				dbMember.setMobile(member.getMobile());
			}
			if (StringUtils.isNotBlank(member.getEmail())) {
				dbMember.setEmail(member.getEmail());
			}
			if (StringUtils.isNotBlank(member.getFace())) {
				dbMember.setFace(member.getFace());
			}
			if (StringUtils.isNotBlank(member.getName())) {
				dbMember.setName(member.getName());
			} else {
				dbMember.setName(member.getNickname());
			}
			updateFlag = true;
		} else {
			if (member.getLv_id() == null) {
				member.setLv_id(memberLvManager.getDefaultLv());
			}
			if (StringUtils.isBlank(member.getNickname())) {
				member.setNickname(member.getUname());
			}
			if (StringUtils.isBlank(member.getName())) {
				member.setName(member.getUname());
			}
			if (member.getRegtime() == null) {
				member.setRegtime(DateUtil.getDateline());
			}
			if (StringUtils.isNotBlank(member.getPassword())) {
				member.setPassword(StringUtil.md5(member.getPassword()));
			}
			if (StringUtils.isBlank(member.getFace())) member.setFace("");
			member.setLastlogin(DateUtil.getDateline());
			member.setLogincount(0);
			member.setPoint(0);
			member.setAdvance(0D);
			member.setMp(0);
			member.setMidentity(0);
		}
		if (updateFlag) {
			String sql = "update es_member set member_id=?,mobile=?,nickname=?,name=?,face=?,email=? where member_id=?";
			this.baseDaoSupport.execute(sql, member.getMember_id(),
					dbMember.getMobile(), dbMember.getNickname(),
					dbMember.getName(), dbMember.getFace(),
					dbMember.getEmail(), dbMember.getMember_id());
			sql = "update es_store set member_id=? where member_id=?";
			this.baseDaoSupport.execute(sql, member.getMember_id(), dbMember.getMember_id());
			logger.debug("Update gome user to cellar succes, member_id from " + dbMember.getMember_id() + " to " + member.getMember_id());
		} else {
			if (member.getMember_id() != null) {
				this.baseDaoSupport.insertByPk("member", member);
				logger.debug("Create gome user to cellar succes, member_id = " + member.getMember_id());
			} else {
				this.baseDaoSupport.insert("member", member);
				long memberid = this.baseDaoSupport.getLastId("member");
				member.setMember_id(memberid);
			}
		}
		
//		if (this.checkname(member.getUname()) == 1) return 0;
//		if (member.getLv_id() == null) {
//			member.setLv_id(memberLvManager.getDefaultLv());
//		}
//		if (StringUtils.isBlank(member.getNickname())) {
//			member.setNickname(member.getUname());
//		}
//		if (StringUtils.isBlank(member.getName())) {
//			member.setName(member.getUname());
//		}
//		if (member.getRegtime() == null) {
//			member.setRegtime(DateUtil.getDateline());
//		}
//		if (StringUtils.isNotBlank(member.getPassword())) {
//			member.setPassword(StringUtil.md5(member.getPassword()));
//		}
//		if (StringUtils.isBlank(member.getFace())) member.setFace("");
//		member.setLastlogin(DateUtil.getDateline());
//		member.setLogincount(0);
//		member.setPoint(0);
//		member.setAdvance(0D);
//		member.setMp(0);
//		member.setMidentity(0);
		
//		if (member.getMember_id() != null) {
//			this.baseDaoSupport.insertByPk("member", member);
//		} else {
//			this.baseDaoSupport.insert("member", member);
//			long memberid = this.baseDaoSupport.getLastId("member");
//			member.setMember_id(memberid);
//		}
		return 1;
	}
	

	public void checkEmailSuccess(Member member) {
		String sql = "update member set is_cheked = 1 where member_id = ?";
		this.baseDaoSupport.execute(sql, member.getMember_id());
		this.memberPluginBundle.onEmailCheck(member);
	}

	public Member get(long memberId) {
		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where member_id=?";
		return this.daoSupport.queryForObject(sql, Member.class, memberId);
	}

	public Member getMemberByUname(String uname) {
		String sql = "select * from es_member where uname=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, uname);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	public Member getMemberByEmail(String email) {
		String sql = "select * from member where email=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, email);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}
	
	public Member getMemberByThirdpartId(String thirdpartId) {
        String sql = "select * from member where thirdpart_id=?";
        List list = this.baseDaoSupport.queryForList(sql, Member.class, thirdpartId);
        Member m = null;
        if (list != null && list.size() > 0) {
            m = (Member) list.get(0);
        }
        return m;
    }

	public Member edit(Member member) {
		// 前后台用到的是一个edit方法，请在action处理好
		this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
		Integer memberpoint = member.getPoint();
		
		//改变会员等级
		if(memberpoint!=null ){
			MemberLv lv =  this.memberLvManager.getByPoint(memberpoint);
			if(lv!=null ){
				if((member.getLv_id()==null ||lv.getLv_id().intValue()>member.getLv_id().intValue())){
					this.updateLv(member.getMember_id(), lv.getLv_id());
				} 
			}
		}
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		return null;
	}
	
	//add by Tension 增加后台修改会员方法
	public Member backendEdit(Member member) {
        // 前后台用到的是一个edit方法，请在action处理好
        this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
        return null;
    }
	
	//add by linyang
	public boolean editRemark(long memid,String remark) {
	    String sql = "update member set remark=? where member_id = ?";
	    this.baseDaoSupport.execute(sql,remark,memid);
	    return true;
	}

	public int checkname(String name) {
		String sql = "select count(0) from member where uname=?";
		int count = this.baseDaoSupport.queryForInt(sql, name);
		return count > 0 ? 1 : 0;
	}

	public int checkemail(String email) {
		String sql = "select count(0) from member where email=?";
		int count = this.baseDaoSupport.queryForInt(sql, email);
		return count > 0 ? 1 : 0;
	}
	
	@Override
	public int checkMobile(String mobile) {
		String sql = "select count(0) from es_member where mobile=?";
		int count = this.daoSupport.queryForInt(sql, mobile);
		return count > 0 ? 1 : 0;
	}

	public void delete(Long[] id) {
		if (id == null || id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(id, ",");
		String sql = "delete from member where member_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	public void updatePassword(String password) {
		Member member = UserConext.getCurrentMember();
		this.updatePassword(member.getMember_id(), password);
		member.setPassword(StringUtil.md5(password));
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

	}
	//2015/9/18 lxl add start
	public void findPassword(String mobile,String password){
		String md5password = password == null ? StringUtil.md5("") : StringUtil.md5(password);
		String sql ="update member set password =? where mobile =? ";
		this.baseDaoSupport.execute(sql, md5password,mobile);
	}
	//2015/9/18 lxl add end
	public void updatePassword(long memberid, String password) {
		String md5password = password == null ? StringUtil.md5("") : StringUtil.md5(password);
		String sql = "update member set password = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, md5password, memberid);
		this.memberPluginBundle.onUpdatePassword(password, memberid);
	}

	public void updateFindCode(long memberid, String code) {
		String sql = "update member set find_code = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, code, memberid);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username, String password) {
		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where (m.uname=? or m.email=? or m.mobile=?) and password=?";
		// 用户名中包含@，说明是用邮箱登录的
//		if (username.contains("@")) {
//			sql = "select m.*,l.name as lvname from "
//					+ this.getTableName("member") + " m left join "
//					+ this.getTableName("member_lv")
//					+ " l on m.lv_id = l.lv_id where m.email=? and password=?";
//		}
		
		

		String pwdmd5 = com.enation.framework.util.StringUtil.md5(password);
		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username,username,username, pwdmd5);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		
		/******************* 2015/11/9 humaodong *************************/
		String sessId = ThreadContextHolder.getSessionContext().getSession().getId();
/* fix bug #408
1.游客添加商品到购物车。
3.购物车页点结算提示登录注册。
4.登录完成，回到购物车页。
5.点浏览器后退键返回，重新登录。
6.再次登录完成后购物车数据被清空。
*/
		//sql = "delete from es_cart where member_id=? and goods_id in (select goods_id from es_cart where session_id=?)";
		//this.daoSupport.execute(sql, member.getMember_id(), sessId);

		//如果登录后添加商品到cart,然后退出,然后再添加同样商品,然后再登录,会出现两条. 应该先检查是否存在
		List cartList = baseDaoSupport.queryForList("select * from es_cart where  session_id=? and (member_id is null or member_id=0)", Cart.class, sessId);
		for (Object o : cartList) {
			Cart cart = (Cart)o;
			//查询是否有
			Integer pid = cart.getProduct_id();
			int count = baseDaoSupport.queryForInt("select count(cart_id) from es_cart where  member_id =? and product_id=?", member.getMember_id(), pid);
			if (count > 0) {
				sql = "delete from es_cart where cart_id=?";
				this.daoSupport.execute(sql, cart.getCart_id());
				sql = "update es_cart set num=num+?,weight=weight+?,price=price+? where member_id =? and product_id=?";
				this.daoSupport.execute(sql, cart.getNum(), cart.getWeight(), cart.getPrice(), member.getMember_id(), pid);
			} else {
				sql = "update es_cart set member_id=? where product_id=? and session_id=? and (member_id is null or member_id=0)";
				this.daoSupport.execute(sql, member.getMember_id(),pid, sessId);
			}
		}

		//删除购物车中过期的商品
		//sql = "delete from es_cart a where a.member_id=? and a.goods_id in (select b.goods_id from es_product b where b.enable_store=0 and a.goods_id=b.goods_id)";
        //this.daoSupport.execute(sql, member.getMember_id());
		
		sql = "update es_cart set session_id=? where member_id=?";
		this.daoSupport.execute(sql, sessId, member.getMember_id());
		/*****************************************************************/

//		HttpCacheManager.sessionChange();
		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}
	
	//第三方登陆        add by Tension
	@Transactional(propagation = Propagation.REQUIRED)
    public int thirdpartLogin(String thirdpartId) {
        String sql = "select m.*,l.name as lvname from "
                + this.getTableName("member") + " m left join "
                + this.getTableName("member_lv")
                + " l on m.lv_id = l.lv_id where thirdpart_id=?";
        
        

        List<Member> list = this.daoSupport.queryForList(sql, Member.class, StringUtils.lowerCase(thirdpartId));
        if (list == null || list.isEmpty()) {
            return 0;
        }

        Member member = list.get(0);
        long ldate = ((long) member.getLastlogin()) * 1000;
        Date date = new Date(ldate);
        Date today = new Date();
        int logincount = member.getLogincount();
        if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
            logincount++;
        } else {
            logincount = 1;
        }
        Long upLogintime = member.getLastlogin();// 登录积分使用
        member.setLastlogin(DateUtil.getDateline());
        member.setLogincount(logincount);
        this.edit(member);
        ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

//      HttpCacheManager.sessionChange();
        this.memberPluginBundle.onLogin(member, upLogintime);

        return 1;
    }

	@Transactional(propagation = Propagation.REQUIRED)
	public int loginWithCookie(String username, String password) {
		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where m.uname=? and password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ this.getTableName("member") + " m left join "
					+ this.getTableName("member_lv")
					+ " l on m.lv_id = l.lv_id where m.email=? and password=?";
		}
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username, password);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	/**
	 * 系统管理员作为某个会员登录
	 */
	public int loginbysys(String username) {
		 
		if (UserConext.getCurrentAdminUser()==null) {
			throw new RuntimeException("您无权进行此操作，或者您的登录已经超时");
		}

		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where m.uname=?";
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
//		HttpCacheManager.sessionChange();
		return 1;
	}

	
	// 2015/10/11 humaodong
	@Transactional(propagation = Propagation.REQUIRED)
	public AdvanceLogs topup(long memberid, Double advance, Double virtual, String business, String note,Integer orderid) {
	    AdvanceLogs log = new AdvanceLogs();
	    //判断是否已经退过
	    //if(this.baseDaoSupport.queryForInt("select count(0) from es_payment_logs where order_id=? and sn=?", orderid,"YE")==0){
	        this.daoSupport.execute("update es_member set advance=advance+?, virtual=virtual+? where member_id=?", advance, virtual, memberid);
	        log.setImport_advance(advance);
	        log.setImport_virtual(virtual);
	        
	        Member member = this.get(memberid);
	        
	        log.setMember_id(memberid);
	        log.setDisabled("false");
	        log.setMtime(DateUtil.getDateline());
	        log.setMoney(advance+virtual);
	        log.setMember_advance(member.getAdvance());
	        log.setMember_virtual(member.getVirtual());
	        log.setShop_advance(member.getAdvance());// 此字段很难理解
	        log.setMemo(business);
	        log.setMessage(note);
	        advanceLogsManager.add(log);
	        return log;
	    //}
	    //return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public AdvanceLogs pay(long memberid, Double money, int virtualFirst, String business, String note) {
	    
        Member member = this.get(memberid);
        AdvanceLogs log = new AdvanceLogs();
        
        Double adv = member.getAdvance();
        Double vir = member.getVirtual();
        if ((adv+vir) < money) throw new RuntimeException("账户余额不足");
        
        if (virtualFirst == 1) {
            if (vir >= money) {
                member.setVirtual(vir-money);
                log.setExport_virtual(money);
            } else {
                member.setVirtual(0.0);
                member.setAdvance(adv+vir-money);
                log.setExport_virtual(vir);
                log.setExport_advance(money-vir);
            }
        } else {
            if (adv >= money) {
                member.setAdvance(adv-money);
                log.setExport_advance(money);
            } else {
                member.setAdvance(0.0);
                member.setVirtual(adv+vir-money);
                log.setExport_advance(adv);
                log.setExport_virtual(money-adv);
            }
        }
        this.edit(member);
       
        log.setMember_id(memberid);
        log.setDisabled("false");
        log.setMtime(DateUtil.getDateline());
        log.setMoney(money);
        log.setMember_advance(member.getAdvance());
        log.setMember_virtual(member.getVirtual());
        log.setShop_advance(member.getAdvance());// 此字段很难理解
        log.setMemo(business);
        log.setMessage(note);
        advanceLogsManager.add(log);
        return log;
    }

	public void cutMoney(long memberid, Double num) {
		Member member = this.get(memberid);
		if (member.getAdvance() < num) {
			throw new RuntimeException("预存款不足:需要[" + num + "],剩余["
					+ member.getAdvance() + "]");
		}
		String sql = "update member set advance=advance-? where member_id=?";
		this.baseDaoSupport.execute(sql, num, memberid);
	}
	

	@Override
	public Page searchMember(Map memberMap, Integer page, Integer pageSize,String other,String order) {
		String sql = createTemlSql(memberMap);
		sql+=" order by "+other+" "+order;
//		System.out.println(sql);
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		
		return webpage;
	}
	
	@Override
	public List<Member> search(Map memberMap) {
		String sql = createTemlSql(memberMap);
		return this.baseDaoSupport.queryForList(sql, Member.class);
	}
	
	public void updateLv(long memberid, int lvid) {
		String sql = "update member set lv_id=? where member_id=?";
		this.baseDaoSupport.execute(sql, lvid, memberid);
	}
	
	
	private String createTemlSql(Map memberMap){

		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String uname =(String) memberMap.get("uname");
		String mobile = (String) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");
	
		
		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");
		
		
		String sql = "select m.*,lv.name as lv_name from "
			+ this.getTableName("member") + " m left join "
			+ this.getTableName("member_lv")
			+ " lv on m.lv_id = lv.lv_id where 1=1 ";
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.uname like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%')";
			}
		}
		
		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}
		
		if (uname != null && !uname.equals("")) {
			sql += " and m.name like '%" + uname + "%'";
			sql += " or m.uname like '%" + uname + "%'";
		}
		if(mobile!=null && !mobile.equals("")){
			sql += " and m.mobile like '%" + mobile + "%'";
		}
		
		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}
		
		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and m.regtime>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		
		return sql;
	}
	
	//setter getter
	
	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}

	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	@Override
	public Member getMemberByMobile(String mobile) {
		String sql = "select * from es_member where mobile=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, mobile);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	@Override
	public int getCouponCount(long member_id) {
		long currentTime = System.currentTimeMillis()/1000;
		String sql= " select count(*) from es_member_coupon where end_time >? "
				+ " and used =0 and memberid=?";
		
		return this.baseDaoSupport.queryForInt(sql, currentTime,member_id);
	}

	@Override
	public int getBonusCount(long member_id) {
		
		String sql= " select count(*) from es_member_bonus where used =0 "
				+ " and member_id=?";
		
		return this.baseDaoSupport.queryForInt(sql, member_id);
	}

	public List<Map> searchByMobile(String mobile) {
	    String sql = "select member_id as user_id, uname, nickname as user_name, mobile  from es_member where mobile='"+ mobile +"'";
	    List<Map> list = this.baseDaoSupport.queryForList(sql);
	    
	    for (int i = 0; i < list.size(); i++) {
	        Map map = list.get(i);
	        
	        if (map.get("user_name") == null) {
	            map.put("user_name", map.get("uname"));
	        }
	        
	        list.set(i, map);
	    }
	    return list;
	}

	
	//绑定信鸽账号
	public void setXingeAccount(long memberId, String xingeAccount) {
	    String sql = "update es_member set xinge_account='"+ xingeAccount +"' where member_id="+ memberId;
        this.baseDaoSupport.execute(sql);
	}
	
	public Map getLiaomoMember(long memberId, long friendId) {
	    Member member = this.get((int)friendId);
	    String nickname = member.getNickname();
	    String birthday = null;
	    Long birthdayLong = member.getBirthday();
	    List<LiaomoFriend> list = liaomoFriendsManager.getByMemberAndFriend(memberId, friendId);
	    int isFriend = 0;
	    
	    if (nickname == null || nickname.isEmpty()) {
	        nickname = member.getUname();
	    }
	    
	    if (birthdayLong != null) {
	        birthday = DateUtil.toString(new Date(birthdayLong * 1000),"yyyy-MM-dd");
	    }
	    
	    if (list.size() > 0 && list.get(0).getStatus() == LiaomoFriend.STATUS_OK) {
	        isFriend = 1;
	    }
	    
	    Map map = new HashMap();
	    map.put("user_id", member.getMember_id());
	    map.put("sex", member.getSex());
	    map.put("user_name", nickname);
	    map.put("birthday", birthday);
	    map.put("hometown", member.getProvince());
	    map.put("hobby", member.getHobby());
	    map.put("email", member.getEmail());
	    map.put("face", UploadUtil.replacePath(member.getFace()));
	    map.put("description", member.getDescription());
	    map.put("is_friend", isFriend);
	    
	    return map;
	}
	
	public void updateLiaomoMember(long memberId, Integer sex, String nickname, String birthday, 
	        String hometown, String hobby, String email, File face, String faceFileName) {
	    String cond = "";
	    
	    if (sex != null) {
	        cond += "sex="+ sex;
	    }
	    
	    if (nickname != null) {
	        if (cond.length() > 0) {
	            cond += ",";
	        }
	        cond += "nickname='"+ nickname +"'";
	    }
	    
	    if (birthday != null) {
            if (cond.length() > 0) {
                cond += ",";
            }
            
            cond += "birthday="+ DateUtil.getDateline(birthday);;
        }
	    
	    if (hometown != null) {
            if (cond.length() > 0) {
                cond += ",";
            }
            cond += "province='"+ hometown +"'";
        }
	    
	    if (hobby != null) {
            if (cond.length() > 0) {
                cond += ",";
            }
            cond += "hobby='"+ hobby +"'";
        }
	    
	    if (email != null) {
            if (cond.length() > 0) {
                cond += ",";
            }
            cond += "email='"+ email +"'";
        }
	    
	    if (face != null) {
	        if (cond.length() > 0) {
                cond += ",";
            }
	        String uploadFace = UploadUtil.upload(face, faceFileName, "liaomo");
	        cond += "face='"+ uploadFace +"'";
	    }
	    
	    String sql = "update es_member set "+ cond +" where member_id="+ memberId;
	    this.baseDaoSupport.execute(sql);
	}
	
    
    public LiaomoFriendsManager getLiaomoFriendsManager() {
        return liaomoFriendsManager;
    }
    
    public void setLiaomoFriendsManager(LiaomoFriendsManager liaomoFriendsManager) {
        this.liaomoFriendsManager = liaomoFriendsManager;
    }

    @Override
    //设置第三方登陆ID
    public void setThirdpartId(String mobile, String thirdpartId) {
        String sql = "update es_member set thirdpart_id='"+ thirdpartId +"' where mobile='"+ mobile +"'";
        this.baseDaoSupport.execute(sql);
    }
    
    //修改用户手机号
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMobile(long member_id, String mobile) {
       this.baseDaoSupport.execute("update es_member set mobile =? where member_id=?", mobile,member_id);
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public AdvanceLogs topup(long memberid, Double advance, Double virtual, String business, String note) {
        AdvanceLogs log = new AdvanceLogs();
        //判断是否已经退过
            this.daoSupport.execute("update es_member set advance=advance+?, virtual=virtual+? where member_id=?", advance, virtual, memberid);
            log.setImport_advance(advance);
            log.setImport_virtual(virtual);
            
            Member member = this.get(memberid);
            
            log.setMember_id(memberid);
            log.setDisabled("false");
            log.setMtime(DateUtil.getDateline());
            log.setMoney(advance+virtual);
            log.setMember_advance(member.getAdvance());
            log.setMember_virtual(member.getVirtual());
            log.setShop_advance(member.getAdvance());// 此字段很难理解
            log.setMemo(business);
            log.setMessage(note);
            advanceLogsManager.add(log);
            return log;
    }
}
