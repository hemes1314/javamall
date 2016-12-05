var CreatedOKLodop7766=null;

function getLodop(oOBJECT,oEMBED){
/**************************
  本函数根据浏览器类型决定采用哪个页面元素作为Lodop对象：
  IE系列、IE内核系列的浏览器采用oOBJECT，
  其它浏览器(Firefox系列、Chrome系列、Opera系列、Safari系列等)采用oEMBED,
  如果页面没有相关对象元素，则新建一个或使用上次那个,避免重复生成。
  64位浏览器指向64位的安装程序install_lodop64.exe。
**************************/
       
        var LODOP;		
	try{	
	     //=====判断浏览器类型:===============
	     var isIE	 = (navigator.userAgent.indexOf('MSIE')>=0) || (navigator.userAgent.indexOf('Trident')>=0);
	     var is64IE  = isIE && (navigator.userAgent.indexOf('x64')>=0);
	     //=====如果页面有Lodop就直接使用，没有则新建:==========
	     if (oOBJECT!=undefined || oEMBED!=undefined) { 
               	 if (isIE) 
	             LODOP=oOBJECT; 
	         else 
	             LODOP=oEMBED;
	     } else { 
		 if (CreatedOKLodop7766==null){
          	     LODOP=document.createElement("object"); 
		     LODOP.setAttribute("width",0); 
                     LODOP.setAttribute("height",0); 
		     LODOP.setAttribute("style","position:absolute;left:0px;top:-100px;width:0px;height:0px;");  		     
                     if (isIE) LODOP.setAttribute("classid","clsid:2105C259-1E0C-4534-8141-A753534CB4CA"); 
		     else LODOP.setAttribute("type","application/x-print-lodop");
		     document.documentElement.appendChild(LODOP); 
	             CreatedOKLodop7766=LODOP;		     
 	         } else 
                     LODOP=CreatedOKLodop7766;
	     };
	     //=====判断Lodop插件是否安装过，没有安装或版本过低就提示下载安装:==========
	     if ((LODOP==null)||(typeof(LODOP.VERSION)=="undefined")) {
	    	 if( $("#lodopDownDlg").size() ==0 ){
	    		 $("body").append("<div id='lodopDownDlg' style='padding: 10px 0 10px 60px'>您未安装Javashop打印控件，请点击此处 <a href=\"http://www.javamall.com.cn/statics/lodop.zip\">下载控件</a>。</div>");
	    	 }
            	 $("#lodopDownDlg").show();
            	　　	$('#lodopDownDlg').dialog({
            	　　		title: '下载打印控件',			
            	　　		width: 600,
            	　　		closed: false,
            	　　		cache: false,
            	　　		modal: true,
            	　　		buttons: [{				
            	　　			 text:'关闭',
            	　　			 handler:function(){
            	　　				 $("#lodopDownDlg").dialog('close');
            	　　				 $.Loading.hide();
            	　　			 }
            	　　		}]
            	　　	});
	        return LODOP;
	     } else 
	     if (LODOP.VERSION<"6.1.8.0") {
	             if (is64IE){
	            	 if(confirm("您的打印控件需要升级，是否执行升级，升级后请重新进入。")){
	            		 location.href="http://www.javamall.com.cn/statics/lodop.zip";
	            	};
	             }  else if (isIE){
	            	 if(confirm("您的打印控件需要升级，是否执行升级，升级后请重新进入。")){
	            		 location.href="http://www.javamall.com.cn/statics/lodop.zip";
	            	};
	             }
	    	     return LODOP;
	     };
	     //=====如下空白位置适合调用统一功能(如注册码、语言选择等):====	     
	     LODOP.SET_LICENSES("易族智汇（北京）科技有限公司","9D32CB078EE17DE9676D08BCA683CAEB","","");

	     //============================================================	     
	     return LODOP; 
	} catch(err) {
	     if (is64IE)	
            document.documentElement.innerHTML="Error:"+strHtm64_Install+document.documentElement.innerHTML;else
            document.documentElement.innerHTML="Error:"+strHtmInstall+document.documentElement.innerHTML;
	     return LODOP; 
	};
};
