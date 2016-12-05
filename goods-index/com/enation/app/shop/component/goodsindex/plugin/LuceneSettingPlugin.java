/**
 * 
 */
package com.enation.app.shop.component.goodsindex.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.goodsindex.LuceneSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * lucene设置插件
 * @author kingapex
 *2015-5-12
 */
@Component
public class LuceneSettingPlugin  extends AutoRegisterPlugin implements
IOnSettingInputShow,IOnSettingSaveEnvent {
	
	private ISettingService settingService;
	
	
	
	/**
	 * 在配置更改时重新加载索引目录 至LuceneSetting
	 */
	@Override
	public void onSave() {
		String index_path  = settingService.getSetting(LuceneSetting.SETTING_KEY, "index_path");
		LuceneSetting.INDEX_PATH= index_path;
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#onShow()
	 */
	@Override
	public String onShow() {
		
		return "lucene-setting";
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getSettingGroupName()
	 */
	@Override
	public String getSettingGroupName() {
		
		return LuceneSetting.SETTING_KEY;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getTabName()
	 */
	@Override
	public String getTabName() {
		
		return "lucene设置";
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getOrder()
	 */
	@Override
	public int getOrder() {
		
		return 150;
	}

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}
	
	

}
