/**
 * 购物车Bar
 * @author kingapex
 * 提供购物车Bar数量加载及悬停效果
 * 并暴露加载数量接口供其它程序使用
 */
var CartBar={
		/**
		 * 初始化方法 
		 * 1.加载数量
		 * 2.绑定hover事件
		 */
		init:function(){
			 var self = this;
			 this.barWrapper = $(".my_cart"); //购物车bar主元素
			 this.numBox = this.barWrapper.find(".addcart_num"); //数量元素
			 var contentBox = this.barWrapper.find(".content");  //购物列表元素
			 var listBox = this.barWrapper.find(".my_cartlist");  //购物列表元素
			 this.loadNum();
			 this.barWrapper.hover(
				 function(){
					 if ($(this).hasClass('hover')) {return false;}
					 $(this).addClass("hover");
				//	 contentBox.show();
					//显示loading 图标
					 $.ajaxSetup ({ cache: false });   //jquery ajax 的load()方法IE下无法获取页面内容
					 listBox.load(ctx+"/cart/cart_bar.html",function(){
						 //加载完列表绑定删除事件
						 $(this).find(".delete").click(function(){
							 var itemid = $(this).attr("itemid");
							 self.deleteItem(itemid);
						 }); 
						 $.ajax({
								url:ctx+"/api/shop/cart!getCartData.do",   //获取购物车数据api
								dataType:"json",
								cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
								success:function(result){
									if(result.result==1){				
											$(".addcart_num").text(result.data.count);   //将得到的结果放入到头部的购物车数量中。
										 
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
				 },function(){
					 $(this).removeClass("hover");
					 contentBox.hide();
				 } 
			 );
		},
		
		/**
		 * 购物车项删除
		 * @param itemid
		 */
		deleteItem:function(itemid){
			var self=this;
			$.Loading.show("请稍候...");
			$.ajax({
				url:ctx+"/api/shop/cart!delete.do?ajax=yes",
				data:"cartid="+itemid,
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){				
						self.loadNum();
						self.barWrapper.find(".item[itemid="+itemid+"]").remove();
						//如果是在购物车页面更新购物车相应信息
						if(typeof Cart != "undefined"){
							Cart.removeItem(itemid);
							Cart.refreshTotal();
						}
					}else{
						$.alert(result.message);
					}	
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});
		},
		/**
		 * 加载购物车中的商品数量
		 * 
		 */
		loadNum:function(){
		 	 var self = this;
			 $.ajax({
				 url: ctx+"/api/shop/cart!getCartData.do?ajax=yes",
				 dataType:'json',
				 cache:false,
				 success:function(result){
					 if(result.result==1){
						 $(".addcart_num").text(result.data.count);
						 $.ajax({
								url:ctx+"/cart/cartTotal.html",
								dataType:"html",
								success:function(html){
									$(".total_wrapper").html(html);
								},
								error:function(){
									//$.alert("糟糕，出错了:(");
								}
							});
					 }
				 }
			 });
		}
};