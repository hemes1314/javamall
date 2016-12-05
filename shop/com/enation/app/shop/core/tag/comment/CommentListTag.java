package com.enation.app.shop.core.tag.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 评论列表挂件
 * @author kingapex
 *2013-8-8上午11:12:54‰=
 */
@Component
@Scope("prototype")
public class CommentListTag extends BaseFreeMarkerTag {

	private IMemberCommentManager memberCommentManager;
	
	/**
	 *  评论列表标签
	 *  @param goodsid:商品id
	 *  @return 评论分页列表,类型：Page
	 *  {@link MemberComment}
	 *  返回的列表中，每一行是一个Map，通过其key可以输出相应的值，如：
	 *  假设返回的变量定义为：comment，那么通过${comment.content}输出评论内容
	 *  此标签返回的列表中，除MemberComment中的属性外，还含有以下属性：
	 *  lv_id：评论会员级别id
	 *  levelname，评论会员级别名称
	 *  sex：评论会员性别，0女，1男
	 *  uname,评论会员会员名
	 *  nickname:评论会员昵称
	 *  face,评论会员头像
	 *  
	 */
	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    @Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id =  (Integer)params.get("goods_id");
		Integer type= (Integer)params.get("type");
		// add by lxl 
		Integer grade = (Integer) params.get("grade");
		if (grade == null){
		    grade = 4;
		}
		if(type==null){
			throw new TemplateModelException("必须输入 type参数 ");
		}
		
		int pageNo = this.getPage();
		int pageSize=this.getPageSize();
		
		//Page page = memberCommentManager.getGoodsComments(goods_id, pageNo, pageSize, type);
		//update by lxl
	    Page page = memberCommentManager.getGoodsComments(goods_id, pageNo, pageSize, type, grade);

		return page;
	}
	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}
	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}
	
	

}
