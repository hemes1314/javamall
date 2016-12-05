package com.enation.app.shop.component.point.plugin;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberEmailCheckEvent;
import com.enation.app.shop.core.plugin.member.IMemberLoginEvent;
import com.enation.app.shop.core.plugin.member.IMemberRegisterEvent;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 会员积分插件
 * @author kingapex
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class MemberPointPlugin extends AutoRegisterPlugin implements
		IMemberRegisterEvent,IMemberLoginEvent,IMemberEmailCheckEvent {
	
	private IDaoSupport baseDaoSupport;
	
	private IMemberPointManger memberPointManger;
	/**
	 * 响应会员注册事件，送出积分
	 */
	@Override
	public void onRegister(Member member) {
		/**
		 * ------------
		 * 增加会员积分
		 * ------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_REGISTER) ){
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER+"_num");
			int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER+"_num_mp");
			memberPointManger.add(member.getMember_id(), point, "成功注册", null,mp);
		}
	
	}
	

	@Override
	public void onEmailCheck(Member member) {
		/**
		 * ------------
		 * 增加会员积分
		 * ------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_EMIAL_CHECK) ){
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num");
			int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num_mp");
			memberPointManger.add(member.getMember_id(), point, "完成邮箱验证", null,mp);
		}
		
	}


	@Override
	public void onLogin(Member member,Long upLogintime) {
		if(upLogintime==null || upLogintime==0)
			upLogintime=member.getLastlogin();
		long ldate = ((long)upLogintime)*1000;
		Date date = new Date(ldate);
		Date today = new Date();
		
		/**
		 * ------------
		 * 增加会员积分(一天只增加一次)
		 * ------------
		 */
		if(!DateUtil.toString(date, "yyyy-MM-dd").equals(DateUtil.toString(today, "yyyy-MM-dd"))){//非今天
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_LOGIN) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_LOGIN+"_num");
				int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_LOGIN+"_num_mp");
				memberPointManger.add(member.getMember_id(), point, DateUtil.toString(today, "yyyy年MM月dd日")+"登录", null,mp);
				
			}
		}
		
	}
	
	@SuppressWarnings("unused")
	private boolean isRepeatedIp(String ip,int parentid){
		String sql ="select count(0) from member where parentid=? and registerip=?";
		int count =this.baseDaoSupport.queryForInt(sql, parentid,ip);
		return count>1;
	}
	
	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}


	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}


	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}


	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}



}
