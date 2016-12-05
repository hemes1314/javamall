<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<script language="JavaScript" src="${staticserver }/js/admin/Tab.js"></script>
<script type="text/javascript">
$(function(){
	new Tab(".seeting_input");
	$(".tab li:first").addClass("active");
	$(".tab-page>div").hide();
	$(".tab-page div:first").show();
});
</script>
<div class="input">

<form method="post" name="theForm" action="setting!save.do" id="theForm">

<div style="display: block;" class="seeting_input">
	<div class="tab-bar" style="position: relative;">
		<c:if test="${showtab==null}">
			<ul class="tab">
				<c:forEach var="tab" items="${tabs}">
					<li id="${tab.key }">${tab.value }</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
<div class="tab-page">

   <c:forEach var="content" items="${htmls}" varStatus="status">
	 	  	<div tabid="${content.key }" <c:if test="${status.index!=0 }">style="display:none"</c:if>  class="tab-panel">
	 	  		${content.value }
	 	  	</div>
   </c:forEach>
	 	  
</div>

<div class="submitlist" align="center">
 <table>
 <tr><td >
  <input name="submit" type="submit" value="确定" class="submitBtn" />
   </td>
   </tr>
 </table>
</div>
</div>

</form>
</div>