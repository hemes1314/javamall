package com.enation.app.shop.core.openapi.service.impl;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.openapi.service.ICommentOpenApiManager;
import com.enation.app.shop.mobile.util.gfs.service.IGFSManager;
import com.gome.open.api.comment.client.dto.request.CommentAddRequest;
import com.gome.open.api.comment.client.dto.request.CommentDeleteRequest;
import com.gome.open.api.comment.client.dto.request.CommentUpdateRequest;
import com.gome.open.api.sdk.GomeException;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 评论openapi管理类
 *
 * @author liushun
 */
@Component
public class CommentOpenApiManager implements ICommentOpenApiManager {

    // OpenApi配置管理器
    private OpenApiConfigManager openApiConfigManager;
    /**
     * 用户信息
     */
    private IMemberManager memberManager;
    /**
     * gfs图片转换器
     */
    private IGFSManager gfsManager;

    /**
     * @return OpenApi配置管理器
     */
    public OpenApiConfigManager getOpenApiConfigManager() {
        return openApiConfigManager;
    }

    /**
     * @param openApiConfigManager OpenApi配置管理器
     */
    public void setOpenApiConfigManager(OpenApiConfigManager openApiConfigManager) {
        this.openApiConfigManager = openApiConfigManager;
    }

    public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public IGFSManager getGfsManager() {
        return gfsManager;
    }

    public void setGfsManager(IGFSManager gfsManager) {
        this.gfsManager = gfsManager;
    }

    @Override
    public void insert(MemberComment memberComment) {
        try {
            CommentAddRequest commentAddRequest = new CommentAddRequest();
            commentAddRequest.setSkuId(memberComment.getGoods_id() + "");
            commentAddRequest.setCommentType(memberComment.getType());
            commentAddRequest.setContent(memberComment.getContent());
            //TODO 这个用哪个路径?

            commentAddRequest.setSkuImage(gfsManager.handleImageToGFS(memberComment.getImg()));
            commentAddRequest.setScore(memberComment.getGrade() + "");
            commentAddRequest.setNickName(memberComment.getMember_id() + "");
            commentAddRequest.setReplies(memberComment.getReply());
            commentAddRequest.setCreatedTime(new Date());
            Member member = memberManager.get(memberComment.getMember_id());
            commentAddRequest.setNickName(member.getNickname());
            commentAddRequest.setAvatarUrl(gfsManager.handleImageToGFS(member.getFace()));
            openApiConfigManager.execute(commentAddRequest);
        } catch (GomeException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getErrMsg());
        }

    }

    @Override
    public void update(MemberComment memberComment) {
        try {
            CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
            commentUpdateRequest.setId(memberComment.getComment_id() + "");
            commentUpdateRequest.setSkuId(memberComment.getGoods_id() + "");
            commentUpdateRequest.setCommentType(memberComment.getType());
            commentUpdateRequest.setContent(memberComment.getContent());
            //TODO 这个用哪个路径?
            commentUpdateRequest.setSkuImage(gfsManager.handleImageToGFS(memberComment.getImg()));
            commentUpdateRequest.setAvatarUrl(memberComment.getImgPath());
            commentUpdateRequest.setScore(memberComment.getGrade() + "");
            commentUpdateRequest.setNickName(memberComment.getMember_id() + "");
            commentUpdateRequest.setReplies(memberComment.getReply());
            Member member = memberManager.get(memberComment.getMember_id());
            commentUpdateRequest.setNickName(member.getNickname());
//            commentUpdateRequest.setAvatarUrl(gfsManager.handleImageToGFS(member.getFace()));
            openApiConfigManager.execute(commentUpdateRequest);
        } catch (GomeException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getErrMsg());
        }
    }

    @Override
    public void delete(Integer[] comment_id) {
        CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest();
        try {
            List<String> ids = new ArrayList<String>();
            for (int i = 0; i < comment_id.length; i++) {
                ids.add(comment_id[i] + "");
            }
            commentDeleteRequest.setIds(ids);
            openApiConfigManager.execute(commentDeleteRequest);
        } catch (GomeException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getErrMsg());
        }
    }

    @Override
    public void delete(int comment_id) {
        CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest();
        try {
            commentDeleteRequest.setIds(Arrays.asList(new String[]{comment_id + ""}));
            openApiConfigManager.execute(commentDeleteRequest);
        } catch (GomeException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getErrMsg());
        }
    }

    @Override
    public String syncOpenAPI(List<MemberComment> list) {
        StringBuilder msg = new StringBuilder("全量更新：");
        for (int i = 0; i < list.size(); i++) {
            try {
                insert(list.get(i));
            } catch (Exception e) {
                msg.append(list.get(i).getComment_id());
                msg.append(e.getMessage());
                msg.append("\n");
            }
        }
        return msg.toString();
    }
}
