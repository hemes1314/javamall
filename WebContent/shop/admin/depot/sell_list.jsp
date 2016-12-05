<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp"%>
<script language="JavaScript" src="depot/FusionCharts.js"></script>
<style>
#tagspan{
	position: absolute;
	display:none;
}
#searchcbox{float:left}
#searchcbox div{float:left;margin-left:10px}
</style>
<script type="text/javascript">
	$(function() {
		$("form.validate").validate();
	});
</script>
<div class="grid">
<table width="98%" border="0" cellspacing="0" cellpadding="3" align="center">

  <tr> 
    <td valign="top" class="text" align="center" colspan="2"> <div id="chartdiv" align="center"> 
        FusionCharts. </div>
      <script type="text/javascript">
		   var chart = new FusionCharts("depot/Line.swf", "ChartId", "100%", "300", "0", "0");
		   var dataxml = "<chart caption='' subcaption='' xAxisName='日期' yAxisName='数量' yAxisMinValue='15000'  numberPrefix='' showValues='0' alternateHGridColor='FCB541' alternateHGridAlpha='20' divLineColor='FCB541' divLineAlpha='50' canvasBorderColor='666666' baseFontColor='666666' lineColor='FCB541'>";
		   <c:forEach var="item" items="${listTask }">
		   dataxml+="<set label='${item.isdate }' value='${item.amount }' />";
		   </c:forEach>
		   dataxml+="<styles><definition><style name='Anim1' type='animation' param='_xscale' start='0' duration='1' /><style name='Anim2' type='animation' param='_alpha' start='0' duration='0.6' /><style name='DataShadow' type='Shadow' alpha='40'/></definition><application><apply toObject='DIVLINES' styles='Anim1' /><apply toObject='HGRID' styles='Anim2' /><apply toObject='DATALABELS' styles='DataShadow,Anim2' /></application></styles></chart>";
		   chart.setDataXML(dataxml);		   
		   chart.render("chartdiv");
		</script> </td>
  </tr>

</table>
<form action="depotMonitor!listSell.do" method="get" class="validate" >
	<div class="toolbar" style="height:50px">	
	<div id="searchcbox" style="margin-left:10px">
		&nbsp;&nbsp;&nbsp;&nbsp;
		
		<div>开始日期:<input type="text" name="startDate"   dataType="date" isrequired="true" class="dateinput" value="${startDate }" /></div>
		
		<div>结束日期:<input type="text" name="endDate"   dataType="date" isrequired="true" class="dateinput" value="${endDate }" /></div>
		
		<input type="submit" name="submit" value="搜索">
	</div>
 		
<div style="clear:both"></div>
	</div>
</form>	
	
<table>
	<thead>
		<tr>
		<th width=150>日期</th>
		<th>销售额</th>
		</tr>
	</thead> 
	<tbody>
	<c:forEach var="item" items="${listTask }">
		<tr>
		<td>${item.isdate } </td>
		<td>￥${item.amount }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>	
	
<div style="clear:both;padding-top:5px;"></div>
</div>