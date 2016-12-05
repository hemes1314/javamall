<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<link href="/adminthemes/default/css/new_dialog.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/Order.js"></script>
<script language="javascript" src="http://www.lodop.net/uploads/file/sample/LodopFuncs.js"></script>
<object  id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
       <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
</object>
 

<div class="grid">
	<form action="order-print!notShipOrder.do" method="get">
		<div class="toolbar"  >
		
			<div style="width: 100%; float: left; height: 25px;">
				<ul>
 					<li><a href="javascript:;" id="searchBtn">高级搜索</a></li>
					<li><a href="javascript:;" id="printShipBtn">打印发货单</a></li>
					<li><a href="javascript:;" id="printExpressBtn">打印快递单</a></li>
					<li><a href="javascript:;" id="scanExpressNo">填写快递单号</a></li>
					<li><a href="javascript:;" id="shipBtn">发货</a></li>
					<input type="radio" name="printtype" value="0" checked="checked">直接打印&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="radio" name="printtype" value="1">预览打印&nbsp;&nbsp;&nbsp;&nbsp;
					 <input type="radio" name="printtype" value="2">设计打印&nbsp;&nbsp;&nbsp;&nbsp;
				</ul>
			</div>
	
			<div id="searchcbox"  style="display:none;position: absolute;background-color: #fff;border:1px solid #ccc;left:0;top:0;width:400px">
				
			<div class="input">
		 
			<table cellspacing="1" cellpadding="3" width="100%" class="form-table">
			
			<html:permission actid="finance,depot_ship">
				<tr>
					<th><label class="text">站点:</label></th>
					<td>
					
				 	<select name="siteid">
				 		<option value="">所有</option>
				 		<option value="2" <c:if test="${params.siteid==2 }">selected="selected"</c:if>>代理平台</option>
				 		<option value="3" <c:if test="${params.siteid==3 }">selected="selected"</c:if>>星空私服</option>
				 	</select>
					
					</td>
				</tr>
			</html:permission>
				
				<tr>
					<th><label class="text">订单号:</label></th>
					<td><input type="text" style="width: 150px" name="sn" value="${params.sn }" /></td>
				</tr>
				<tr>
					<th><label class="text">物流单号:</label></th>
					<td><input type="text" style="width: 150px" name="ship_no" value="${params.ship_no }" /></td>
				</tr>
				<tr>
					<th><label class="text">订购人：</label></th>
					<td><input type="text" style="width: 150px" name="member_name" value="${params.member_name }" /> </td>
				</tr>
				<tr>
					<th><label class="text">收货人：</label></th>
					<td><input type="text" style="width: 150px" name="ship_name" value="${params.ship_name }" /></td>
				</tr>
				
				<tr>
					<th><label class="text">下单时间：</label></th>
					<td>
					<input type="text" style="width: 100px" name="start" value="${params.start }" class="dateinput" />--<input type="text" style="width: 100px" name="end" value="${params.end }" class="dateinput"/>
					
					</td>
				</tr>
						
				<tr>
					<th><label class="text">发货时间：</label></th>
					<td>
					<input type="text" style="width: 100px" name="ship_start" value="${params.ship_start }" class="dateinput" />--<input type="text" style="width: 100px" name="ship_end" value="${params.ship_end }" class="dateinput"/>
					
					</td>
				</tr>
				
								
				<tr>
					<th><label class="text">订单状态：</label></th>
					<td> 
					 <select name="order_state" style="width: 150px;height:25px;font-size:14px;font-weight: bold;">
					<option  value="">所有状态</option>
				
					<option <c:if test="${params.order_state == '0' }">selected="selected" </c:if>  value="0">未付款（新订单）</option>
					<option <c:if test="${params.order_state == '2' }">selected="selected" </c:if>  value="2">已确认支付</option>
					<option <c:if test="${params.order_state == 'wait_ship' }">selected="selected" </c:if>  value="wait_ship">待发货</option>
					<option <c:if test="${params.order_state == '5' }">selected="selected" </c:if>  value="5">已发货</option>
					<option <c:if test="${params.order_state == '6' }">selected="selected" </c:if>  value="6">已收货</option>
					<option <c:if test="${params.order_state == '8' }">selected="selected" </c:if>  value="8">作废</option>
					<option <c:if test="${params.order_state == '9' }">selected="selected" </c:if>  value="9">订单已确认</option>
					<option <c:if test="${params.order_state == '-2' }">selected="selected" </c:if>  value="-2">已退货</option>
				</select>
					</td>
				</tr>
 
 				<tr>
					<th><label class="text">配送方式：</label></th>
					<td>
					<select name="shiptype">
						<option value="">全部</option>
						<c:forEach items="${shipTypeList }" var="shipType">
						<option value="${shipType.type_id }" <c:if test="${shiptype==shipType.type_id }">selected="selected"</c:if> >${shipType.name }</option>
						</c:forEach>
					</select>	
					
					
					</td>
				</tr>
				<tr>
					<th><label class="text">支付方式：</label></th>
					<td>
					<select name="paytype">
						<option value="">全部</option>
						<c:forEach items="${payTypeList }" var="payType">
						<option value="${payType.id }"  <c:if test="${paytype==payType.id }">selected="selected"</c:if> >${payType.name }</option>
						</c:forEach>
					</select>	
					</td>
				</tr>
			</table>
			<div class="submitlist" align="center">
			<table>
				<tr>  
					<td>
					<input type="submit" name="submit" value="搜索" class="submitBtn" />
					&nbsp;&nbsp;<input type="button" id="closeBtn"  value="取消" class="submitBtn" />
					</td>
				</tr>
			</table>
			</div>
		 
			</div>
				
				
			</div>
			
			<div style="clear: both"></div>
		</div>

   </form>
