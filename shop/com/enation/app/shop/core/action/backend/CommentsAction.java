package com.enation.app.shop.core.action.backend;

import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 产品评论管理
 * 
 * @author Dawei
 * @author LiFenLong 2014-4-1; 4.0版本改造
 */


@SuppressWarnings({ "serial", "unused", "rawtypes" })
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("comments")
@Results({
	@Result(name="bglist", type="freemarker", location="/shop/admin/comments/list.html"),
	@Result(name="gmlist", type="freemarker", location="/shop/admin/comments/gm_list.html"),
	@Result(name="detail", type="freemarker", location="/shop/admin/comments/detail.html")
})
public class CommentsAction extends WWAction {

	private IMemberCommentManager memberCommentManager;
	private IMemberPointManger memberPointManger;
	private IMemberOrderItemManager memberOrderItemManager;
	private IMemberManager memberManager;
	private IGoodsManager goodsManager;

	// 评论类型:1为评论，2为咨询
	private int type;
	private int status;
	

	private int commentId;

	private MemberComment memberComment;

	private Member member;

	private String reply;

	private Integer[] comment_id;

	/**
	 * 全量同步
	 * @return
     */
	public String syncAll() {
		String msg = memberCommentManager.syncOpenAPI();
		this.showSuccessJson(msg);
		return JSON_MESSAGE;
	}
	/**
	 * 评论(咨询)列表
	 * @author LiFenLong 
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @return 评论、咨询列表页面
	 */
	public String list() {
		if(type==2){
			return "gmlist";
		}
		return "bglist";
	}
	/**
	 * 评论(咨询)列表json
	 * @param pageNo 分页页数,Integer
	 * @param pageSize  每页分页的数量,Integer
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @return  评论、咨询列表json
	 */
	public String listJson() {
		setPageSize(10);
		webpage = memberCommentManager.getAllComments(getPage(), pageSize, type);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	/**
	 * 查看所有带状态的评论或问答
	 * @param pageNo 分页页数,Integer
	 * @param pageSize  每页分页的数量,Integer
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @param status 回复评论
	 * @return 评论列表
	 */
	 public String msgList(){
	 this.webpage = memberCommentManager.getCommentsByStatus(getPage(), getPageSize(), type, status);
	 return "bglist";
	 }
	/**
	 * 查看评论、咨询详细
	 * @param commentId 评论、咨询Id,Integer
	 * @param memberComment 会员评论对象,MemberComment
	 * @return 查看评论、咨询详细页面
	 */
	public String detail() {
	 
		memberComment = memberCommentManager.get(commentId);
		if (memberComment.getMember_id() != null&& memberComment.getMember_id().intValue() != 0) {
			member = memberManager.get(memberComment.getMember_id());
		}
		if (memberComment != null && !StringUtil.isEmpty(memberComment.getImg())) {
			memberComment.setImgPath(UploadUtil.replacePath(memberComment.getImg()));
		}
		return "detail";
	}

	/**
	 * 回复
	 * @param reply 回复内容,String
	 * @param commentId 评论、咨询Id
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String add() {
		if (StringUtil.isEmpty(reply)) {
			this.showErrorJson("回复不能为空！");
			return JSON_MESSAGE;
		}
		MemberComment dbMemberComment = memberCommentManager.get(commentId);
		if (dbMemberComment == null) {
			this.showErrorJson("此评论或咨询不存在！");
			return JSON_MESSAGE;
		}
		dbMemberComment.setReply(reply);
		dbMemberComment.setReplystatus(1);
		dbMemberComment.setReplytime(DateUtil.getDateline());
		try {
			memberCommentManager.update(dbMemberComment);
			this.showSuccessJson("回复成功");
		} catch (Exception e) {
			this.showErrorJson("回复失败！");
			return JSON_MESSAGE;
		}
		return JSON_MESSAGE;
	}
	/**
	 * 删除
	 * @param comment_id 评论、咨询Id数组,Integer[]
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String delete() {
		
		try {
			memberCommentManager.delete(comment_id);
			this.showSuccessJson("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("操作失败");
		}
			return JSON_MESSAGE;
	}
	/**
	 * 评论、咨询不通过
	 * @param commentId 评论、咨询Id,Integer
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String hide() {
		try {
			memberComment = memberCommentManager.get(commentId);
			memberComment.setStatus(2);
			memberCommentManager.update(memberComment);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("操作失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 评论、咨询通过
	 * @param commentId 评论、咨询Id,Integer
	 * @param cat_id 商品分类,Integer
	 * @param point 积分,Integer
	 * @param reson 评论类型,图片、文字,String
	 * @param mp 获取积分值,Integer 
	 * @return  json
	 * result 1.操作成功.0.操作失败
	 */
	public String show() {
		try {
			memberComment = memberCommentManager.get(commentId);
			boolean isFirst = false;

			// 首次评论额外50积分
			if (memberCommentManager.getGoodsCommentsCount(memberComment
					.getGoods_id()) == 0&&memberComment.getType()!=2) {
				isFirst = true;
			}

			memberComment.setStatus(1);
			memberCommentManager.update(memberComment);

			// 清除浏览器缓存
			Map goods = goodsManager.get(memberComment.getGoods_id());
			if (goods != null) {
				String url = "";
				if (goods.get("cat_id") != null) {
					switch (StringUtil.toInt(goods.get("cat_id").toString(), 0)) {
					case 1:
					case 2:
						url = "yxgoods";
						break;
					case 3:
					case 4:
					case 12:
					case 18:
						url = "jkgoods";
						break;
					case 6:
						url = "jpgoods";
						break;
					case 5:
					case 7:
					case 8:
					case 9:
						url = "goods";
						break;
					case 19:
						url = "gngoods";
						break;
					}
				}
//				HttpCacheManager.updateUrlModified("/" + url + "-"
//						+ memberComment.getGoods_id() + ".html");
			}

			/**
			 * ------------ 增加会员积分 ------------
			 */
			// 判断是否是带图片的评论
			String reson ="文字评论";
			String type = IMemberPointManger.TYPE_COMMENT;
			if (!StringUtil.isEmpty(memberComment.getImg())) {
				type = IMemberPointManger.TYPE_COMMENT_IMG;
				reson="上传图片评论";
			}

			if (memberPointManger.checkIsOpen(type)) {
				if (memberComment.getMember_id() != null && memberComment.getMember_id().intValue() != 0&&memberComment.getType()!=2) {
					int point = memberPointManger.getItemPoint(type + "_num");
					int mp = memberPointManger.getItemPoint(type + "_num_mp");
					memberPointManger.add(memberComment.getMember_id(), point, reson, null, mp);
				 
				}
			}

			// 首次评论额外50积分
			if (isFirst) {
				if (memberPointManger
						.checkIsOpen(IMemberPointManger.TYPE_FIRST_COMMENT)) {
					int point = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FIRST_COMMENT
									+ "_num");
					int mp = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FIRST_COMMENT
									+ "_num_mp");
					memberPointManger.add(memberComment.getMember_id(), point,
							"首次评论", null, mp);
				}
			}

			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("操作失败");
		}
		return JSON_MESSAGE;
	}

	/**
	 * 删除一条信息
	 * @param commentId 评论、咨询Id
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String deletealone() {
		memberComment = memberCommentManager.get(commentId);
		if (memberComment != null) {
			memberCommentManager.deletealone(commentId);
		}
		this.showSuccessJson("删除成功");
		return JSON_MESSAGE;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}

	public void setMemberCommentManager(
			IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	

	public MemberComment getMemberComment() {
		return memberComment;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public Integer[] getComment_id() {
		return comment_id;
	}

	public void setComment_id(Integer[] comment_id) {
		this.comment_id = comment_id;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
