package com.enation.framework.sms;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.enation.app.base.core.model.ShortMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 短信发送
 * @author humaodong
 *
 */

public class SmsSender {
    /*
    public static String sn = "2SDK-EMY-xxx-xxx";// 软件序列号,请通过亿美销售人员获取
    public static String key = "go179053286me";// 序列号首次激活时自己设定
    public static String password = "xxx";// 密码,请通过亿美销售人员获取
    public static String baseUrl = "http://hprpt2.eucp.b2m.cn:8080/sdkproxy/";
    
    public static void sendSms(ShortMsg sms) throws Exception {
        String content = sms.getContent();
        String sign = "【国美酒窖】";
        if (!sign.isEmpty() && !content.startsWith(sign)) content = sign+content;
        
        String url = baseUrl + "regist.action";
        String param = "cdkey=" + sn + "&password=" + password;
        String ret = SDKHttpClient.registAndLogout(url, param);
        System.out.println("注册结果:" + ret);
        
        String message = URLEncoder.encode(content, "UTF-8");
        String code = "888";
        long seqId = System.currentTimeMillis();
        param = "cdkey=" + sn + "&password=" + key + "&phone=" + sms.getTarget() + "&message=" + message + "&addserial=" + code + "&seqid=" + seqId;
        url = baseUrl + "sendtimesms.action";
        ret = SDKHttpClient.sendSMS(url, param);
        System.out.println("sendSms: " + ret);
        
    }
	*/
	/**
	 * 短信模板
55 订单{txt}提交成功  
56 您已成功购买{txt}元面值的{txt},卡号{txt}密码{txt}, 请注意查收 
57 您已使用礼品卡成功充值{txt}元，充值后虚拟余额为：{txt}元 
58 您在国美酒业账户中的礼品卡{txt}刚刚被会员{txt}使用和充值，如有疑问请立即致电客服人员
59 您已成功充值{txt}元，充值后现金余额为：{txt}元 
60 您本次的验证码为：{txt} 

	 * 
	 * @param sms
	 *	1)接口有IP鉴权，非鉴权IP地址不允许访问
		2)接口统一使用get方式提交，字段不分先后顺序
		3)各字段名统一使用小写方式
		4)Phone为电话号码字段，最多一次传50个手机号，以英文半角逗号隔开；
		5)message为短信内容，强制采用gb2312编码格式传送短信内容；
		6)linkid字段为每次的发送标识，要有唯一性，长度不超过20位；
		7)username为用户名，建议使用英文名称；
		8)epid为客户企业ID，使用数字格式；
		9)subcode为发送时传送的扩展码，可为空；
		10)password为密码字段，兼容明文密码与32位的MD5密文密码，建议使用32位的MD5密文密码；
		11)注意：如果返回值错误，重复提交次数不要超过3次
	 * @return
	 *  00	提交成功
		1	参数不完整
		2	鉴权失败（包括：用户状态不正常、密码错误、用户不存在、地址验证失败，黑户）
		3	号码数量超出50条
		4	发送失败
		5	余额不足
		6	发送内容含屏蔽词
		7	短信内容超出350个字数 
	 * @throws Exception
	 */
	public static void sxx(ShortMsg sms) throws Exception {
	    String content = sms.getContent();
	    //String sign = "【国美酒窖】";
        //if (!sign.isEmpty() && !content.startsWith(sign)) content = sign+content;
        
		//鸿联企信通接口
		String url = "http://q.hl95.com:8061/?"; 
		List<NameValuePair> args = new LinkedList<NameValuePair>();
		args.add(new BasicNameValuePair("username", "gmgj"));
		args.add(new BasicNameValuePair("password", "gmgj123"));
		args.add(new BasicNameValuePair("epid", "120649"));
		args.add(new BasicNameValuePair("linkid", ""));
		args.add(new BasicNameValuePair("subcode", ""));
		args.add(new BasicNameValuePair("phone", sms.getTarget()));
		args.add(new BasicNameValuePair("message", content));
		String argsString = URLEncodedUtils.format(args, "gb2312");
	    url += argsString;
		HttpGet httpGet = new HttpGet(url);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpGet);
		int statusCode = response.getStatusLine().getStatusCode();
		if (HttpStatus.SC_OK != statusCode) throw new Exception("HTTP error: "+statusCode);
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		System.out.println("sendSms: "+result);
		if (!result.equals("00")) throw new Exception("sendSms error: "+result);
	}
	
	// http://web.duanxinwang.cc/asmx/smsservice.aspx?name=登录名&pwd=接口密码&mobile=手机号码&content=内容&sign=签名&stime=发送时间&type=pt&extno=自定义扩展码
	public static void sss(ShortMsg sms) throws Exception {
	    //String content = "您本次的验证码为：235448【国美酒窖】";
	    String content = sms.getContent();
	    String sign = "";
        String suffix = "【国美酒窖】";
        if (!suffix.isEmpty() && !content.endsWith(suffix)) content += suffix;
        
        // 创建StringBuffer对象用来操作字符串
        StringBuffer sb = new StringBuffer("http://web.cr6868.com/asmx/smsservice.aspx?");

        // 向StringBuffer追加用户名
        sb.append("name=15998421320_2");

        // 向StringBuffer追加密码（登陆网页版，在管理中心--基本资料--接口密码，是28位的）
        sb.append("&pwd=9FA6EB4D075A98F84F896CF0D451");

        // 向StringBuffer追加手机号码
        sb.append("&mobile="+sms.getTarget());

        // 向StringBuffer追加消息内容转URL标准码
        sb.append("&content="+URLEncoder.encode(content,"UTF-8"));
        
        //追加发送时间，可为空，为空为及时发送
        sb.append("&stime=");
        
        //加签名
        sb.append("&sign="+URLEncoder.encode(sign,"UTF-8"));
        
        //type为固定值pt  extno为扩展码，必须为数字 可为空
        sb.append("&type=pt&extno=");
        // 创建url对象
        //String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
        System.out.println("sb:"+sb.toString());
        URL url = new URL(sb.toString());

        // 打开url连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置url请求方式 ‘get’ 或者 ‘post’
        connection.setRequestMethod("POST");

        // 发送
        InputStream is =url.openStream();

        //转换返回值
        String returnStr = convertStreamToString(is);
        
        // 返回结果为‘0，20140009090990,1，提交成功’ 发送成功   具体见说明文档
        System.out.println(returnStr);
        // 返回发送结果

        
	}
	
	public static String convertStreamToString(InputStream is) {    
        StringBuilder sb1 = new StringBuilder();    
        byte[] bytes = new byte[4096];  
        int size = 0;  
        
        try {    
            while ((size = is.read(bytes)) > 0) {  
                String str = new String(bytes, 0, size, "UTF-8");  
                sb1.append(str);  
            }  
        } catch (IOException e) {    
            e.printStackTrace();    
        } finally {    
            try {    
                is.close();    
            } catch (IOException e) {    
               e.printStackTrace();    
            }    
        }    
        return sb1.toString();    
    }
	
	public static void sendSms(ShortMsg sms) throws Exception {
        String content = sms.getContent();
        //String sign = "【国美酒窖】";
        //if (!sign.isEmpty() && !content.startsWith(sign)) content = sign+content;
        /*
        JSONObject obj = new JSONObject();
        obj.put("account", "gomejiuye"); //帐号
        obj.put("password", "gomejiuye_974"); //密码
        obj.put("content", content); // 短信内容
        obj.put("immediate", 1); // 是否立即发送(如果为1，则不需要传发送时间)
        //obj.put("sendTime", "2012-08-01 12:30:09"); // 发送时间
        obj.put("removeDuplicate", 1); // 是否去重
        JSONArray nums = new JSONArray(); // 手机号码
        nums.add(sms.getTarget());
        obj.put("phoneNums", nums);
        System.out.println(obj.toString());
        String result = sendReqeuest("http://qxt.qixinhl.com/dx/push/sendMessage.action", obj.toString(), 200000);
        */
        
        String url = "http://vip.qixinhl.com/dx/push/sendMessage.action?"; 
        List<NameValuePair> args = new LinkedList<NameValuePair>();
        args.add(new BasicNameValuePair("account", "gomejiuye_HY"));
        args.add(new BasicNameValuePair("password", "gomejiuye_HY_790"));
        args.add(new BasicNameValuePair("content", content));
        args.add(new BasicNameValuePair("immediate", "1"));
        args.add(new BasicNameValuePair("removeDuplicate", "1"));
        args.add(new BasicNameValuePair("phoneNums", sms.getTarget()));
        String argsString = URLEncodedUtils.format(args, "UTF-8");
        url += argsString;
        System.out.println(url);
        HttpGet httpGet = new HttpGet(url);
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) throw new Exception("HTTP error: "+statusCode);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        
        /*
         * 响应数据示例：
            message=0;batchNumber=3323;total=100;valid=99;duplicate=0
            0:成功
            1:系统暂停发送短信
            2:用户名或密码错误
            3:没有分配通道组
            4:必要属性为空或填写不合法
            5:余额不足
            6:短信包含敏感词
            7:没有权限
            8:推送地址为空
            9:系统错误
         */
        System.out.println("sendSms: "+result);
        if (!result.startsWith("message=0")) throw new Exception("sendSms error: "+result);
	}
	
	public static String sendReqeuest(String url, String input, int timeout) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        InputStream inputStream = null;
        StringBuffer outStr = new StringBuffer();
        try {
            HttpParams httpParams = httpclient.getParams();
            httpParams.setIntParameter("http.socket.timeout", timeout);
            httpParams.setParameter("User-Agent", "htq-sever");
            HttpPost httpPost = new HttpPost(url);
            // 设置 context
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute("Content-Type", "text/plain; charset=UTF-8");

            // 设置http头
            httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            httpPost.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            httpPost.setHeader("User-Agent", "htq-server 1.0");
            
            HttpEntity entityPost = new ByteArrayEntity(input.getBytes());
            httpPost.setEntity(entityPost);
            HttpResponse response = httpclient.execute(httpPost, localContext);
            
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                inputStream = entity.getContent();
                byte[] inputs = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(inputs)) != -1) {
                    outStr.append(new String(inputs, 0, len, "UTF-8"));
                }
            }
            httpPost.abort();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
            httpclient.getConnectionManager().shutdown();
        }
        String output = outStr.toString();
        return output;
    }
	
	public static void sendSms(String mobile, String content) throws Exception {
		ShortMsg sms = new ShortMsg();
		sms.setTarget(mobile);
		sms.setContent(content);
		sendSms(sms);
		//sss(sms);
	}
}
