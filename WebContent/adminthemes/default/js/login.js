var Loginer={
		init:function(){
			var self = this;
			self.initValidCode();
			if($("#username").val()){$("#psdinput").focus();}
			$("#login_btn").click(function(){
				self.login();
			});
			$("#login_btn").attr("disabled",false).val("确定"); 
			
			$(document).keydown(function(event){
				if(event.keyCode==13){
					self.login();
				}
				
			});
			
		},
		login:function(){
			$("#login_btn").attr("disabled",true).val("正在登录..."); 
			 
			var options = {
					url : "../core/admin/adminUser!login.do",
					type : "POST",
					dataType : 'json',
					success : function(result) {				
						if(result.result==1){
							var referer=$("#referer").val();
							var url = "backendUi!main.do";
							if(referer!=""){
								url=referer;
								//url+="?referer="+referer;
							}
							location.href=url;
						}else{
							$("#login_btn").attr("disabled",false).val("确定"); 
							 alert(result.message);
						}
					},
					error : function(e) {
						$("#login_btn").attr("disabled",false).val("确定"); 
						alert("出现错误 ，请重试");
					}
				};

				$('form').ajaxSubmit(options);		
		},
		initValidCode:function(){
		 
			$("#username").focus();
		    var that =this;
			$("#code_img").attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime())
			.click(function(){
				$(this).attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime() );
				
			});		
		}
}