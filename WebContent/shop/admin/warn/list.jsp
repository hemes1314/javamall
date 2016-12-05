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
	<strong>紧急库存维护任务</strong>
</div>

<div class="grid">
<div>
<form action="warnTask!goodslist.do"><input type="submit" value="创建紧急库存任务"/></form>
</div>

	<div style="clear: both; padding-top: 5px; font-size: 14px;">各仓库维护任务列表：(度数如果显示'all'，表示此商品的全部度数)</div>
	<hr width="70%" SIZE=1 align="left">
	<grid:grid from="webpage">
		<grid:header>
			<grid:cell>仓库</grid:cell>
			<grid:cell>商品编号</grid:cell>
			<grid:cell>商品名称</grid:cell>
			<grid:cell>商品类别</grid:cell>
			<grid:cell>任务状态</grid:cell>
		</grid:header>


		<grid:body item="goods">
			<grid:cell>&nbsp;${goods.depotname } </grid:cell>
			<grid:cell>&nbsp;${goods.sn } </grid:cell>
			<grid:cell>&nbsp;<a	href="/shop/goodsLink.do?goodsid=${goods.goodsid }"
					target="_blank">${goods.name }</a>
					<div>
			<c:if test="${goods.catid == 18 || goods.catid == 3 || goods.catid == 4 || goods.catid == 12}"><span style="color: red;">[颜色：${goods.color} ]</span></c:if>
			<c:if test="${ goods.catid == 1 || goods.catid == 2 ||goods.catid == 19  }"><span style="color: red;">[度数：${goods.sphere} ]</span></c:if>
			<c:if test="${goods.catid == 6 }"><span style="color: red;">${goods.glasses_sphere}</span></c:if>
			</div>
			</grid:cell>
			<grid:cell>&nbsp;${goods.catname } </grid:cell>
			<grid:cell>
				<c:if test="${goods.flag == 1 }">未维护</c:if>
				<c:if test="${goods.flag == 2 }">已维护</c:if>
			</grid:cell>
		</grid:body>
	</grid:grid>
</div>

