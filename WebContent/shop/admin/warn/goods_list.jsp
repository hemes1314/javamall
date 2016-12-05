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
<div class="toolbar">
	<strong>紧急库存维护任务-搜索商品</strong>
</div>
		<form action="/shop/admin/warnTask.do" method="get">
	<input type="submit" name="submit" value="查看任务列表">
	</form>
<div class="grid">
	<form action="/shop/admin/warnTask!goodslist.do" method="get">
		<div class="toolbar" style="height: 50px">
			<div id="searchcbox" style="margin-left: 10px">
				&nbsp;&nbsp;&nbsp;&nbsp;

				<div>
					商品名称:<input type="text" style="width: 200px" name="name"
						value="${name }" />
				</div>

				<div>
					商品编号:<input type="text" style="width: 150px" name="sn"
						value="${sn }" />
				</div>
				<input type="submit" name="submit" value="查找商品">
			</div>

			<div style="clear: both"></div>
		</div>
	</form>

	<grid:grid from="webpage">
		<grid:header>
			<grid:cell sort="sn" width="120px">商品编号</grid:cell>
			<grid:cell sort="name">商品名称</grid:cell>
			<grid:cell>操作</grid:cell>
		</grid:header>


		<grid:body item="goods">

			<grid:cell>&nbsp;${goods.sn } </grid:cell>
			<grid:cell>&nbsp;<a
					href="/shop/goodsLink.do?goodsid=${goods.goods_id }"
					target="_blank">${goods.name }</a>
			</grid:cell>
			<grid:cell>
				<a href="javascript:;"
					onclick="javascript:openDlg(${goods.goods_id });">创建任务</a>
			</grid:cell>

		</grid:body>

	</grid:grid>
	<div style="clear: both; padding-top: 5px;"></div>
</div>
<script type="text/javascript">
function openDlg(goodsid){
	//alert(goodsid);
	var content = "<div id='store_box'>";
	content+="<div style='height:500px;overflow:auto;margin-top:5px;margin-left:10px' id='store_content'></div></div>";
	$("body").append(content);
	Eop.Dialog.init({id:"store_box",modal:true,title:"紧急库存维护任务-创建",width:"600px",height:"650px",remove:true});
	Eop.Dialog.open("store_box");
	var self = this;
	$("#store_content").load("warnTask!addTask.do?ajax=yes&goodsId=" +goodsid,function(){
		$("#submitbtn").click(function(){
			self.saveDlg();
		});
	});
	
}
function saveDlg(){
	var depots="";//仓库串
	$("input[name='depots']").each(function(){
		if($(this).attr("checked")){
			depots+=$(this).attr("value")+",";
		}
	});
	if(depots==""){
		alert("请选择仓库");
		return;
	}
	var sphere="";//度数
	var cylinder="";//散光
	
	if($("#spheresall").attr("checked")){
		sphere="all";
		$("input[name='cylindersall']").each(function(){
			if($(this).attr("checked")){
				cylinder+=$(this).attr("value")+",";
			}
		});
	}else{
		$("input[name='spheres']").each(function(){
			if($(this).attr("checked")){
				sphere+=$(this).attr("value")+",";
				var flag=$(this).attr("flag");
				$("input[name='cylinders"+flag+"']").each(function(){
					if($(this).attr("checked")){
						cylinder+=$(this).attr("value")+",";
					}
				});
				cylinder+="|";
			}
		});
	}
	var productids="";//颜色 productid串
	$("input[name='productids']").each(function(){
		if($(this).attr("checked")){
			productids+=$(this).attr("value")+",";
		}
	});
	/**
	if(sphere=="" && cylinder=="" && productids==""){
		alert("你的设置不正确!");
		return;
	}
	**/
	$("#depotval").val(depots);
	$("#sphereval").val(sphere);
	$("#cylinderval").val(cylinder);
	$("#productval").val(productids);
	$.Loading.show('正在保存，请稍候...');
	var options = {
			url : "warnTask!saveTask.do?ajax=yes",
			type : "POST",
			dataType : 'json',
			success : function(result) {		
				
				alert(result.message);
				$.Loading.hide();
				
			},
			error : function(e) {
				alert("出现错误 ，请重试");
				$.Loading.hide();
			}
		};

	$('#savetask').ajaxSubmit(options);
	Eop.Dialog.close("store_box");
}
</script>