<div class="input">
	<table cellspacing="1" cellpadding="3" width="100%" class="form-table" >
     <tr> 
      
       <td>
      	共找到订单:${webpage.totalCount}个&nbsp;&nbsp;&nbsp;&nbsp;合计${ params.amount}元
        
	   </td>
     </tr>
   
   </table>
</div>
<form id="print_form">
		<grid:grid from="webpage">

			<grid:header>
				<grid:cell width="30px">
					<input type="checkbox" id="toggleChk" />
				</grid:cell>
				<grid:cell sort="sn" width="110px">订单号&nbsp;&nbsp;<span
						class="help_icon" helpid="order_showdetail"></span>
				</grid:cell>
				<grid:cell sort="create_time">下单日期</grid:cell>
				<grid:cell>订单总额</grid:cell>
				<grid:cell>收货人</grid:cell>
				<grid:cell>状态</grid:cell>
				<grid:cell sort="shipping_id">配送方式</grid:cell>
				<grid:cell>支付方式</grid:cell>
				<grid:cell>站点来源</grid:cell>
				<grid:cell>类型</grid:cell>
				<grid:cell>操作</grid:cell>
			</grid:header>

			<grid:body item="order">
				<grid:cell>
					<input type="checkbox" name="orderid" value="${order.order_id }" autocomplete="off"/>
				</grid:cell>
				<grid:cell>
					<a href="../../shop/admin/order!detail.do?orderId=${order.order_id}&sn=${sn}&logi_no=${logi_no}&uname=${uname}&ship_name=${ship_name}&status=${status}" title="订单详细">
						${order.sn } </a>
				</grid:cell>
				<grid:cell>
					<html:dateformat pattern="yyyy-MM-dd" time="${order.create_time}"></html:dateformat>
				</grid:cell>
				<grid:cell>
					<fmt:formatNumber value="${order.order_amount}" type="currency" pattern="￥.00" />
				</grid:cell>
				<grid:cell>
					${order.ship_name}
				</grid:cell>
				<grid:cell>
					
					${order.orderStatus}
					
				</grid:cell>
				<grid:cell>
					${order.shipping_type}
					
				</grid:cell>
				<grid:cell>
					${order.payment_name}
					
				</grid:cell>
				<grid:cell>
					<a href="../../shop/admin/order!detail.do?orderId=${order.order_id}&sn=${sn}&logi_no=${logi_no}&uname=${uname}&ship_name=${ship_name}&status=${status}" title="订单详细"><img
						class="modify" src="images/transparent.gif"> </a>
				</grid:cell>
				
			</grid:body>

		</grid:grid>
	</form>
	<div style="clear: both; padding-top: 5px;"></div>
</div>
<script type="text/javascript">

