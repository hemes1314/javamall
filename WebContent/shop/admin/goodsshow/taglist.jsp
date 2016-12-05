<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<script type="text/javascript" src="js/Brand.js"></script>

<div class="grid">
 <table>

	<thead><tr>

	<th width='350px'>标签</th>
	<th width='100px'>商品设置</th>
	</tr></thead>

  <tbody>
  <c:forEach items="${taglist }" var="tag">
  <tr>
  
     <td >${tag.tag_name}</td>

     <td > 
      	<a href="goodsShow.do?tagid=${tag.tag_id} " >设置</a>
     </td>
       
  </tr></c:forEach>
	 
	<div style="clear: both; padding-top: 5px;"></div>
</div>