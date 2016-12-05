var spec1=
[
{value:'红',valueid:3,specid:1},
{value:'黑色',valueid:3,specid:1}
];

var spec2=
[
{value:'S',valueid:3,specid:2},
{value:'SL',valueid:3,specid:2}
 ];
var spec3=
[
{value:'棉',valueid:2,specid:3},
{value:'真丝',valueid:3,specid:3}
];

var spec=[spec1,spec2,spec3];
 
var size=spec.length;

/**
 * 组合两个数组
 * @param ar1
 * @param ar2
 */
function zuhe_ar(ar1,ar2){
	var new_ar = new Array();
	for(var i=0;i<ar1.length;i++){
		for(var j=0;j<ar2.length;j++){
			//可能是白，也可能是[白,x]
			var temp = cloneAr(ar1[i]);
			if(temp.value){
				new_ar.push([temp,ar2[j]]); //白的情况
			}else{
				temp.push(ar2[j]);
				new_ar.push(temp);	
			}			 
			
		}
	}
	
	return new_ar;	
}

function cloneAr(ar){
	var new_ar=[];
	for(var i in ar){
		new_ar[i]=ar[i]
	}
	return new_ar;
}

function zh(){
	var temp =spec[0];
	for(var i=1;i<spec.length;i++){		 
		temp = zuhe_ar(temp,spec[i]);
	}
	print(temp);
}
 
function print(ar){
	 
	for(var i=0;i<ar.length;i++){
 
		log(ar[i]);
	}
	
	
}


function log(text){
	var value="";
	for(t in text){
		value+=text[t].value;
		value+=",";
	}
	$("#log").append("<li>"+value+"</li>");
}
$(function(){
	zh();
});
