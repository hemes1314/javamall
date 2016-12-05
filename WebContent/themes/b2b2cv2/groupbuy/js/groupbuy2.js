 var ams = [];
    var day = [];
    var hour = [];
    var minute = [];
    var second = [];
    function takeCount1() {
        setTimeout("takeCount1()", 1000);
        for (var i = 0, j = ams.length; i < j; i++) {
        	//alert(111);
            ams[i] -= 1;
            //计算天、时、分、秒、
            var days = Math.floor(ams[i] / (1 * 60 * 60 * 24));
            var hours = Math.floor(ams[i] / (1 * 60 * 60)) % 24;
            var minutes = Math.floor(ams[i] / (1 * 60)) % 60;
            var seconds = Math.floor(ams[i] / 1) % 60;
            if (days < 0) days = 0;
            if (hours < 0) hours = 0;
            if (minutes < 0) minutes = 0;
            if (seconds < 0) seconds = 0;
            //将天、时、分、秒插入到html中
           $(".day[i]").innerHTML = days;
           //alert(days);
            $(".hour[i]").innerHTML = hours;
            $(".minute[i]").innerHTML = minutes;
            $(".second[i]").innerHTML = seconds;
        }
    }
    setTimeout("takeCount1()", 1000);