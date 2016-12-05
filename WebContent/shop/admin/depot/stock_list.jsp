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
<form action="depotMonitor!listStock.do" method="get">
	<div class="toolbar" style="height:50px">	
	<div id="searchcbox" style="margin-left:10px">
		<div><strong>库存余量查询</strong></div>
		<div>&nbsp;&nbsp;&nbsp;&nbsp;商品名称:<input type="text" style="width:150px" name="name" value="${name }"/></div>
		
		<div>商品编号:<input type="text" style="width:150px" name="sn" value="${sn }"/></div>
		<div id="searchCat"></div>
		
		<input type="submit" name="submit" value="搜索">
	</div>
 		
<div style="clear:both"></div>
	</div>
</form>	
<grid:grid  from="webpage">

	<grid:header>
		<grid:cell sort="sn" width="120px">商品编号</grid:cell> 
		<grid:cell sort="name" >商品名称</grid:cell>
		<grid:cell sort="store"  width="100px">库存</grid:cell> 
		<grid:cell  >操作</grid:cell> 
	</grid:header>


  <grid:body item="goods" >
  
        <grid:cell>&nbsp;${goods.sn } </grid:cell>
        <grid:cell>&nbsp;<a href="/shop/goodsLink.do?goodsid=${goods.goods_id }" target="_blank" >${goods.name }</a></grid:cell> 
        <grid:cell>&nbsp;${goods.goodsStore} </grid:cell> 
        <grid:cell>
        	<a href="javascript:;" onclick="javascript:openDepotDlg(${goods.goods_id });">查看仓库库存情况</a>
        </grid:cell> 
         
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
	//alert(goodsid);
	var content = "<div id='store_box'>";
	content+="<div style='height:500px;overflow:auto;margin-top:5px;margin-left:10px' id='store_content'></div></div>";
	$("body").append(content);
	Eop.Dialog.init({id:"store_box",modal:true,title:"查看商品所有仓库库存情况",width:"500px",height:"650px",remove:true});
	Eop.Dialog.open("store_box");
	$("#store_content").load("depotMonitor!depotStock.do?ajax=yes&goodsid=" +goodsid,function(){});
	
}

</script>
