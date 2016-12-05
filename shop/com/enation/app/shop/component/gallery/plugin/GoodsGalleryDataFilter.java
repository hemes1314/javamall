package com.enation.app.shop.component.gallery.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.search.IGoodsDataFilter;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 商品中的新主图规则数据过滤
 * @author lzf
 * 2012-10-20下午8:07:03
 * ver 1.0
 */

//商品图片通过@image输出可自行转换路径，无需在这里转换
//@Component
@Deprecated
public class GoodsGalleryDataFilter extends AutoRegisterPlugin implements
		IGoodsDataFilter {

	@Override
	public void filter(List<Map> goodsList) {
		for(Map goods:goodsList){
			goods.put("original", UploadUtil.replacePath((String)goods.get("original")));
			goods.put("big", UploadUtil.replacePath((String)goods.get("big")));
			goods.put("small", UploadUtil.replacePath((String)goods.get("small")));
			goods.put("thumbnail", UploadUtil.replacePath((String)goods.get("thumbnail")));
		}

	}

}
