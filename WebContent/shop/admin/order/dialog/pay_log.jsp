<%@page import="java.util.Date"%>
<%@page import="com.enation.framework.util.DateUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<%
String now  = DateUtil.toString(new Date(), "yyyy-MM-dd");
%>

<div class="division">
<table width="100%">
  <tbody><tr>
    <th>订单号：</th>
    <td>${ord.sn } 【${ord.payStatus}】</td>
    <th>下单日期：</th>
    <td><html:dateformat pattern="yy-MM-dd hh:mm:ss" time="${ord.create_time}"></html:dateformat></td>
      </tr>
  <tr> 
  
    <th>订单总金额：</th>
    <td >￥<fmt:formatNumber value="${ord.order_amount}" maxFractionDigits="2" /></td>
    
    <th>已收金额：</th>
    <td> ￥<fmt:formatNumber value="${ord.paymoney}" maxFractionDigits="2" /> </td>
    
    </tr>
    </tbody></table>
</div>

<div class="division">
<table width="100%">
  <tbody>

  <tr>
    <th>付款方式</th>
    <td><select  name="pay_method"  >
      <option value="货到付款">货到付款</option>
    </select></td>
    <th>收款日期：</th>
    <td>&nbsp;<input type="text" name="paydate"  dataType="date" required="true" class="dateinput"  value="<%=now %>" /></td>
  </tr>
  <tr>
      <th height="22">收款金额：</th>
      <td><input type="text"  style="width: 100px;" value="0.0" name="paymoney" autocomplete="off" /></td>
      <th>流水号:</th>
      <td>&nbsp;<input type="text" name="sn"   /></td>
  </tr>    
  
  <tr>
			<th>备注：</th>
			<td colspan="3"><textarea
 type="text"  name="remark" style="width:400px; height:100px; line-height:15px;"></textarea></td>
		</tr>
    </tbody>
    </table>
    
</div>