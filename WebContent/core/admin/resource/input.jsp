<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>

<div class="input">

 
<div class="tab-page">
 	<c:if test="${haveNewDisplaoy }" >
 		<span style="color:red">当前有新部署状态</span>
 	</c:if>
 	
 	<c:if test="${!haveNewDisplaoy }" >
 		当前没有新部署状态
 	</c:if>
 	
 	
</div>

<div class="submitlist" align="center">
 <table>
 	<tr>
 	<td >
	  <input name="button" type="button" value="更新为有新的部署" class="submitBtn" />
   </td>
   </tr>
 </table>
</div>


</div>

<script>
$(function(){
	$(".submitBtn").click(function(){
		$.ajax({
			url:'resourceState!save.do?ajax=yes',
			dataType:"json",
			success:function(result){
				if(result.result==1){
					location.reload();	
				}else{
					alert(result.message);
				}
				
			},
			error:function(){
				alert("更新出现意外错误");
			}
		});
	});
});
</script>