package com.enation.app.shop.mobile.payment;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSON;
import com.enation.app.shop.mobile.util.QRCodeUtils;
import com.enation.eop.sdk.utils.UploadUtil;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 微信支付工具类.
 * 
 * @author baoxiufeng
 */
public class WeixinUtil {
	
	/** 支付结果代码KEY */
	public static final String RESULT_CODE = "result_code";
	/** 支付回调代码KEY */
	public static final String RETURN_CODE = "return_code";
	/** 支付回调消息KEY */
	public static final String RETURN_MSG = "return_msg";
	
	/** 微信订单号 */
	public static final String TRANSACTION_ID = "transaction_id";
	/** 商户订单号 */
	public static final String OUT_TRADE_NO = "out_trade_no";
	
	/** 预支付交易会话标识 */
	public static final String PREPAY_ID = "prepay_id";
	/** 二维码链接 */
	public static final String QCODE_URL = "code_url";
	
	/** 支付回调结果 - 成功 */
	public static final String SUCCESS = "SUCCESS";
	/** 支付回调结果 - 失败 */
	public static final String FAILURE = "FAIL";
	/** 支付回调失败消息 */
	public static final String FAILURE_MSG = "签名失败";
	
	/** 商品金额标签 */
	public static final String TOTAL_FEE = "total_fee";
	/** 退款金额标签 */
	public static final String REFUND_FEE = "refund_fee";
	
	/** 微信支付退款请求服务地址 */
	public static final String PAY_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	/** 微信统一下单请求服务地址 */
	public static final String PAY_UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 微信支付退款查询服务地址 */
	public static final String PAY_REFUNDQUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
	
	/** 退款用私钥信息的证书文件 */
	private static final String CERT_PKCS12 = "apiclient_cert.p12";
	
