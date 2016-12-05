<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<style>
#tagspan{
	position: absolute;
	display:none;
}
#searchcbox{float:left}
#searchcbox div{float:left;margin-left:10px}
</style>

<div class="grid">
<form action="depotMonitor!listSellNum.do" method="get" class="validate" >
	<div class="toolbar" style="height:50px">	
	<div id="searchcbox" style="margin-left:10px">
		&nbsp;&nbsp;&nbsp;&nbsp;
		
		<div>开始日期:<input type="text" name="startDate"   dataType="date" isrequired="true" class="dateinput" value="${startDate }" /></div>
		
		<div>结束日期:<input type="text" name="endDate"   dataType="date" isrequired="true" class="dateinput" value="${endDate }" /></div>
		<div id="searchCat"></div>
		
		<input type="submit" name="submit" value="搜索">
	</div>
 		
<div style="clear:both"></div>
	</div>
</form>	
	
<table>
	<thead>
		<tr>
		<th width=40%>商品</th>
		<th>销售量</th>
		</tr>
	</thead> 
	<tbody>
	<c:forEach var="item" items="${listTask }">
		<tr>
		<td><a href="/shop/goodsLink.do?goodsid=${item.goods_id }" target="_blank" >${item.name }</a> </td>
		<td>${item.total }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>	
	
<div style="clear:both;padding-top:5px;"></div>
</div>
<script type="text/javascript">

$(function(){
	$.ajax({
		url:basePath+'goods!getCatTree.do?ajax=yes',
		type:"get",
		dataType:"html",
		success:function(html){
			 
			var serachCatSel =$(html).appendTo("#searchCat");
			serachCatSel.removeClass("editinput").attr("name","catid");
			serachCatSel.children(":first").before("<option value=\"\" selected>全部类别</option>");
			<c:if test="${catid!=null}">serachCatSel.val(${catid})</c:if>
		},
		error:function(){
			alert("获取分类树出错");
		}
	});

	
});

</script>