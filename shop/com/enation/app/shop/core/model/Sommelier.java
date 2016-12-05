package com.enation.app.shop.core.model;

import java.util.List;

import com.enation.framework.database.PrimaryKeyField;

public class Sommelier {

	private int id;
	private int userid;
	private String username;
	private String pass;
	private String sex;
	private String name;
	private String mobile;
	private String introduce;
	private String detail;
	private String email;
	private String grade;
	private Integer good_comment;
	private Integer bad_comment;
	private Integer tasting_count;
	private String img_url;    //图片地址
	private List recommendList;    //图片地址
	
    @PrimaryKeyField
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

   
    
    public String getUsername() {
        return username;
    }

    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    
    public void setPass(String pass) {
        this.pass = pass;
    }
  
    public String getIntroduce() {
        return introduce;
    }

    
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    
    public String getSex() {
        return sex;
    }

    
    public void setSex(String sex) {
        this.sex = sex;
    }
  
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public Integer getGood_comment() {
        return good_comment;
    }

    
    public void setGood_comment(Integer good_comment) {
        this.good_comment = good_comment;
    }

    
    public Integer getBad_comment() {
        return bad_comment;
    }

    
    public void setBad_comment(Integer bad_comment) {
        this.bad_comment = bad_comment;
    }

    
    public Integer getTasting_count() {
        return tasting_count;
    }

    
    public void setTasting_count(Integer tasting_count) {
        this.tasting_count = tasting_count;
    }

    
    public String getImg_url() {
        return img_url;
    }

    
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    
    public int getUserid() {
        return userid;
    }

    
    public void setUserid(int userid) {
        this.userid = userid;
    }

    
    public String getDetail() {
        return detail;
    }

    
    public void setDetail(String detail) {
        this.detail = detail;
    }

    
    public List getRecommendList() {
        return recommendList;
    }

    
    public void setRecommendList(List recommendList) {
        this.recommendList = recommendList;
    }

    
    public String getGrade() {
        return grade;
    }

    
    public void setGrade(String grade) {
        this.grade = grade;
    } 
    
    
}
