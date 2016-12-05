<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<script type="text/javascript">
$("#paylog").unbind("click");
$("#paylog").bind("click",function(){
	$("#order_dialog .con").load(basePath+"payment!showPayDialog.do?ajax=yes&orderId="+OrderDetail.orderid,function(){
		$("#order_dialog .submitBtn").unbind("click");
		$("#order_dialog .submitBtn").bind("click",function(){
			OrderDetail.paylog();
		});
	}); 
	
});
</script>
<div class="division">

<table width="100%">
  <tbody><tr>
    <th>订单号：</th>
    <td>${payment.order_sn}</td>
    <th>支付方式：</th>
    <td>${payment.pay_method}</td>
      </tr>
  <tr> 
    <th>订单金额：</th>
    <td >${ord.order_amount}</td>
    <th>付款人：</th>
    <td>${payment.pay_user}</td>
    </tr>
 <c:forEach items="${ metaList}" var="meta">
  <c:if test="${ meta.meta_key =='bonusdiscount'}" >
 	<tr>	
 	
	<th>优惠卷抵扣： </th>
	<td>
	
	${meta.meta_value}
	
	</td>
	<th>&nbsp;</th>
	<td>&nbsp;</td>
    </tr>
   </c:if>
 </c:forEach>
  	<tr>	
		<th>实付款： </th>
		<td>${payment.money}</td>
		<th>已结算金额</th>
		<td>${payment.paymoney}</td>
    </tr>
    
      
    </tbody>
   </table>
    
    
</div> 
  
<div class="grid">
<table>
	<tr><th>付款日期</th>
	<th>付款金额</th>
	<th>操作人</th></tr>
	<c:forEach items="${paymentList}" var="paydetail">
	<tr><td><html:dateformat pattern="yyyy-MM-dd hh:mm:ss" time="${ paydetail.pay_date }"></html:dateformat></td>
	<td>${paydetail.pay_money }</td>
	<td>${paydetail.admin_user }</td>
	 </tr>
	</c:forEach>
	</table>
	<center><div style="margin-top: 50px;">
	<label>本次付款</label><input type="text" id="paymoney" name="paymoney" value="${payment.money-payment.paymoney}" />
	<input type="hidden" value="${payment_id}" name="payment_id" /></div>
	
     </center>

		
</div>		



 