	/** HTTP客户端工具 */
	private static CloseableHttpClient httpClient = null;
	private static CloseableHttpClient getHttpClient(String mchid) {
		if (httpClient == null) {
			try {
				KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		        InputStream is = null;
		        try {
		        	is = WeixinUtil.class.getResourceAsStream(CERT_PKCS12);
		            keyStore.load(is, mchid.toCharArray());
		        } finally {
		        	if (is != null) {
		        		is.close();
		        	}
		        }
		        // Trust own CA and all self-signed certs
		        SSLContext sslcontext = SSLContexts.custom()
		                .loadKeyMaterial(keyStore, mchid.toCharArray())
		                .build();
		        // Allow TLSv1 protocol only
		        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		                sslcontext,
		                new String[] { "TLSv1" },
		                null,
		                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return httpClient;
	}
	
	/**
	 * 进行SHA1签名.
	 * 
	 * @param params 回调参数集合
	 * @return 签名后字符串
	 */
	public static String sha1Sign(Map<String, String> params) {
		return Sha1.encode(createLinkString(params));
	}
	
	/**
	 * 生成签名字符串.
	 * 
	 * @param params 回调参数集合
	 * @param key 支付key(API密钥)
	 * @return 签名后字符串
	 */
	public static String createSign(Map<String, String> params, String key) {
		String url = createLinkString(params);
		return DigestUtils.md5Hex(url + "&key=" + key).toUpperCase();
	}

	/**
	 * 集合对象转XML文档对象.
	 * 
	 * @param map 集合对象
	 * @return 转换后的XML文档对象
	 */
	public static String mapToXml(Map<String, String> map) {
		Document document = DocumentHelper.createDocument();
		Element rootEl = document.addElement("xml");
		Element keyEl = null;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			keyEl = rootEl.addElement(entry.getKey());
			if (TOTAL_FEE.equals(entry.getKey()) || REFUND_FEE.equals(entry.getKey())) {
				keyEl.setText(entry.getValue());
			} else {
				keyEl.addCDATA(entry.getValue());
			}
		}
		return doc2String(document);
	}
	
	/**
	 * XML文档对象转集合对象.
	 * 
	 * @param document XML文档对象
	 * @return 转换后的集合对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> xmlToMap(Document document) {
		List<Element> els = document.getRootElement().elements();
		Map<String, String> map = new HashMap<>(els.size());
		for (Element el : els) {
			map.put(el.getName(),el.getText());
		}
		return map;
	}
	
	/**
	 * XML文档对象转为字符串.
	 * 
	 * @param document XML文档对象
	 * @return 转换后的文档字符串
	 */
	public static String doc2String(Document document) {
		try {
			// 使用输出流来进行转化
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 使用UTF-8编码
			new XMLWriter(out, new OutputFormat("   ", true, "UTF-8")).write(document);
			return out.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据参数集合生成签名字符串.
	 * 
	 * @param params 参数集合
	 * @return 生成后的签名字符串
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder build = new StringBuilder();
		for (String key : keys) {
			if ("sign".equals(key)) {
				continue;
			}
			if (build.length() == 0) {
				build.append(key).append("=").append(params.get(key));
			} else {
				build.append("&").append(key).append("=").append(params.get(key));
			}
		}
		return build.toString();
	}
	
	/**
	 * 微信支付退款结果查询请求调用.
	 * 
	 * @param params 统一下单请求参数
	 * @throws Exception 
	 */
	public static HttpResponse sendRefundQueryReq(Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (httpClient == null) return null;
    	HttpPost httpPost = new HttpPost(PAY_REFUNDQUERY_URL);
    	StringEntity stringEntity = new StringEntity(mapToXml(params), "UTF-8");
    	httpPost.addHeader("Content-Type", "text/xml");
    	httpPost.setEntity(stringEntity);
        return httpClient.execute(httpPost);
	}
	
	/**
	 * 微信统一下单请求调用.
	 * 
	 * @param params 统一下单请求参数
	 * @throws Exception 
	 */
	public static HttpResponse sendUnifiedOrderReq(Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = getHttpClient(params.get("mch_id"));
		if (httpClient == null) return null;
    	HttpPost httpPost = new HttpPost(PAY_UNIFIEDORDER_URL);
    	StringEntity stringEntity = new StringEntity(mapToXml(params), "UTF-8");
    	httpPost.addHeader("Content-Type", "text/xml");
    	httpPost.setEntity(stringEntity);
        return httpClient.execute(httpPost);
	}
	
	/**
	 * 微信退款请求调用.
	 * 
	 * @param params 退款请求参数
	 * @throws Exception 
	 */
	public static HttpResponse sendRefundReq(Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = getHttpClient(params.get("mch_id"));
		if (httpClient == null) return null;
    	HttpPost httpPost = new HttpPost(PAY_REFUND_URL);
    	StringEntity stringEntity = new StringEntity(mapToXml(params), "UTF-8");
    	httpPost.addHeader("Content-Type", "text/xml");
    	httpPost.setEntity(stringEntity);
        return httpClient.execute(httpPost);
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("appid", "");
		map.put("mch_id", "");
		map.put("nonce_str", WeixinUtil.generateNonceStr());
		map.put("transaction_id", "4004842001201610156773048035");
		map.put("sign", WeixinUtil.createSign(map, ""));
		HttpResponse response = WeixinUtil.sendRefundQueryReq(map);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(entity.getContent());
			Map<String, String> resultParams = WeixinUtil.xmlToMap(document);
			String return_code = resultParams.get(WeixinUtil.RETURN_CODE);
			String result_code = resultParams.get(WeixinUtil.RESULT_CODE);
			System.out.println(JSON.toJSONString(resultParams));
			if (WeixinUtil.SUCCESS.equals(return_code) && WeixinUtil.SUCCESS.equals(result_code)) {
				String sign = WeixinUtil.createSign(resultParams, "");
				if (sign.equals(resultParams.get("sign"))) {
					String refund_batchno = resultParams.get("out_refund_no_0");
					System.out.println(refund_batchno);
				} else {
					System.out.println("weixin sign:" + resultParams.get("sign"));
					System.out.println("my sign:" + sign);
				}
			}
		}
	}
	
	/**
	 * 将扫码支付链接作为参数生成二维码并构建展示二维码页面.
	 * 
	 * @param prepayId 预支付交易标识
	 * @param qcodeUrl 扫码支付链接（作为二维码内容）
	 * @return 二维码展示页面内容
	 * @throws IOException 异常信息
	 */
	public static String buildQCodeReq(String prepayId, String qcodeUrl) {
		BufferedImage qcodeImage = QRCodeUtils.createImage(qcodeUrl, 300, 300);
		StringBuilder filePath = new StringBuilder();
		filePath.append(System.getProperty("java.io.tmpdir"));
		filePath.append("/").append(prepayId).append(".jpg");
        File qrcodeFile = new File(filePath.toString());
        try {
        	if (!qrcodeFile.exists()) {
        		qrcodeFile.mkdirs();
        		qrcodeFile.createNewFile();
        	}
			ImageIO.write(qcodeImage, "jpg", qrcodeFile);
			String path = UploadUtil.upload(qrcodeFile, prepayId + ".jpg",  "wxpay");
			return UploadUtil.replacePath(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 生成随机串.
	 * 
	 * @return 随机串
	 */
	public static String generateNonceStr() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 微信统一下单支付类型.
	 * 
	 * @author baoxiufeng
	 */
	public enum WxPayTradeType {
		JSAPI("公众号支付", "JSAPI"),
		NATIVE("原生扫码支付", "NATIVE"),
		APP("APP支付", "APP");
		
		private String name;
		private String type;
		
		/**
		 * 构造方法.
		 * 
		 * @param name 下单支付交易类型名称
		 * @param type 下单支付交易类型代码
		 */
		private WxPayTradeType(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}
	}
}
