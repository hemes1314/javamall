var MemberPrice={
	priceBox:undefined,//当前会员价格div容器，规则为“会员价操作按钮”的class=member_price_box 下一个 节点
	init:function(){
		var self = this;
		Eop.Dialog.init({id:"memberpricedlg",title:"编辑会员价格",width:'500px'});
		self.bindMbPricBtnEvent();
	},
	bindMbPricBtnEvent:function(){
		var self = this;
		$(".mempriceBtn").unbind("click").bind("click",function(){
			self.priceBox= $(this).next(".member_price_box"); //当前的会员价box
			var price = $(this).siblings(".price").val(); // 获取价格，规则为当前操作按钮的
			if( parseFloat(price)!= price) {
				alert("价格必须是数字");
				return false;
			}else{
				Eop.Dialog.open("memberpricedlg");
				$("#memberpricedlg").load('memberPrice.do?price='+price+'&ajax=yes',function(){
					self.initDlg();
				});
			}
		});		
	}
	,
	
	//初始化弹出对话框，由pricebox计算lvprice
	initDlg:function(){
		var self = this;
		var editor = $("#levelPriceEditor");
	
		this.priceBox.find(".lvPrice").each(function(){//由.lvPrice找到 价格输入框
			var lvid = $(this).attr("lvid");
			$("#levelPriceEditor input.lvPrice[lvid='"+lvid+"']").val(this.value);
			
		});
		
		$("#mbPriceBtn").click(function(){
			self.createPriceItems();
		});
	},
	//由对话框的input 创建会员价格表单项
	createPriceItems:function(){
		var member_price_box = this.priceBox;
		member_price_box.empty();

		$("#levelPriceEditor>tbody>tr").each(function(){
			var tr = $(this);
			var lvid = tr.find(".lvid").val(); 
			var lvPrice = tr.find(".lvPrice").val(); 
			member_price_box.append("<input type='hidden' class='lvid' name='lvid' value='"+lvid+"' />");
			member_price_box.append("<input type='hidden' class='lvPrice' name='lvPrice' value='"+lvPrice+"' lvid='"+lvid+"'/>");
			
		});
		
		Eop.Dialog.close("memberpricedlg");
	}
};
$(function(){
	MemberPrice.init();
}); 