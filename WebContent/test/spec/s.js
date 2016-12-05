function zuhe_ar(ar1,ar2){
	var new_ar = new Array();
	for(var i=0;i<ar1.length;i++){
		for(var j=0;j<ar2.length;j++){
			//可能是白，也可能是[白,x]
			var temp = cloneAr(ar1[i]);
			if(temp.value){
				new_ar.push([temp,ar2[j]]); //白的情况
			}else{
				temp.push(ar2[j]);
				new_ar.push(temp);	
			}			 
			
		}
	}
	
	return new_ar;	
}

function cloneAr(ar){
	var new_ar=[];
	for(var i in ar){
		new_ar[i]=ar[i];
	}
	return new_ar;
}


var SpecOper={
	specProp:{},
	
	init:function(){
		var self= this;
		this.syncSpecFromTable();
		this.bindTableEvent();
		self.buttonBind();
		
		$("#spec-input .spec .chk").click(function(){
			self.createSpec();
		});
		
	},
	
	/**
	 * 开启或关闭规格按钮事件
	 */
	buttonBind:function(){
		
		var self = this;
		$("#specOpenBtn").click(function(){
			self.openSpec();
		});

		$("#specCloseBtn").click(function(){
			
			var goodsid = $(this).attr("goodsid");
			if("0"!=goodsid){
				$.ajax({
					url:"../../admin/plugin?action=check-goods-in-order&ajax=yes&beanid=goodsSpecPlugin&goodsid="+goodsid,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							if(confirm("此商品已经有客户购买，如果关闭规格此订单将无法配货发货，请谨慎操作!\n点击确定关闭规格，点击取消保留此规格。")){
								self.closeSpec();
							}
						}else{
							self.closeSpec();
						}
					},
					error:function(){
						alert("检测出错");
					}
				});
			}else{
				self.closeSpec();
			}
			
		});		
		
	}
	,
	
	/**
	 * 开启规格
	 */
	 closeSpec:function(){
	
		$("#spec-input").hide();
		$("#no-spec-input").show();
		$("#haveSpec").val(0);
		
	}
	
	/**
	 * 关闭规格
	 */
	,
	openSpec:function(){
		$("#spec-input").show();
		$("#no-spec-input").hide();
		$("#haveSpec").val(1);
	}
	
	
	,
	createSpec:function(){
		var self = this;
		
		var specUl= $("#spec-input ul");
		var specs=[];
		var specnames=[];
		$.each( specUl,function(i,ul){
			var chks = $(ul).find(".chk:checked");
			if(chks.size()>0){
				var valueAr =self.createSpecFromChk(chks); //根据chk生成的规格值数组
				specs.push(valueAr);
				specnames.push($(ul).attr("specname"));
			}
		} );
		
		if(specs.length>1){
			$(".spec_table").show();
			var temp =specs[0];
			for(var i=1;i<specs.length;i++){		 
				temp = zuhe_ar(temp,specs[i]);
			}
			$.ajax({
				url:'../../shop/admin/memberPrice!disLvInput.do?ajax=yes',
				dataType:'html',
				success:function(html){
					self.createSpecHead(specnames);
					self.createSpecBody(temp,html);	
					self.bindTableEvent();
					MemberPrice.bindMbPricBtnEvent();
				},
				error:function(){
					alert("获取会员价出错");
				}
			});

		}else{
			$(".spec_table").hide();
		}
		
		
	},
	bindTableEvent:function(){
		var self = this;
		$(".ipt").unbind("blur").bind("blur",function(){
			var $this= $(this);
			var propid = $this.attr("propid");
			if(!self.specProp[propid]){
				self.specProp[propid] =[];
			}
			
			self.specProp[$this.attr("propid")][$this.attr("prop")]=$this.val();
		});
		
		$(".spec_table .delete").unbind('click').bind('click',function(){
			self.deleteProRow($(this));
		});
		
	}
	,
	createSpecHead:function(specnames){
		
		var thead=$(".spec_table thead tr").empty();
		
		for(var i in specnames){
			thead.append("<th style='width: auto;'>"+specnames[i]+"</th>");
		}
		thead.append("<th  style='width: 200px;'>货号</th><th style='width: 150px;'>价格</th><th  style='width: auto;'>重量</th><th style='width: auto;'>成本价</th><th style='width: auto;'>操作</th>");
		
	} 
	
	,

	/**
	 * 生成货品表格
	 * @param specAr  规格数组
	 * @param lvPriceHtml 会员价格页面
	 */
	createSpecBody:function(specAr,memPriceInput){
		

		
		
		var self = this;
		var body=$(".spec_table tbody");
		body.empty();
		for(i in specAr){ 
			
			var priceInputHtml = memPriceInput.replace(/name="lvid"/g,'name="lvid_'+i+'"');
			priceInputHtml = priceInputHtml.replace(/name="lvPrice"/g,'name="lvPrice_'+i+'"');
			
			var childAr=specAr[i];//这是一行
			var tr=$("<tr></tr>");
			
			
			var propid="";
			var specvids="";
			var specids ="";
			
			for(j in childAr){ //这是一列
				var spec = childAr[j];
				if(propid!="")propid+="_";
				propid+=spec.valueid;				
				tr.append($("<td>"+spec.value+"</td>"));

				if(j!=0){
					specvids+=",";
					specids+=",";
				}
				specvids+=spec.valueid;
				specids +=spec.specid;
				
			}
			var specProp = self.specProp[propid];
			var price =0;
			var weight= 0;
			var cost = 0;
			var sn="";
			var productid="";
			
			if(specProp){
				price = specProp["price"];
				weight= specProp["weight"];
				cost = specProp["cost"];
				sn = specProp["sn"];
				productid=specProp["productid"];
				
				if(!price)price ="0";			
				if(!weight)weight ="0";
				if(!cost)cost ="0";	
				if(!sn)sn="";
				if(!productid)productid="";
				
			}
			
			
			var hidden ='<input type="hidden" value="'+specvids+'" name="specvids">';
				hidden+='<input type="hidden" value="'+specids+'" name="specids">';
			
			var td ='<td><input type="text" class="ipt" style="width:150px" name="sns" value="'+sn+'" autocomplete="off" propid="'+propid+'" prop="sn">';
			td+='<input type="hidden"  name="productids" value="'+productid+'" class="ipt" propid="'+propid+'" prop="productid"></td>';
			td+="<td>"+hidden+"<input class='ipt price' propid='"+propid+"' prop='price' size='8'  value='"+price+"' type='text'  name='prices' />";
			td+='<input type="button" class="mempriceBtn" value="会员价" /><div class="member_price_box" index="'+i+'">'+priceInputHtml+'</div></td>';
			
			td+='<td><input type="text" size="10" name="weights" value="'+weight+'" autocomplete="off" class="ipt" propid="'+propid+'" prop="weight"></td>';
			td+='<td><input type="text" size="8" name="costs" value="'+cost+'" autocomplete="off"  class="ipt" propid="'+propid+'" prop="cost"></td>';
			td+='<td><a  href="javascript:;"><img  class="delete" src="images/transparent.gif" productid="0"> </a></td>';
			tr.append($(td));
			
			body.append(tr);
			
			if(specProp){
				member_price =specProp["member_price"];//会员价格是数组
				
				//同步会员价格
				if(member_price){
					var lvPriceEl = tr.find(".member_price_box .lvPrice");
					lvPriceEl.each(function(pindex,v){
						$(this).val(member_price[pindex]);
					});					
				}
			}
		}
	}
	,
	
	/**
	 * 根据checkbox生成规格数组
	 */
	createSpecFromChk:function(chks){
		var ar=[];
		$.each(chks,function(i,c){
			var chk=$(c);
			var spec={};
			spec.valueid=parseInt(chk.val());
			spec.specid=parseInt(chk.attr("specid"));
			spec.value=chk.attr("spec_value");
			ar.push(spec);
			
		});
		return ar;
	}
	,
	
	/**
	 * 由规格表格同步规格
	 * 1.价格、重量等属性至specProp对象
	 * 2.选中checkbox
	 */
	syncSpecFromTable:function(){
		var self = this;
		$(".spec_table tbody tr").each(function(i,v){
			
			var tr=$(this);
			var inputs=tr.find(".ipt");
			var propid =inputs.attr("propid");
			self.specProp[propid]=[];
			
			//同步各个属性
			inputs.each(function(){
				$this= $(this);
				var propname= $this.attr("prop");
				self.specProp[propid][propname]=$this.val();
				 
			});
			
			
			
			//同步规格复选框 
			var propidAr  = propid.split("_");
			for(var i in propidAr){
				$("input[value="+propidAr[i]+"]").attr("checked",true);
			}
			
		});
	},
	/**
	 * 由规格表格中同步会员价
	 */
	syncMemberPriceFromTable:function(){
		var self = this;
		
		$(".spec_table tbody tr").each(function(i,v){
			var tr=$(this);
			var inputs=tr.find(".ipt");
			var propid =inputs.attr("propid");
			var member_price=[];
			//同步会员价信息
			inputs= tr.find(".member_price_box input.lvPrice");
			inputs.each(function(){
				var value = $(this).val();
				member_price.push(value);
				
			});
			self.syncMemberPrice(propid, member_price);			
			
		});
	}
	
	,
	
	/**
	 * 同步某个货品的会员价格
	 * @param propid
	 * @param member_price
	 */
	syncMemberPrice:function(propid,member_price){ 
		var oneSpecProp = this.specProp[propid];
		if(!oneSpecProp){
			this.specProp[propid]=[];
			 oneSpecProp= this.specProp[propid];
		}
		oneSpecProp["member_price"]=member_price;
	}

	/**
	 * 删除一行规格
	 */
	,
	deleteProRow:function(link){
		if(confirm("确定删除此货品吗？删除后不可恢复")){
			var productid = link.attr("productid");
			if("0"!=productid && "0"!=productid){
				$.ajax({
					url:"../../admin/plugin?action=check-pro-in-order&ajax=yes&beanid=goodsSpecPlugin&productid="+productid,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							if(confirm("此货品已经有顾客购买，如果删除此订单将不能配货发货，请谨慎操作!\n点击确定删除此货品，点击取消保留此货品。")){
								link.parents("tr").remove();
							}
						}else{
							link.parents("tr").remove();
						}
					},
					error:function(){
						
					}
				});
			}else{
				link.parents("tr").remove();
			}
					
		}
		
	}
	
 
};

$(function(){
	SpecOper.init();
});