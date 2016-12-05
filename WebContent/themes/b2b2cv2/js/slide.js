window.onload=function(){
   function adsorption(){
    var headerWrap=document.getElementById('r-float-hide');
    var scrollTop=0;
    window.onscroll=function(){
     scrollTop=document.body.scrollTop||document.documentElement.scrollTop;
     if(scrollTop>600){
      headerWrap.className='r-float';
     }else{
      headerWrap.className='r-float-hide';
     }
    }
   }
   adsorption();
}