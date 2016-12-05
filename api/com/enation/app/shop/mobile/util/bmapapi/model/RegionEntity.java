package com.enation.app.shop.mobile.util.bmapapi.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 行政区划信息实体.
 * 
 * @author baoxiufeng
 */
@Deprecated
public class RegionEntity {

    // 省ID
    private Integer provinceId;
    // 市ID
    private Integer cityId;
    // 县ID
    @JSONField(name = "adcode")
    private Integer countyId;
    // 省名称
    @JSONField(name = "province")
    private String provinceName;
    // 市名称
    @JSONField(name = "city")
    private String cityName;
    // 县名称
    @JSONField(name = "district")
    private String countyName;
    
    /**
     * @return 省ID
     */
    public Integer getProvinceId() {
        return provinceId;
    }
    
    /**
     * @param provinceId 省ID
     */
    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }
    
    /**
     * @return 市ID
     */
    public Integer getCityId() {
        return cityId;
    }
    
    /**
     * @param cityId 市ID
     */
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
    
    /**
     * @return 县ID
     */
    public Integer getCountyId() {
        return countyId;
    }
    
    /**
     * @param countyId 县ID
     */
    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }
    
    /**
     * @return 省名称
     */
    public String getProvinceName() {
        return provinceName;
    }
    
    /**
     * @param provinceName 省名称
     */
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    
    /**
     * @return 市名称
     */
    public String getCityName() {
        return cityName;
    }
    
    /**
     * @param cityName 市名称
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    /**
     * @return 县名称
     */
    public String getCountyName() {
        return countyName;
    }
    
    /**
     * @param countyName 县名称
     */
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
