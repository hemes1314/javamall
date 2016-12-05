$(function(){
		$(".Recommend ul li dl dt").hover(
			function(){
				var that=this;	
				item1Timer=setTimeout(function(){
					$(that).find("a").animate({"top":0,"height":300},300,function(){
						$(that).find("h3").fadeIn(200);
						$(that).find("p").fadeIn(200);
					});
				},100);
			},
			function(){
				var that=this;	
				clearTimeout(item1Timer);
				$(that).find("h3").fadeOut(200);
				$(that).find("p").fadeOut(200);
				$(that).find("a").animate({"top":150,"height":0},300);
			}
		)
});
	
