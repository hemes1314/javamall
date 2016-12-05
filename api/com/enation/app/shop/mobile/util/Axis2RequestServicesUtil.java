package com.enation.app.shop.mobile.util;

import org.apache.axis.client.Call;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * WEBservice 调用工具类
 * Created by wangli-tri on 2016/3/21.
 *
 * Method: goodsArrival & returnGoods
 */
public class Axis2RequestServicesUtil {
    private static Logger logger = Logger.getLogger(Axis2RequestServicesUtil.class);

    public static final String ERP_NS = "http://webservice.services.platform.services.www";


    public static String sendRequest(String url, String method, String parametersXML, String parm) {
        org.apache.axis.client.Service service = new org.apache.axis.client.Service();
        Call call = null;
        //默认失败
        String result = "0";
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(url));
            call.setUseSOAPAction(true);
            // 调用的方法名
            call.setOperationName(new QName(ERP_NS, method));
            // 需要判断参数为空
            // 设置参数名
            call.addParameter(
                    new QName(ERP_NS, parm), // 设置要传递的参数
                    org.apache.axis.encoding.XMLType.XSD_STRING,
                    javax.xml.rpc.ParameterMode.IN);

            // 设置返回值类型
            call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
            call.setSOAPActionURI(ERP_NS+method);
            result = (String) call.invoke(new Object[]{parametersXML});// 远程调用
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        logger.info("\n请求数据：\n"+parametersXML);
        logger.info("\n请求地址："+url+"  \n请求方法: "+method +" \n命名空间："+ERP_NS+"\n响应数据：\n"+result);
        return result;
    }


    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        String url = "http://10.129.32.179:8080/services/SupportGOMEService";

        //String ret = sendRequest(url, method, parametersXML, parm);
        boolean ret = ErpActionUtils.requestOmsForDefault("145906939281", url);
        System.out.println(ret);
    }

    public static void test2() {
        String url = "http://10.129.32.179:8080/services/SupportGOMEService";

        //String ret = sendRequest(url, method, parametersXML, parm);
        boolean ret = ErpActionUtils.requestOmsForRefund("AA00001", url);
        System.out.println(ret);
    }


}
