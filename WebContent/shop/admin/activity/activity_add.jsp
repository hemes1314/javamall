<#include '/admin/header.html' > 
<style>
.pr {
	height: 40px;
}
</style>
<div class="main" style="background-color: white;">
	<form id="addForm">
		<input type="hidden" name="activity.id" id="id" value=""/>
		<table width="98%" border="0" cellspacing="0" cellpadding="8">
			<tr>
				<th>促销活动名称：</th>
				<td>
					<input class="input_text easyui-validatebox" type="text" id="name" name="activity.name" data-options="required:true" maxlength="30"></input>
				</td>
			</tr>
			<tr>
				<th>促销活动时间：</th>
				<td>
					<input class="input_text easyui-datetimebox" name="start_time" data-options="required:true" id="start_time" style="height: 28px;width: 150px" /> ~
					<input class="input_text easyui-datetimebox" name="end_time" data-options="required:true" id="end_time" style="height: 28px;width: 150px" />
				</td>
			</tr>
			<tr>
				<th>满减：</th>
				<td>
					<div id="promotionRulesDiv">
						<div class="pr">
							满&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs" style="width: 40px" />(元)
							&nbsp;&nbsp;
							减&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs prInputs2" style="width: 40px" />(元)
							&nbsp;&nbsp;
							<a href="javascript:addPr();">添加</a>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>是否启用：</th>
				<td>
					<select name="activity.is_enable" id="is_enable">
						<option value="1">是</option>
						<option value="0">否</option>
					</select>
				</td>
			</tr>
		</table>
	</form>
	<div class="buttonWrap fixed">
		<a id="searchAdvance" class="easyui-linkbutton" onclick="submitForm()" href="javascript:;">保存设置</a>
	</div>
	<div id="divdia"></div>
</div>
<script>
	$(function(){     
		/*JQuery 限制文本框只能输入数字和小数点*/  
		$(".price01").keyup(function(){    
				$(this).val($(this).val().replace(/[^0-9.]/g,''));    
			}).bind("paste",function(){  //CTR+V事件处理    
				$(this).val($(this).val().replace(/[^0-9.]/g,''));     
			}).css("ime-mode", "disabled"); //CSS设置输入法不可用    
    });  

	function removePr(obj) {
		$(obj).parent().remove();
	}
	
	function addPr() {
		var html = $("#promotionRulesTemplate").html();
		$("#promotionRulesDiv").append(html);
	}
	
	function submitForm() {
		var prState = 0; //校验金额是否合法
		var num1 = 0;
		var num2 = 0;
		var index = 1
		$("#promotionRulesDiv .pr").each(function() {
			var input1 = $(this).find("input").eq(0).val();
			var input2 = $(this).find("input").eq(1).val();
			var value1 = Number(input1);
			var value2 = Number(input2);

			if ($.trim(input1) == "" || $.trim(input2) == "") {
				prState = 1;
				return false;
			}
			if (value1 == 0 || value2 == 0) {
				prState = 5;
				return false;
			}
			if (value1 <= value2) {
				prState = 2;
				return false;
			}
			if(num1 >= value1)
			{
				prState = 3;
				return false;
			}
			if(num2 >= value2)
			{
				prState = 4;
				return false;
			}
			index++;
			num1 = value1;
			num2 = value2;
		}); 
		
		if(prState == 1)
		{
			alert("第" + index + "个活动，满金额或减金额不能为空！");
			return false;
		} else if (prState == 5) {
			alert("第" + index + "个活动，满金额或减金额值应大于0！");
			return false;
		}else if(prState == 2)
		{
			alert("第" + index + "个活动，满金额应大于减金额！");
			return false;
		}else if(prState == 3)
		{
			var message = "第"+index+"个活动，满金额应大于第"+(index-1)+"活动的满金额";
			alert(message);
			return false;
		}else if(prState == 4)
		{
			var message = "第"+index+"个活动，优惠金额应大于第"+(index-1)+"活动的优惠金额";
			alert(message);
			return false;
		}
		
		if ($.trim($("#name").val()) == "") {
			alert("名称不能为空");
			return false;
		}
		
		if ($("input[name='start_time']").val() == "") {
			alert("开始时间不能为空");
			return false;
		}
		
		if ($("input[name='end_time']").val() == "") {
			alert("结束时间不能为空");
			return false;
		}
		
		
		if ($("input[name='end_time']").val() < $("input[name='start_time']").val()) {
			alert("结束时间不能小于开始时间");
			return false;
		}
		
		var formflag = $("#addForm").form().form('validate');
		if (formflag) {
			$.Loading.show("正在添加......");
			var options = {
				url : "activity!saveActivity.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {
					if (result.result == 1) {
						$.Loading.success(result.message);
					}
					if (result.result == 0) {
						$.Loading.error(result.message);
					}
					newTab( "查看促销活动","${ctx}/shop/admin/activity!list.do");
					//window.location = "${ctx}/shop/admin/activity!list.do";
					parent.CloseTabByTitle("新增促销活动");

				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};
			$("#addForm").ajaxSubmit(options);
		}
	}
</script>

<div id="promotionRulesTemplate" style="display: none">
	<div class="pr">
		满&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs" style="width: 40px" value=""/>(元)
		&nbsp;&nbsp;
		减&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs prInputs2" style="width: 40px" value=""/>(元)
		&nbsp;&nbsp;
		<a onclick="removePr(this);" href="#">删除</a>
	</div>
</div>

<#include '/admin/footer.html' >