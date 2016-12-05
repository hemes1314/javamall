<%@page import="java.util.Date"%>
<%@page import="com.enation.framework.util.DateUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<%
String now  = DateUtil.toString(new Date(), "yyyy-MM-dd");
%>
<div class="division">
<table width="100%">
  <tbody>
  
  <tr>
    <th width="15%">订单号：</th>
    <td width="27%">${ord.sn } <br>【${ord.payStatus}】</td>
    <th width="37%">下单日期：</th>
    <td width="21%"><html:dateformat pattern="yyyy-MM-dd hh:mm:ss" time="${ord.create_time}"></html:dateformat>:</td>
      </tr>
      
      
  <tr>
    <th>订单总金额：</th>
    <td>
    
    <fmt:formatNumber value="${ord.order_amount}" type="currency" pattern="￥.00"/>
    
      </td>
    <th>已收金额：</th>
    <td><fmt:formatNumber value="${ord.paymoney}" type="currency" pattern="￥.00"/></td>
    </tr>
    
  <tr>
    <th>退款类型</th>
    <td><label>
      <input type="radio" style="" id="onlineRdio" value="1" name="refund.type" />
在线支付</label>
      <label>
      <input type="radio" style=""  id="offlineRdio"  checked="checked" value="2" name="refund.type" />
线下支付</label></td>
    <th>退款日期：</th>
    <td>&nbsp;<input type="text" name="refund.pay_date_str" id="pay_date"  dataType="date" required="true" class="dateinput"  value="<%=now %>" /></td>
  </tr>
  <tr class="offline_box">
    <th>银行：</th>
    <td><select  name="refund.pay_method"  >
      <option value="中国工商银行">中国工商银行</option>
      <option value="中国建设银行">中国建设银行</option>
      <option value="招商银行">招商银行</option>
      <option value="中国农业银行">中国农业银行</option>
      <option value="中国银行">中国银行</option>
    </select></td>
    <th>流水号:</th>
    <td>&nbsp;<input type="text" name="refund.sn"   /></td>
  </tr>
  
  <tr class="offline_box">
    <th>户名：</th>
    <td><input type="text" name="refund.pay_user" /> </td>
    <th>帐号：</th>
    <td><input type="text"  style="width: 200px;" value="" name="refund.account" id="payAccount" autocomplete="off" /></td>
  </tr>
  
  <tr class="online_box" style="display:none">
    <th>退款方式：</th>
    <td><input type="text" name="refund.pay_method" disabled="disabled"  /></td>
    <th>流水号:</th>
    <td>&nbsp; <input type="text" name="refund.sn" disabled="disabled"  /></td>
  </tr>
  
  
  <tr>
      <th height="22">退款金额：</th>
      <td><input type="text"  style="width: 100px;" value="${ord.paymoney}" name="refund.money" autocomplete="off" /></td>
      <th>退款人：</th>
      <td><input type="text" style="width: 100px;"   name="refund.op_user"  /></td>
  </tr>    
    </tbody>
    </table>
</div>