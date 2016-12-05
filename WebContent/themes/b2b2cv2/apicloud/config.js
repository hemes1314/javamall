/**
 @Name：基础配置js
 @Author：Sylow
 @version v1.0  -  2015-08-22
 */

var APP_CONFIG = {
	domain : 'http://192.168.1.99:8080',
	app : 'yes',
	debug : 'no',
	file_store_prefix : 'fs:',
	static_img : 'http://192.168.1.99:8080/statics'
};

/**
 * 动态加载文件  js 或者 css
 */
function loadjscssfile(filename, filetype) {
	if (filetype == "js") {
		var fileref = document.createElement('script');
		fileref.setAttribute("type", "text/javascript");
		fileref.setAttribute("src", filename);
	} else if (filetype == "css") {

		var fileref = document.createElement('link');
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}
	if ( typeof fileref != "undefined") {
		document.getElementsByTagName("head")[0].appendChild(fileref);
	}

}

/**
 * 从服务器加载配置js  对象名为：APP_SETTING 
 */
loadjscssfile(APP_CONFIG["domain"] + "/api/mobile/config!get.do", "js");