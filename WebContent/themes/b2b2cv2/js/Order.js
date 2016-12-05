var Order={
	init:function(){
		var self = this;
		//取消订单
		$(".cancelBtn").click(function(){
			var sn = $(this).attr("sn");
 			var html = $("#cancelForm").html();
 			var ordersn= $.trim("<div class='order_delsn'><span>订单号：</span><p>"+sn+"</p></div>");
			var dialog = $.dialog({
				title:"取消订单",content:ordersn+html,lock:true,
			});
			$(".ui_content .yellow_btn").jbtn().click(function(){
				var orderdelword = $(".ui_content").find("textarea ").val();
				if(orderdelword.length > 100){
					alert("输入的内容最多不能超过100个字符");	
					return false;
				}
				$.Loading.show("正在取消您的订单，请稍候...");
				$('#cancelForm').ajaxSubmit({
					url:ctx+"/api/shop/order!cancel.do?sn="+sn+"&reason="+orderdelword,
					type : "POST",
					dataType : 'json',
					success : function(data) {	
						if(data.result==1){
							alert("取消订单成功");
						}
						else{
							alert(data.message);
						}
						window.location.reload();
					},
					 cache:false
				});	
	    	});
		});
		
		//确认收货
		$(".rogBtn").click(function(){

			var orderId = $(this).attr("orderid");
			if( confirm( "请您确认已经收到货物再执行此操作！" )){
				$.Loading.show("请稍候..."); 
				$.ajax({
					url:"../api/shop/order!rogConfirm.do?orderId="+orderId+"&qrshstatus=7",
					dataType:"json",
					success:function(result){
						if(result.result==1){
							location.reload();
						}else{
							 
							$.alert(result.message);
							$.Loading.hide();
						}
						
					},
					error:function(){
						$.alert("出错了:(");
					}
				});	
						
			}
		});
		//解冻积分
		$(".thawBtn").click(function(){
			var orderid = $(this).attr("orderid");
			$.confirm("<div class='thaw_order' style='width:450px;'>提前解冻积分后，被冻结积分相关的订单商品，将不能进行退换货操作。确认要解冻吗？</div>",
				function(){
					$.Loading.show("请稍候..."); 
					$.ajax({
						url:"../api/shop/returnorder!thaw.do?orderid="+orderid,
						dataType:"json",
						cache:false,
						success:function(result){
							if(result.result==1){
								location.reload();
							}else{
								$.Loading.hide(); 
								$.alert(result.message);
							}
						},error:function(){
							$.Loading.hide(); 
							$.alert("抱歉，解冻出错现意外错误");
						}
					});
				}	
			);
		
		});
		
	}

}