<#include '/admin/header.html' > 
<#assign dateformat= "com.enation.framework.directive.DateformateDirective"?new()>
<style>
.pr {
	height: 40px;
}
</style>
<div class="main" style="background-color: white;">
	<form id="addForm">
		<input type="hidden" name="activity.id" id="id" value="${activity.id}"/>
		<table width="98%" border="0" cellspacing="0" cellpadding="8">
			<tr>
				<th>促销活动名称：</th>
				<td>
					<input class="input_text easyui-validatebox" type="text" id="name" name="activity.name" data-options="required:true" value="${activity.name}" />
				</td>
			</tr>
			<tr>
				<th>促销活动时间：</th>
				<td>
					<input class="input_text easyui-datetimebox" name="start_time" data-options="required:true" id="start_time" style="height: 28px;width: 150px" value="<@dateformat time='${(activity.start_time)}' pattern='yyyy-MM-dd HH:mm:ss'/>" /> ~
					<input class="input_text easyui-datetimebox" name="end_time" data-options="required:true" id="end_time" style="height: 28px;width: 150px" value="<@dateformat time='${(activity.end_time)}' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
				</td>
			</tr>
			<tr>
				<th>满减：</th>
				<td>
					<div id="promotionRulesDiv">
					<div class="pr">
						满&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs" style="width: 40px" 
						value="<#if activity.promotionRules?size != 0>${activity.promotionRules[0].d1}</#if>"/>(元)
						&nbsp;&nbsp;
						减&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs prInputs2" style="width: 40px" 
						value="<#if activity.promotionRules?size != 0>${activity.promotionRules[0].d2}</#if>"/>(元)
						&nbsp;&nbsp;
						<a href="javascript:addPr()">添加</a>
					</div>
					<#list activity.promotionRules as pr>
						<#if pr_index != 0 >
							<div class="pr">
								满&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs" style="width: 40px" 
								value="<#if pr.d1?exists>${pr.d1}</#if>"/>(元)
								&nbsp;&nbsp;
								减&nbsp;<input class="input_text price01" name="prInputs" data-options="required:false" class="prInputs prInputs2" style="width: 40px" 
								value="<#if pr.d2?exists>${pr.d2}</#if>"/>(元)
								&nbsp;&nbsp;
									<a onclick="removePr(this);" href="#">删除</a>
							</div>
						</#if>
					</#list>
					</div>
				</td>
			</tr>
			<tr>
				<th>是否启用：</th>
				<td>
					<select name="activity.is_enable" id="is_enable">
						<option value="1" <#if activity.is_enable == 1>selected</#if>>是</option>
						<option value="0" <#if activity.is_enable == 0>selected</#if>>否</option>
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
		if ($.trim($("#name").val()) == "") {
			alert("名称不能为空");
			return false;
		}
		
		var prState = 0;
		$("#promotionRulesDiv .pr").each(function() {
			var input1 = $(this).find("input").eq(0).val();
			var input2 = $(this).find("input").eq(1).val();
			if ($.trim(input1) == "" || $.trim(input2) == "") {
				prState = 1;
				return;
			}
			if (parseInt(input1) <= parseInt(input2)) {
				prState = 2;
				return;
			}
			
		});
		
		if (prState > 0) {
			if (prState == 1)
				alert("满减不能为空");
			if (prState == 2)
				alert("第一位数字必须大于第二位数字");
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
		
		if ($("input[type=checkbox]").eq(0).attr("checked")) {
			if ($.trim($("#fill_minus").val()) == "") {
				alert("满减不能为空");
				return false;
			}
			
			if ($.trim($("#minus_value").val()) == "") {
				alert("满减不能为空");
				return false;
			}
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
						window.location = "${ctx}/shop/admin/activity!list.do";
					}
					if (result.result == 0) {
						$.Loading.error(result.message);
					}
					//parent.CloseTab();
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
	<script>
	$(function(){     
		/*JQuery 限制文本框只能输入数字和小数点*/  
		$(".price01").keyup(function(){  
				$(this).val($(this).val().replace(/[^0-9.]/g,''));    
			}).bind("paste",function(){  //CTR+V事件处理    
				$(this).val($(this).val().replace(/[^0-9.]/g,''));     
			}).css("ime-mode", "disabled"); //CSS设置输入法不可用    
    });  
	</script>
</div>
<#include '/admin/footer.html' >