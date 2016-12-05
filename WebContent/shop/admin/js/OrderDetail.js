var OrderStatus={};

// 订单状态
OrderStatus.ORDER_CHANGED = -7;// 已换货
OrderStatus.ORDER_CHANGE_REFUSE = -6;// 换货被拒绝
OrderStatus.ORDER_RETURN_REFUSE = -5;// 退货被拒绝
OrderStatus.ORDER_CHANGE_APPLY = -4;// 申请换货
OrderStatus.ORDER_RETURN_APPLY = -3; // 申请退货
OrderStatus.ORDER_CANCEL_SHIP = -2; // 退货
OrderStatus.ORDER_CANCEL_PAY = -1; // 退款
OrderStatus.ORDER_NOT_PAY = 0; // 未付款
OrderStatus.ORDER_PAY = 1; // 已支付待确认
OrderStatus.ORDER_PAY_CONFIRM = 2; // 已确认支付
OrderStatus.ORDER_ALLOCATION = 3; // 配货中
OrderStatus.ORDER_ALLOCATION_YES = 4; // 配货完成，待发货
OrderStatus.ORDER_SHIP = 5; // 已发货
OrderStatus.ORDER_ROG = 6; // 已收货
OrderStatus.ORDER_COMPLETE = 7; // 已完成
OrderStatus.ORDER_CANCELLATION = 8; // 作废
OrderStatus.ORDER_NOT_CONFIRM = 9; // 已生效

// 付款状态
OrderStatus.PAY_NO = 0; // 未付款
OrderStatus.PAY_YES = 1; // 已付款待确认
OrderStatus.PAY_CONFIRM = 2; // 已确认付款
OrderStatus.PAY_CANCEL = 3; // 已经退款
OrderStatus.PAY_PARTIAL_REFUND = 4; // 部分退款
OrderStatus.PAY_PARTIAL_PAYED = 5;// 部分付款

// 货运状态
OrderStatus.SHIP_ALLOCATION_NO = 0; // 0未配货
OrderStatus.SHIP_ALLOCATION_YES = 1; // 1配货中
OrderStatus.SHIP_NO = 2; // 0未发货
OrderStatus.SHIP_YES = 3;// 1已发货
OrderStatus.SHIP_CANCEL = 4;// 2.已退货
OrderStatus.SHIP_PARTIAL_SHIPED = 5; // 4 部分发货
OrderStatus.SHIP_PARTIAL_CANCEL = 6;// 3 部分退货
OrderStatus.SHIP_PARTIAL_CHANGE = 7;// 5部分换货
OrderStatus.SHIP_CHANED = 8;// 6已换货
OrderStatus.SHIP_ROG=9 ; //已收货

