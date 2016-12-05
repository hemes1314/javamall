package com.gomecellar.fake.model;

import com.enation.app.b2b2c.core.model.member.StoreMemberComment;


public class MemberCommentsForFake extends StoreMemberComment{
    // 伪造用户名
    private String fake_name;

    public String getFake_name() {
        return fake_name;
    }

    public void setFake_name(String fake_name) {
        this.fake_name = fake_name;
    }
    

}
