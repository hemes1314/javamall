<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="division">
<table cellspacing="0" cellpadding="0">
	<tbody>
		<tr>
			<th>订单号:</th>
			<td>${ord.sn } 【${ord.payStatus}】</td>
			<th>下单日期:</th>
			<td><html:dateformat pattern="yy-MM-dd hh:mm:ss" time="${ord.create_time}"></html:dateformat></td>
		</tr>
		<tr>
			<th>配送方式:</th>
			<td>
			<select  name="delivery.ship_type" id="shiptype">
				<c:forEach items="${dlyTypeList}" var="type" >
					<option value="${type.name }" label="${type.name }">${type.name }</option>
				</c:forEach>
			</select>
			</td>
			<th>配送费用:</th>
			<td><fmt:formatNumber value="${ord.shipping_amount }" type="currency" pattern="￥.00"/></td>
		</tr>
		<tr>
			<!--
  <th>配送地区:</th>
  <td></td> -->
			<th>是否要求保价:</th>
			<td colspan="3"><c:if test="${is_protect}" >是</c:if><c:if test="${!is_protect}" >否</c:if></td>
		</tr>
		<tr>
			<th>物流公司:</th>
			<td><select   name="delivery.logi_id" >
				<c:forEach items="${logiList}" var="logi" >
					<option value="${logi.id }" label="${logi.name }">${logi.name }</option>
				</c:forEach>
			   </select> 
			</td>
			<th>物流单号:</th>
			<td><input type="text"  style="width: 100px;" name="delivery.logi_no" autocomplete="off"></td>
		</tr>
		<tr>
			<th>物流费用:</th>
			<td><input type="text" style="width: 50px;" value="${ord.shipping_amount }" name="delivery.money" autocomplete="off"></td>
			<th>物流保价:</th>
			<td>
			<label><input type="radio"   style="" checked="checked" value="0" name="delivery.is_protect">否</label> 
			<label><input type="radio"   style="" value="1" name="delivery.is_protect">是</label>
			</td>
		</tr>
		<tr>
			<th>保价费用:</th>
			<td colspan="3"><input type="text"    style="width: 50px;" value="${ord.protect_price }"
				name="delivery.protect_price" autocomplete="off"></td>
		</tr>
		<tr>
			<th>收货人姓名:</th>
			<td><input type="text" style="width: 80px;" value="${ord.ship_name}" name="delivery.ship_name" autocomplete="off"></td>
			<th>电话:</th>
			<td><input type="text"style="width: 150px;" value="${ord.ship_tel}" name="delivery.ship_tel" autocomplete="off"></td>
		</tr>
		<tr>
			<th>手机:</th>
			<td><input type="text" style="width: 150px;" value="${ord.ship_mobile}" name="delivery.ship_mobile" autocomplete="off"></td>
			<th>邮政编码:</th>
			<td><input type="text" style="width: 80px;" value="${ord.ship_zip}" name="delivery.ship_zip" autocomplete="off"></td>
		</tr>
		<tr>
			<th>地区:</th>
			<td colspan="3">
			<html:regionselect></html:regionselect>
			</td>
		</tr>
		<tr>
			<th>地址:</th>
			<td colspan="3"><input type="text"  style="width: 360px;" value="${ ord.ship_addr}" name="delivery.ship_addr" autocomplete="off"></td>
		</tr>
		<tr>
			<th>发货单备注:</th>
			<td colspan="3"><textarea  style="width: 95%;" name="delivery.remark"
				type="textarea"></textarea></td>
		</tr>
	</tbody>
</table>
</div>


<div class="division">
<table cellspacing="0" cellpadding="0" class="finderInform">
	<col style="width: 20%;">
	<col style="width: 35%;">
	<col style="width: 15%;">
	<col style="width: 10%;">
	<col style="width: 10%;">
	<col style="width: 10%;">
	<thead>
		<tr>
			<th>货号</th>
			<th>商品名称</th>
			<th>护展信息</th>			
			<th>配货情况</th>
			<th>购买数量</th>
			<th>已发货</th>
			<th>此单发货</th>
		</tr>
	</thead>
	<tbody>
	
	<c:forEach items="${itemList}" var="item">
		<tr>
			<td>${ item.sn}</td>
			<td>${ item.name}
			<input type="hidden" name="goods_idArray" value="${ item.goods_id}"/>
			<input type="hidden" name="goods_nameArray" value="${ item.name}"/>
			<input type="hidden" name="product_idArray" value="${ item.product_id}"/>
			<input type="hidden" name="goods_snArray" value="${ item.sn}"/>
			<input type="hidden" name="item_idArray" value="${ item.item_id}"/>
			</td>
			
			<td>
			&nbsp;${item.other}
			</td>
			
			<td>
			
		<input type="button" class="view_allo_btn" value="配货情况" itemid="${item.item_id}" />
			
				<div class="allo_box">
					<div class="all_content">
					</div>
					<div class="close_box"><a href="javascript:;" class="close">关闭</a></div>
				</div>
				
			</td>
			<td>${ item.num}</td>
			<td>${ item.ship_num}</td>
			<td><input type="text" style="width: 50px;" value="${ item.num-item.ship_num}" name="numArray" autocomplete="off"></td>
		</tr>
	</c:forEach>
	
	</tbody>
</table>
</div>


<script>

$(function(){
	$("#shiptype").val("${ord.shipping_type}");
	RegionsSelect.load(${ord.ship_provinceid},${ord.ship_cityid},${ord.ship_regionid});
});
</script>