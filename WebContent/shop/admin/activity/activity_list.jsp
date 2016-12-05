<#include '/admin/header.html' >
<#assign dateFormat="com.enation.framework.directive.DateformateDirective"?new()>
<style>
.searchAdvancedS{
	background-color: #FDEA7A;
}

</style>
<div id="loading"></div>
<div class="main">
	<form id="gridform" method="post">
		<div style="display: block;" class="searchAdvanced" >
			<table width="98%" border="0" cellspacing="0" cellpadding="8">
				<tr>
					<td width="70" align="right">促销名称</td>
					<td><input type="text" value="" id="name"  class="input_text" style="width: 95%;" ></td>
					
					<td width="70" align="right">促销编号</td>
					<td><input type="text" value="" id="id" class="input_text" style="width: 95%;"></td>
					
					<td width="70" align="right">促销状态</td>
					<td>
						<select name="status" id="status">
							<option value ="">全部</option>
							<option value ="1">未开始</option>
							<option value ="2">进行中</option>
							<option value ="3">暂停</option>
							<option value ="4">结束</option>
						</select>
					</td>
				</tr>
				<tr>
					<td width="70" align="right">促销时间</td>
					<td>
						<input class="input_text easyui-datetimebox" style="width: 160px;height: 28px;" id="start_time" /> <span>&nbsp;&nbsp;~&nbsp;&nbsp;</span>
						<input class="input_text easyui-datetimebox" style="width: 160px;height: 28px;" id="end_time" />
					</td>
					
					<td width="70" align="right"></td>
					<td><a id="searchAdvance" class="button blueButton" onclick="searchActivities()" href="javascript:;">开始搜索</a></td>
				</tr>
			</table>
		</div>
		<div class="clear height10"></div>
		<div class="shadowBoxWhite tableDiv">
			<table class="easyui-datagrid"  url="activity!listJson.do" data-options="pageList: [5,10,15,20],pageSize:${pageSize},fitColumns:'true'" 
			pagination="true" width="width" id="activitydata" sortName="activity_id" sortOrder="desc">
				<thead>
					<tr>
						<th data-options="field:'id',width:100,align:'center'" >促销编号</th>
						<th data-options="field:'name',width:200,align:'center'" >促销活动名称</th>
						<th data-options="field:'start_time',width:300,align:'center'" formatter="formatTime">促销开始时间</th>
						<th data-options="field:'end_time',width:300,align:'center'" formatter="formatTime">促销结束时间</th>
						<th data-options="field:'status',width:100,align:'center'">状态</th>
						<th data-options="field:'action',align:'center',width:200" formatter="formatAction">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</form>
</div>
<script type="text/javascript">
function formatName(value,row,index){
	var val="<a href=\"../../goods-"+row.activity_id+".html\"target=\"_blank\">"+row.name+"</a>";
	return val;
}

function formatMoney(value,row,index){
	var val="￥"+value;
	return val;
}

function formatTime(value,row,index){
	
	return getFormatDateByLong(value, "yyyy-MM-dd hh:mm:ss");
}

function formatAction(value,row,index){
	var val="";
	if(row.status=='未开始'){
		val+="<a class='edit' title='编辑' href='#' onclick='newTab(\""+(row.name).substring(0,11)+"..\",\"${ctx}/shop/admin/activity!edit.do?activityId="+row.id+"\")' ></a>";
	}
	val+="<a class='delete' title='删除' href='#' onclick='del("+ row.id +")' ></a>";
	return val;
}

function del(id){ 
	var rows = $('#activitydata').datagrid("getSelections"); 
	
	if(!confirm("确认删除促销活动吗？")){	
		return ;
	}
	
	$.Loading.show("正在删除......");
 	var options = {
			url : "activity!delete.do?ajax=yes&activityId=" + id,
			type : "POST",
			dataType : 'json',
			success : function(result) {
				if(result.result==1){
					$.Loading.success(result.message);
		 			$('#activitydata').datagrid("reload");      
				}
				if(result.result==0){
					$.Loading.error(result.message);
				}
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试");
			}
		};
		$('#gridform').ajaxSubmit(options);	
}

//搜索
function searchActivities(){

	var name = $.trim($("#name").val());
	var id = $.trim($("#id").val());
	var status = $("#status").val();
	var startTime = $("#start_time").datebox("getValue");
	var endTime = $("#end_time").datebox("getValue");
	
	if ((endTime != "") && (endTime < startTime)) {
		alert("结束时间不能小于开始时间");
		return false;
	}
	
	$('#activitydata').datagrid('load', {
		 name: name,
		 id: id,
		 status: status,
		 startTime: startTime,
		 endTime: endTime,
		 page: 1
    }); 
}

var buttons = $.extend([], $.fn.datebox.defaults.buttons);
buttons.splice(1, 0, {
	text: '清空',
	handler: function(target){
	 	$('#start_time').datebox('setValue',"");
	}
});

var e_buttons = $.extend([], $.fn.datebox.defaults.buttons);
	e_buttons.splice(1, 0, {
	text: '清空',
	handler: function(target){
		$('#end_time').datebox('setValue',"");
	}
});
 	
</script>

<div id="promotionRulesTemplate" style="display: none">
	<div class="pr">
		满<input class="input_text price01" name="prInput" data-options="required:false" class="prInput" style="width: 40px" 
		value=""/>(元)&nbsp;&nbsp;
		减<input class="input_text price01" name="prInput" data-options="required:false" class="prInput" style="width: 40px" 
		value=""/>(元)
	</div>
</div>

<#include '/admin/footer.html' >