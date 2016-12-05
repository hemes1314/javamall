<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<div class="toolbar">
	<strong>${tag.tag_name}</strong>
</div>

<div class="input">
<form action="goodsShow!search.do" method="get">
<input type="hidden" name="tagid" value="${tagid }"/>
<c:if test="${catid != null && catid != 0}">
<input type="hidden" name="catid" value="${catid }"/>
</c:if>
<table cellspacing="1" cellpadding="3" width="100%" class="form-table">
<input type="hidden" name="tagids" value=""/>
	<tr>
		<th><label class="text">商品名称:</label></th>
		<td><input type="text" style="width:150px" name="name" value="${name }"/></td>
	</tr>
	<tr>
		<th><label class="text">商品编号:</label></th>
		<td><input type="text" style="width:150px" name="sn" value="${sn }"/></td>
	</tr>
	<c:if test="${catid == null || catid == 0}">
	<tr>
		<th><label class="text">商品分类:</label></th>
		<td><div id="searchCat"></div></td>
	</tr>
	</c:if>
	
</table>
<div class="submitlist" align="center">
<table>
	<tr>
		<td align="left"><input type="submit" name="submit" value="搜索">
		</td>
	</tr>
</table>
</form>
</div>
<c:if test="${catid == null || catid == 0}">
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
</c:if>