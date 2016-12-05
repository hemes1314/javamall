package com.enation.app.b2b2c.core.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 移动端 - 首页轮播
 * 
 * @author Jeffrey
 *
 */
public class DataAppAdv implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer aid;

    @PrimaryKeyField
    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    private int img_type;// 1商品列表 2商品详情 3专题 4闪购 5  预售
    private String detail; //商品分类ID 商品ID 专题链接  S就是个标识，让手机端知道跳转到闪购页面 Y就是个标识，让手机端知道跳转到预售页面
    private String prop; //商品类型
    private String img_url; //图片地址
    private String introduce; //标题

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
        return img_url;
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
    
    @NotDbField
    public String getImgUrl() {
        if (StringUtils.isBlank(img_url))
            return "";
        return UploadUtil.replacePath(img_url);
    }

    @NotDbField
    public Integer getAids() {
        return aid;
    }
    
    @NotDbField
    public String getImgTypeFmt() {
        switch(img_type) {
            case 1:
                return "商品列表";
            case 2:
                return "商品详情";
            case 3:
                return "专题";
            case 4:
                return "闪购";
            default:
                return "预售";
        }
    }
    
    @NotDbField
    public String getPropFmt() {
        switch(img_type) {
            case 1:
                return "商品类型: " + prop;
            case 2:
                return "无";
            case 3:
                return "无";
            case 4:
                return "无";
            default:
                return "无";
        }
    }
    
    @NotDbField
    public String getDetailFmt() {
        switch(img_type) {
            case 1:
                return "商品分类id: " + detail;
            case 2:
                return "商品id: " + detail;
            case 3:
                return "专题链接: " + detail;
            case 4:
                return "标识: s";
            default:
                return "标识: y";
        }
    }
}