var OrderDetail = {
	orderid : undefined,
	orderStatus : undefined,
	payStatus : undefined,
	shipStatus : undefined,
	isCod : false,
	init : function(orderid, orderStatus, payStatus, shipStatus, isCod,paymentid) {
		// 初始化订单的状态
		this.orderStatus = orderStatus;
		this.payStatus = payStatus;
		this.shipStatus = shipStatus;
		this.isCod = isCod;
		this.paymentid = paymentid;
		var self = this;
		this.orderid = orderid;
		new Tab(".order_detail");
		this.bindFlowEvent();

	},

	/**
	 * 绑定订单流程按钮事件
	 */
	bindFlowEvent : function() {
		var self = this;
		// 付款事件绑定 确认付款
		$("#pay").unbind("click");
		$("#pay").bind("click",function() {
					var disabled=  $("#pay").hasClass("l-btn-disabled");
					if( !disabled ){
					 	$("#orderinfo").show();
					　　	$('#orderinfo').dialog({
					　　		title: '付款',	
					　　		width: 750,
					　　		closed: false,
					　　		cache: false,
					　　		href: ctx+'/shop/admin/payment!showPayDialog.do?ajax=yes&orderId='+ self.orderid, 	
					　　		modal: true,
					　　		buttons: [{		
					　　			 text:'保存',
					　　			 handler:function(){
					　　				 var savebtn = $(this);
					　　				 var disabled=savebtn.hasClass("l-btn-disabled");
					　　				 if(!disabled){
				　　				 			self.pay(self.orderid,savebtn);
				　　				 		}
					　　			 }
					　　			 },{
					　　			 text:'还原',
					　　			 handler:function(){
					　　				$('#order_form')[0].reset() ;
					　　			 }
					　　		}]
					　　	});
					}
				});
		

		// 退款事件绑定
		$("#refund").unbind("click");
		$("#refund").bind("click",function() {Eop.Dialog.open("order_dialog");
			var disabled=  $("#refund").hasClass("l-btn-disabled");
			if( !disabled ){
				$("#order_dialog .con").load(basePath+ "payment!showRefundDialog.do?ajax=yes&orderId="+ self.orderid,
						function() {$("#pay_date").datepicker();
									$("#onlineRdio").unbind("click");
									$("#onlineRdio").bind("click",
											function() {$(".online_box").show().find("input,select").attr("disabled",false);
														$(".offline_box").hide().find("input,select").attr("disabled",true);});
									$("#offlineRdio").unbind("click");
									$("#offlineRdio").bind("click",function() {$(".offline_box").show().find("input,select").attr("disabled",false);
									$(".online_box").hide().find("input,select").attr("disabled",true);});
									$("#order_dialog .submitBtn").unbind("click");
									$("#order_dialog .submitBtn").bind("click",function() {self.refund();});
									});
			}
		});
			
		// 退货事件绑定
		$("#returned").unbind("click");
		$("#returned").bind("click",function() {
			var disabled=  $("#returned").hasClass("l-btn-disabled");
			if( !disabled ){
					newTab("申请退货","../shop/admin/sellBack!add.do?orderId="+ self.orderid);
			}
		});

		// 收货事件绑定
		$("#rog").unbind("click");
		$("#rog").bind("click",function() {
			var disabled=  $("#rog").hasClass("l-btn-disabled");
			if( !disabled ){
					if (confirm("确认顾客已收到货了吗")) {
						$.Loading.show("正在保存请稍候");
						$("#rog").linkbutton("disable");
						$.ajax({url : ctx+"/shop/admin/order!rogConfirm.do?ajax=yes&orderId="+self.orderid,
							dataType : "json",
							success : function(res) {
								if (res.result == 1) {
									$.Loading.success(res.message);
									location.reload();
								} else {
									$.Loading.error(res.message);
								}
							}

						});
					}
			}
			});

		// 完成事件绑定
		$("#complete").unbind("click");
		$("#complete").bind("click", function() {
			var disabled=  $("#complete").hasClass("l-btn-disabled");
			if(!disabled){
				if (confirm("完成 操作会使该订单归档且不允许再做任何操作，确定要执行吗？")) {
					$.Loading.show("正在保存请稍候");
					self.complete(self.orderid);
				}
			}
		});

		// 作废事件绑定
		$("#cancel").unbind("click");
		$("#cancel").bind("click",function() {
			var disabled=  $("#cancel").hasClass("l-btn-disabled");
			if(!disabled){
				if (confirm("作废操作会使该订单归档且不允许再做任何操作，确定要执行吗？")) {
					$("#cancelorder").show();
				　　	$('#cancelorder').dialog({
				　　		title: '作废',	
				　　		width:630,
				　　		closed: false,
				　　		modal: true,
				　　		buttons: [{		
				　　			 text:'保存',
				　　			 handler:function(){
				　　				 var savebtn = $(this);
				　　				 var disabled=savebtn.hasClass("l-btn-disabled");
				　　				 if(!disabled){
					　　				var canel_reason = $("#canel_reason").val();
									if (canel_reason == "") {
										$.Loading.error("请输入取消原因");
										return false;
									}
				　　				 }
								 self.cancel(self.orderid,canel_reason,savebtn);
				　　			 }
				　　		}]
			　　		});
				}
			}
		});
		// 确认订单绑定
		$("#confirmorder").unbind("click");
		$("#confirmorder").bind("click", function() {
			var disabled=  $("#confirmorder").hasClass("l-btn-disabled");
			if(!disabled){
				if (confirm("确认要确认此订单吗？")) {
					self.confirmOrder(self.orderid);
				}
			}
		});
		//发货
		$("#ship").unbind("click");
		$("#ship").bind("click",function(){
			var disabled=  $("#ship").hasClass("l-btn-disabled");
			//add by lxl 
			var disbaledship = $("#savePressNo").hasClass("l-btn-disabled");

			if (!disbaledship){
				alert("请保存快递单号");
				return false;
			}
			if($(".waybill_number").val() == ""){
				alert("请输入运单号");
				return false;
		    }
			else if(!disabled){
				if(confirm("确认发货？快递公司："+$("#logi").find("option:selected").text()+"  快递单号："+$("input[name='expressNo']").val())){
					self.ship(self.orderid);
				}
			}
		});
		this.initBtnStatus();
	},
	/**
	 * 加载一个配货项的输入页面
	 * 
	 * @param box
	 * @param itemid
	 */
	loadProductStore : function(box, itemid) {
		box.load(
				basePath + "ship!getProductStore.do?ajax=yes&itemid=" + itemid,
				function() {
					// 通过input.depotid样式找到点击的配货仓库复选框
					$(this).find("input.depotid").click(
							function() {
								var li = $(this).parent();
								if (parseInt(li.attr("store")) == 0) {
									$.Loading.error("此仓库库存为0，不能配货");
									return false;}
								li.find("input[type=text],input[type=hidden]")
										.attr("disabled", !this.checked);
								if (this.checked) {
									li.addClass("selected");
								} else {
									li.removeClass("selected");
								}

							});
				});
	},
	/**
	 * 初始化按钮状态
	 */
	initBtnStatus : function() {
		if (
			   (!this.isCod && this.payStatus == OrderStatus.PAY_NO)// 非货到付款的话，是未支付时使按钮可用
			|| (!this.isCod && this.payStatus == OrderStatus.PAY_PARTIAL_PAYED)//非货到付款，部分付款时，按钮可用
			|| (this.isCod && (this.orderStatus == OrderStatus.ORDER_ROG) && (this.payStatus== OrderStatus.PAY_NO||this.payStatus==OrderStatus.PAY_PARTIAL_PAYED/*2015/10/21 humaodong*/)) //货到付款的，完成后付款按钮可用
		) {
		} else {
			$("#pay").linkbutton("disable");
		}
		//如果已确认付款，禁用付款按钮
		if(this.payStatus == OrderStatus.PAY_CONFIRM){
			$("#pay").linkbutton("disable");
		}
		
		
		//发货后可用
		//收货后不可用
		if ( this.shipStatus == OrderStatus.SHIP_YES) {
		}else if (this.shipStatus == OrderStatus.SHIP_ROG) {
			$("#rog").linkbutton("disable");
		}else{
			$("#rog").linkbutton("disable");
		} 

		
		// 已发货，禁用作废按钮
		if (this.orderStatus >= OrderStatus.ORDER_SHIP
				&& this.orderStatus != OrderStatus.ORDER_NOT_PAY) {
			$("#cancel").linkbutton("disable"); 
		}

		// 确认订单
		// 订单付款方式为货到付款（支付方式id为2）时且订单状态为未付款

		
		
		
		//订单退货
		if(this.orderStatus==OrderStatus.ORDER_RETURN_APPLY){
			$(".toolbar a").linkbutton("disable");
		}
		// 订单状态为完成或作废，则禁用 所有钮
		if (this.orderStatus == OrderStatus.ORDER_COMPLETE
				|| this.orderStatus == OrderStatus.ORDER_CANCELLATION) {
			$(".toolbar a").linkbutton("disable");
			$("#nextorder input").removeAttr("disabled");
		}
		// 已收货 退货按钮可用
		//订单完成退货按钮可用s
		// 其它状态都禁用(未发货、已换货) 禁用按钮
		if ( this.orderStatus == 	OrderStatus.ORDER_ROG
			|| this.orderStatus==	OrderStatus.ORDER_COMPLETE
		) {
			if(this.orderStatus==OrderStatus.ORDER_RETURN_APPLY){
				$("#rog").linkbutton("disable");
				$("#returned").linkbutton("disable");
			}else{
				$("#returned").linkbutton("enable");
				$("#cancel").linkbutton("enable");
			}
			
		} else {
			$("#returned").linkbutton("disable");
		}
		
		// 如果未确认，则可用
		if (this.orderStatus==OrderStatus.ORDER_NOT_CONFIRM) {
		} else {
			$("#confirmorder").linkbutton("disable");
		}
		if((!this.isCod && this.payStatus == OrderStatus.PAY_CONFIRM&&this.shipStatus==OrderStatus.SHIP_NO)
			||(this.isCod&&this.orderStatus==OrderStatus.ORDER_NOT_PAY&&this.shipStatus==OrderStatus.SHIP_NO)){
			
		}else{
			$("#ship").linkbutton("disable");
		}
	}

	,

	/**
	 * 支付
	 */
	pay : function(orderId,savebtn) {
		var self = this;
	 	var formflag= $("#order_form").form().form('validate');
	 	if(formflag){
	 		$.Loading.show("正在处理付款，请稍后...");
	 		savebtn.linkbutton("disable");
			var options = {
				url :  ctx+'/shop/admin/payment!pay.do?ajax=yes',
				type : "post",
				dataType : "json",
				success : function(responseText) {
					if (responseText.result == 1) {
						$.Loading.success(responseText.message);
						self.refresh(responseText);
					}
					if (responseText.result == 0) {
						$.Loading.error(responseText.message);
						savebtn.linkbutton("enable");
					}
				},
				error : function() {
					$.Loading.error("出错了:(");
					savebtn.linkbutton("enable");
				}
			};
			$('#order_form').ajaxSubmit(options);
	 	}
	},

	/**
	 * 退款
	 */
	refund : function() {
		var self = this;
		var options = {
			url : basePath + "payment!cancel_pay.do?ajax=yes",
			type : "post",
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					Eop.Dialog.close("order_dialog");
					self.payStatus = responseText.payStatus;
					self.bindFlowEvent();
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		};
		$('#order_form').ajaxSubmit(options);
	},

	refresh : function(responseText) {
		// var self=this;
		//			
		// self.orderStatus = responseText.orderStatus;
		// self.payStatus = responseText.payStatus;
		// self.shipStatus = responseText.shipStatus;
		// self.bindFlowEvent();

		location.reload();
	},
	/**
	 * 退货
	 */
	returned : function() {
		var flag = true;
		$("input[name=numArray]").each(function(i, v) {
			if ($.trim(v.value) == '') {
				flag = false;
			} else {
				if (!isdigit(v.value)) {
					flag = false;
				} else if (parseInt(v.value) < 0) {
					flag = false;
				}
			}

		});

		if (!flag) {
			$.Loading.error("请输入正确的退货数量");	
			return;
		}
		$("#order_dialog .submitBtn").attr("disabled", true);
		var self = this;
		var options = {
			url : basePath + "ship!returned.do?ajax=yes",
			type : "post",
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					Eop.Dialog.close("order_dialog");
					self.shipStatus = responseText.shipStatus;
					self.bindFlowEvent();
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		};
		$('#order_form').ajaxSubmit(options);
	},
	/**
	 * 换货
	 */
	changed : function() {
		var self = this;
		var options = {
			url : basePath + "ship!change.do?ajax=yes",
			type : "post",
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					Eop.Dialog.close("order_dialog");
					self.shipStatus = responseText.shipStatus;
					self.bindFlowEvent();
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		};
		$('#order_form').ajaxSubmit(options);
	},
	/**
	 * 完成
	 */
	complete : function(orderId) {
		var self = this;
		$.ajax({
			url :  'order!complete.do?ajax=yes&orderId=' + orderId,
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		});
	},
	/**
	 * 作废
	 */
	cancel : function(orderId, canel_reason,savebtn) {
		var self = this;
		$.Loading.show("正在保存请稍候..");
		savebtn.linkbutton("disable");
		$.ajax({
			url : 'order!cancel.do?ajax=yes&orderId=' + orderId+ "&cancel_reason=" + canel_reason,
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
					location.href="order!list.do";
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
					savebtn.linkbutton("enable");
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
				savabtn.linkbutton("enable");
			}
		});
	},
	ship:function(orderId){
		var self=this;
		$.Loading.show("正在处理发货...");
		$("#ship").linkbutton("disable");
		$.ajax({
			url :  ctx+'/shop/admin/orderPrint!ship.do?ajax=yes&&order_id='+orderId,
			dataType : "json",
			cache:false,
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					alert(responseText.message);
					$.Loading.hide();
					$("#ship").linkbutton("enable");
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
				$("#ship").linkbutton("enable");
			}
		});
	},
	
	
	/**
	 * 订单确认
	 * 
	 * @param orderId
	 */
	confirmOrder : function(orderId) {
		var self = this;
		$.Loading.show("正在保存请稍候");
		$("#confirmorder").linkbutton("disable");
		$.ajax({
			url :  'order!confirmOrder.do?ajax=yes&orderId='+ orderId,
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
					$("#confirmorder").linkbutton("enable");
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
				$("#confirmorder").linkbutton("enable");
			}
		});
	}

};

function isdigit(s) {
	var r, re;
	re = /\d*/i; // \d表示数字,*表示匹配多个数字
	r = s.match(re);
	return (r == s);
}