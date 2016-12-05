/**
 * 商品操作js
 */
var Goods={
	init:function(scroll){
		var self = this;
		Favorite.init();
		
		$(".buynow").click(function(){	
			var $this= $(this); 
			self.addToCart($this);
			
		});
		$(".addcart").click(function(){	
			var $this= $(this); 
			self.addToCart($this);
		});
		$(".addGroupbuyGoods").click(function(){
			var $this=$(this);
			self.addGroupBuy($this);
		});
	},
	addToCart:function(btn){
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var id = btn.attr("id");
		var action = $("#goodsform [name='action']").val();
		var options={
			url:ctx+"/api/shop/cart!" + action + ".do?ajax=yes&store_id="+$("#storeid").val(),
			dataType:"json",
			async:false,
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				$.Loading.hide();
				if(result.result==1){
					if(id!="buyNow"){
						self.showAddSuccess();
					}else{
						window.location.href="cart.html";
					}
				}else{
					$.dialog({title:'提示',content: result.message,lock: true});
				}
				btn.attr("disabled",false);
			},
			error:function(){
				$.Loading.hide();
				$.dialog({title:'提示',content: "抱歉,发生错误",lock: true});
				btn.attr("disabled",false);
			}
		};
		$("#goodsform").ajaxSubmit(options);		
	},
	showAddSuccess:function(){
		var html = $(".add_success_msg").html();
		$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
			var self = this;
			$(".ui_content .btn").jbtn();
			$(".ui_content .returnbuy_btn").click(function(){
				self.close();     //关闭自己
				$.ajax({
					url:ctx+"/api/shop/cart!getCartData.do",   //获取购物车数据api
					dataType:"json",
					cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
					success:function(result){
						if(result.result==1){				
								$(".num").text(result.data.count);   //将得到的结果放入到头部的购物车数量中。
						}else{
							$.alert(result.message);
						}	
					},
					error:function(){
						$.Loading.hide();
						$.alert("出错了:(");
					}
				});
			});

			$(".ui_content .checkout_btn").click(function(){
				location.href=ctx+"/cart.html";
			});
			
		}});
	},addGroupBuy:function(btn){
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var options={
			url:ctx+"/api/shop/cart!addGoods.do?ajax=yes&store_id="+$("#storeid").val(),
			dataType:"json",
			async:false,
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				$.Loading.hide();
				if(result.result==1){
					self.showAddSuccess();
				}else{
					$.dialog({title:'提示',content: result.message,lock: true});
				}
				btn.attr("disabled",false);
			},
			error:function(){
				$.Loading.hide();
				$.dialog({title:'提示',content: "抱歉,发生错误",lock: true});
				btn.attr("disabled",false);
			}
		};
		$("#goodsform").ajaxSubmit(options);		
	}
};