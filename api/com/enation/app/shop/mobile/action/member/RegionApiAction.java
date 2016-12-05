/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：地区api
 * 修改人：Sylow  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.app.shop.mobile.util.bmapapi.model.RegionEntity;
import com.enation.app.shop.mobile.util.bmapapi.service.IBaiduApiManager;
import com.enation.app.shop.mobile.util.bmapapi.service.impl.BaiduApiManager.LocatType;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 地区api
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@Component("mobileRegionApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("region")
public class RegionApiAction extends WWAction {

    private IRegionsManager regionsManager;
    private IBaiduApiManager baiduApiManager;
    
    private String point;
    private String ip;
    private String callback;

    /**
     * 省级单位列表
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String listProvince(){
    	
    	try {
    		List list = regionsManager.listProvince();
    		this.json = JsonMessageUtil.getMobileListJson(list);
    	} catch(RuntimeException e) {
    		this.logger.error("获取省级地区列表出错", e);
			this.showErrorJson("获取省级地区列表出错[" + e.getMessage() + "]");
    	}
    	
    	return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 根据parentid获取地区列表
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String list(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			int parentId = NumberUtils.toInt(request.getParameter("parentid"));
			List list =regionsManager.listChildrenByid(parentId);
			this.json = JsonMessageUtil.getMobileListJson(list);

		} catch (RuntimeException e) {
			this.logger.error("获取地区列表出错", e);
			this.showErrorJson("获取地区列表出错[" + e.getMessage() + "]");
		}

		return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 根据经纬度或ip获取用户所在的省市县.
     * <br/>此接口已不再使用，相应功能已在其他服务中添加支持.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public void locat() {
        boolean invalidType = false;
        LocatType type = LocatType.UNKNOWN;
        if (StringUtils.isNotBlank(point)) {
            type = LocatType.POINT;
        } else if (StringUtils.isNotBlank(ip)) {
            type = LocatType.IP;
        }
        
        RegionEntity result = null;
        JSONObject json = new JSONObject();
        switch (type) {
        case POINT:
            result = baiduApiManager.rgc(point);
            break;
        case IP:
            result = baiduApiManager.ip(ip);
            break;
        default:
            invalidType = true;
            break;
        }
        if (invalidType) {
            json.put("result", "0");
            json.put("message", "传入参数point或ip,其中一个必须有值");
        } else {
            Map map = null;
            if (result != null) {
                try {
                    map = regionsManager.getRegionEntity(result.getCountyId());
                } catch (Exception e) {
                }
            }
            if (map == null) {
                json.put("result", "0");
                json.put("message", "没有查询到数据");
            } else {
                json.put("result", "1");
                json.put("region", map);
            }
        }
        PrintWriter writer = null;
        try {
            String resultJson = null;
            if (StringUtils.isNotBlank(callback)) {
                resultJson = HttpUtils.jsonp(callback, json.toJSONString());
            } else {
                resultJson = json.toJSONString();
            }
            writer = ThreadContextHolder.getHttpResponse().getWriter();
            writer.write(resultJson);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }

    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }
    
    public void setBaiduApiManager(IBaiduApiManager baiduApiManager) {
        this.baiduApiManager = baiduApiManager;
    }

    public String getPoint() {
        return point;
    }
    
    public void setPoint(String point) {
        this.point = point;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
