var Products=[];
var Spec={
	init:function(goodsid){
		var self = this;
 
		
		$("#buyNow").click(function(){	
			//alert("#buyNow");
			var $this= $(this); 
			self.addToCart($this);
			
		});
		$("#addCart").click(function(){	
			//alert("#addCart");
			var $this= $(this); 
			self.addToCart($this);
		});
/*		$(".addGroupbuyGoods").click(function(){
			//alert(".addGroupbuyGoods");
			var $this=$(this);
			self.addGroupBuy($this);
		});*/
	$("#addGroupbuyToCart").click(function(){
			//alert("#addGroupbuyToCart1111");
			var $this=$(this);
			self.addToCart($this);
		});
		
		
		
		
		this.loadProduct(goodsid);
	 
	},
	loadProduct:function(goodsid){
		//alert("loadProduct");
		var self= this;
		$.ajax({
			url:ctx+"/api/shop/goods!productList.do?goodsid="+goodsid,
			dataType:"json",
			cache: false, //关闭AJAX缓存
			success:function(json){
			
				if(json.result==1){
					Products = json.data;
					//alert(JSON.stringify(Products));
					self.refresh();
					$("a[name='goods_spec'][specvid!='']").click(function(){
						var link = $(this);
						if(link.attr("class")!='hovered' && link.attr("class")!='disabled' ){
							self.specClick($(this));
						}
						return false;
					});
					$("#goodsform [name='action']").val("addProduct");
					
				}else{
					alert(json.message);
				}
			},
			error:function(){
				//alert("意外错误")
			}
		});
	}
	,
	specClick:function(specLink){
		specLink.parents("ul").find("a[specvid!='']").removeClass("hovered");
		specLink.parent().parent().parent().parent().find("em").addClass("checked");
		specLink.addClass("hovered");
 		
		this.refresh(specLink);
	},
	findGoodsImg:function(vid){
		for(i in  spec_imgs){
			var specimg = spec_imgs[i];
			if(specimg.specvid==parseInt(vid)){
				return specimg.goods_img;
			}
		}
	},
	
	//根据当前选择的规格找到货品
	findProduct:function(vidAr){
		var pros =[];
		//判断两个数组元素值是否相同，不判断位置情况
		function arraySame(ar1,ar2){
			//if(ar1.length!=ar2.length) return false;
			
			for(i in ar1){
				if($.inArray(ar1[i],ar2)==-1){ //不存在
					return false;
				}
			}
			return true;
		}
		
		var self = this;
	 
		for(i in Products){
			var product= Products[i];
			if(arraySame(vidAr,product.specs)){
				pros[pros.length] =product; 
			}
		}	
		 
		return pros;
	}
	,
	refresh:function(specLink){
		var self = this;
		var product_ar=[];
		$(".spec-item a.hovered").each(function(){
			var link = $(this);
			product_ar[product_ar.length]=parseInt(link.attr("specvid"));
		});

		var pro =this.findProduct(product_ar);
		for(i in Refresh){
			Refresh[i].refresh(pro,specLink,product_ar);
		}

		if(pro.length==1){
			//alert(pro[0].enable_store);
			$("strong[nctype='goods_stock']").html(pro[0].enable_store<0?0:pro[0].enable_store);
			$("#productid").val(pro[0].product_id);
		}
	}
,
	 
	addToCart:function(btn){
		//alert("addToCart");
	     
		
	    var value = $("#quantity").val();
        
		//alert("value"+value);
		
		if(value <= 0)
	    {
		  alert("购买数量小于0");
		  return false;
	    }
		
 if(isNaN(value))
	    {
	        alert("必须是数字");//lang.only_number
	        return false;
	    }
    if(value.indexOf('-') > -1)
	    {
	        alert("必须是数字");//lang.only_number
	        return false;
	    }
		
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var id = btn.attr("id");
		var action = $("#goodsform [name='action']").val();
		//alert("action=========="+action);
		
	
		var options={
	
		url:ctx+"/api/shop/cart!" + action + ".do?ajax=yes&store_id="+$("#storeid").val(),
		
			dataType:"json",
			async:false,
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				//alert(result);
				//alert(result.result);
				//alert(action);
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
	
	addGroupbuyToCart:function(btn){
		//alert("addGroupbuyToCart");
		var value = 1;
		if(value <= 0)
	    {
		  alert("购买数量小于0");
		  return false;
	    }
		
	    if(isNaN(value))
	    {
	        alert("必须是数字");//lang.only_number
	        return false;
	    }
	    if(value.indexOf('-') > -1)
	    {
	        alert("必须是数字");//lang.only_number
	        return false;
	    }
		
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var id = btn.attr("id");
		var action = $("#goodsform [name='action']").val();
		var  storeid= $("#goodsform [id='storeid']").val();
		
		
		var options={
			url:ctx+"/api/shop/cart!" + action + ".do?ajax=yes&store_id="+$("#storeid").val(),
			dataType:"json",
			async:false,
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				//alert(action);
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
	
	
	
	
	
	
	
	showCartCount:function(){
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
	}
	,
	showAddSuccess:function(){
		//alert(444);
		var myself = this;
		var html = $(".add_success_msg").html();
		$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
			//alert("提示信息");
			var self = this;
			$(".ui_content .btn").jbtn();
			$(".ui_content .returnbuy_btn").click(function(){
				self.close();     //关闭自己
				myself.showCartCount();
			});
	
			$(".ui_content .checkout_btn").click(function(){
				location.href=ctx+"/cart.html";

			});
			
			$(".ui_close").click(function(){
				myself.showCartCount();
			})
			
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
var StateRefresh={
	ArrrRemove:function( ar,obj) {  
		var new_ar =[];
		for( var i in ar ){
			if(obj!= ar[i]){
				new_ar.push(ar[i]);
			}
		}
		return new_ar;
	},

	refresh:function(pro,specLink,product_ar){
		//pro:找到的product [{sprc:{}},{}]
		//product_ar:选中的规格[1,2]
		
		var self  = this;
		if(product_ar.length>0){
		//从目前未选中的规格中循环
			$(".spec-item").not( specLink.parents(".spec-item") ).find("a").each(function(){
				var link = $(this);
				var proar=product_ar;
				link.parents(".spec-item").find("a").not(this).each(function(){
					var specvid = parseInt($(this).attr("specvid"));
					proar= self.ArrrRemove(proar,specvid);
				});
				
				var specvid = parseInt(link.attr("specvid"));
				proar.push(specvid);
				
				var result =Spec.findProduct(proar);
				if(!result || result.length==0){
					link.addClass("disabled");
				}else{
					link.removeClass("disabled");
				}
				proar.pop();
				
			});
		}
	}
};
var SelectTipRefresh={
	refresh:function(pro){
		var i=0;
		var specHtml="";
		$(".spec-item a.hovered").each(function(){
			if(i==0) specHtml="";
			if(i!=0) specHtml+="、";
			specHtml +=$(this).attr("title")+"";
			i++;
		});	
		if(i>0){
			specHtml="<dt>您已选择：</dt>"+"<dd><font color='red'>"+specHtml+"</font></dd>";
		}else{
			specHtml="<dt>请选择：</dt><dd>下列规格</dd>";
		}
		$(".spec-tip").html(specHtml);
	}
};
var PriceRefresh={
	refresh:function(pro){
		if(pro.length==1){
			   //add by linyang 判断是否为整型，整型补.00
			   var t=/^\d+(\.\d+)?$/;
			   var x = true;
			   x = t.test(pro[0].price);
			   if(x)
			   {
				   $("#goods_price strong").text("￥"+pro[0].price.toFixed(2));
			   }
			   
			$("#productid").val(pro[0].product_id);
			$("#buy_count").text(pro[0].buy_count);
			$("#comment_count").text(pro[0].comment_count);
		}
		else{
			var maxPrice=0,minPrice=-1;
			for(i in pro){
				if( maxPrice<pro[i].price){
					maxPrice = pro[i].price;
				}
				if(minPrice==-1|| minPrice>pro[i].price){
					minPrice = pro[i].price;
				}
			}	
			$("#goods_price strong").text("￥"+minPrice+"-￥" +maxPrice);
		}
	}
};
function canBuy(){
	$("input[name=action]").val("addProduct");
	$("#buyNow").unbind('click');
	$("#buyNow").bind('click',function(){
		Spec.addToCart($(this));
	});
	
	$("#addCart").unbind('click');
	$("#addCart").bind('click',function(){
		Spec.addToCart($(this));
		return false;
	});
	
	$("#buyNow").css("cursor","pointer");
	$("#buyNow").tip({'disable':true});
	
	$("#addCart").css("cursor","pointer");
	$("#addCart").tip({'disable':true});
	
	
	$("#buyNow").removeClass('disabled');
	$("#addCart").removeClass('disabled');
}

function cantbuy(){
	$("#buyNow").unbind('click');
	$("#buyNow").bind('click',function(){return false;});
	$("#buyNow").css("cursor","not-allowed");
	
	$("#addCart").unbind('click');
	$("#addCart").bind('click',function(){return false;});
	$("#addCart").css("cursor","not-allowed");
}

var BtnTipRefresh = {
	refresh:function(pro){
		$("#buyNow").attr('tip','');
		$("#addCart").attr('tip','');
		
		$("#buyNow").addClass('disabled');
		$("#addCart").addClass('disabled');
		
		if(pro.length==1){
			if(pro[0].enable_store==0){
				cantbuy();
				$("#addCart,#buyNow").tip({'disable':false,className:"cantbuy",text:"此商品库存不足"});

			}else{
				canBuy();
			 
			}
		}else{
			
			var i=0;
			var tip='';
			$("#goodsform .spec-item em").each(function(){
				var em = $(this);
				
				if(em.attr("class")!='checked'){
					if(i!=0)tip+="、";
					tip+=em.text();
					i++;
				}
			});
			$("#addCart,#buyNow").tip({'disable':false,className:"cantbuy",text:"请选择:"+tip});
		}
	}	
};
var Refresh=[SelectTipRefresh,PriceRefresh,BtnTipRefresh,StateRefresh];

//tip插件
(function($) {
	$.fn.tip = function(options) {
		 
		var opts = $.extend({}, $.fn.tip.defaults, options);
		var tipEl= $(".tipbox");
		if(tipEl.size()==0){
			var html="<div class='tipbox' style='position: absolute;z-index:99'>";
			html+='<div class="tip-top"></div>';
			html+='<div class="tip">';
			html+='<div class="tip-text"></div>';
			html+='</div>';
			html+='<div class="tip-bottom"></div>';
			html+='</div>';
			tipEl=$(html).appendTo($("body"));
			tipEl.addClass(opts.className);
			tipEl.hide();
		}
		 tipEl.find(".tip>.tip-text").html(opts.text);
		 if( opts.disable){
			 $(this).unbind("mouseover").unbind("mousemove").unbind("mouseout");
		 }else{
			 $(this).bind("mouseover",function(e){
				 tipEl.show(); 
			 }).bind("mousemove",function(e){
				 tipEl.css('top',e.pageY+15).css('left',e.pageX+15);
			 }).bind("mouseout",function(){
				tipEl.hide();
			 });
		 }
	};
	
    $.fn.tip.defaults = {
    	className:"tip",
        text:"", 
        disable:false
    };
    
})(jQuery);



