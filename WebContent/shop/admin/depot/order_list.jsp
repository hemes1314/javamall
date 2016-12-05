<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<div class="grid">
	<div class="toolbar">
		<ul><li>仓库订单任务</li></ul>
		<div style="clear:both"></div>
	</div>
	
<table>
	<thead>
		<tr>
		<th width=200>【${payTotal }】订单需要确认收款</th>
		<th>
			<c:if test="${payTotal>0 }">
			<a href="order!list.do?optype=monitor&status=1">查看</a>
			</c:if>
		</th>
		</tr>
		<tr>
		<th width=200>【${pildTotal }】订单需要下达配货任务</th>
		<th>
			<c:if test="${pildTotal>0 }">
			<a href="order!list.do?optype=monitor&status=2">查看</a>
			</c:if>
		</th>
		</tr>
	</thead> 
</table>	
	
<div style="clear:both;padding-top:5px;"></div>
</div>