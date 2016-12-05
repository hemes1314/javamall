package com.enation.app.shop.mobile.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.util.web.ServletUtils;

/**
 * Created by ken on 16/3/21.
 */
public class ErpActionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ErpActionUtils.class);
    
    /**
     * TODO:方便本地调试的时候不连接WMS
     */
    public static boolean CONFIG_NO_WMS = true;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DATETIME_FORMAT1 = "yyyy-MM-dd HH:mm:ss:SSS";

    public static Document createDefaultXMLDocumentResponse(boolean success, String reason, String data) throws Exception {
        Document doc = DocumentHelper.parseText(createDefaultXMLStringResponse(success, reason, data));
        return doc;
    }

    public static String createDefaultXMLStringResponse(boolean success, String reason, String data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Response>");
        sb.append("<success>").append(success ? "T" : "F").append("</success>");
        sb.append("<reason>").append(reason == null ? "" : reason).append("</reason>");
        sb.append("<opertime>").append(DateUtil.toString(new Date(), DATETIME_FORMAT)).append("</opertime>");
        sb.append("<data>").append(data == null ? "" : data).append("</data>");
        sb.append("</Response>");
        String xml = sb.toString();
        return xml;
    }


    public static Document createDefaultErpXMLResponse() {

        try {
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><hasData>T</hasData><Wms><root><SalesOrderReceivings></SalesOrderReceivings></root></Wms><Erp><data><entry></entry></data></Erp></root>";
            return parseRequestXML(xml);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    public static Document createDefaultErpXMLNoDataResponse() {

        try {
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><hasData>F</hasData></root>";
            return parseRequestXML(xml);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 组织需要的xml节点,wms部分
     *
     * @param wmsRoot
     */
    public static void appendOrderWmsElement(Element wmsRoot, Map<Order, List<StoreOrder>> map, IRegionsManager regionsManager) {
        for (Map.Entry<Order, List<StoreOrder>> e : map.entrySet()) {
            Order mo = e.getKey();
            List<StoreOrder> soList = e.getValue();

            for (StoreOrder so : soList) {
                Element body = wmsRoot.addElement("SalesOrderReceiving");
                body.addElement("orderCode").setText(toStringValue(so.getSn())); //客户订单号
                body.addElement("companyCode").setText("7593459"); //货主编码
                body.addElement("billTypeCode").setText("OUT_SALES"); //入库单类型, 单据类型的编码，入库为IN_PURCHASE, 出库OUT_SALES
                body.addElement("orderDate").setText(toDateString(so.getCreate_time())); //订单日期
                body.addElement("fromOrgName").setText(""); //发货方名称 TODO:没有这个
                body.addElement("fromContactName").setText(""); //发货人 TODO:没有这个
                body.addElement("fromTelephone").setText(""); //发货人联系电话 TODO:没有这个
                body.addElement("fromAddress").setText(""); //发货人地址 TODO:没有这个
                body.addElement("toOrgName").setText(""); //收货方名称  TODO:没有这个
                body.addElement("toContactName").setText(toStringValue(mo.getShip_name())); //收货人
                body.addElement("toTelephone").setText(toStringValue(mo.getShip_mobile())); //收货人联系电话
                if(mo.getShipping_area()!=null){
                    String address = mo.getShipping_area().replace("-", "");
                    body.addElement("toAddress").setText(address+toStringValue(mo.getShip_addr())); //址收货地
                }
                body.addElement("carrierCode").setText(toStringValue(mo.getShipping_type())); //承运商编码
                body.addElement("finsuType").setText("2064"); //保险类型, 保险类型 finsuType String 10 否 传类型对应数字,委托投保：2061、自代投保：2062、保价：2063、不保险：2064
                body.addElement("receiveAmount").setText("0"); //代收金额 TODO:没有这个
                body.addElement("declaredAmount").setText("0"); //声明价值 toStringValue(so.getOrder_amount())
                body.addElement("premium").setText("0"); //保险费 TODO:没有这个
                body.addElement("beReturn").setText("Y"); //是否原单返回 TODO:没有这个
                body.addElement("shipWarehouseCode").setText("BB6J12"); //发货仓库编码 TODO:, 这个也是写死的
                body.addElement("description").setText(toStringValue(so.getRemark())); //备注


                /** 接口变更说明(2016-04-01)要求去掉
                Receipt receipt = so.getReceipt();
                if (receipt == null) {
                    body.addElement("invoiceType").setText("false"); //是否打印发票
                    body.addElement("invoiceTitle").setText(""); //发票抬头
                } else {
                    body.addElement("invoiceType").setText("true"); //是否打印发票
                    body.addElement("invoiceTitle").setText(receipt.getTitle()); //发票抬头
                }
                body.addElement("postCode").setText(toStringValue(so.getShip_zip())); //邮政编码
                 //*/



                body.addElement("operTime").setText(DateUtil.toString(new Date(), DATETIME_FORMAT1)); //报文生成时间（精确到毫秒)


                Element productDetail = body.addElement("productDetail");
                List<OrderItem> items = so.getOrderItemList();
                for (OrderItem item : items) {
                    //订单明细列表
                    Element product = productDetail.addElement("product");

                    product.addElement("itemCode").setText(toStringValue(item.getSn())); //货品代码
                    product.addElement("itemName").setText(toStringValue(item.getName())); //货品名称
                    product.addElement("baseUnit").setText(toStringValue(item.getUnit())); //包装单位
                    //product.addElement("unitPrice").setText(toStringValue(item.getPrice())); //金额 接口变更说明(2016-04-01)要求去掉
                    product.addElement("expectedQuantity").setText(toStringValue(item.getNum())); //数量
                    product.addElement("supplierCode").setText(""); //供应商编码 TODO:没有这个

                    product.addElement("extendPropC1").setText(""); //生产批号 TODO:没有这个
                    product.addElement("extendPropC2").setText(""); //颜色 TODO:没有这个
                    product.addElement("extendPropC3").setText(""); //尺码 TODO:没有这个
                    product.addElement("extendPropC4").setText(""); //渠道 TODO:没有这个
                    product.addElement("extendPropC5").setText(""); //所属人 TODO:没有这个
                    product.addElement("extendPropC6").setText(""); //材质 TODO:没有这个
                    product.addElement("extendPropC7").setText(""); //规格 TODO:没有这个
                    product.addElement("extendPropC8").setText(""); //所属部门 TODO:没有这个
                    product.addElement("extendPropC9").setText(""); //单价 TODO:没有这个

                }
            }

        }
    }

    /**
     * 组织需要的xml节点,erp部分
     *
     * @param erpRoot
     */
    public static void appendOrderErpElement(Element erpRoot, Map<Order, List<StoreOrder>> map, IRegionsManager regionsManager, Map<Integer,Double> weightMap) {
        for (Map.Entry<Order, List<StoreOrder>> e : map.entrySet()) {
            Order mo = e.getKey();
            List<StoreOrder> soList = e.getValue();

            for (StoreOrder so : soList) {
                List<OrderItem> items = so.getOrderItemList();
                for (OrderItem item : items) {
                    Element body = erpRoot.addElement("body");
                    body.addElement("order_main_id").setText(toStringValue(mo.getSn())); //主订单号
                    body.addElement("order_id").setText(toStringValue(so.getSn())); //小订单号
                    body.addElement("order_src").setText(toStringValue(mo.getSite())); //订单来源（pc，app，wap）
                    body.addElement("status").setText(toStringValue(so.getStatus())); //订单状态
                    body.addElement("ship_name").setText(toStringValue(mo.getShip_name())); //收货人姓名
                    body.addElement("ship_addr").setText(toStringValue(mo.getShip_addr())); //收货人地址
                    body.addElement("ship_zip").setText(toStringValue(mo.getShip_zip())); //收货人邮编
                    body.addElement("ship_email").setText(toStringValue(mo.getShip_email())); //收货人email
                    body.addElement("ship_mobile").setText(toStringValue(mo.getShip_mobile())); //收货人手机
                    body.addElement("ship_tel").setText(toStringValue(mo.getShip_tel())); //收货人电话
                    body.addElement("ship_day").setText(""); //送货日期, 直接留空
                    body.addElement("ship_time").setText(""); //送货时间, 直接留空
                    body.addElement("is_protect").setText(toStringValue(so.getIs_protect())); //是否保价
                    body.addElement("protect_price").setText(toStringValue(so.getProtect_price())); //保价费用
                    body.addElement("goods_amount").setText(toStringValue(so.getGoods_amount())); //订单总额（用户总共需支付的金额990）
                    body.addElement("goods_price").setText(toStringValue(item.getPrice())); //商品销售单价490
                    body.addElement("shipping_amount").setText(toStringValue(mo.getShipping_amount())); //配送费用50
                    body.addElement("order_amount").setText(toStringValue(so.getOrder_amount())); //（小订单）订单总额_已商品金额计算，规则同goods_amount

                    Double weight = weightMap.get(item.getProduct_id());
                    body.addElement("weight").setText(toStringValue(weight)); //单商品重量

                    body.addElement("goods_num").setText(toStringValue(item.getNum())); //商品数量
                    body.addElement("depotid").setText("0100100101"); //仓库 TODO: 不是so.getDepotid()),看看是不是把这个属性放到数据库里面，这个属性是erp的
                    body.addElement("shipping_type").setText(toStringValue(so.getShipping_type())); //配送公司


                    body.addElement("ship_provinceid").setText(toStringValue(regionsManager.get(so.getShip_provinceid()))); //配送地区-省（传名称）
                    body.addElement("ship_cityid").setText(toStringValue(regionsManager.get(so.getShip_cityid()))); //配送地区-城市（传名称）
                    body.addElement("ship_regionid").setText(toStringValue(regionsManager.get(so.getShip_regionid()))); //配送地区-区（传名称）
                    body.addElement("goods_sn").setText(item.getSn()); //商品编码（U8的存货编码）
                    //body.addElement("goods_sn").setText("10100000004"); //商品编码（U8的存货编码）
                    body.addElement("igive").setText("0"); //是否赠送, 目前没有满赠,所以都是0
                    body.addElement("goods_cbatch").setText(""); //批次,给erp的,强制加上,没用


                    Receipt receipt = so.getReceipt();
                    if (receipt == null) {
                        body.addElement("receipt_title").setText(""); //发票title
                        body.addElement("receipt_add_time").setText(""); //发票时间
                        body.addElement("receipt_content").setText(""); //发票内容
                    } else {
                        long l = System.currentTimeMillis();
                        Date date = new Date(l);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        body.addElement("receipt_title").setText(toStringValue(receipt.getTitle())); //发票title
                        body.addElement("receipt_add_time").setText(dateFormat.format(date)); //toDateString(receipt.getAdd_time())发票时间
                        body.addElement("receipt_content").setText(toStringValue(receipt.getContent())); //发票内容
                    }

                    body.addElement("finsutype").setText("2062"); //2062 TODO:这是干什么的?
                    body.addElement("receiveamount").setText(""); //代收金额 TODO:没有这个
                    body.addElement("declaredamount").setText(""); //声明价值 TODO:没有这个
                    body.addElement("premium").setText(""); //保险费 TODO:没有这个
                    body.addElement("pay_type").setText(toStringValue(so.getPayment_name())); //支付方式（货到付款、在线支付)
                    body.addElement("pay_time").setText(toDateString(so.getCreate_time())); //支付时间
                    body.addElement("serial_number").setText(toStringValue(so.getSn())); //交易流水号
                    body.addElement("ccuscode").setText("101"); //TODO: 店铺来源编码, 这是给erp用的, 这个应该和店放一起,上线可能会改.
                    body.addElement("store_id").setText(toStringValue(so.getStore_name())); //店铺名称

                }
            }

        }

    }

    /**
     * 仅仅给妥投使用...改来改去,尽量适配Erp的格式
     *
     * @param orderSn
     * @param omsRestUrl
     * @return
     */
    public static boolean requestOmsForDefault(String orderSn, String omsRestUrl) {
        if (CONFIG_NO_WMS) {
            return true;
        }

        String requestXml = createDefaultOmsXMLStringRequest(orderSn);
        logger.info("requestOmsForDefault, orderSn:{}", orderSn);
        Document document = requestOms(omsRestUrl, "goodsArrival", requestXml);

        /**
         * <data>
         <vouch>
                 <entry>
                        <success>T</success><message>信息</message><vouchercode>订单号</vouchercode>
                 </entry>
         </vouch>
         </data>
         */
        Element resRoot = document.getRootElement().element("vouch").element("entry");
        String success = resRoot.elementTextTrim("success");
        String message = resRoot.elementTextTrim("message");
        String vouchercode = resRoot.elementTextTrim("vouchercode");

        logger.info("response from oms(requestOmsForDefault), success:{}, message:{}, vouchercode:{}", success, message, vouchercode);

        if (success != null && !success.equals("") && success.equals("T")) {
            return true;
        }

        //这里是不是应该抛出异常把结果提示文字扔回去?
        return false;

    }

    public static boolean requestOmsForRefund(String requestXml, String omsRestUrl) {
        if (CONFIG_NO_WMS) {
            return true;
        }
        Document document = requestOms(omsRestUrl, "returnGoods", requestXml);
        Element resRoot = document.getRootElement();
        String success = resRoot.elementTextTrim("success");
        String reason = resRoot.elementTextTrim("reason");
        String opertime = resRoot.elementTextTrim("opertime");
        String data = resRoot.elementTextTrim("data");
        logger.info("response from oms(requestOmsForRefund), success:{}, reason:{}, opertime:{}, data:{}", success, reason, opertime, data);


        if (success != null && !success.equals("") && success.equals("T")) {
            return true;
        }

        //这里是不是应该抛出异常把结果提示文字扔回去?
        return false;
    }

    public static Document createDefaultErpRefundXMLResponse() {

        try {
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><SalesOrderReceivings></SalesOrderReceivings></root>";
            return parseRequestXML(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 组织需要的xml节点,wms部分
     * 退货 tradeno是退货单号
     * @param root
     */
    public static void appendOrderRefundElement(Element root, Order so, List<OrderItem> items, IRegionsManager regionsManager,String tradeno) {

        Element body = root.addElement("SalesOrderReceiving");
        body.addElement("orderCode").setText(tradeno); //客户订单号，toStringValue(so.getSn())
        body.addElement("companyCode").setText("7593459"); //货主编码
        body.addElement("billTypeCode").setText("IN_RETURN"); //入库单类型, 单据类型的编码，入库为IN_PURCHASE, 出库OUT_SALES, 退货IN_RETURNS
        body.addElement("orderDate").setText(toDateString(so.getCreate_time())); //订单日期
        body.addElement("fromOrgName").setText(""); //发货方名称 TODO:没有这个
        body.addElement("fromContactName").setText(""); //发货人 TODO:没有这个
        body.addElement("fromTelephone").setText(""); //发货人联系电话 TODO:没有这个
        body.addElement("fromAddress").setText(""); //发货人地址 TODO:没有这个
        body.addElement("toOrgName").setText("zjs"); //收货方名称  TODO:没有这个
        body.addElement("toContactName").setText(toStringValue(so.getShip_name())); //收货人
        body.addElement("toTelephone").setText(toStringValue(so.getShip_mobile())); //收货人联系电话
        body.addElement("toAddress").setText(toStringValue(so.getShip_addr())); //址收货地
        body.addElement("warehouseCode").setText("BB6J12"); //仓库编码 TODO:没有这个
        body.addElement("containerQuantity").setText("0"); //集装箱数 TODO:没有这个
        body.addElement("description").setText(toStringValue(so.getRemark())); //备注

        //订单明细列表
        Element productDetail = body.addElement("ProductDetail");
        for (OrderItem item : items) {
            Element product = productDetail.addElement("Product");
            product.addElement("itemCode").setText(toStringValue(item.getSn())); //货品代码
            product.addElement("itemName").setText(toStringValue(item.getName())); //货品名称
            product.addElement("baseUnit").setText(toStringValue(item.getUnit())); //包装单位
            product.addElement("unitPrice").setText(toStringValue(item.getPrice())); //金额
            product.addElement("expectedQuantity").setText(toStringValue(item.getNum())); //数量
            product.addElement("supplierCode").setText("");
            product.addElement("extendPropC1").setText(""); //生产批号 TODO:没有这个
            product.addElement("extendPropC2").setText(""); //颜色 TODO:没有这个
            product.addElement("extendPropC3").setText(""); //尺码 TODO:没有这个
            product.addElement("extendPropC4").setText(""); //渠道 TODO:没有这个
            product.addElement("extendPropC5").setText(""); //所属人 TODO:没有这个
            product.addElement("extendPropC6").setText(""); //材质 TODO:没有这个
            product.addElement("extendPropC7").setText(""); //规格 TODO:没有这个
            product.addElement("extendPropC8").setText(""); //所属部门 TODO:没有这个
            product.addElement("extendPropC9").setText(""); //单价 TODO:没有这个
        }
    }

    public static Document requestOms(String omsRestUrl, String method, String requestXml) {
        String xml = Axis2RequestServicesUtil.sendRequest(omsRestUrl, method, requestXml, "parametersXML");
        try {
            Document document = parseRequestXML(xml);
            return document;
        } catch (Exception e) {
            logger.error("请求oms返回数据解析错误:{},method:{} data:{}", omsRestUrl, method, xml);
            logger.error("", e);
            return null;
        }
    }

    public static Document requestOms1(String requestXml, String omsRestUrl) {
        HttpPost httpPost = new HttpPost(omsRestUrl);
        String returnData = null;

        try {
            StringEntity entity = new StringEntity(requestXml, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType(ServletUtils.XML_TYPE);
            //entity.setContentType(ServletUtils.TEXT_TYPE);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 解析数据
                returnData = EntityUtils.toString(response.getEntity());
            }
        } catch(Exception e) {
            logger.error("请求oms错误:" + omsRestUrl, e);
        }


        try {
            Document document = parseRequestXML(returnData);
            return document;
        } catch (Exception e) {
            logger.error("请求oms返回数据解析错误:{}, data:{}", omsRestUrl, returnData);
            logger.error("", e);
            return null;
        }
    }


    private static String createDefaultOmsXMLStringRequest(String orderSn) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><data><body>");
        //这里可能是个循环,目前当单一的用
        sb.append("<entry>");
        //子订单号
        sb.append("<cvouchcode>").append(orderSn == null ? "" : orderSn).append("</cvouchcode>");
        //妥投时间
        sb.append("<cvouchtime>").append(DateUtil.toString(new Date(), DATETIME_FORMAT)).append("</cvouchtime>");
        //类型保留留空
        sb.append("<voucher_type></voucher_type>");
        sb.append("</entry>");
        sb.append("</body></data>");
        String xml = sb.toString();
        return xml;

    }

    private static String createOmsRefundXMLStringRequest(String orderid, String shopid, String shoptype) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><item>");
        sb.append("<orderid>").append(orderid == null ? "" : orderid).append("</orderid>");
        sb.append("<shopid>").append(shopid == null ? "" : shopid).append("</shopid>");
        sb.append("<shoptype>").append(shoptype == null ? "" : shoptype).append("</shoptype>");
        sb.append("</item></root>");
        String xml = sb.toString();
        return xml;
    }

    /**
     * DOM4J不接受null,必须转为空字符串
     * @param o
     * @return
     */
    private static String toStringValue(Object o) {
        if (o == null) {
            return "";
        }

        if (o instanceof String) {
            return (String)o;
        }

        if (o instanceof Double) {
            return Double.toString((Double)o);
        }

        if (o instanceof Integer) {
            return Integer.toString((Integer) o);
        }

        if (o instanceof Long) {
            return Long.toString((Long) o);
        }

        if (o instanceof Regions) {
            Regions r = (Regions)o;
            if (r.getLocal_name() == null) {
                return "";
            }
            return r.getLocal_name();
        }

        return "";
    }

    private static String toDateString(Long d) {
        if (d == null) {
            return "";
        }
        return DateUtil.toString(new Date(d * 1000L), DATETIME_FORMAT);
    }

    public static Document parseRequest(HttpServletRequest request) throws Exception {
        String body = extractPostRequestBody(request);
        Document doc = DocumentHelper.parseText(body);
        return doc;
    }

    public static Document parseRequestXML(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        return doc;
    }


    public static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    public static void main(String[] args) {



        try {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root><goods><good><sku>10100000004</sku><inventory>15</inventory><warehouse_id>0100100102</warehouse_id><warehouse_name>土豆-发芽</warehouse_name></good><good><sku>10100000003</sku><inventory>20</inventory><warehouse_id>0100100102</warehouse_id><warehouse_name>地球-大连</warehouse_name></good><good><sku>10100000002</sku><inventory>24</inventory><warehouse_id>0100100101</warehouse_id><warehouse_name>火星-天宫</warehouse_name></good><good><sku>10100000003</sku><inventory>997</inventory><warehouse_id>0100100101</warehouse_id><warehouse_name>????-??</warehouse_name></good><good><sku>10100000004</sku><inventory>993</inventory><warehouse_id>0100100101</warehouse_id><warehouse_name>银河-悬臂</warehouse_name></good></goods></root>";
            Document doc = requestOms1(xml, "http://localhost:8080/api/mobile/erp!updateStock.do");
            System.out.println(doc.asXML());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
