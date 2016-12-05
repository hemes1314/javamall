<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<style>
#tagspan {
	position: absolute;
	display: none;
}

#searchcbox {
	float: left
}

#searchcbox div {
	float: left;
	margin-left: 10px
}
</style>
<div class="grid">
<form id="maintaintask" action="warnTask!maintain.do" method="post" >
	<input type="hidden" id="taskIdval" name="taskIdval"  value="${taskId}">
	<table class="form-table" style="width: 100%; float: left">
		<c:if test="${DepotStore!=null && listCylinder==null && listSphere!=null}">
			<tr>
				<th>度数</th>
				<th>库存</th>
				<th>进货量</th>
			</tr>
				
			<c:forEach items="${DepotStore}" var="item" varStatus="status">
				<tr>
					<td>
					${item.sphere}
					<input type="hidden" name="sphere"  value="${item.sphere}" />
					</td>
					<td>
					${item.store}
					</td>
					<td>
					<input type="hidden" name="depotid"   value="${item.depotid}" />
					<input type="hidden" name="degreeid"   value="${item.id}" />
					<input type="hidden" name="goodsid"  value="${item.goodsid}" />
					<input type="text"  name="degree_store" store="${item.store}" value="0"/>
					</td>
				</tr>
			</c:forEach>
				
		</c:if>
		
		<c:if test="${DepotStore!=null && listCylinder!=null}">
			<tr>
				<th>球镜</th>
				<th>柱镜</th>
				<th>库存</th>
				<th>进货量</th>
			</tr>
				
			<c:forEach items="${DepotStore}" var="item" varStatus="status">
				<tr>
					<td>${item.sphere}
					<input type="hidden" name="spheres"  value="${item.sphere}"/></td>
					<td>${item.cylinder}
					<input type="hidden" name="cylinders"  value="${item.cylinder}"/></td>
					<td>${item.store}</td>
					<td><input type="hidden" name="depotid"   value="${item.depotid}" />
					<input type="hidden" name="degreeid"   value="${item.id}" />
					<input type="hidden" name="goodsid"  value="${item.goodsid}" />
					<input type="text"  name="degree_store" store="${item.store}" value="0"/></td>
				</tr>
			</c:forEach>
				
		</c:if>
		<c:if test="${listColor!=null}">
			<tr>
				<th>颜色</th>
				<th>库存</th>
				<th>进货量</th>
			</tr>
				
			<c:forEach items="${listColor}" var="item" varStatus="status">
				<tr>
					<td>${item.color}</td>
					<td>${item.store}</td>
					<td>
					<input type="hidden" name="productid" value="${item.productid}" />
					<input type="hidden" name="depotid"   value="${item.depotid}" />
					<input type="hidden" name="degreeid"   value="${item.id}" />
					<input type="hidden" name="goodsid"  value="${item.goodsid}" />
					<input type="text" name="degree_store" store="${item.store}" value="0"  /></td>
				</tr>
			</c:forEach>
				
		</c:if>
		<c:if test="${DepotStore!=null && listCylinder==null && listColor==null && listSphere==null}">
			<tr>
				<th>库存</th>
				<th>进货量</th>
			</tr>
				
			<c:forEach items="${DepotStore}" var="item" varStatus="status">
				<tr>
					<td>${item.store}</td>
					<td>
					<input type="hidden" name="depotid"   value="${item.depotid}" />
					<input type="hidden" name="degreeid"   value="${item.id}" />
					<input type="hidden" name="goodsid"  value="${item.goodsid}" />
					<input type="text" name="degree_store" store="${item.store}" value="0"  /></td>
				</tr>
			</c:forEach>
				
		</c:if>
		<tr>
			<td colspan="10" >
			<input style="margin-left:40%" type="button" id="submitbtn" value="保存">
			</td>
		</tr>
	</table>
</form>
	<div style="clear: both; padding-top: 5px;"></div>
</div>
<script type="text/javascript">
<!--
$(function(){
	$(".tempCloseBtn").attr("class","closeBtn");
	$.Loading.hide();
});
//-->
</script>
