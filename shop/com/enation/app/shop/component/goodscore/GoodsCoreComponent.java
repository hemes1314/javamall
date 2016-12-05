package com.enation.app.shop.component.goodscore;

import org.springframework.stereotype.Component;

import com.enation.framework.component.IComponent;
import com.enation.framework.plugin.page.JspPageTabs;

@Component
public class GoodsCoreComponent implements IComponent {

	@Override
	public void install() {
		 
		// JspPageTabs.addTab("setting",2, "相册设置");
	}

	@Override
	public void unInstall() {
		 

	}

}