function sanEvent(){
	$("#autoinc").click(function(){
		$("#expressno_form [name=expressno]:first").focus();
	});
	$("#expressno_form [name=expressno]").focus(function(e){
		$(this).select();
	}).keydown(function(e){
		var curr=this;
		if(e.keyCode==13){
			var  autoinc = $("#autoinc:checked").size()>0?true:false;
			var ship_type=  $("#autoinc").attr("ship_type");
			var no= ($(this).val());
			var index=0;
			  $("#expressno_form [name=expressno]").each(function(){
				  index++;
				if(autoinc){
					if("3"==ship_type){
						no =cretaeZJS(""+no);
					}
					if("2"==ship_type){
						no =parseInt(no)+1;
					}
					
					$("#expressno_form [name=expressno]:eq("+index+")").val(no);
				}else{
					if(this==curr){
						 $("#expressno_form [name=expressno]:eq("+index+")").select();
						 return;
					}	
				}
				
				
			});    
		}
	});
	
	$("#expressno_form [name=expressno]:first").focus();
}



/*
 * 宅急送的号码规则：倒数第2位10进制，最后一位6进制
 */
function cretaeZJS(no){
	var newNo = parseInt( no.substring(0,no.length-1) ) *10;
	
	var last  =  parseInt(no) -newNo;
	last++;
	if(last>6){
		last=0;
	}
	newNo=newNo+1*10;
	return newNo+last;
}
 
//saveShipNo
//保存发货单号
function saveShipNoEvent(){
	$("#saveNoBtn").click(function(){
		$("#expressno_form").ajaxSubmit({
			url:"/shop/admin/orderPrint!saveShipNo.do?ajax=yes",
			dataType:"json",
			success:function(res){
					alert(res.message);
			},
			error:function(){
				alert("发生意外错误");
			}
		});
	});
}
 
$(function(){
	
	$("#toggleChk").click(function(){
		$("[name=orderid]").attr("checked",this.checked);
	});
	
	
	$("#scanExpressNo").click(function(){
		
		if( $("[name=orderid]:checked").size() ==0){
			alert("请选择订单");
			return false;
		}
		
		
		var dialog=	$.dialog({ title:'填写快递单号',lock:false,width:550,self:true});
		
		$("#print_form").ajaxSubmit({
			url:"/wine/admin/order-print!listForExpressNo.do?ajax=yes&order=${params.order}",
			success:function(html){
				dialog.content(html);
				sanEvent();
				saveShipNoEvent();
			},
			cache:false
			,  error:function(){
				dialog.content("加载失败");
		    }
			
		});
		
		
		 
	});
	
	$("#printShipBtn").click(function(){
		
		if( $("[name=orderid]:checked").size() ==0){
			alert("请选择要打印的订单");
			return false;
		}
		

		 $.Loading.show("请稍后...");
		$("#print_form").ajaxSubmit({
			url:"/shop/admin/orderPrint!shipScript.do?ajax=yes",
			success:function(json){
				
				 LODOP=getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM') );
				 LODOP.PRINT_INIT("发货单打印");
				 LODOP.SET_PRINT_PAGESIZE(1,2400,1400,"");
				
				 eval(json);
				 LODOP.PREVIEW();
				 $.Loading.hide();
			}
		});
	});
	
	
	$("#printExpressBtn").click(function(){
		
		if( $("[name=orderid]:checked").size() ==0){
			alert("请选择要打印的订单");
			return false;
		}
         var printtype = $("[name=printtype]:checked").val();
         
		 $.Loading.show("请稍后...");
		$("#print_form").ajaxSubmit({
			url:"/shop/admin/orderPrint!expressScript.do?ajax=yes",
			success:function(json){
				
				 LODOP=getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM') );
				 LODOP.PRINT_INIT("快递单打印");
				 
				
				 eval(json);
				 
				 if("0"==printtype)
				 	LODOP.PRINT();
				 
				 if("1"==printtype)
					 LODOP.PREVIEW();
				 
				 if("2"==printtype)
					 LODOP.PRINT_DESIGN();
				 
				 $.Loading.hide();
			}
		});
	});
	
	$("#shipBtn").click(function(){
		if( $("[name=orderid]:checked").size() ==0){
			alert("请选择订单");
			return false;
		}
		
		if(confirm("确认发货？")){
			 $.Loading.show("请稍后...");
			$("#print_form").ajaxSubmit({
				url:"/shop/admin/orderPrint!ship.do?ajax=yes",
				dataType:"json",
				success:function(json){
					 $.Loading.hide();
					 if(json.result==1){
						 alert("发货成功");
						 location.reload();
					 }else{
						 alert(json.message);
					 }
					
				},
				error:function(){
					alert("出现意外错误");
				}
			});
		}
	});
	
	$("#searchBtn").click(function(){
		$("#searchcbox").show();
	});
	
	$("#closeBtn").click(function(){
		$("#searchcbox").hide();
	});
});
</script>
