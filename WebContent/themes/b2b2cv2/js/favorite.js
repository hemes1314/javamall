Favorite={
	init:function()	{
	 
		var self =this;
		//收藏
		//modify by lin yang common.js 中已经有收藏函数，此处多余冲突
/*		$(".favorites").click(function(){
			var currgoodsid = $(this).attr("goodsid");
	 
			if(!isLogin){
				$.LoginDialog.open({loginSuccess:function(){
				  login_dialog(window.location.href);
				}});
		
			}else{
				 self.doFavorite(currgoodsid);
			}
			return false;
		});*/
		
		$(".gnotifybtn").click(function(){
			var currgoodsid = $(this).attr("goodsid");
			Favorite.gnotify(currgoodsid);			
			return false;			
		});
		
		$(".store_favoritebtn").click(function(){
			var store_id = $(this).attr("rel");
			Favorite.doStoreFavorite(store_id);			
			return false;			
		});
	},
	gnotify:function(currgoodsid){
		var self =this;
		if(!isLogin){
			alert("请先登录，在进行登记");
			//$.LoginDialog.open({loginSuccess:function(){
			//   self.doGnotify(currgoodsid);
			//}});
		}else{
			 self.doGnotify(currgoodsid);
		}
	},
	doGnotify:function(id){
		$.Loading.show("正在登记......");
		$.ajax({ 
			url:ctx+'/api/shop/gnotify!add.do?goodsid='+id,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
			}
		});
	}
	,
	doFavorite:function(currgoodsid){
		$.Loading.show("正在收藏...");
		$.ajax({ 
			url:ctx+'/api/shop/collect!addCollect.do?goods_id='+currgoodsid,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
			} 
		});
	},
	
	doStoreFavorite:function(store_id){
		$.Loading.show("正在收藏...");
		$.ajax({ 
			url:ctx+'/api/b2b2c/storeCollect!addCollect.do?store_id='+store_id,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
				 location.reload();
			} 
		});
	}
};