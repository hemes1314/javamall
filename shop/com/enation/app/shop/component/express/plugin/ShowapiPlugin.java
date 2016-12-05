package com.enation.app.shop.component.express.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.express.AbstractExpressPlugin;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.framework.util.JsonUtil;
import com.show.api.util.ShowApiUtils;

/**
 * showapi 中的快递API接口
 * @author xulipeng
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Component
public class ShowapiPlugin extends AbstractExpressPlugin implements IExpressEvent   {

	@Override
	public Map getExpressDetail(String com, String nu, Map params) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(new Date());
		String appid = (String) params.get("appid");
		String app_secret =(String) params.get("app_secret");
		String md5_secret = shouquan(com, nu, appid, app_secret, time);
		
		Request remoteRequest  = new RemoteRequest();
		String kuaidiurl="http://route.showapi.com/64-19?com="+com+"&nu="+nu+"&showapi_appid="+appid+"&showapi_timestamp="+time+"&showapi_sign="+md5_secret;
		//System.out.println(kuaidiurl);
		Response remoteResponse = remoteRequest.execute(kuaidiurl);
		String content  = remoteResponse.getContent();
		Map map = JsonUtil.toMap(content);
		Map datamap=(Map) map.get("showapi_res_body");
		List<Map> datalist = (List) datamap.get("data");
//		List datalist = new ArrayList();
//		for (Map m : list) {
//			Map dmap = new HashMap();
//			dmap.put("time", m.get("time"));
//			dmap.put("content", m.get("context"));
//			datalist.add(dmap);
//		}
		//Collections.reverse(datalist);
		map.put("message", "ok");
		map.put("data",datalist);
		map.remove("showapi_res_body");
		return map; 
	}
	
	
	private static String shouquan(String com,String nu,String appid,String app_secret,String time){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Map params = new HashMap();
			params.put("com", com);
			params.put("nu", nu);
			params.put("showapi_appid", appid);
			params.put("showapi_timestamp", time);
			String secret=app_secret;
			
			String code =ShowApiUtils.signRequest(params, secret, false);
			return code.toLowerCase();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(new Date());
		String appid ="3030";
		String app_secret ="725f4b62e44b4d679db5bb086e27ae43";
		String com="shunfeng";
		String nu="590289050510";
		String md5_secret = shouquan(com, nu, appid, app_secret, time);
		
		System.out.println(md5_secret);
		
		String kuaidiurl="http://route.showapi.com/64-19?com="+com+"&nu="+nu+"&showapi_appid="+appid+"&showapi_timestamp="+time+"&showapi_sign="+md5_secret;
		System.out.println(kuaidiurl);
	}

	@Override
	public String getId() {
		return "showapiPlugin";
	}

	@Override
	public String getName() {
		return "showapi快递插件";
	}
	
}
