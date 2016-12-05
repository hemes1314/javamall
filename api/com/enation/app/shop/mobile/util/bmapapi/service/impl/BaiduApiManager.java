package com.enation.app.shop.mobile.util.bmapapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.app.shop.mobile.util.bmapapi.model.RegionEntity;
import com.enation.app.shop.mobile.util.bmapapi.service.IBaiduApiManager;

/**
 * Baidu地图API调用处理接口实现.
 * <br/>此接口已不再使用，相应功能已在其他服务中添加支持.
 * 
 * @author baoxiufeng
 */
@Component
@Deprecated
public class BaiduApiManager implements IBaiduApiManager {

    @Value("#{configProperties['bmap.api.key']}")
    private String apiKey;
    
    @Value("#{configProperties['bmap.api.ip_url']}")
    private String apiIpUrl;
    
    @Value("#{configProperties['bmap.api.rgc_url']}")
    private String apiRgcUrl;
    
    @Value("#{configProperties['bmap.api.gc_url']}")
    private String apiGcUrl;
    
    /**
     * 定位类型.
     * 
     * @author baoxiufeng
     */
    public enum LocatType {
        POINT, IP, UNKNOWN
    }
    
    /**
     * 结果代码枚举.
     * 
     * @author baoxiufeng
     */
    public enum ResultCode {
        C0(0, ""),
        C1(1, "服务器内部错误"),
        C10(10, "上传内容超过8M"),
        C101(101, "AK参数不存在"),
        C102(102, "MCODE参数不存在，mobile类型mcode参数必需"),
        C200(200, "APP不存在，AK有误请检查再重试"),
        C201(201, "APP被用户自己禁用，请在控制台解禁"),
        C202(202, "APP被管理员删除"),
        C203(203, "APP类型错误"),
        C210(210, "APP IP校验失败"),
        C211(211, "APP SN校验失败"),
        C220(220, "APP Referer校验失败"),
        C230(230, "APP Mcode码校验失败"),
        C240(240, "APP 服务被禁用"),
        C250(250, "用户不存在"),
        C251(251, "用户被自己删除"),
        C252(252, "用户被管理员删除"),
        C260(260, "服务不存在"),
        C261(261, "服务被禁用"),
        C301(301, "永久配额超限，限制访问"),
        C302(302, "天配额超限，限制访问"),
        C401(401, "当前并发量已经超过约定并发配额，限制访问"),
        C402(402, "当前并发量已经超过约定并发配额，并且服务总并发量也已经超过设定的总并发配额，限制访问");
        
        /* 错误代码 */
        private int code;
        /* 错误描述 */
        private String desc;
        
        /**
         * 构造方法.
         * 
         * @param code 错误代码
         * @param desc 错误描述
         */
        private ResultCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        /**
         * @return 错误代码
         */
        public int getCode() {
            return code;
        }

        /**
         * @return 错误描述
         */
        public String getDesc() {
            return desc;
        }
    }
    
    @Override
    public RegionEntity ip(String ipAddr) {
        String url = String.format(apiIpUrl, apiKey, ipAddr);
        try {
            HttpResponse httpResponse = HttpUtils.execute(new HttpPost(url));
            return rgc(getPointFromIpResult(EntityUtils.toString(httpResponse.getEntity(), "UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RegionEntity rgc(double lng, double lat) {
        return rgc(new StringBuilder().append(lat).append(",").append(lng).toString());
    }

    @Override
    public RegionEntity rgc(String point) {
        if (StringUtils.isBlank(point)) return null;
        String url = String.format(apiRgcUrl, apiKey, point);
        try {
            HttpResponse httpResponse = HttpUtils.execute(new HttpPost(url));
            return buildRegionResult(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String gc(String address) {
        if (StringUtils.isBlank(address)) return null;
        String url = String.format(apiGcUrl, apiKey, address);
        try {
            HttpResponse httpResponse = HttpUtils.execute(new HttpPost(url));
            return getPointFromRcResult(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据IP接口调用结果返回经纬度信息.
     * 
     * @param result IP接口调用结果
     * @return 经纬度信息（纬度,经度）
     */
    private String getPointFromIpResult(String result) {
        if (StringUtils.isBlank(result)) return null;
        JSONObject json = (JSONObject) JSONObject.parse(result);
        if (ResultCode.C0.getCode() == json.getInteger("status")) {
            json = json.getJSONObject("content").getJSONObject("point");
            String x = json.getString("x");
            String y = json.getString("y");
            return y + "," + x;
        } else {
            System.out.println(json.get("message"));
        }
        return null;
    }
    
    /**
     * 根据GC接口调用结果返回经纬度信息.
     * 
     * @param result GC接口调用结果
     * @return 经纬度信息（纬度,经度）
     */
    private String getPointFromRcResult(String result) {
        if (StringUtils.isBlank(result)) return null;
        JSONObject json = (JSONObject) JSONObject.parse(result);
        if (ResultCode.C0.getCode() == json.getInteger("status")) {
            json = json.getJSONObject("result").getJSONObject("location");
            String x = json.getString("lng");
            String y = json.getString("lat");
            return y + "," + x;
        } else {
            System.out.println(json.get("message"));
        }
        return null;
    }
    
    /**
     * 根据RGC接口结果获取行政区划信息（名称和代码）.
     * 
     * @param result RGC接口调用结果
     * @return 行政区划信息
     */
    public static RegionEntity buildRegionResult(String result) {
        if (StringUtils.isBlank(result)) return null;
        JSONObject json = (JSONObject) JSONObject.parse(result);
        if (ResultCode.C0.getCode() == json.getInteger("status")) {
            return json.getJSONObject("result").getObject("addressComponent", RegionEntity.class);
        } else {
            System.out.println(json.get("message"));
        }
        return null;
    }
}
