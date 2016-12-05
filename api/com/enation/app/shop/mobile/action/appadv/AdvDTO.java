package com.enation.app.shop.mobile.action.appadv;

import java.io.Serializable;

import com.enation.eop.sdk.utils.UploadUtil;


/**
 * 移动端 返回首页轮播数据
 * @author Jeffrey
 *
 */
public class AdvDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer aid;
    private int img_type;// 1商品列表 2商品详情 3专题 4闪购 5  预售
    private String detail; //商品分类ID 商品ID 专题链接  S就是个标识，让手机端知道跳转到闪购页面 Y就是个标识，让手机端知道跳转到预售页面
    private String prop; //商品类型
    private String img_url; //图片地址
    private String introduce; //标题
    
    public Integer getAid() {
        return aid;
    }
    
    public void setAid(Integer aid) {
        this.aid = aid;
    }
    
    public int getImg_type() {
        return img_type;
    }
    
    public void setImg_type(int img_type) {
        this.img_type = img_type;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getProp() {
        return prop;
    }
    
    public void setProp(String prop) {
        this.prop = prop;
    }
    
    public String getImg_url() {
        return UploadUtil.replacePath(img_url);
    }
    
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    
    public String getIntroduce() {
        return introduce;
    }
    
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
    
}
