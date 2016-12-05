/**
 @Name：页面基础js
 @Author：Sylow 
 @version 1.0 2015-08-22
 */

/**
 * 返回顶部 
 */
function backTop(){
	$("html, body").animate({
		"scroll-top":0
	},"fast");
}

/**
 * 返回上一页面 也就是关闭当前页面
 */
function back(){
	e$.closeWin({});
};

/**
 * 时间戳转换为日期格式
 * @param timestamp 时间戳
 * @return 2015年09月01日 这种格式
 */
function toData(timestamp){
	return new Date(parseInt(timestamp) * 1000).format("yyyy年MM月dd日 hh:mm:ss");
};

/**
 * 获取一个完整的图片路径 
 */
function getImgSrc(url){
	//如果图片地址为空 就返回默认图片地址
	//alert(url);
	if(url == undefined) {
		url = "";
	}
	if (url == "") {
		url = APP_SETTING["default_img_url"];
	} else {
		url = url.replace(APP_CONFIG["file_store_prefix"], APP_CONFIG["static_img"]);
	}
	//alert(url);
	return url;
}

/**
 * 简单ajax
 * @param {String} url 地址 , 自动包装成包含域的地址
 * @param {Json} data 发送的数据
 * @param {Function} callback 执行完的回调函数
 * @deprecated  暂时无用 废弃
 */
function SimpleAjax(url, data, callback) {
	var url = getFullUrl(url);
	var options = {
		url : url,
		data : data,
		success : callback
	};
	e$.ajax(options);
}

/**
 * 初始化js模板
 * @param {JSON} data	json数据
 * @param {Object} tplObj 模板jquery对象
 * @param {Object} viewObj 视图jquery对象
 * @param {function} callback 执行完的回调函数
 */
function initTpl(data, tplObj, viewObj, callback) {
	var tpl = tplObj.html();
	//laytpl
	laytpl(tpl).render(data, function(viewHtml) {
		viewObj.html(viewHtml);
		if (callback) {
			callback(data);
		}
	});
}

/**
 * 获取一个完整的url 例如:http://192.168.1.116:8080/api/mobile/goods!listByTag.do
 * @param {Object} url 域后的路径  必须是绝对路径
 */
function getFullUrl(url) {
	var domain = APP_CONFIG['domain'];
	if (domain == undefined || domain == '') {
		domain = 'http://127.0.0.1:8080';
	}
	return domain + url;
}

Date.prototype.format = function(format) {
   var date = {
          "M+": this.getMonth() + 1,
          "d+": this.getDate(),
          "h+": this.getHours(),
          "m+": this.getMinutes(),
          "s+": this.getSeconds(),
          "q+": Math.floor((this.getMonth() + 3) / 3),
          "S+": this.getMilliseconds()
   };
   if (/(y+)/i.test(format)) {
          format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
   }
   for (var k in date) {
          if (new RegExp("(" + k + ")").test(format)) {
                 format = format.replace(RegExp.$1, RegExp.$1.length == 1
                        ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
          }
   }
   return format;
}