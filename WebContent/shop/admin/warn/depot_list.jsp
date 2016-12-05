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
<div class="grid">
	<form action="warnTask!listdepot.do" method="get">
		<div class="toolbar" style="height: 50px">
			<div id="searchcbox" style="margin-left: 10px">
				&nbsp;&nbsp;&nbsp;&nbsp;

				<div>
					商品名称:<input type="text" style="width: 150px" name="name"
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
	<div style="clear: both; padding-top: 5px; font-size: 14px;">紧急库存维护任务列表：(度数如果显示'all'，表示此商品的全部度数)</div>
	<hr width="70%" SIZE=1 align="left">
	<grid:grid from="webpage">
		<grid:header>
			<grid:cell sort="sn" width="120px">商品编号</grid:cell>
			<grid:cell sort="name">商品名称</grid:cell>
			<grid:cell sort="cat_id" width="100px">商品类别</grid:cell>
			<grid:cell sort="brand_id">商品品牌</grid:cell>
			<grid:cell>商品操作</grid:cell>
		</grid:header>


		<grid:body item="goods">

			<grid:cell>&nbsp;${goods.sn } </grid:cell>
			<grid:cell>&nbsp;<a
					href="/shop/goodsLink.do?goodsid=${goods.goods_id }"
					target="_blank">${goods.name }</a>
					<div>
			<c:if test="${goods.cat_id == 18 || goods.cat_id == 3 || goods.cat_id == 4 || goods.cat_id == 12}"><span style="color: red;">[颜色：${goods.color} ]</span></c:if>
			<c:if test="${ goods.cat_id == 1 || goods.cat_id == 2 ||goods.cat_id == 19  }"><span style="color: red;">[度数：${goods.sphere} ]</span></c:if>
			<c:if test="${goods.cat_id == 6 }"><span style="color: red;">${goods.glasses_sphere}</span></c:if>
			</div>
			</grid:cell>
			<grid:cell>&nbsp;${goods.cat_name} </grid:cell>
			<grid:cell>&nbsp;${goods.brand_name} </grid:cell>
			<grid:cell>
				<a href="javascript:;"
					onclick="javascript:openDlg(${goods.task_id });" id ="task${goods.task_id }">维护任务</a>
			</grid:cell>

		</grid:body>

	</grid:grid>
	<div style="clear: both; padding-top: 5px;"></div>
</div>
<script type="text/javascript">
function openDlg(taskid){
	//alert(goodsid);
	var content = "<div id='store_box'>";
	content+="<div style='height:500px;overflow:auto;margin-top:5px;margin-left:10px' id='store_content'></div></div>";
	$("body").append(content);
	Eop.Dialog.init({id:"store_box",modal:true,title:"紧急库存维护任务-维护",width:"500px",height:"650px",remove:true});
	Eop.Dialog.open("store_box");
	var self = this;
	$.Loading.show('正在加载，请稍候...');
	$("#store_content").load("/shop/admin/warnTask!maintaintask.do?ajax=yes&taskId=" +taskid,function(){
		$("#submitbtn").click(function(){
			self.saveDlg();
		});
	});
	$(".closeBtn").attr("class","tempCloseBtn");
	//$.Loading.hide();
}
function saveDlg(){
	var taskIds = $("#taskIdval").val();
	$.Loading.show('正在保存，请稍候...');
	var options = {
			url : "/shop/admin/warnTask!maintain.do?ajax=yes",
			type : "POST",
			dataType : 'json',
			success : function(result) {		
				if(result.result==1){
				    Boxy.ask("进货保存成功！<br>此商品所有的度数都已经完成了进货吗？<br>如果都已完成，请点'是'，此商品在您的进货列表中将不在出现。<br>如果没有完成，请点'否'，此商品依然保留在您的进货列表中。", ["是", "否"], function(val) {
				     	if(val=='是'){
				     		//GoodsStore.updateGoodsCmpl();
				     		$.ajax({
								url:"/shop/admin/warnTask!updateMaintain.do?ajax=yes&taskId="+taskIds,
								type : "POST",
								dataType:"json",
								success:function(result){
									if(result.result==1){
										//alert("已标记此商品为维护完成状态")
										Eop.Dialog.close("store_box");
										$("#task"+taskIds).parents("tr").remove();
									}else{
										alert("标记状态 失败:"+result.message);
									}				 
								},
								error:function(){
									alert("出错了:(");
								}
							});	
				     	}
				    }, {title: "是否完成了库存维护?",draggable:false});
			   		$(".boxy-wrapper").css("z-index","3001");
				}else{
					alert("进货发生错误"+ result.message);
				}
				$.Loading.hide();
				
			},
			error : function(e) {
				alert("出现错误 ，请重试");
				$.Loading.hide();
			}
		};

	$('#maintaintask').ajaxSubmit(options);
	Eop.Dialog.close("store_box");
}
</script>
