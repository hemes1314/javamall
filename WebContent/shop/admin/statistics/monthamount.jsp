<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%> 
	<div>
	    <form action="statistics!monthamount.do" method="post">
		<table>
			<tr>
				<td>
				请选择年份：
				</td>
				<td>
					<select name="year" >
					    <option value="2005" <c:if test="${year == '2005' }">selected</c:if> >2005年</option>
					    <option value="2006" <c:if test="${year == '2006' }">selected</c:if> >2006年</option>
					    <option value="2007" <c:if test="${year == '2007' }">selected</c:if> >2007年</option>
					    <option value="2008" <c:if test="${year == '2008' }">selected</c:if> >2008年</option>
					    <option value="2009" <c:if test="${year == '2009' }">selected</c:if> >2009年</option>
					    <option value="2010" <c:if test="${year == '2010' }">selected</c:if> >2010年</option>
					    <option value="2011" <c:if test="${year == '2011' }">selected</c:if> >2011年</option>
					    <option value="2012" <c:if test="${year == '2012' }">selected</c:if> >2012年</option>
					    <option value="2013" <c:if test="${year == '2013' }">selected</c:if> >2013年</option>
					    <option value="2014" <c:if test="${year == '2014' }">selected</c:if> >2014年</option>
					    <option value="2015" <c:if test="${year == '2015' }">selected</c:if> >2015年</option>
					</select>
				</td>
				<td>
				请选择月份：
				</td>
				<td>
					<select name="month" >
					    <option value="01" <c:if test="${month == '01' }">selected</c:if> >1月份</option>
					    <option value="02" <c:if test="${month == '02' }">selected</c:if> >2月份</option>
					    <option value="03" <c:if test="${month == '03' }">selected</c:if> >3月份</option>
					    <option value="04" <c:if test="${month == '04' }">selected</c:if> >4月份</option>
					    <option value="05" <c:if test="${month == '05' }">selected</c:if> >5月份</option>
					    <option value="06" <c:if test="${month == '06' }">selected</c:if> >6月份</option>
					    <option value="07" <c:if test="${month == '07' }">selected</c:if> >7月份</option>
					    <option value="08" <c:if test="${month == '08' }">selected</c:if> >8月份</option>
					    <option value="09" <c:if test="${month == '09' }">selected</c:if> >9月份</option>
					    <option value="10" <c:if test="${month == '10' }">selected</c:if> >10月份</option>
					    <option value="11" <c:if test="${month == '11' }">selected</c:if> >11月份</option>
					    <option value="12" <c:if test="${month == '12' }">selected</c:if> >12月份</option>
					</select>
				</td>
				<td><input type="submit" value="提交"/></td>
			</tr>
		</table>
		</form>
	</div>  
    <script language="javascript" type="text/javascript" src="js/highcharts.js"></script>
    <div style="width:99%;height:400px;margin-left:10px;" id="container"></div>
    <script language="javascript" type="text/javascript">
    var data11 = new Array();
    <c:forEach var="ma" items="${month_AmountList }" varStatus="state">  
    data11.push(${ma.amount}); 
    </c:forEach>
var chart;
$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'container',
			defaultSeriesType: 'column'
		},
		title: {
			text: '${year}全年 月-销售额统计图'
		},
		xAxis: {
			categories: [
				'一月份', 
				'二月份', 
				'三月份', 
				'四月份', 
				'五月份', 
				'六月份', 
				'七月份', 
				'八月份', 
				'九月份',
				'十月份',
				'十一月份',
				'十二月份' 
			],
			labels: {
				rotation: -45,
				align: 'right'
			}
		},
		yAxis: {
			min: 0,
			title: {
				text: '月销售额'
			}
		},
		legend: {
			layout: 'vertical',
			backgroundColor: '#FFFFFF',
			align: 'left',
			verticalAlign: 'top',
			x: 100,
			y: 70,
			floating: true,
			shadow: true
		},
		tooltip: {
			formatter: function() {
				return ''+
					this.x +': '+ this.y ;
			}
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
	        series: [{
			name: '月销售额',
			data: data11
			       
		}]
	});
	
	
});
   
</script>

<div style="width:99%;height:400px;margin-left:10px;" id="container1"></div>
    <script language="javascript" type="text/javascript">
    var data21 = new Array();
    var data22 = new Array();
    <c:forEach var="da" items="${day_AmountList }" varStatus="state_1">  
    data21.push('${da.day}'); 
    data22.push(${da.amount}); 
    </c:forEach>
var chart;
$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'container1',
			defaultSeriesType: 'column'
		},
		title: {
			text: '${year}年${month}月全月 日-销售额统计图 '
		},
		xAxis: {
			categories: data21,
			labels: {
				rotation: -45,
				align: 'right'
			}
		},
		yAxis: {
			min: 0,
			title: {
				text: '日销售额'
			}
		},
		legend: {
			layout: 'vertical',
			backgroundColor: '#FFFFFF',
			align: 'left',
			verticalAlign: 'top',
			x: 100,
			y: 70,
			floating: true,
			shadow: true
		},
		tooltip: {
			formatter: function() {
				return ''+
					this.x +': '+ this.y ;
			}
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
	        series: [{
			name: '日销售额',
			data: data22
			       
		}]
	});
	
	
});
   
</script>
 
   
 