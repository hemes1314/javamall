<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="grid">
<grid:grid  from="listComments">

	<grid:header>
	<grid:cell width="400px">内容</grid:cell> 
	<grid:cell >发表时间</grid:cell>
	<grid:cell >显示状态</grid:cell> 
	</grid:header>

  <grid:body item="comments">
        <grid:cell><c:out value="${fn:substring(comments.content, 0, 25)}" /></grid:cell>  
        <grid:cell><html:dateformat pattern="yyyy-MM-dd HH:mm" time="${comments.dateline}"></html:dateformat></grid:cell>
        <grid:cell > 
        	<c:if test="${comments.status == 0 }">待审核</c:if>
        	<c:if test="${comments.status == 1 }">审核通过</c:if>
        	<c:if test="${comments.status == 2 }">审核拒绝</c:if>
        </grid:cell>
  </grid:body>  
  
</grid:grid>
</div>