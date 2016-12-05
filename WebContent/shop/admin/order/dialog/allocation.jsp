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
			<th>发货点:</th>
			<td>
			<select  name="depotid" id="depotid">
				<option value="0">--请选择--</option>
				<c:forEach items="${depotList}" var="depot" >
					<option value="${depot.id }" label="${depot.name }">${depot.name }</option>
				</c:forEach>
			</select>
			</td>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
		</tr>
	</tbody>
</table>
</div>


<div class="division" id="allo_box">
<table  cellpadding="0" cellspacing="0" class="finderInform">
	<col style="width: 20%;">
	<col style="width: 35%;">
	<col style="width: 15%;">
	<col style="width: 10%;">
	<col style="width: 10%;">
	<col style="width: 10%;">
	<thead>
		<tr>
			<th width="70">货号</th>
			<th width="179">商品名称</th>
			<th>扩展信息</th>
			<th width="82">购买数量</th>
			<th width="37">配货</th>
		</tr>
	</thead>
	<tbody>
	
	<c:forEach items="${itemList}" var="item">
		<tr>
			<td>${ item.sn}

			</td>
			<td>
			${ item.name}
			</td>
			<td>
			&nbsp;${item.other}
			</td>
			<td>${ item.num}</td>
			<td><a href="javascript:;" class="opbtn" itemid="${item.item_id}">配货</a></td>
		</tr>
		<tr>
		  <td>&nbsp;</td>
		  <td colspan="4">
			<div class="allo_items" num="${ item.num}" goodsname="${ item.name}">
			</div>
		  </td>
	    </tr>
	</c:forEach>
	</tbody>
</table>

</div>