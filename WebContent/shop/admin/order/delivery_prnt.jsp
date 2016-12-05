<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<style type="text/css" media="print">
.noprint {
	display: none
}
</style>

<style type="text/css" media="screen,print">
.x-barcode {
	padding: 0;
	margin: 0
}

body {
	margin: 0;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
}

.td_frame {
	padding: 5px 0 5px 0;
	border-bottom: 2px solid #000000;
}

img {
	padding: 2px;
	border: 0;
}

p {
	margin: 8px 0 8px 0;
}

h1 {
	font-size: 16px;
	font-weight: bold;
	margin: 0;
}

h2 {
	font-size: 14px;
	font-weight: bold;
	margin: 0;
}

.table_data_title {
	font-size: 14px;
	font-weight: bold;
	height: 25px;
}

.table_data_content {
	height: 20px;
	line-height: 20px;
}

#print_confirm {
	width: 100%;
	height: 30px;
	border-top: 1px solid #999999;
	padding-top: 4px;
	background-color: #5473ae;
	position: absolute;
}

#btn_print {
	width: 71px;
	height: 24px;
	background-image: url(images/btn_print.gif);
	cursor: pointer;
	margin-left: auto;
	margin-right: auto;
}
</style>

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
											<td><span
												style="font-family: EanP36Tt; font-size: 48px;">${ord.sn
													}</span>
											</td>
											<td>
												<div class="noprint" id="chk_print">
													<input type="checkbox" checked="checked"
														id="chk_pic_print1" name="chk_pic_print"> <label
														for="chk_pic_print1" class="label_pic_print">打印图片</label>
													&nbsp;&nbsp;&nbsp;&nbsp; <input type="checkbox"
														checked="checked" id="chk_address" name="chk_address">
													<label for="chk_address" class="label_pic_print">打印配送地址</label>
												</div></td>
										</tr>
										<tr>
											<td width="70%">
												<p>
													订单号<strong>：${ord.sn }</strong>
												</p>

												<p>
													日期：
													<html:dateformat pattern="yyyy-MM-dd HH:mm:ss"
														time="${ord.create_time}" />
												</p>
												<p>
															发货仓库:				${all_depotname }	
												</p>
												</td>
											<td width="30%">
												
												<p>配货下达时间：
							<html:dateformat
 pattern="yyyy-MM-ddHH:mm:ss"
  time="${all_time*1000}"></html:dateformat>	</p>
												</td>
										</tr>
									</tbody>
								</table></td>
						</tr>
					</tbody>
				</table></td>
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
					<c:forEach items="${allocationList}" var="item" varStatus="index">
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
				</table></td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" align="center" cellpadding="0"
					cellspacing="0">
					<tr>
						<td width="50%" valign="top" style="height: 150px;">

							<p>赠品：</p> <c:forEach items="${orderGiftList }" var="gift">
								<p>${gift.gift_name }</p>
							</c:forEach>

							<p>备注：</p>
							<p>${ord.remark }</p></td>

						<td width="50%" valign="top">
							<div id="print_address">
								<p>配 送：${ord.shipping_type }</p>
								<p>收货人：${ord.ship_name }</p>
								<p>电 话：${ord.ship_tel }</p>
								<p>手 机：${ord.ship_mobile }</p>
								<p>地 址：${ord.shipping_area }${ord.ship_addr }</p>
								<p>邮 编：${ord.ship_zip }</p>
							</div></td>
					</tr>

				</table></td>
		</tr>
		<tr>
			<td style="height: 100px;"><h1>签字：</h1>
			</td>
		</tr>
	<!-- 
		<tr>
			<td style="height: 30px; padding-bottom: 50px;" align="right"><strong>Powered
					by <label class="domain"></label></strong>
			</td>
		</tr>
		 -->
	</table>
	<script type="text/javascript">
		$(function() {
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

			$("#btn_print").click(function() {
				window.print();
			});

		});
	//-->
	</script>
</div>
<div class="noprint submitlist" align="center">
	<table>
		<tr>
			<td><input name="button" type="button" value=" 确    定   "
				class="submitBtn" id="btn_print" />
			</td>
		</tr>
	</table>
</div>
