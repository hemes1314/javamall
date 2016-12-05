package com.enation.app.b2b2c.core.test;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

public class UpdateGoodsImage extends SpringTestSupport{
	private IDaoSupport daoSupport;
	@Before
	public void first(){
		daoSupport = this.getBean("daoSupport");
	}
	@Test
	public void test(){
		List<Map> list=this.daoSupport.queryForList("select * from es_goods");
		for (Map map:list) {
//			if(map.get("thumbnail").toString().indexOf("http://static.b2b2cv2.javamall.com.cn")>=0){
//				this.daoSupport.execute("delete  from es_goods_gallery where img_id="+map.get("img_id"));
//			}else{
//				map.put("thumbnail",map.get("thumbnail").toString().replace("fs:", "http://static.b2b2cv2.javamall.com.cn"));
//				map.put("small",map.get("small").toString().replace("fs:", "http://static.b2b2cv2.javamall.com.cn")); 
//				map.put("big", map.get("big").toString().replace("fs:", "http://static.b2b2cv2.javamall.com.cn")); 
//				map.put("original", map.get("original").toString().replace("fs:", "http://static.b2b2cv2.javamall.com.cn"));
//				map.put("tiny", map.get("tiny").toString().replace("fs:", "http://static.b2b2cv2.javamall.com.cn"));
//				this.daoSupport.update("es_goods_gallery", map, "img_id="+map.get("img_id"));
//			}
			map.put("original", map.get("original").toString().replace("_tiny", ""));
			this.daoSupport.update("es_goods", map, "goods_id="+map.get("goods_id"));
		}
	}
}
