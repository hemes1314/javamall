var Cart={
		init:function(staticserver){
			var self=this;
			self.refreshTotal(1);
			this.bindEvent();
		},
		bindEvent:function(){
			var self=this;
			
			//购物数量调整
			$(".Numinput .increase,.Numinput .decrease").click(function(){
				$this = $(this);
				var number = $this.parents(".Numinput");
				var itemid =number.attr("itemid");
				var productid =number.attr("productid");
				var objipt = number.find("input");
				var num=objipt.val();
				num =parseInt(num);
				if (!isNaN(num)){
					if($this.hasClass("increase")){
						num++;
					}
					if($this.hasClass("decrease")){
						 if(num == 1 ){
							 if(confirm("确定要删除该商品?")){
								 self.deleteGoodsItem(itemid);
							 }
							 return false;
						} 
						num--;
					}
					 num = (num <=1 || num > 100000) ? 1 : num;
					 self.updateNum(itemid, num, productid,objipt);
				}
			});
			
			//购物数量手工输入
            $(".Numinput input").keydown(function(e){
                var kCode = $.browser.msie ? event.keyCode : e.which;
                //判断键值  
                if (((kCode > 47) && (kCode < 58)) 
                    || ((kCode > 95) && (kCode < 106)) 
                    || (kCode == 8) || (kCode == 39) 
                    || (kCode == 37)) { 
                    return true;
                } else{ 
                    return false;  
                }
            }).focus(function() {
                this.style.imeMode='disabled';// 禁用输入法,禁止输入中文字符
            }).keyup(function(){
                var pBuy   = $(this).parent();//获取父节点
                var itemid  = pBuy.attr("itemid");
                var productid  = pBuy.attr("productid");
                var numObj = pBuy.find("input[name='num']");//获取当前商品数量
                var num    = parseInt(numObj.val());
                if (!isNaN(num)) {
                    var numObj = $(this);
                    var num    = parseInt(numObj.val());
                    num = (num <=1 || num > 100000) ? 1 : num;
                    self.updateNum(itemid, num, productid,numObj);
                }
            });
            
			//删除商品
			$(".border .delete").click(function(){
				var cartid = $(this).parents("tr").attr("itemid");
				if(confirm("您确实要把该商品移出购物车吗？") ){
					self.deleteGoodsItem(cartid);
				}
			});
			
			//清空购物车
			$(".border .clean_btn").click(function(){
				if(confirm("您确认要清空购物车吗？") ){
					self.clean();
				}
			});
			
			//继续购物
			$(".border .returnbuy_btn").click(function(){
				location.href="index.html";
			});
			
			//去结算
			$(".border .checkout_btn").click(function(){

				//判断选中的商品，提交的数量是否为空或正整数
				$(".select_item:checked").each(function () {
					var num = $(this).parent().parent().find(".numtext").val();
					if (!(/^(\+|-)?\d+$/.test(num)) || num < 1) {
						alert("数量不能为空且为正整数");
						return false;
					}
				});
				
				var hfcount = 0;
				//判断合法的商品数量
				var is_limit = false; //是否限制提交购物车
				$(".select_item").each(function(){
					if($(this).prop("checked")){
						hfcount++;
					}
				});
				if(hfcount==0){
					alert("请选择您要结算的商品！");
					return false;
				}
				
				if(is_limit)
				{
					return false;
				}
				
				var hfcountagain = 0;
				//判断是否选择了商品
				var selected = false;
				$(".select_item").each(function(){
					if($(this).prop("checked")){
						$mthis = $(this).parent().next().next().next().next().next().find(".increase");
						var number = $mthis.parents(".Numinput");
						var itemid =number.attr("itemid");
						var productid =number.attr("productid");
						var objipt = number.find("input");
						var num=objipt.val();
						num =parseInt(num);
						if (!isNaN(num)){
							$.ajax({
								url:"api/shop/cart!updateNumAnd.do?ajax=yes",
								data:"cartid="+itemid +"&num="+num +"&productid="+productid,
								dataType:"json",
								success:function(result){
									if(result.result==1){
										if(result.store==-1){
											alert("抱歉！您所选择的货品已下架。");
											location.reload();
											return false;
										}
										if(result.store<num){
											num = result.store;
											alert("抱歉！您所选择的货品库存不足。");
											location.reload();
											return false;
										}
										self.refreshTotal(1);
										hfcountagain++;
										if(hfcount == hfcountagain){
											if(isLogin){
												location.href="checkout.html";
											}else{
												self.showLoginWarnDlg();					
											}
										}
									}else{
										if(result.message != undefined && result.message != ''){
											alert(result.message)
										}else{
											alert("更新失败");
											return false;
										}
									}
								},
								error:function(){
									alert("出错了:(");
									return false;
								}
							});	
						}else{
							return false;
						}
					}
				});
				
				
			});

			//全选
			$(".select_all").click(function(){
				var checked = $(this).prop("checked");
				$(".select_store, .select_item").each(function(){
					$(this).prop("checked", checked);
				});
				self.selectItem();
			});

			//选择店铺
			$(".select_store").click(function(){
				var checked = $(this).prop("checked");
				var storeid = $(this).attr("storeid");
				$("input[name='select_item_" + storeid + "']").each(function(){
					$(this).prop("checked", checked);
				});
				self.selectItem();
			});

			//选择商品
			$(".select_item").click(function(){
				self.selectItem();
			});
		},

		//选择商品
		selectItem:function(){
			var self = this;
			var itemids = new Array();
			var storeids = new Array();

			var checkAll = true;
			$(".select_store").each(function(){
				var checkStore = true;

				var storeid = $(this).attr("storeid");
				$("input[name='select_item_" + storeid + "']").each(function(){
					if($(this).prop("checked")){
						itemids.push($(this).attr("itemid"));
					}else{
						checkStore = false;
					}
				});

				if(checkStore){
					storeids.push(storeid);
				}else{
					checkAll = false;
				}
				$(this).prop("checked", checkStore);
			});
			$(".select_all").prop("checked", checkAll);

			//ajax请求选择的商品及店铺
			$.ajax({
				url:"api/shop/cart!selectItems.do?ajax=yes",
				data:"itemids=" + itemids.join(","),
				dataType:"json",
				success:function(result){
					if(result.result==1){
						self.refreshTotal(1);
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
		
		//提示登录信息
		showLoginWarnDlg:function(btnx,btny){
			var html = $("#login_tip").html();
			$.dialog({ title:'提示信息',content:html,lock:true,width:330,init:function(){
				
				$(".ui_content input").jbtn();
				$(".ui_content .to_login_btn").click(function(){
					 location.href=ctx+"/store/login.html?forward=checkout.html";
				});

				$(".ui_content .to_checkout_btn").click(function(){
					location.href=ctx+"/store/register_phone.html";
				});///store/register.html?forward=checkout.html
				
			}});
		},
		
		//删除一个购物项
		deleteGoodsItem:function(itemid){
			var self=this;
			$.Loading.show("请稍候...");
			$.ajax({
				url:"api/shop/cart!delete.do?ajax=yes",
				data:"cartid="+itemid,
				dataType:"json",
				success:function(result){
					if(result.result==1){
						self.refreshTotal(0);
						//self.removeItem(itemid);
					}else{
						$.alert(result.message);
					}	
					$.Loading.hide();
					
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});
		},
		
		//移除商品项
		removeItem:function(itemid){
			$(".border tr[itemid="+itemid+"]").remove();
		},
		
		//清空购物车
		clean:function(){
			$.Loading.show("请稍候...");
			var self=this;
			$.ajax({
				url:"api/shop/cart!clean.do?ajax=yes",
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){
						location.href='cart.html';
					}else{
						$.alert("清空失败:"+result.message);
					}				 
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});		
		},
		
		//更新数量
		updateNum:function(itemid,num,productid,num_input){
			var self = this;
			$.ajax({
				url:"api/shop/cart!updateNum.do?ajax=yes",
				data:"cartid="+itemid +"&num="+num +"&productid="+productid,
				dataType:"json",
				success:function(result){
					if(result.result==1){
						if(result.store<num){
							num = result.store;
							alert("抱歉！您所选择的货品库存不足。");
						}
						self.refreshTotal(1);
						var price = parseFloat($("tr[itemid="+itemid+"]").attr("price"));
						//price =price* num;
						price =self.changeTwoDecimal_f(price* num);
						$("tr[itemid="+itemid+"] .itemTotal").html("￥"+price);
						num_input.val(num);
						if(result.store<num){
							return false;
						}
					}else{
						if(result.message != undefined && result.message != ''){
							alert(result.message)
						}else{
							alert("更新失败");
							return false;
						}
					}
				},
				error:function(){
					alert("出错了:(");
					return false;
				}
			});		
		},
		
		//刷新价格
		refreshTotal:function(type){
			if(type==0){
				location.href=ctx+"/cart.html";
				return false;
			}
			var self = this;
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
		},
		
		changeTwoDecimal_f:function(x) {
	        var f_x = parseFloat(x);
	        if (isNaN(f_x)) {
	            alert('参数为非数字，无法转换！');
	            return false;
	        }
	        var f_x = Math.round(x * 100) / 100;
	        var s_x = f_x.toString();
	        var pos_decimal = s_x.indexOf('.');
	        if (pos_decimal < 0) {
	            pos_decimal = s_x.length;
	            s_x += '.';
	        }
	        while (s_x.length <= pos_decimal + 2) {
	            s_x += '0';
	        }
	        return s_x;
	    }
};

$(function(){
	Cart.init();
});