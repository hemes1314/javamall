package com.enation.app.shop.core.plugin.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.enation.app.b2b2c.component.plugin.store.IStoreDisEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.util.StringUtil;
import com.opensymphony.xwork2.ActionContext;

/**
 * 商品插件桩
 * 
 * @author kingapex
 * 
 */
public class GoodsPluginBundle extends AutoRegisterPluginsBundle {

	public String getName() {
		return "商品插件桩";
	}

	public Map<Integer, String> getTabList() {
		Map<Integer, String> tabMap = new TreeMap<Integer, String>();
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			List<String> tabList = new ArrayList<String>();
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsTabShowEvent) {
					IGoodsTabShowEvent event = (IGoodsTabShowEvent) plugin;
					String name = event.getTabName();
					if (!StringUtil.isEmpty(name)) {
							tabMap.put(event.getOrder(), name);
					}
				}
			}
		}
		return tabMap;
	}

	/**
	 * 激发商品添加前事件
	 * 
	 * @param goods
	 */
	public void onBeforeAdd(Map goods) {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsBeforeAddEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onBeforeGoodsAdd 开始...");
					}
					IGoodsBeforeAddEvent event = (IGoodsBeforeAddEvent) plugin;
					event.onBeforeGoodsAdd(goods, request);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onBeforeGoodsAdd 结束.");
					}
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}

	/**
	 * 激发商品添加完成后事件
	 * 
	 * @param goods
	 */
	public void onAfterAdd(Map goods) {

		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (loger.isDebugEnabled()) {
					loger.debug("check plugin : " + plugin.getClass() + " is IGoodsAfterAddEvent ?");
				}

				if (plugin instanceof IGoodsAfterAddEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug(" yes ,do event...");
					}
					IGoodsAfterAddEvent event = (IGoodsAfterAddEvent) plugin;
					
					try {
						event.onAfterGoodsAdd(goods, request);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}

	/**
	 * 到添加商品页面，为此页填充input数据
	 * 
	 */
	public Map<Integer, String> onFillAddInputData() {
		Map<Integer, String> htmlMap = new TreeMap<Integer, String>();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", new HashMap(0));
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGetGoodsAddHtmlEvent) {
					IGetGoodsAddHtmlEvent event = (IGetGoodsAddHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					String html = event.getAddHtml(request);
					if (!StringUtil.isEmpty(html)) {
						if (plugin instanceof IGoodsTabShowEvent) {
							IGoodsTabShowEvent tabEvent = (IGoodsTabShowEvent) plugin;
							htmlMap.put(tabEvent.getOrder(), html);
						}
					}
				}
			}
		}

		return htmlMap;
	}

	/**
	 * 到商品修改页面前为此页填充input数据
	 * 
	 */
	public Map<Integer, String> onFillEditInputData(Map goods) {
		Map<Integer, String> htmlMap = new TreeMap<Integer, String>();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGetGoodsEditHtmlEvent) {
					IGetGoodsEditHtmlEvent event = (IGetGoodsEditHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					freeMarkerPaser.setPageName(null);
					String html = event.getEditHtml(goods, request);
					if (!StringUtil.isEmpty(html)) {
						if (plugin instanceof IGoodsTabShowEvent) {
							IGoodsTabShowEvent tabEvent = (IGoodsTabShowEvent) plugin;
							htmlMap.put(tabEvent.getOrder(), html);
						}
					}
				}
			}
		}
		return htmlMap;
	}

	/**
	 * 激发商品修改更新前事件
	 * 
	 * @param goods
	 *            页面传递的商品数据
	 */
	public void onBeforeEdit(Map goods) {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsBeforeEditEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件[" + plugin.getClass() + "] onBeforeGoodsEdit 开始...");
					}
					IGoodsBeforeEditEvent event = (IGoodsBeforeEditEvent) plugin;
					event.onBeforeGoodsEdit(goods, request);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件[" + plugin.getClass() + "] onBeforeGoodsEdit 结束.");
					}
				}
			}
		}
	}

	/**
	 * 激发商品修改后事件
	 * 
	 * @param goods
	 *            修改后的商品基本数据
	 */
	public void onAfterEdit(Map goods) {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx
				.get(ServletActionContext.HTTP_REQUEST);
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsAfterEditEvent) {
					IGoodsAfterEditEvent event = (IGoodsAfterEditEvent) plugin;
					
					try {
						event.onAfterGoodsEdit(goods, request);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					
				}
			}
		}
	}

	/**
	 * lzf add 20120417
	 * 
	 * @param goods
	 */
	public void onVisit(Map goods) {
		List<IPlugin> plugins = this.getPlugins();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsVisitEvent) {
					IGoodsVisitEvent event = (IGoodsVisitEvent) plugin;
					event.onVisit(goods);
				}
			}
		}
	}

	/**
	 * 激发商品删除事件
	 * 
	 * @param goodsid
	 */
	public void onGoodsDelete(Integer[] goodsid) {
		List<IPlugin> plugins = this.getPlugins();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {

				if (plugin instanceof IGoodsDeleteEvent) {
					IGoodsDeleteEvent event = (IGoodsDeleteEvent) plugin;
					event.onGoodsDelete(goodsid);
				}
			}
		}
	}

	/**
	 * 激发获取select sql语句字串
	 * 
	 * @return
	 */
	public String onGetSelector() {
		StringBuffer sql = new StringBuffer();
		List<IPlugin> plugins = this.getPlugins();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsSearchFilter) {
					IGoodsSearchFilter event = (IGoodsSearchFilter) plugin;
					sql.append(event.getSelector());
				}
			}
		}
		return sql.toString();
	}

	public String onGetFrom() {
		StringBuffer sql = new StringBuffer();
		List<IPlugin> plugins = this.getPlugins();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsSearchFilter) {
					IGoodsSearchFilter event = (IGoodsSearchFilter) plugin;
					sql.append(event.getFrom());
				}
			}
		}
		return sql.toString();
	}

	public void onSearchFilter(StringBuffer sql) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsSearchFilter) {
					IGoodsSearchFilter event = (IGoodsSearchFilter) plugin;
					event.filter(sql);
				}
			}
		}
	}
	/**
	 * 商品变化，主用于商品生成静态页面后商品优惠需要更新ß商品信息
	 * @param goods 商品
	 */
	public void onStartchange(Map goods) {
		List<IPlugin> plugins = this.getPlugins();
		
		//判断插件列表是否为空
		if (plugins != null) {
			//循环插件列表
			for (IPlugin plugin : plugins) {

				//判断实现商品变化事件
				if (plugin instanceof IGoodsStartChange) {
					//执行插件
					IGoodsStartChange event = (IGoodsStartChange) plugin;
					event.startChange(goods);
				}
				
			}
			
		}
		
	}
	
	
	   
    /**
     * 响应店铺禁用后事件
     * @param storeId 店铺id
     */
    public void onAfterStoreDis(int storeId){
        try{
            List<IPlugin> plugins = this.getPlugins();
            
            if (plugins != null) {
                for (IPlugin plugin : plugins) {
                    if (plugin instanceof IStoreDisEvent) {
                        if (loger.isDebugEnabled()) {
                            loger.debug("调用插件 : " +plugin.getClass() + " onAfterStoreDis 开始...");
                        }
                        IStoreDisEvent event = (IStoreDisEvent)plugin;
                        event.onAfterStoreDis(storeId);
                        if (loger.isDebugEnabled()) {
                            loger.debug("调用插件 : " +plugin.getClass() + " onAfterStoreDis 结束.");
                        }
                    } 
                }
            }
        }catch(RuntimeException  e){
            if(this.loger.isErrorEnabled())
            this.loger.error("调用店铺插件[店铺禁用]事件错误", e);
            e.printStackTrace();
        }
    }

}
