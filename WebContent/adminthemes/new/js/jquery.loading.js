(function($){

	$.Loading = $.Loading || {};
	
	if(top!=window){
		$.Loading=parent.$.Loading;
	}else{
		$.Loading.show=function(text){
			
			$.blockUI({ 
		         css: { top: '10px',backgroundColor:'#f8c684',border:'1px solid #f7b885',color:'#fff',width:'auto',padding:'0px 40px'} , 
				 message:text,
				 showOverlay:false
		    }); 
		
		};
		$.Loading.success=function(text){
			$.blockUI({ 
		         css: { top: '10px',backgroundColor:'#9adda7',border:'1px solid #a8cbb9',color:'#fff',width:'auto',padding:'0px 40px 0px 20px'} , 
				 message:"<div class='success'>"+text+"</div>",
				 showOverlay:false
		    }); 
			setTimeout($.unblockUI, 5000); 
		};
		
		$.Loading.error=function(text){
			$.blockUI({ 
		         css: { top: '10px',backgroundColor:'#faa499',border:'1px solid #f49292',color:'#fff',width:'auto',padding:'0px 40px 0px 20px'} , 
				 message:"<div class='error'>"+text+"</div>",
				 showOverlay:false
		    }); 
			setTimeout($.unblockUI, 5000); 
		};
		
		//

		$.Loading.hide=function(){
			$.unblockUI();
		};
	}

	
})(jQuery);