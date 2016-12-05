/*!

 @Name jquery-eapi v1.0
 @Author Sylow
 @Site http://www.javamall.com.cn/
 @License Commercial
 @version v1.0 2015-08-22
 */

;! function(window, undefined) {
"use strict";

	var EApi = function(selector, context) {
		return new EApi.fn.init(selector, context);
	};
	
	EApi.fn = EApi.prototype = {
		constructor : EApi,
		init : function(selector, context ) {
			var match, elem, ret, doc;
			//读取config
			this.initConfig();
			
			if ($.isFunction(selector)) {
				this.ready(selector);
			} else if ( typeof selector === "string" ){
				this.selector = selector;
				return this;
			}

		},
		ready : function(fun) {
		
			//如果是app 则用apiCloud
			if (EApi.config.app == 'yes') {
			
				//调试时用jquery
				if (EApi.config.debug == 'yes') {
					$(fun);
				} else {
					window.apiready = function() {
						api.setStatusBarStyle({
							style : 'light'
						});
						fun();
					};
				}

			} else {
				$(fun);
			}
		},
		initConfig : function() {
		
			//一些配置 从 config.js中读取并覆盖
			var options = window.APP_CONFIG;
			EApi.config = $.extend(EApi.config, options);
		},
		submitForm :function (options) {
			//此方法的url不能带参数 也就是不能含有? 否则重复
			options.url = options.url + '?' + $(this.selector).serialize();
			EApi.ajax(options);
		}
		
	};

	EApi.fn.init.prototype = EApi.fn;
	
	EApi.config = EApi.fn.config = {
		v : '1.0',
		app : 'yes',
		debug : 'no'
	};
//	//版本
//	EApi.v = EApi.fn.v = '1.0';
//	//是否是app
//	EApi.config.app = EApi.fn.app = 'yes';
//	//是否是调试模式
//	EApi.debug = EApi.fn.debug = 'no';

	/**
	 * ajax
	 */
	EApi.ajax = EApi.fn.ajax = function(options) {
		var conf = options;
		// 如果是app  则使用jquery jsonp跨域
		if (EApi.config.app == 'yes') {
			conf.data = $.extend({
				"isJsonp" : 1
			}, conf.data);
			conf.dataType = 'JSONP';
			conf = $.extend({
				jsonp : "callback"
			}, conf);
			if (conf.error == undefined) {
				conf.error = function() {
					e$.alert("系统错误，请稍后重试。");
				};
			}
		}
		$.ajax(conf);
	};

	/**
	 * openWin 打开新窗口
	 * @param options 配置   包括：name 页面的名称（逻辑上用） url 路径（不能使用绝对路径） pageParam 参数（json格式）
	 * @param callback	回调函数
	 */
	EApi.openWin = EApi.fn.openWin = function(options, callback) {
		if (EApi.config.app == 'yes') {
			var conf = {
				bounces : false,
				opaque : false
			}
			conf = $.extend(conf, options);
			api.openWin(conf, callback);

		} else {
			//H5 版本

		}
	};

	/**
	 * 关闭窗口 
	 * @param 配置  name不传时默认关闭当前窗口 root页无效  动画不传递使用默认动画
	 */
	EApi.closeWin = EApi.fn.closeWin = function(options) {
		if (EApi.config.app == 'yes') {
			
			api.closeWin(options);

		} else {
			//H5 版本

		}
	};
	

	/**
	 *  openFrame 打开一个frame
	 *  @param options 配置  页面的名称（逻辑上用） url 路径（不能使用绝对路径） parentDiv 容器div jquery对象
	 *  @param callback	回调函数
	 */
	EApi.openFrame = EApi.fn.openFrame = function(options, callback) {
		if (EApi.config.app == 'yes') {
			var parentDiv = $(options.parentDiv);
			//坐标
			var x, y, h, w;
			//通过父容器得到 坐标信息
			x = parentDiv.offset().left;
			y = parentDiv.offset().top;
			h = parentDiv.height();
			w = parentDiv.width();

			var offset = $api.offset(parentDiv[0]);
			var y = offset.t;
			var conf = {

				rect : {
					x : x,
					y : y,
					h : h,
					w : w
				},
				index : 0
			};
			conf = $.extend(conf, options);

			api.openFrame(conf, callback);

		} else {
			//H5 版本
		}
	};

	/**
	 *  openFrameGroup 打开一个frame组
	 *  @param options 配置  页面的名称（逻辑上用）       parentDiv 容器div jquery对象
	 * 	@param callback	回调函数
	 */
	EApi.openFrameGroup = EApi.fn.openFrameGroup = function(options, callback) {
		if (EApi.config.app == 'yes') {
			var parentDiv = $(options.parentDiv);
			//坐标
			var x, y, h, w;
			//通过父容器得到 坐标信息
			x = parentDiv.offset().left;
			y = parentDiv.offset().top;
			h = parentDiv.height();
			w = parentDiv.width();
			var conf = {
				background : '#f7f8f8',
				preload : 0,
				rect : {
					x : x,
					y : y,
					w : w,
					h : h
				},
				bounces : true, //是否可弹动
				index : 0,
			};
			conf = $.extend(conf, options);
			api.openFrameGroup(conf, callback);
		} else {
			//H5  code
		}

	}
	/**
	 *  setFrameGroupIndex  设置一个frame页显示
	 *  @param options  配置
	 */
	EApi.setFrameGroupIndex = EApi.fn.setFrameGroupIndex = function(options) {
		if (EApi.config.app == 'yes') {
			api.setFrameGroupIndex(options);
		} else {

		}
	}
	/**
	 * 关闭一个Frame
	 * @param name Frame name
	 */
	EApi.closeFrame = EApi.fn.closeFrame = function(name) {
		if (EApi.config.app == 'yes') {
			api.openFrameGroup({
				name : name
			});
		} else {

		}

	};

	/**
	 * 显示Frame
	 * @param name Frame name
	 */
	EApi.showFrame = EApi.fn.showFrame = function(name) {
		if (EApi.config.app == 'yes') {
			api.setFrameAttr({
				name : name,
				hidden : false
			});
		} else {

		}

	};

	/**
	 * 隐藏Frame
	 * @param name Frame name
	 */
	EApi.hideFrame = EApi.fn.hideFrame = function(name) {
		if (EApi.config.app == 'yes') {
			api.setFrameAttr({
				name : name,
				hidden : true
			});
		} else {

		}

	};

	/**
	 * 获得当前frame的名称
	 */
	EApi.getFrameName = EApi.fn.getFrameName = function() {

		if (EApi.config.app == 'yes') {
			return api.frameName;
		} else {

		}

	};

	/**
	 * 获取上一个页面传递过来的参数 (H5版本的是获取url上的参数)
	 */
	EApi.getParam = EApi.fn.getParam = function() {
		if (EApi.config.app == 'yes') {
			return api.pageParam;
		} else {
			//H5 版本
		}
	}
	
	/**
	 * alert 简单提示
	 * @param msg  消息内容
	 */
	EApi.alert = EApi.fn.alert = function(msg) {
		if (EApi.config.app == 'yes') {
			api.alert({
				title : '提示',
				msg : msg
			});
		} else {
			alert(msg);
		}
	}
	
	/**
	 * msg 复杂提示
	 * @param options 配置包括{title 标题,msg 内容 ,buttons ['按钮1','按钮2']  按钮  callback 执行完毕的回调函数}
	 */
	EApi.msg = EApi.fn.msg = function(options) {
		if (EApi.config.app == 'yes') {
			api.confirm(options, options.callback);
		} else {
			// H5 版本接口

		}
	}
	
	/**
	 * 带两个或三个按钮和输入框的对话框 
	 */
	EApi.prompt = EApi.fn.prompt = function (options) {
		if (EApi.config.app == 'yes') {
			api.prompt(options, options.callback);
		} else {
			// H5 版本接口

		}
	};
	
	/**
	 *  loading 加载中提示框
	 * @param modal  是否模态
	 */
	EApi.loading = EApi.fn.loading = function(modal) {
		if (EApi.config.app == 'yes') {
			if (modal) {
				modal = true;
			}
			api.showProgress({
				style : 'default',
				animationType : 'fade',
				title : '加载中...',
				modal : modal
			});
		}
	};
	
	/**
	 * 自动消失气泡
	 * @param msg  内容
	 * @duration 消失时间  单位毫秒
	 * @location 位置   top middle bottom  头部 中部  底部
	 */
	EApi.toast = EApi.fn.toast = function(msg, duration, location) {
	
		if (EApi.config.app == 'yes') {
			api.toast({
				msg : msg,
				duration : duration,
				location : location
			});
		}
	};

	/**
	 * closeLoad 关闭加载中提示
	 */
	EApi.closeLoad = EApi.fn.closeLoad = function() {
		if (EApi.config.app == 'yes') {
			api.hideProgress();
		}
	};
	
	
	/**
	 * 在某个页面上执行js
	 * @param options name： win名称 FrameName：frame名称：  script 脚本内容
	 */
	EApi.execScript = EApi.fn.execScript = function(options){
		if (EApi.config.app == 'yes') {
			api.execScript(options);
		} else {
		
		}
	};
	
	
	/**
	 * 转换为货币格式  
	 * @param number	数字
	 * @param places	精度
	 * @param symbol	金钱符号
	 * @param thousand	千位 符号
	 * @param decimal	小数点符号
	 * @return ￥1,000.00  这种格式
	 */
	EApi.formatMoney = EApi.fn.formatMoney = function (number, places, symbol, thousand, decimal) {
		//number = parseInt(number);
        number = number || 0;
        places = !isNaN(places = Math.abs(places)) ? places : 2;
        symbol = symbol !== undefined ? symbol : "￥";
        thousand = thousand || ",";
        decimal = decimal || ".";
        var negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
        return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
    }

	window.EApi = window.e$ = EApi;

	if ( typeof define === "function" && define.amd && define.amd.EApi) {
		define("EApi", [], function() {
			return EApi;
		});
	}

}(window);
