
function fileDialogComplete(numFilesSelected, numFilesQueued){
	this.debug("-------选择了上传文件 "  +  numFilesSelected + "个" );
	this.startUpload();
}

function uploadStart(file) {
	var progress = new FileProgress(file, this.customSettings.progressTarget);
	progress.setStatus("上传" + file.name );
	this.debug("-------上传文件 "  +  file.name );
}

function uploadProgress(file, bytesLoaded, bytesTotal){
	var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
	var progress = new FileProgress(file, this.customSettings.progressTarget);
	progress.setProgress( percent);
	
	this.debug("-------正在上传文件 "  +  file.name +" file id is " + file.id );
}


function uploadSuccess(file, r) {
 
		var name = r;
		var progress = new FileProgress(file, this.customSettings.progressTarget);
	 
		 progress.setSuccess(name,file.id);
		 var id  = file.id;
		 //alert(id.substring(id.length-2 ,id.length));
		 if(id.substring(id.length-2 ,id.length)=='_0'){
			 	
		 		viewPic(name,file.id);
		 }
		 bindPhotoEvent();
		this.debug("-------上传文件 "  +  file.name +" 完成 " + r );
	 
	 
}

function uploadComplete(file) {
	this.debug("-------上传文件111111 "  +  file.name +" 完成 " +this.getStats().files_queued );
	
}

function bindPhotoEvent(){
	
	//绑定切换默认图片事件
	 $(".proWrapper>a").unbind("click");
	 $(".proWrapper>a").click(function(){
		 var link = $(this);
		 
		 viewPic(link.children("img").attr("src"));
		
		 $(".proWrapper.selected").removeClass("selected");
		 link.parent().addClass("selected");
	 });
	 
	 
	 //绑定图片删除事件
	 $(".progressContainer .deleteBtn").unbind("click");
	 $(".progressContainer .deleteBtn").click(function(){
		 deletePic($(this));
	 });
}

/**
 * 设置默认图片
 * @param imgPath
 * @return
 */
function viewPic(imgPath){
	 var preImg = $("<img src='"+imgPath +"' width='240' height='220' border=0 />" );
	 $("#imgPrivew").empty()
	 .append(preImg);
	 $("#defaultpic").val(imgPath);
}

/**
 * 删除图片
 * @param delBtn
 * @return
 */
function deletePic(delBtn){
	 var pre =$("#imgPrivew");
	 
	 var defaultpic = $("#imgPrivew>img");
	
 
	var imgName= delBtn.parents("div.progressContainer").find("img.uploadImg").attr("src"); //要删除的图片地址

	$.ajax({
		type: "get",
		url:'../../api/shop/goods-gallery!delete.do?ajax=yes',
		data:"photoName="+imgName+"&rdm=" + new Date().getTime(),
		dataType:"json",
		success:function(result){
		   if(result.result==1){
				delBtn.parents(".progressContainer").remove();
				if(defaultpic.attr("src") == imgName) { //正在删除默认图片
					var inputs =  document.getElementById("uploadProgress").getElementsByTagName("input");
					if(inputs.length>0){
						var defaultInput = inputs[0];
					 	var defname = defaultInput.value;
					 	viewPic(defname);
				 	}else if(inputs.length==0){
				 		pre.html("<p style=\"margin-top:100px\">此处显示商品页默认图片<br/>[您还未上传商品图片！]</p>");
				 		document.getElementById("defaultpic").value="";
				 	}
				}else{
					//alert(defaultpic.id.replace('img_','') +"--" + id.replace('container_','') );
				}			   
		   }else{
  				alert(result.message);
  			}
		 
		},
		 error :function(res){alert("请求失败" + res);}
	});
	
	

}

 
function initUpload(ctx,ext){
 
	var settings = {
		flash_url :  ctx+"/resource/com/enation/app/shop/component/gallery/plugin/js/swfupload.swf",
		upload_url: ctx+"/api/shop/goods-gallery!upload.do",	// Relative to the SWF file
		post_params: {"ajax" : 'yes'},
		file_size_limit : "10 MB",
		file_types : "*.gif;*.jpg;*.bmp",
		file_types_description : "gif jpg bmp",
		file_upload_limit : 100,
		file_queue_limit : 0,
		custom_settings : {
			progressTarget : "uploadProgress",
			cancelButtonId : "btnCancel"
		},
		debug: false,

		// Button settings
		button_image_url: ctx+"/resource/com/enation/app/shop/component/gallery/plugin/images/XPButtonUploadText_61x22.png",	// Relative to the Flash file
		button_width: "61",
		button_height: "22",
		button_placeholder_id: "spanButtonPlaceHolder",
		//button_text: '<span class="theFont">上传图片</span>',
		//button_text_style: ".theFont { font-size: 12px; }",
		button_text_left_padding: 12,
		button_text_top_padding: 3,

		file_dialog_complete_handler : fileDialogComplete,
		upload_start_handler : uploadStart,
		upload_progress_handler : uploadProgress,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete
	}; 
	swf=new SWFUpload(settings);
}
	
function deleteUnSavePic(imgName){
	$.ajax({
		type: "get",
		url:ctx+'/api/shop/goods-gallery!delete.do?ajax=yes',
		data:"photoName="+imgName+"&rdm=" + new Date().getTime(),
		dataType:"json",
		success:function(result){
			 
		}
	});
 
}
