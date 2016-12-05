package com.enation.app.shop.component.payment.plugin.alipaymobile.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088102971854915";
	// 商户的私钥
	public static String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANhg06q9mwuDPNb3UZbm6X4EgzsK/n+jo4yLtPWMSfKzivd8UEeIdXmffd1+drsREBStU+vAoAU0pAZGBiqEqVwI+Rtw90CPEzFWl5Z9YRHRjLiYOiNCAiCf7PAGHqes58Q9kyXjVZJx3WXpTMgIzG1bZ0piA8hR4OaU8J5GhqRtAgMBAAECgYEAt8qPIEFLWB0SaXnLyMS84fiNS3KMN/jUK8ZjYArYqRmOWaczPX+QYU1zCNepnD3jDd0oImEMyz8qb9W82RK0EtOVPVyCvlFiJJt5lIFcxsPWNkwXSH2jcziG3+icWmDrvxoJKEVsL5A+JRD+j28IFhMPEvtIWIGS5gbyRxksbjUCQQD2+TNZCHk7IrgTzKz+w/8tH5t016JXdxUDVHa4AlKd3wmj3gtODusSDxCLvxCAUNUUbPRF6/I1b0mUh9EWJQ4LAkEA4Elc9EAXIT2GtHNS6/icYJuy5C5fsL3NfKlI84zmr9bBAL8TwYAUKl1unwvkPC/NS73UgWOJq69Ao/j8mH66ZwJAOUjJ+S+29tlxut3xjlIlwPCg3TQa6pCrZg9UTg/z27xc/w5ErwFU0uZ9nvxdNnYJRmiTLBizIGPEvFfYTnufywJBAMHVxXb1+MdqhANp840qJAO+LAHWLi7yKigFqZ2K9UecYtrBSFKf3U2rx9G+ljaJ3XQLEB/upWlyIXxU0AD+7RsCQBhnSb9YubKOZV5PfMArJEbTELhn0cOSBzJSVxs1p4jqDOU6/M/0n513xFYAm1ab9r0UiC2qdhpYWS7Wl6UmjbA=";
	
	// 支付宝的公钥，无需修改该值
	public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDYYNOqvZsLgzzW91GW5ul+BIM7Cv5/o6OMi7T1jEnys4r3fFBHiHV5n33dfna7ERAUrVPrwKAFNKQGRgYqhKlcCPkbcPdAjxMxVpeWfWER0Yy4mDojQgIgn+zwBh6nrOfEPZMl41WScd1l6UzICMxtW2dKYgPIUeDmlPCeRoakbQIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
