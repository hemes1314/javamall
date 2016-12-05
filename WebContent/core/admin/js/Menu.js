var currimg;
var this_img;
function onClick(event, treeId, treeNode, clickFlag){
	$("#detail_wraper").load("menu!edit.do?id="+treeNode.menuid,function(){
		//$("#editForm").validate();
		$("#saveEditBtn").click(function(){ 
			saveEdit();
		});
		$(".icon_wrap img").click(function(){ 
			this_img= $(this);
			openImgDlg(setSrc);
		});
	});
}
function saveAdd(){
	
	/*if( !$("form").checkall() ){
		return ;
	}*/
	$.Loading.show('正在保存，请稍侯...');
	var options = {
			url :"menu!saveAdd.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
			 	if(result.result==1){
			 		$.Loading.success("保存成功");
			 		$("#menuid").attr("disabled",false).val( result.menuid );
					$("#saveAddBtn").unbind().bind("click",function(){
						saveEdit();
					});
			 	}else{
			 		alert(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
				}
		};
	$("form").ajaxSubmit(options);	
	
}


function saveEdit(){
//	if( !$("form").checkall() ){
//		return ;
//	}
	$.Loading.show('正在保存，请稍侯...');
	var options = {
			url :"menu!saveEdit.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
				 
			 	if(result.result==1){
			 		
			 		$.Loading.success("保存成功");
			 		 
			 	}else{
			 		alert(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
				}
		};

	$("form").ajaxSubmit(options);			
}


function setSrc(path){
	this_img.attr("src",path);
	alert(path);
	$("#menu_icon").val(path);
	alert($("#menu_icon").val());
	this_img.prev(".icon").val(path);
}


function deleteMenu(id){
	$.Loading.show('请稍侯...');
	$.ajax({
		url:'menu!delete.do?id='+id,
		type:'post',
		dataType:'json',
		success:function(result){
			 
		 	if(result.result==1){
		 		$.Loading.success("删除成功");
		 	 
		 	}else{
		 		$.Loading.hide();
		 		$.Loading.error(result.message);
		 	}			
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});
}


function beforeRemove(treeId, treeNode) {
	if ((treeNode.children == undefined)) {
		return confirm("确认删除菜单 " + treeNode.name + " 吗？");
	} else {
		alert("不能删除有子菜单的选项！");
		return false;
	}
}

function onRemove(e, treeId, treeNode) {
	deleteMenu(treeNode.menuid);
}


function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy){
	var node  = treeNodes[0];
	// alert(moveType+"  "+ node.parentTId +"　　" +targetNode.parentTId);
	moveNode(node.menuid,targetNode.menuid,moveType);
}


function moveNode(menuid,target_menu_id,moveType){
	
	$.Loading.show('请稍侯...');
	$.ajax({
		url:'menu!move.do?id='+menuid+"&targetid="+target_menu_id+"&movetype="+moveType,
		type:'post',
		dataType:'json',
		success:function(result){
			 
		 	if(result.result==1){
		 		$.Loading.success("菜单移动成功");
		 		
		 	}else{
		 		$.Loading.error(result.message);
		 	}	
		 	
		 	
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});	
	
}

 



$(function(){
	
	var setting = {
			async: {
				enable: true,
				url:"menu!json.do",
				autoParam:["menuid"]
			},
			callback: {
				onClick: onClick,
				beforeRemove: beforeRemove,
				onRemove: onRemove,
				onDrop:onDrop
			},
			edit:{
				drag:{
					isCopy:false
				},
				enable:true,
				showRenameBtn:false
			}
			
		};	
	
	$.fn.zTree.init($(".ztree"), setting);
 
	$("#add-menu-btn").click(function(){
		$("#detail_wraper").load("menu!add.do?parentid=0",function(){
			$("#saveAddBtn").click(function(){
				saveAdd();
			});
			$(".icon_wrap img").click(function(){
				this_img= $(this);
				openImgDlg(setSrc);
			});
		});
	});
	
});

