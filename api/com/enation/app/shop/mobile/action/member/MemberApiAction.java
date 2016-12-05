/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：会员api
 * 修改人：Sylow  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.member;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.mobile.util.ValidateUtils;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.sms.SmsSender;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员api
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings({ "serial", "rawtypes", "unused" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("member")
public class MemberApiAction extends WWAction {

	private IMemberManager memberManager;
	private IMemberCommentManager memberCommentManager;
	/**
	 * 重新发送激活邮件
	 */
	private JavaMailSender mailSender;
	private EmailProducer mailMessageProducer;
	private IMemberPointManger memberPointManger;

	private String username;
	private String password;
	private String validcode;
	/**
	 * 会员注册
	 */

	private String email;
	private String mobile;
	private String sendSmsCode;
	/**
	 * 更改密码
	 */
	private String oldpassword;
	private String re_passwd;

	/**
	 * 设置第三方登陆ID
	 */
	private Integer memberId;
	private String thirdpartId;

	private Integer typeId;

	/**
	 * 上传头像变量
	 */
	private File faceFile;
	private File face; // 633行有单独的上传头像，没动，酌情处理，需要这里，
	private String faceFileName;

	private String faceFileFileName;

	private String photoServer;
	private String photoId;
	private String type;
	private String turename;

	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private String province;
	private String city;
	private String region;
	private String address;
	private String zip;
	private String tel;
	private String nickname;
	private String sex;
	private String mybirthday;

	private File file;
	private String fileFileName;

	private ISmsManager smsManager;

	/**
	 * 会员登录
	 *
	 * @return json字串 result 为1表示登录成功，0表示失败 ，int型 message 为提示信息 ，String型
	 */
	public String login() {
		try {
			if (memberManager.login(username, password) != 1) {
				this.showPlainErrorJson("账号密码错误");
				return WWAction.JSON_MESSAGE;
			}
			String cookieValue = EncryptionUtil1.authcode("{username:\""
					+ username + "\",password:\"" + StringUtil.md5(password)
					+ "\"}", "ENCODE", "", 0);
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
					"GomeUser", cookieValue, 60 * 24 * 14);
			Member member = UserConext.getCurrentMember();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("member_id", member.getMember_id().toString());
			map.put("username", member.getUname());
			map.put("level", member.getLvname());
			map.put("mobile", member.getMobile());
			map.put("face", UploadUtil.replacePath(member.getFace()));
			this.json = JsonMessageUtil.getMobileObjectJson(map);
		} catch (RuntimeException e) {
			this.logger.error("登录出错", e);
			this.showPlainErrorJson("登录出错[" + e.getMessage() + "]");

		}

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 是否已登录
	 * 
	 * @return
	 */
	public String isLogin() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			this.json = JsonMessageUtil.expireSession();
		} else {
			// this.showPlainSuccessJson("已经登录");
			this.json = JsonMessageUtil.getMobileObjectJson(member);
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 注销会员登录
	 *
	 * @return json字串 result 为1表示注销成功，0表示失败 ，int型 message 为提示信息 ，String型
	 */
	public String logout() {
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.json = JsonMessageUtil.expireSession();
			return WWAction.JSON_MESSAGE;
		}
		try {
			this.memberManager.logout();
			// 设置cookie有效时间为0 即删除
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
					"JavaShopUser", "", 0);
			this.showPlainSuccessJson("注销成功");
		} catch (RuntimeException e) {
			this.logger.error("退出登录出错", e);
			this.showPlainErrorJson("退出出错[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 修改会员密码
	 *
	 * @return json字串 result 为1表示修改成功，0表示失败 ，int型 message 为提示信息 ，String型
	 */
	public String changePassword() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			this.json = JsonMessageUtil.expireSession();
			return WWAction.JSON_MESSAGE;
		}
		HttpServletRequest request = getRequest();
		String oldpassword = request.getParameter("oldpassword");
		oldpassword = oldpassword == null ? "" : StringUtil.md5(oldpassword);

		if (!oldpassword.equals(member.getPassword())) {
			this.showPlainErrorJson("修改失败！原始密码不符");
			return WWAction.JSON_MESSAGE;
		}

		try {
			memberManager.updatePassword(password);
			this.json = JsonMessageUtil.expireSession();
			return WWAction.JSON_MESSAGE;
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
		}

		this.showPlainErrorJson("修改密码失败");
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 验证手机是否已注册
	 * 
	 * @return json result 为1表示发送成功，0表示失败 ，int型 message 为提示信息 ，String型
	 */
	public String checkMobile() { // typeId 0 : 注册 1： 找回密码
		if (!ValidateUtils.checkMobile(mobile)) {
			this.showPlainErrorJson("手机号码不正确，请重新输入！");
			return WWAction.JSON_MESSAGE;
		}
		int result = this.memberManager.checkMobile(mobile);
		if (result == 1 && typeId == 0) {
			this.showPlainErrorJson("此号码已注册");
			return WWAction.JSON_MESSAGE;
		}
		if (result == 1 && typeId == 1) { // 找回密码
			this.showPlainSuccessJson("可以");
			return WWAction.JSON_MESSAGE;
		}
		if (result == 0 && typeId == 1) {
			this.showPlainErrorJson("此号码不存在");
			return WWAction.JSON_MESSAGE;
		}
		this.showPlainSuccessJson("可以注册");
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 发送手机验证码
	 *
	 * @return json字串 result 为1表示发送成功，0表示失败 ，int型 message 为提示信息 ，String型
	 */
	public String sendSmsCode() {
		if (!ValidateUtils.checkMobile(mobile)) {
			this.showPlainErrorJson("手机号码不正确，请重新输入！");
			return WWAction.JSON_MESSAGE;
		}
		try {
			String mobileCode = "" + (int) ((Math.random() * 9 + 1) * 100000);
			String content = "您本次的验证码为：" + mobileCode;
			SmsSender.sendSms(mobile, content);
			ThreadContextHolder.getHttpResponse().setContentType(
					"text/json;charset=UTF-8");
			this.json = "{\"result\":1,\"message\":\"" + mobileCode + "\"}";
		} catch (Exception e) {
			this.showPlainErrorJson("发送失败");
		}

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 会员注册
	 */
	public String register() {
		try {
			Member member = new Member();
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String registerip = request.getRemoteAddr();

			if (StringUtil.isEmpty(mobile)) {
				this.showPlainErrorJson("用户名不能为空！");
				return WWAction.JSON_MESSAGE;
			}
			if (StringUtil.isEmpty(password)) {
				this.showPlainErrorJson("密码不能为空！");
				return WWAction.JSON_MESSAGE;
			}
			if (password.matches("^[0-9]*$")) {
				this.showPlainErrorJson("密码不能全为数字！");
				return WWAction.JSON_MESSAGE;
			}
			if (memberManager.checkMobile(mobile) > 0) {
				this.showPlainErrorJson("此用户名已经存在!");
				return WWAction.JSON_MESSAGE;
			}
			member.setPassword(password);
			member.setMobile(mobile);
			member.setUname(mobile);
			member.setName(mobile);
			member.setNickname(mobile);
			member.setRegisterip(registerip);

			if (memberManager.register(member) != 1) {
				this.showPlainErrorJson("用户名[" + member.getUname() + "]已存在!");
				return WWAction.JSON_MESSAGE;
			}

			this.memberManager.login(username, password);

			this.showPlainSuccessJson("注册成功");

		} catch (RuntimeException e) {
			this.logger.error("注册出错", e);
			this.showPlainErrorJson("注册出错[" + e.getMessage() + "]");

		}

		return WWAction.JSON_MESSAGE;
	}

	public String findPw() {

		if (StringUtil.isEmpty(mobile)) {
			this.showPlainErrorJson("用户名不能为空！");
			return WWAction.JSON_MESSAGE;
		}
		int result = this.memberManager.checkMobile(mobile);
		if (result == 0) {
			this.showPlainErrorJson("此用户不存在！");
			return WWAction.JSON_MESSAGE;
		}
		if (password.matches("^[0-9]*$")) {
			this.showPlainErrorJson("密码不能全为数字！");
			return WWAction.JSON_MESSAGE;
		}
		try {
			memberManager.findPassword(mobile, password);
			Member member = UserConext.getCurrentMember();

			if (member != null) {
				this.memberManager.logout();
			}

			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
					"JavaShopUser", "", 0);
			this.showPlainSuccessJson("密码修改成功");
			return WWAction.JSON_MESSAGE;
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
		}

		this.showPlainErrorJson("操作失败！");
		return WWAction.JSON_MESSAGE;

	}

	/**
	 * 校验验证码
	 * 
	 * @param validcode
	 * @param name
	 *            (1、memberlogin:会员登录 2、memberreg:会员注册)
	 * @return 1成功 0失败
	 */
	private int validcode(String validcode, String name) {
		if (validcode == null) {
			return 0;
		}

		String code = (String) ThreadContextHolder.getSessionContext()
				.getAttribute(ValidCodeServlet.SESSION_VALID_CODE + name);
		if (code == null) {
			return 0;
		} else {
			if (!code.equalsIgnoreCase(validcode)) {
				return 0;
			}
		}
		return 1;
	}

	/*
	 * 
	 * 会员个人信息
	 */
	public String memberInfo() {
		try {
			Member member = UserConext.getCurrentMember();

			if (member == null) {
				this.json = JsonMessageUtil.expireSession();
				return WWAction.JSON_MESSAGE;
			}

			// edit by Tension 修改会员修改头像后没有及时显示
			member = memberManager.get(member.getMember_id());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", member.getMember_id().toString());
			map.put("username", member.getUname());
			map.put("level", member.getLvname());
			map.put("level_id", member.getLv_id());
			map.put("total_money", member.getAdvance() + member.getVirtual());
			map.put("advance", member.getAdvance());
			map.put("virtual", member.getVirtual());
			map.put("point", member.getPoint());
			map.put("face", UploadUtil.replacePath(member.getFace()));
			map.put("mobile", member.getMobile());
			this.json = JsonMessageUtil.getMobileObjectJson(map);

		} catch (RuntimeException e) {
			this.showPlainErrorJson("数据库错误 [" + e.getMessage() + "]");

		}

		return WWAction.JSON_MESSAGE;

	}

	/**
	 * 我的评价
	 * 
	 */
	public String myComment() {

		try {
			Member member = UserConext.getCurrentMember();
			HttpServletRequest request = getRequest();
			int page = NumberUtils.toInt(request.getParameter("page"), 1);
			int pageSize = 20;
			long member_id = member.getMember_id();
			Page commentPage = memberCommentManager.getCommentsForGoods(page,
					pageSize, member_id, 1);
			List list = (List) commentPage.getResult();
			for (int i = 0; i < list.size(); i++) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) list.get(i);
				map.put("thumbnail",
						UploadUtil.replacePath(map.get("thumbnail")));
				map.put("small", UploadUtil.replacePath(map.get("small")));
				map.put("content", map.get("content").replace("&nbsp;", " "));
			}

			this.json = JsonMessageUtil.getMobileObjectJson(commentPage);

		} catch (RuntimeException e) {

			this.showPlainErrorJson("数据库运行错误" + e);
		}

		return WWAction.JSON_MESSAGE;
	}

	public String setApiSession() {
		this.showPlainSuccessJson("success");

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 设置第三方登陆id并登陆
	 * 
	 */
	public String setThirdpartId() {
		try {
			memberManager.setThirdpartId(mobile,
					StringUtils.lowerCase(thirdpartId));
			memberManager.thirdpartLogin(StringUtils.lowerCase(thirdpartId));
			Member member = UserConext.getCurrentMember();
			String cookieValue = EncryptionUtil1.authcode(
					"{username:\"" + member.getUname() + "\",password:\""
							+ member.getPassword() + "\"}", "ENCODE", "", 0);
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
					"GomeUser", cookieValue, 60 * 24 * 14);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("member_id", member.getMember_id().toString());
			map.put("username", member.getUname());
			map.put("level", member.getLvname());
			this.json = JsonMessageUtil.getMobileObjectJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			this.showPlainErrorJson("设置第三方登陆id失败");
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 第三方登陆
	 * 
	 */
	public String thirdpartLogin() {
		try {
			if (memberManager.getMemberByThirdpartId(StringUtils
					.lowerCase(thirdpartId)) == null) {
				this.json = JsonMessageUtil.notFountThirdpartId();
				return WWAction.JSON_MESSAGE;
			}

			if (memberManager
					.thirdpartLogin(StringUtils.lowerCase(thirdpartId)) != 1) {
				this.showPlainErrorJson("等三方登陆ID错误");
				return WWAction.JSON_MESSAGE;
			}

			Member member = UserConext.getCurrentMember();
			String cookieValue = EncryptionUtil1.authcode(
					"{username:\"" + member.getUname() + "\",password:\""
							+ member.getPassword() + "\"}", "ENCODE", "", 0);
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
					"GomeUser", cookieValue, 60 * 24 * 14);
			Map<String, String> returnMap = new HashMap<String, String>();
			returnMap.put("mobile", member.getMobile());
			returnMap.put("member_id", member.getMember_id().toString());
			this.json = JsonMessageUtil.getMobileObjectJson(returnMap);
		} catch (RuntimeException e) {
			this.logger.error("登录出错", e);
			this.showPlainErrorJson("登录出错[" + e.getMessage() + "]");

		}
		return WWAction.JSON_MESSAGE;
	}

	public String saveInfo() {
		Member member = UserConext.getCurrentMember();

		member = memberManager.get(member.getMember_id());

		// 先上传图片
		String faceField = "faceFile";

		if (file != null) {

			// 判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(
						fileFileName.lastIndexOf(".") + 1,
						fileFileName.length());
				if (allowTYpe.toString().indexOf(ex.toLowerCase()) < 0) {
					this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					return JSON_MESSAGE;
				}
			}

			// 判断文件大小

			if (file.length() > 200 * 1024) {
				this.showErrorJson("'对不起,图片不能大于200K！");
				return JSON_MESSAGE;
			}

			String imgPath = UploadUtil.upload(file, fileFileName, faceField);
			member.setFace(imgPath);
		}

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		if (StringUtil.isEmpty(mybirthday)) {
			member.setBirthday(0L);
		} else {
			member.setBirthday(DateUtil.getDateline(mybirthday));
		}

		member.setProvince_id(province_id);
		member.setCity_id(city_id);
		member.setRegion_id(region_id);
		member.setProvince(province);
		member.setCity(city);
		member.setRegion(region);
		member.setAddress(address);
		member.setZip(zip);
		member.setEmail(email);
		if (mobile != null) {
			member.setMobile(mobile);
		}
		member.setTel(tel);
		if (nickname != null) {
			member.setNickname(nickname);
		}
		member.setSex(Integer.valueOf(sex));

		// 身份
		String midentity = request.getParameter("member.midentity");
		if (!StringUtil.isEmpty(midentity)) {
			member.setMidentity(StringUtil.toInt(midentity, 0));
		} else {
			member.setMidentity(0);
		}
		// String pw_question = request.getParameter("member.pw_question");
		// member.setPw_question(pw_question);
		// String pw_answer = request.getParameter("member.pw_answer");
		// member.setPw_answer(pw_answer);
		try {
			// 判断否需要增加积分
			boolean addPoint = false;
			if (member.getInfo_full() == 0
					&& !StringUtil.isEmpty(member.getName())
					&& !StringUtil.isEmpty(member.getNickname())
					&& !StringUtil.isEmpty(member.getProvince())
					&& !StringUtil.isEmpty(member.getCity())
					&& !StringUtil.isEmpty(member.getRegion())
					&& (!StringUtil.isEmpty(member.getMobile()) || !StringUtil
							.isEmpty(member.getTel()))) {
				addPoint = true;
			}
			// 增加积分
			if (addPoint) {
				member.setInfo_full(1);
				memberManager.edit(member);
				if (memberPointManger
						.checkIsOpen(IMemberPointManger.TYPE_FINISH_PROFILE)) {
					int point = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE
									+ "_num");
					int mp = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE
									+ "_num_mp");
					memberPointManger.add(member.getMember_id(), point,
							"完善个人资料", null, mp);
				}
			} else {
				memberManager.edit(member);
			}
			this.showSuccessJson("编辑个人资料成功！");
			return JSON_MESSAGE;
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("编辑个人资料失！");
			return JSON_MESSAGE;
		}
	}

	// ************to宏俊：以api先不用书写文档****************/
	protected String toUrl(String path) {
		String static_server_domain = SystemSetting.getStatic_server_domain();
		String urlBase = static_server_domain;
		return path.replaceAll("fs:", urlBase);
	}

	// 修改用户的mobile
	public String updateMobile() {
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.json = JsonMessageUtil.expireSession();
			return WWAction.JSON_MESSAGE;
		}
		try {
			this.memberManager.updateMobile(member.getMember_id(), mobile);
			this.showPlainSuccessJson("修改成功");
		} catch (RuntimeException e) {
			this.showPlainErrorJson("修改手机号失败");

		}
		return WWAction.JSON_MESSAGE;

	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public void setMemberCommentManager(
			IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getRe_passwd() {
		return re_passwd;
	}

	public void setRe_passwd(String re_passwd) {
		this.re_passwd = re_passwd;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getThirdpartId() {
		return thirdpartId;
	}

	public void setThirdpartId(String thirdpartId) {
		this.thirdpartId = thirdpartId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public String getSendSmsCode() {
		return sendSmsCode;
	}

	public void setSendSmsCode(String sendSmsCode) {
		this.sendSmsCode = sendSmsCode;
	}

	public File getFaceFile() {
		return faceFile;
	}

	public void setFaceFile(File faceFile) {
		this.faceFile = faceFile;
	}

	public File getFace() {
		return face;
	}

	public void setFace(File face) {
		this.face = face;
	}

	public String getFaceFileName() {
		return faceFileName;
	}

	public void setFaceFileName(String faceFileName) {
		this.faceFileName = faceFileName;
	}

	public String getFaceFileFileName() {
		return faceFileFileName;
	}

	public void setFaceFileFileName(String faceFileFileName) {
		this.faceFileFileName = faceFileFileName;
	}

	public String getPhotoServer() {
		return photoServer;
	}

	public void setPhotoServer(String photoServer) {
		this.photoServer = photoServer;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTurename() {
		return turename;
	}

	public void setTurename(String turename) {
		this.turename = turename;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMybirthday() {
		return mybirthday;
	}

	public void setMybirthday(String mybirthday) {
		this.mybirthday = mybirthday;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}

}
