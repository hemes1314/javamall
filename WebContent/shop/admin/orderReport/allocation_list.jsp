<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<style>
.other {text-align:left;width:500px;color:#f59806}
.other li{text-align:left;width:500px;float:left;display:block}
</style>
<div class="grid">
	
<form method="POST">
<grid:grid  from="webpage">

	<grid:header>
	<grid:cell   width="250px">订单号</grid:cell> 
	<grid:cell >状态</grid:cell>
	<grid:cell >商品名称</grid:cell>
	<grid:cell >扩展信息</grid:cell>
	<grid:cell >数量</grid:cell>
	<grid:cell>操作</grid:cell> 
	</grid:header>

  <grid:body item="allocation">
        <grid:cell>${allocation.sn } </grid:cell>
        <grid:cell><html:orderstatus status="${allocation.status}" type="order"></html:orderstatus> </grid:cell>
        <grid:cell>${allocation.name}
        
		</grid:cell>
		<grid:cell>&nbsp;${allocation.other} </grid:cell>
        <grid:cell>${allocation.num} </grid:cell>
        <grid:cell> <c:if test="${allocation.iscmpl==0 }"><input type="button" name="allocation_btn" value="配货完成" allocationid="${allocation.allocationid}" orderid="${allocation.orderid}"   /></c:if> </grid:cell>
  </grid:body>  
  
</grid:grid>  
</form>	
</div>
<script type="text/javascript">
<!--

$(function(){
	
	$("input[name='allocation_btn']").click(function(){
		allocationFinish($(this));
	});
});

function allocationFinish(btn){
	
	var id = btn.attr("allocationid");
	var orderId= btn.attr("orderid");
	//alert(orderId+"ddd"+id);
	if(confirm("是否确认完成配货？")){
		$.Loading.show('请稍侯...');
		$.ajax({
			url:basePath+'ship!allocationFinish.do?ajax=yes&id='+id+'&orderId='+orderId,
			dataType:"json",
			success:function(responseText){
				
				$.Loading.hide();
				if(responseText.result==1){
					btn.parents("tr").remove();
					parent.ShortMsg.checkNewMsg();
					alert(responseText.message);
					
				}
				if(responseText.result==0){
					alert(responseText.message);
				}		
				
			},
			error:function(){
				$.Loading.hide();
				alert("出错了:(");
			}
		});
	}
}
//-->
</script>

