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
<div class="grid">
<form id="savetask" action="warnTask!saveTask.do" method="post" >
<input type="hidden" name="goodsId" value="${goods.goods_id }"/>
	<table class="form-table" style="width: 100%; float: left">

		<tr>
			<th width="80">选择仓库：</th>
			<td>
			<c:forEach items="${listDepot}" var="item">
					<input type="checkbox" name="depots" value="${item.id }" />${item.name }
			</c:forEach>
			</td>
		</tr>
		<c:if test="${listSphere!=null }">
			<tr>
				<th>选择度数：</th>
				<td>
					<div>
						<input type="checkbox" id="spheresall" name="spheres" flag="all"
							value="all" />全部
							<c:if test="${listCylinder!=null }">
									<div class="CylinderPanel" id="CylinderPanelall" style="padding-left: 20px;">
										<c:forEach items="${listCylinder}" var="item1" >
											<div style="float: left; width: 60px;">
												<input type="checkbox"  name="cylindersall"
													value="${item1}" />${item1}
											</div>
										</c:forEach>
									</div>
								</c:if>
					</div>
					<div id="spheresother">
						<c:forEach items="${listSphere}" var="item" varStatus="status">
							<div>
								<input type="checkbox" name="spheres" value="${item}" flag="${status.index }"/>${item}
								<c:if test="${listCylinder!=null }">
									<div class="CylinderPanel" id="CylinderPanel${status.index}" style="padding-left: 20px;">
										<c:forEach items="${listCylinder}" var="item1" >
											<div style="float: left; width: 60px;">
												<input type="checkbox"  name="cylinders${status.index}"
													value="${item1}" />${item1}
											</div>
										</c:forEach>
									</div>
								</c:if>
							</div>
						</c:forEach>
					</div>
				</td>
			</tr>
		</c:if>

		<c:if test="${listColor!=null }">
			<tr>
				<th>选择颜色：</th>
				<td><c:forEach items="${listColor}" var="item">
						<input type="checkbox" name="productids" value="${item.productid}" />${item.color}
			</c:forEach></td>
			</tr>
		</c:if>
		<tr>
			<th>&nbsp;</th>
			<td>
			<input style="margin-left:30%" type="button" id="submitbtn" value="保存"></td>
		</tr>
	</table>
	<input type="hidden" id="depotval" name="depotval" value=""/>
	<input type="hidden" id="sphereval" name="sphereval" value=""/>
	<input type="hidden" id="cylinderval" name="cylinderval" value=""/>
	<input type="hidden" id="productval" name="productval" value=""/>
	</form>
	<div style="clear: both; padding-top: 5px;"></div>
</div>
<script type="text/javascript">
	$(function() {
		$(".CylinderPanel").hide();
		$("#spheresall").click(function() {
			if ($(this).attr("checked")) {
				$("#CylinderPanelall").show();
				$("#spheresother").hide();
			} else {
				$("#CylinderPanelall").hide();
				$("#spheresother").show();
			}
		});
		$("input[name='spheres']").click(function() {
			if ($(this).attr("checked")) {
				$("#CylinderPanel"+$(this).attr("flag")).show();
			} else {
				$("#CylinderPanel"+$(this).attr("flag")).hide();
			}
		});
		
	});
</script>
