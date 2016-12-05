package com.enation.app.shop.core.action.api;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 评论api
 * @author kingapex
 *2013-8-7下午5:41:07
 */
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("commentApi")
public class CommentApiAction extends WWAction {

	private File file ;
	private String fileFileName;
	private IGoodsManager goodsManager;
	private IMemberCommentManager memberCommentManager;
	private IMemberOrderItemManager memberOrderItemManager;
    private String content;
    private int grade;
    private int comment_id;
    private int commenttype;
    private int goods_id;
	
	/**
	 * 发表评论
	 * @param goods_id:商品id,int型，必填项
	 * @param commenttype:评论类型，int型，必填项，可选值：1或2，1为评论，2为咨询。
	 * @param content:评论内容，String型，必填项。
	 * @param file:评论的图片，File类型，可选项。
	 * @param grade :评分
	 * @return 返回json串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息
	 */
	public String add(){

		MemberComment memberComment = new MemberComment();
		//先上传图片
		String subFolder = "comment";
		
		if(file!=null){
		
			//判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
				if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
					this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					return this.JSON_MESSAGE;
				}
			}
			
			//判断文件大小
			
			if(file.length() > 200 * 1024){
				this.showErrorJson("'对不起,图片不能大于200K！");
				return this.JSON_MESSAGE;
			}
			
			String imgPath=	UploadUtil.upload(file, fileFileName, subFolder);
			memberComment.setImg(imgPath);
		}
		
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		int type = commenttype; //StringUtil.toInt(request.getParameter("commenttype"),0);
		//判断是不是评论或咨询
		if(type != 1 && type != 2){
			this.showErrorJson("系统参数错误！");
			return this.JSON_MESSAGE;
		}
		memberComment.setType(type);
		
		//int goods_id = StringUtil.toInt(request.getParameter("goods_id"),0);
		if(goodsManager.get(goods_id) == null){
			this.showErrorJson("此商品不存在！");
			return this.JSON_MESSAGE;
		}
		memberComment.setGoods_id(goods_id);
		
		//String content = request.getParameter("content");
		if(StringUtil.isEmpty(content)){
			this.showErrorJson("评论或咨询内容不能为空！");
			return this.JSON_MESSAGE;
		}else if(content.length()>1000){
			this.showErrorJson("请输入1000以内的内容！");
			return this.JSON_MESSAGE;
		}
		content = StringUtil.htmlDecode(content);
		memberComment.setContent(content);
		
		Member member = UserConext.getCurrentMember();
		if(type == 1){
			if(member == null){
				this.showErrorJson("只有登录且成功购买过此商品的用户才能发表评论！");
				return this.JSON_MESSAGE;
			}
			int buyCount = memberOrderItemManager.count(member.getMember_id(), goods_id);
			int commentCount = memberOrderItemManager.count(member.getMember_id(), goods_id,1);
			if(buyCount <= 0){
			//	this.showErrorJson("只有成功购买过此商品的用户才能发表评论！");
				//return this.JSON_MESSAGE;
			}
		//	if(commentCount >= buyCount){
		//		this.showErrorJson("对不起，您已经评论过此商品！");
		//		return this.JSON_MESSAGE;
		//	}
			//int grade = StringUtil.toInt(request.getParameter("grade"),0);
			if(grade < 0 || grade > 5){
				memberComment.setGrade(5);
			}else{
				memberComment.setGrade(grade);
			}
		}else{
			memberComment.setImg(null);
			memberComment.setGrade(0);
		}
		memberComment.setMember_id(member == null ? 0 : member.getMember_id());
		memberComment.setDateline(System.currentTimeMillis()/1000);
		memberComment.setIp(request.getRemoteHost());

		try {
			memberCommentManager.add(memberComment);
			//更新为已经评论过此商品
			if(type == 1){
				MemberOrderItem memberOrderItem = memberOrderItemManager.get(member.getMember_id(), goods_id,0);
				if(memberOrderItem != null){
					memberOrderItem.setComment_time(System.currentTimeMillis());
					memberOrderItem.setCommented(1);
					memberOrderItemManager.update(memberOrderItem);
				}
			}
			
			this.showSuccessJson("发表成功");
			
		} catch (RuntimeException e) {
			this.logger.error("发表评论出错",e);
			this.showErrorJson("发表评论出错"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	} 
    
	/**
	 * 修改会员评论
	 * @param comment_id 评论Id
	 * @param grade 评级
	 * @param content 评论信息
	 * @return
	 */
	public String update(){

		try {
			MemberComment memberComment = new MemberComment();
			memberComment = this.memberCommentManager.get(comment_id);
			memberComment.setComment_id(comment_id);
			memberComment.setGrade(grade);
			memberComment.setContent(content);
			memberCommentManager.update(memberComment);
			this.showSuccessJson("更新成功");
		} catch (RuntimeException e) {
			this.logger.error("修改评论出错",e);
			this.showErrorJson("更新失败");
		}
		return this.JSON_MESSAGE;
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

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public IMemberOrderItemManager getMemberOrderItemManager() {
		return memberOrderItemManager;
	}

	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public int getCommenttype() {
		return commenttype;
	}

	public void setCommenttype(int commenttype) {
		this.commenttype = commenttype;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	
}
