package com.enation.app.shop.mobile.util.bmapapi.service;

import com.enation.app.shop.mobile.util.bmapapi.model.RegionEntity;

/**
 * Baidu地图API调用处理接口.
 * <br/>此接口已不再使用，相应功能已在其他服务中添加支持.
 * 
 * @author baoxiufeng
 */
@Deprecated
public interface IBaiduApiManager {

    /**
     * IP定位.
     * 
     * @param ipAddr IP地址
     * @return 定位结果
     */
    public RegionEntity ip(String ipAddr);
    
    /**
     * RGC接口调用.
     * 
     * @param lng 经度
     * @param lat 纬度
     * @return 定位结果
     */
    public RegionEntity rgc(double lng, double lat);
    
    /**
     * RGC接口调用.
     * 
     * @param lng 经度
     * @param lat 纬度
     * @return 定位结果
     */
    public RegionEntity rgc(String point);
    
    /**
     * GC接口调用.
     * 
     * @param address 门牌地址信息
     * @return 门牌地址对应的经纬度信息（纬度,经度）
     */
    public String gc(String address);
}
