<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<div class="grid">
	<div class="toolbar">
		<ul><li>${goods.name }</li></ul>
		<div style="clear:both"></div>
	</div>
	
<table>
	<thead>
		<tr>
		<th width=150>仓库</th>
		<th>库存</th>
		</tr>
	</thead> 
	<tbody>
	<c:forEach var="item" items="${listTask }">
		<tr>
		<td>${item.name }</td>
		<td>${item.num  }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>	
	
<div style="clear:both;padding-top:5px;"></div>
</div>