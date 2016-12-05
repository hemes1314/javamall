<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<div class="toolbar">
	<strong>${tag.tag_name}</strong>
</div>
<div class="grid" id="gridDiv">
	

	<div class="toolbar">
	<ul>
	<!--li><a href="goodsShow!add.do?tagid=${tagid }&catid=${catid}">添加</a> </li-->
	<li><a href="goodsShow!search.do?tagid=${tagid }&catid=${catid}">添加</a></li>
	<li><a href="javascript:;" onclick="javascript:document.getElementById('bind_form').submit();">保存排序</a> </li>
	</ul>
	<div style="clear:both"></div>
	</div>
<form action="goodsShow!saveOrdernum.do" id="bind_form" method="post">		
<input type="hidden" name="tagid" value="${tagid }"/>
<input type="hidden" name="catid" value="${catid }"/>
<grid:grid  from="webpage">

	<grid:header>
	<grid:cell width="350px">商品名称</grid:cell>
	<grid:cell width="100px">排序</grid:cell>
	<grid:cell width="100px">删除</grid:cell>
	</grid:header>

  <grid:body item="goods">
  
     <grid:cell><a href="../../goods-${goods.goods_id }.html" target="_blank" >${goods.name }</a></grid:cell>
     <grid:cell> 
     	<input type="text" name="ordernum" value="${goods.ordernum}" class="input_text"/>
     	<input type="hidden" name="id" value="${goods.goods_id }"/>
     	<input type="hidden" name="tagids" value="${goods.tag_id }"/>
     </grid:cell>
      <grid:cell> 
     	<a href="goodsShow!delete.do?tagid=${goods.tag_id}&goodsid=${goods.goods_id}&catid=${catid}" onclick="javascript:return confirm('您确认要删除此条记录？');"><img class="delete" goods_id="${goods.goods_id }" src="images/transparent.gif" > </a>
     </grid:cell> 
       
  </grid:body>  
  
</grid:grid>  
</div>

</form>
