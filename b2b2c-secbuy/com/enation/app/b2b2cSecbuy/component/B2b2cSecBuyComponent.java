package com.enation.app.b2b2cSecbuy.component;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.enation.framework.cache.CacheFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.solution.InstallUtil;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;
/**
 * 多商户秒拍组件 
 * @author fenlongli
 * @date 2015-7-13 上午10:50:53
 */
@Component
public class B2b2cSecBuyComponent implements IComponent{
	protected final Logger logger = Logger.getLogger(getClass());
	private IMenuManager menuManager;
	private IDaoSupport daoSupport;
	@Override
	public void install() {
		//修改菜单
		this.editMenu();
		
		//修改数据库结构
		DBSolutionFactory.dbImport("file:com/enation/app/b2b2cSecbuy/component/b2b2cSecbuy_install.xml", "es_");	
		
		//安装示例数据
		String app_apth = StringUtil.getRootPath();
		String dataSqlPath = app_apth+ "/b2b2c/admin/secbuy/example_data.xml";
		
		try {	
			//找到文件位置
			File xmlFile = new File(dataSqlPath);
			
			loggerPrint("安装datasqlPath:" + dataSqlPath);
			
			InstallUtil.putMessaage("正在安装秒拍基础数据...");
			//开始安装
			anyDBInstall(dataSqlPath);
			 
			loggerPrint("示例数据安装完毕");
			
			//修改演示数据
			daoSupport.execute("update es_goods set is_secbuy=1 where goods_id in (select goods_id from es_secbuy_goods)");
			try {
                //hp清除缓存
                com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
                
                String  sql1="SELECT * from es_secbuy_goods ";
                List<Map> goods_list=daoSupport.queryForList(sql1);
                for (Map goods:goods_list) {
                    //hp清除缓存
                    Long goodsid =(Long)goods.get("goods_id");
                    iCache.remove(String.valueOf(goodsid));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.debug(e.getMessage(),e);
			throw new RuntimeException("安装示例数据出错...");
		}		 
	}
	
	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
	}
	private void editMenu(){
		Menu menu=menuManager.get("秒拍活动");
		menu.setUrl("/b2b2c/admin/secBuy!list.do");
		menuManager.edit(menu);
	}
	/**
	 * 使用新的数据库中间件来导入数据
	 * @param productId
	 * @param fragment
	 * @return
	 */
	private void anyDBInstall(String dataSqlPath) throws Exception{
		if(!DBSolutionFactory.dbImport(dataSqlPath,""))
			throw new RuntimeException("安装示例数据出错...");
	}
	private void loggerPrint(String text){
		if(logger.isDebugEnabled()){
			this.logger.debug(text);
		}
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
