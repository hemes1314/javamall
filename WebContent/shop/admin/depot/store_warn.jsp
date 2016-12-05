<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="grid">
	<div class="toolbar">
		<ul>
			<li><strong>最新库存报警【${totalCount }】条</strong>	</li>
		</ul>
		<div style="clear: both"></div>
	</div>
	<grid:grid from="storeList">
		<grid:header>
			<grid:cell width="120px">商品编号</grid:cell>
			<grid:cell width="100px">价格</grid:cell>
			<grid:cell width="300px">商品名称</grid:cell>
			<grid:cell width="100px"><span style="color: red;">库存</span></grid:cell>
			<grid:cell width="100px">类别</grid:cell>
			<grid:cell width="100px">品牌</grid:cell>			
			<grid:cell width="100px">上架</grid:cell>
			<grid:cell width="100px">最后修改时间</grid:cell>
		</grid:header>
		<grid:body item="goods">
			<grid:cell>&nbsp;${goods.sn } </grid:cell>
			<grid:cell>&nbsp;${goods.price}</grid:cell>
			<grid:cell>&nbsp;<a	href="/shop/goodsLink.do?goodsid=${goods.goods_id }" target="_blank">${goods.name }</a>
			<div>
			<c:if test="${goods.cat_id == 18 || goods.cat_id == 3 || goods.cat_id == 4 || goods.cat_id == 12}"><span style="color: red;">[颜色：${goods.color} ]</span></c:if>
			<c:if test="${ goods.cat_id == 1 || goods.cat_id == 2 || goods.cat_id == 6 ||goods.cat_id == 19  }"><span style="color: red;">[度数：${goods.sphere} ]</span></c:if>
			<c:if test="${goods.cat_id == 6 }"><span style="color: red;">[散光：${goods.cylinder} ]</span></c:if>
			</div>
			</grid:cell>
			<grid:cell>&nbsp;<span style="color: red;">${goods.realstore}</span> </grid:cell>
			<grid:cell>&nbsp;${goods.catname}</grid:cell>
			<grid:cell>&nbsp;${goods.brandname}</grid:cell>
			<grid:cell>&nbsp;<c:if test="${goods.market_enable==0}"><span style="color: red;">否</span> </c:if><c:if test="${goods.market_enable==1}">是</c:if></grid:cell>
			<grid:cell>&nbsp;<html:dateformat pattern="yyyy-MM-dd" time="${goods.last_modify}"></html:dateformat></grid:cell>
		</grid:body>
	</grid:grid>
</div>