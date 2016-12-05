<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<div class="grid">
	<div class="toolbar">
		<ul><li>仓库发货任务列表</li></ul>
		<div style="clear:both"></div>
	</div>
	
<table>
	<thead>
	<c:forEach var="item" items="${listTask }">
		<tr>
		<th width=150>${item.name }发货任务${item.num }个</th>
		<th><a href="order!list.do?optype=monitor&status=4&depotid=${item.depotid }">查看</a></th>
		</tr>
		</c:forEach>
	</thead> 
</table>	
	
<div style="clear:both;padding-top:5px;"></div>
</div>