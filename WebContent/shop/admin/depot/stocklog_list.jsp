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
<form action="depotMonitor!listStockLog.do" method="get" class="validate" >
	<div class="toolbar" style="height:50px">	
	<div id="searchcbox" style="margin-left:10px">
		&nbsp;&nbsp;&nbsp;&nbsp;
		
		<div>开始日期:<input type="text" name="startDate"   dataType="date" isrequired="true" class="dateinput" value="${startDate }" /></div>
		
		<div>结束日期:<input type="text" name="endDate"   dataType="date" isrequired="true" class="dateinput" value="${endDate }" /></div>
		<div>仓库:<select name="depotid">
		<option value="0">全部</option>
		<c:forEach var="depot" items="${list }">
		<option value="${depot.id }" <c:if test="${depotid==depot.id }">selected</c:if> >${depot.name }</option>
		</c:forEach>
		</select></div>
		<div>仓库类型:<select name="depotType"><option value="-1">不限</option><option value="0" <c:if test="${depotType==0 }">selected</c:if>>网站</option><option value="1" <c:if test="${depotType==1 }">selected</c:if>>实体店</option></select></div>
		<div>操作类型:<select name="opType"><option value="-1">不限</option><option value="0" <c:if test="${opType==0 }">selected</c:if>>进货</option><option value="1" <c:if test="${opType==1 }">selected</c:if>>实体店出货</option><option value="2" <c:if test="${opType==2 }">selected</c:if>>网店订单发货</option></select></select></div>
		
		<input type="submit" name="submit" value="搜索">
	</div>
 		
<div style="clear:both"></div>
	</div>
</form>	
<grid:grid  from="webpage">

	<grid:header>
		<grid:cell sort="name" >商品名称</grid:cell>
		<grid:cell sort="num"  width="100px">数量</grid:cell>
		<grid:cell sort="op_type"  width="100px">操作类型</grid:cell>
		<grid:cell sort="depot_type"  width="100px">仓库类型</grid:cell>
		<grid:cell sort="depotname"  width="100px">仓库</grid:cell>
		<grid:cell sort="username"  width="100px">操作员</grid:cell>    
		<grid:cell sort="dateline"  width="200px">日期</grid:cell> 
	</grid:header>


  <grid:body item="item" >
  
        <grid:cell>&nbsp;<a href="/shop/goodsLink.do?goodsid=${item.goodsid }" target="_blank" >${item.name }</a></grid:cell> 
        <grid:cell>&nbsp;${item.num} </grid:cell> 
        <grid:cell>&nbsp;<c:if test="${item.op_type==0}">进货</c:if><c:if test="${item.op_type==1}">实体店出货</c:if><c:if test="${item.op_type==2}">网店订单发货</c:if> </grid:cell> 
        <grid:cell>&nbsp;<c:if test="${item.depot_type==0}">网站</c:if><c:if test="${item.depot_type==1}">实体店</c:if> </grid:cell> 
        <grid:cell>&nbsp;${item.depotname} </grid:cell> 
        <grid:cell>&nbsp;${item.username} </grid:cell> 
        <grid:cell>&nbsp;<html:dateformat pattern="yyyy-MM-dd" time="${item.dateline*1000}"></html:dateformat> </grid:cell>          
  </grid:body>
  
</grid:grid>
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
function openDepotDlg(goodsid){
	var content = "<div id='store_box'>";
	content+="<div style='height:500px;overflow:auto;margin-top:5px;margin-left:10px' id='store_content'></div></div>";
	$("body").append(content);
	Eop.Dialog.init({id:"store_box",modal:true,title:"查看商品所有仓库库存情况",width:"500px",height:"650px",remove:true});
	Eop.Dialog.open("store_box");
	$("#store_content").load("../shop/admin/depotMonitor!depotStock.do?ajax=yes&goodsid=" +goodsid,function(){});
	
}

</script>
