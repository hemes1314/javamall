package com.enation.app.shop.core.openapi.service;

import com.enation.app.shop.core.model.MemberComment;

import java.util.List;

/**
 * 评论openapi
 * @author liushun
 */
public interface ICommentOpenApiManager {


    void insert(MemberComment memberComment);

    void update(MemberComment memberComment);

    void delete(Integer[] comment_id);

    void delete(int comment_id);

    String syncOpenAPI(List<MemberComment> list);
}
