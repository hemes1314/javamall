package com.enation.app.shop.core.tag.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;

import freemarker.template.TemplateModelException;
@Component
/**
 * 验证重置密码标签
 * @author LiFenLong
 *
 */
public class CheckFindPwdTag extends BaseFreeMarkerTag{
	private IMemberManager memberManager;
	/**
	 *  s 返回验证字符串
	 *  memberid 会员Id
	 *  member 会员
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String s = request.getParameter("s");
		if(s==null){
			result.put("type",1);
			result.put("message","非法链接地址,请重新找回");
			return result;
		}
		String str = EncryptionUtil1.authcode(s, "DECODE","",0);
		String[] array = StringUtils.split(str,",");
		if(array.length!=2){
			result.put("type",1);
			result.put("message","验证字串不正确,请重新找回");
			return result;
		}
		int memberid  = Integer.valueOf(array[0]);
		long regtime = Long.valueOf(array[1]);
		
		Member member = this.memberManager.get(memberid);
		if(member==null || member.getRegtime() != regtime){
			result.put("type",1);
			result.put("message","验证字串不正确,请重新找回");
			return result;
		}
		if(member.getFind_code()==null||"".equals(member.getFind_code())||member.getFind_code().length()!=10){
			result.put("type",1);
			result.put("message","地址已经过期,请重新找回");
			return result;
		}
		int time = NumberUtils.toInt(member.getFind_code()) + 60 * 60;
		if(DateUtil.getDateline()>time){
			result.put("type",1);
			result.put("message","地址已经过期,请重新找回");
			return result;
		}
		//成功 
		result.put("type", 0);
		result.put("s", s);
		result.put("email", member.getEmail());
		return result;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
