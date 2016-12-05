package com.enation.framework.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.enation.framework.context.webcontext.ThreadContextHolder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * json数据结果生成成工具
 * 结果总是返回result 为1表示成功 为0表示失败
 *
 * @author kingapex
 */
public class JsonMessageUtil {

    public static String getObjectJson(Object object) {

        if (object == null) {
            return getErrorJson("object is null");
        }

        try {

            String objStr = JSONObject.fromObject(object).toString();

            return "{\"result\":1,\"data\":" + objStr + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }

    //2015/9/18 lxl add start
    public static String getMobileObjectJson(Object object) {

        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        if (object == null) {
            return getErrorJson("object is null");
        }

        try {
            //JSONObject jobj = JSONObject.fromObject(object);
            String objStr = JSON.toJSONString(object);
            objStr = objStr.replace("currentPageNo", "current_page");
            objStr = objStr.replace("pageSize", "page_size");
            //objStr = objStr.replaceAll("\\\"", "\"");
            
            String ret = "{\"result\":1,\"data\":" + objStr + "}";
            //System.out.println(ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }

    //2015/9/18 lxl add end
    public static String getObjectJson(Object object, String objectname) {

        if (object == null) {
            return getErrorJson("object is null");
        }

        try {
            JsonConfig jsonConfig = new JsonConfig();

            // 排除,避免循环引用 There is a cycle in the hierarchy!
            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            jsonConfig.setIgnoreDefaultExcludes(true);
            jsonConfig.setAllowNonStringKeys(true);

            String objStr = JSONObject.fromObject(object, jsonConfig).toString();

            return "{\"result\":1,\"" + objectname + "\":" + objStr + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    //2015/9/18 lxl add start
    public static String getMobileObjectJson(Object object, String objectname) {

        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        if (object == null) {
            return getErrorJson("object is null");
        }

        try {

            String objStr = JSONObject.fromObject(object).toString();

            return "{\"result\":1,\"" + objectname + "\":" + objStr + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    //2015/9/18 lxl add end
    public static String getStringJson(String name, String value) {
        return "{\"result\":1,\"" + name + "\":\"" + value + "\"}";
    }

    //2015/9/18 lxl add start
    public static String getMobileStringJson(String name, String value) {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":1,\"" + name + "\":\"" + value + "\"}";
    }

    //2015/9/18 lxl add end
    public static String getNumberJson(String name, Object value) {
        return "{\"result\":1,\"" + name + "\":" + value + "}";
    }

    //2015/9/18 lxl add start
    public static String getMobileNumberJson(String name, Object value) {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":1,\"" + name + "\":" + value + "}";
    }

    //2015/9/18 lxl add end
    public static String getListJson(List list) {
        if (list == null) {
            return getErrorJson("list is null");
        }
        String listStr = JSONArray.fromObject(list).toString();
        return "{\"result\":1,\"data\":" + listStr + "}";
    }

    //2015/9/18 lxl add start
    public static String getMobileListJson(List list) {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        if (list == null) {
            return getErrorJson("list is null");
        }
        String listStr = JSONArray.fromObject(list).toString();
        return "{\"result\":1,\"data\":" + listStr + "}";
    }

    //2015/9/18 lxl add end
    public static String getErrorJson(String message) {

        return "{\"result\":0,\"message\":\"" + message + "\"}";

    }
    
    //购物车商品发生改变
    public static String getCartChangeJson(String message) {
        return "{\"result\":10,\"message\":\"" + message + "\"}";
    }

    //购物车商品方式改变，刷新订单提交页
    public static String getCheckoutRefreshJson(String message) {
        return "{\"result\":11,\"message\":\"" + message + "\"}";
    }

    //2015/9/18 lxl add start
    public static String getMobileErrorJson(String message) {

        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");

        return "{\"result\":0,\"message\":\"" + message + "\"}";

    }

    //2015/9/18 lxl add end
    public static String getSuccessJson(String message) {

        return "{\"result\":1,\"message\":\"" + message + "\"}";

    }

    //2015/9/18 lxl add start
    public static String getMobileSuccessJson(String message) {

        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":1,\"message\":\"" + message + "\"}";

    }

    //2015/9/18 lxl add end
    public static String getMobileListJson(Map list) {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        if (list == null) {
            return getErrorJson("list is null");
        }
        String listStr = JSONArray.fromObject(list).toString();
        return "{\"result\":1,\"data\":" + listStr + "}";
    }

    public static String getListJson(Map list) {
        if (list == null) {
            return getErrorJson("list is null");
        }
        String listStr = JSONArray.fromObject(list).toString();
        return "{\"result\":1,\"data\":" + listStr + "}";
    }

    public static String expireSession() {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":-1,\"message\":\"session已经过期\"}";
    }

    public static String expireSession(String message) {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":-1,\"message\":\"" + message + "\"}";
    }

    //add by Tension
    public static String notFountThirdpartId() {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":1,\"data\":{\"member_id\":-1}}";
    }

    //add by Tension
    public static String fountThirdpartId() {
        ThreadContextHolder.getHttpResponse().setContentType("text/json;charset=UTF-8");
        return "{\"result\":1,\"data\":{\"member_id\":1}}";
    }
}
