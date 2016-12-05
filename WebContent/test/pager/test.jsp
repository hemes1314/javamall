<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="../../statics/js/common/jquery-1.3.2.js"></script>
</head>

<body>
<ul>
   <li id=1>list item 1</li>
   <li id=2>list item 2</li>
   <li id=3 class="third-item">list item 3</li>
   <li id=4>list item 4</li>
   <li id=5>list item 5</li>
</ul>

<script>
$(function(){
	$('li').click(function(){
		var self = this;
		var flag=false;
		$("li").each(function(){
			if(flag){
				$(this).css('background-color', 'red');;
			}
			if(self==this){
				flag=true;
			} 

		});
	});
});

</script>

</body>
</html>
