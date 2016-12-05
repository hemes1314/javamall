<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<style type="text/css" media="print">
.noprint{ display : none }
</style>

<style type="text/css" media="screen,print">
.x-barcode{padding:0;margin:0}
body { margin:0; font-family:Arial, Helvetica, sans-serif; font-size:12px;}
.td_frame { padding:5px 0 5px 0; border-bottom:2px solid #000000; }
img { padding:2px; border:0;}
p { margin:8px 0 8px 0;}
h1 { font-size:16px; font-weight:bold; margin:0;}
h2 { font-size:14px; font-weight:bold; margin:0;}
.table_data_title { font-size:14px; font-weight:bold; height:25px; }
.table_data_content { height:20px; line-height:20px;}
#print_confirm { width:100%;  height:30px;  border-top:1px solid #999999; padding-top:4px;   background-color:#5473ae; position:absolute; }
#btn_print { width:71px; height:24px; background-image:url(images/btn_print.gif); cursor:pointer; margin-left:auto; margin-right:auto;}
#barcode { width:150px; height:50px; background:url(images/bar_code.gif) no-repeat;}
</style>



<div id="print1">
 
<table class="table_frame" width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="td_frame"><table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="2" ></td>

          <td><div id="chk_print1" class="noprint">
          <input name="chk_pic_print" type="checkbox" id="chk_pic_print"  checked="checked" />
            <label class="label_pic_print" for="chk_pic_print">打印图片</label>
            </div></td>
        </tr>
          <tr>
            <td><img src="${site.logofile }" width="150" height="60" /></td>
            <td width="80%"><h1>${site.sitename }</h1>

              <label class="domain"></label>
              <script>$(function(){$(".domain").html(document.domain);});</script></td>
            <td width="20%"><p>收货人：${ord.ship_name }</p>
            <p>
           收货人电话：${ord.ship_tel }</p></td>
          </tr>
        </table></td>
      </tr>

    </table></td>
  </tr>
  <tr>
    <td class="td_frame"><table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td><h2>订单号：${ord.sn }</h2></td>
        <td align="right"><h2>订购日期：<html:dateformat pattern="yyyy-MM-dd HH:mm:ss" time="${ord.create_time}"/></h2></td>
      </tr>

    </table></td>
  </tr>
  <tr>
    <td class="td_frame">
    <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr class="table_data_title">
        <td>No</td>
        <td>货号</td>
        <td>商品名称</td>

        <td>单价</td>
        <td>数量</td>
        <td>小计</td>
      </tr>
	<c:forEach items="${itemList}" var="item" varStatus="index">
		<tr>
        <td>${index.index + 1 }</td>
        <td>${item.sn }</td>
        <td><img class="product" src="${item.image }" width="50" height="50" align="absmiddle" />${item.gname }
						
          </td>
        <td><fmt:formatNumber value="${item.price }" type="currency" /></td>
        <td>${item.num }</td>
        <td><fmt:formatNumber value="${item.price * item.num }" type="currency" /></td>
      </tr>
	</c:forEach>
    </table></td>
  </tr>
  <tr>
    <td class="td_frame"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td width="80%" valign="top" style="height:150px;">
  

         <p>备注：</p>
         <p>${ord.remark }</p>
         </td>

        <td width="20%" valign="top"><p>商品总价：<fmt:formatNumber value="${ord.goods_amount }" type="currency" /></p>
          <p>配送费用：<fmt:formatNumber value="${ord.shipping_amount }" type="currency" /></p>
          <p>保价费用：<fmt:formatNumber value="${ord.protect_price }" type="currency" /></p>
          </td>
      </tr>

    </table></td>
  </tr>
  <tr>
    <td class="td_frame"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td width="80%"><p>服务商：${site.sitename }</p>
            <p>电话：${site.tellist }</p>
            <p>邮箱：${site.email }</p>

          <p>网址：<label class="domain"></label></p></td>
        <td width="20%" valign="top"><h2>应付总金额：<fmt:formatNumber value="${ord.order_amount }" type="currency" />元</h2>
</td>
      </tr>
    </table></td>
  </tr>
  <c:if test="${member!=null }">
  <tr>
    <td class="td_frame">您目前的总积分为 ${member.point } ，累计购物${ordermap.buyTimes }次，累计购物金额<fmt:formatNumber value="${ordermap.buyAmount }" type="currency" />元

 </td>
  </tr>
   </c:if>
  <!-- 
  <tr>
    <td style="height:30px; padding-bottom:50px;" align="right"><strong>Powered by <label class="domain"></label></strong>
</td>
  </tr>
   -->
</table>
</div>

<div id="print2">

<table class="table_frame" width="90%" border="0" align="center"
	cellpadding="0" cellspacing="0">
	<tr>
		<td>
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tbody>
				<tr>
					<td class="td_frame">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tbody>
							<tr>
								<td><span style="font-family: EanP36Tt; font-size: 36px;">${ord.sn
								}</span></td>
								<td>
								<div class="noprint" id="chk_print"><input type="checkbox"
									checked="checked" id="chk_pic_print1" name="chk_pic_print">
								<label for="chk_pic_print1" class="label_pic_print">打印图片</label>
								&nbsp;&nbsp;&nbsp;&nbsp; <input type="checkbox"
									checked="checked" id="chk_address" name="chk_address">
								<label for="chk_address" class="label_pic_print">打印配送地址</label>
								</div>
								</td>
							</tr>
							<tr>
								<td width="70%">
								<p>订单号<strong>：${ord.sn }</strong></p>

								<p>日期：<html:dateformat pattern="yyyy-MM-dd HH:mm:ss"
									time="${ord.create_time}" /></p>
								</td>
								<td width="30%">
								</td>
							</tr>
						</tbody>
					</table>
					</td>
				</tr>
			</tbody>
		</table>
		</td>
	</tr>
	<tr>
		<td class="td_frame">
		<table width="98%" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr class="table_data_title">
				<td>No</td>
				<td>货号</td>
				<td>商品名称</td>
				<td>配货仓库</td>
				<td>数量</td>
				<td>配货状态</td>
			</tr>
			<c:forEach items="${itemList}" var="item" varStatus="index">
				<tr>
							<td>${index.index + 1 }</td>
							<td>${item.sn }</td>
							<td>${item.gname }
								<div class="other">${item.other}</div>
							</td>
							<td>${item.a_name }</td>
							<td>${item.num }</td>
							<td>
					<div
						style="height: 10pt; width: 10pt; border: 1pt solid rgb(0, 0, 0);"></div>
					</td>
						</tr>
			</c:forEach>
		</table>
		</td>
	</tr>
	<tr>
		<td>
		<table width="100%" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr>
				<td width="50%" valign="top" style="height: 150px;">

				<p>赠品：</p>
				<c:forEach items="${orderGiftList }" var="gift">
					<p>${gift.gift_name }</p>
				</c:forEach>

				<p>备注：</p>
				<p>${ord.remark }</p>
				</td>

				<td width="50%" valign="top">
				<div id="print_address">
				<p>配 送：${ord.shipping_type }</p>
				<p>收货人：${ord.ship_name }</p>
				<p>电 话：${ord.ship_tel }</p>
				<p>手 机：${ord.ship_mobile }</p>
				<p>地 址：${ord.shipping_area }${ord.ship_addr }</p>
				<p>邮 编：${ord.ship_zip }</p>
				</div>
				</td>
			</tr>

		</table>
		</td>
	</tr>
	<tr>
		<td style="height: 100px;"><h1>签字：</h1></td>
	</tr>
	<!-- 
	<tr>
		<td style="height: 30px; padding-bottom: 50px;" align="right"><strong>Powered
		by <label class="domain"></label></strong></td>
	</tr>
	 -->
</table>
<script type="text/javascript">
	$(function() {

		$("#chk_pic_print").click(function(){
			if(this.checked){
				$("#print1 img").show();
			}else{
				$("#print1 img").hide();
			}
		});

		$("#btn_print").click(function(){window.print();}); 
		
		$("#chk_pic_print1").click(function() {
			if (this.checked) {
				$("#print2 img").show();
			} else {
				$("#print2 img").hide();
			}
		});
		
		$("#chk_address").click(function() {
			if (this.checked) {
				$("#print_address").show();
			} else {
				$("#print_address").hide();
			}
		});
	});
	//-->
</script></div>
<div class="noprint submitlist" align="center">
<table>
	<tr>
		<td><input name="button" type="button" value=" 确    定   "
			class="submitBtn" id="btn_print"/></td>
	</tr>
</table>
</div>
