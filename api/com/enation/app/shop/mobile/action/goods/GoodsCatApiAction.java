/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.impl.TagManager;
import com.enation.app.shop.mobile.model.ApiCat;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商品分类api 获取商品分类
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileGoodsCatApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("goodscat")
public class GoodsCatApiAction extends WWAction {

	private IGoodsCatManager goodsCatManager;
	private TagManager tagManager;

	/**
	 * 获取商品分类
	 * 
	 * @return
	 */
	public String list() {
		try {
			List<Cat> cat_tree = goodsCatManager.listAllChildren(0);
			List<ApiCat> apiCatTree = new ArrayList<ApiCat>();
			
			for (Cat cat : cat_tree) {
			    apiCatTree.add(toApiCat(cat, cat.getCat_id(), ""));
			}
			this.json = JsonMessageUtil.getMobileListJson(apiCatTree);

		} catch (RuntimeException e) {
			this.logger.error("获取商品分类出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 单独适配麦子店
	 */
	public String listMai(){
	    try {
            List<Cat> cat_tree = goodsCatManager.listAllChildren(0);
            List<ApiCat> apiCatTree = new ArrayList<ApiCat>();
            
            for (Cat cat : cat_tree) {
               
                if(cat.getName().equals("洋酒")){
                    if(cat.getHasChildren()){
                        List<Cat> cat_tree2 = goodsCatManager.listAllChildren(cat.getCat_id());
                       
                        for (Cat cat2: cat_tree2){
                            if(!cat2.getCat_id().equals(cat.getCat_id())){
                                apiCatTree.add(toApiCatForMZD(cat2,cat2.getCat_id(),cat.getName()));

                            }
                        }
                    }
                   
                }else{
                    apiCatTree.add(toApiCatForMZD(cat, cat.getCat_id(), ""));
                }
            }
            this.json = JsonMessageUtil.getMobileListJson(apiCatTree);

        } catch (RuntimeException e) {
            this.logger.error("获取商品分类出错", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE;
	    
	}
	/**
	 * 把Cat转换为ApiCat
	 * 
	 * @param cat
	 * @return
	 */
	private ApiCat toApiCat(Cat cat, int rootId, String secondName) {
	    ApiCat apiCat = null;
	    if (rootId == cat.getCat_id()) {
	        apiCat = new ApiCat();
	        apiCat.setName(cat.getName());
	        apiCat.setCat_id(cat.getCat_id().toString());
	        apiCat.setImage(cat.getImage());
	        apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
	        apiCat.setParent_id(cat.getParent_id());
	        
	        for (Cat subcat : cat.getChildren()) {
                apiCat.getChildren().add(toApiCat(subcat, rootId, cat.getName()));
            }
	    } else {
	        if (cat.getParent_id() == 1) {
	            apiCat = new ApiCat();
                apiCat.setName(cat.getName());
                apiCat.setImage(cat.getImage());
                apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
                apiCat.setParent_id(cat.getParent_id());
                List<Tag> tagList = tagManager.keyselectlist();
	            
	            for (Tag tag: tagList) {
	                if (cat.getName().equals(tag.getTag_name())) {
	                    apiCat.setCat_id(rootId + "&tagid=" + tag.getTag_id());
	                    break;
	                }
	            }
	        } else {
	            apiCat = new ApiCat();
	            apiCat.setName(cat.getName());
	            apiCat.setCat_id(rootId + "&prop=" + secondName + "_" + cat.getName());
	            apiCat.setImage(cat.getImage());
	            apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
	            apiCat.setParent_id(cat.getParent_id());
	            
	            if (cat.getHasChildren()) {
	                for (Cat subcat : cat.getChildren()) {
	                    apiCat.getChildren().add(toApiCat(subcat, rootId, cat.getName()));
	                }
	            }
	        }
	    }
	    
		return apiCat;
	}
	/**
	 * 适配麦子店
     * 把Cat转换为ApiCat
     * 
     * @param cat
     * @return
     */
    private ApiCat toApiCatForMZD(Cat cat, int rootId, String secondName) {
        ApiCat apiCat = null;
        if (rootId == cat.getCat_id()) {
            apiCat = new ApiCat();
          
                apiCat.setName(cat.getName());
                apiCat.setCat_id(cat.getCat_id().toString());
                apiCat.setImage(cat.getImage());
                apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
                apiCat.setParent_id(cat.getParent_id());
                
                for (Cat subcat : cat.getChildren()) {
                    apiCat.getChildren().add(toApiCatForMZD(subcat, rootId, cat.getName()));
                } 
          
            
            
        } else {
            if (cat.getParent_id() == 1) {
                apiCat = new ApiCat();
                apiCat.setName(cat.getName());
                apiCat.setImage(cat.getImage());
                apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
                apiCat.setParent_id(cat.getParent_id());
                List<Tag> tagList = tagManager.keyselectlist();
                
                for (Tag tag: tagList) {
                    if (cat.getName().equals(tag.getTag_name())) {
                        apiCat.setCat_id(rootId + "&tagid=" + tag.getTag_id());
                        break;
                    }
                }
            } else{
                apiCat = new ApiCat();
                apiCat.setName(cat.getName());
                apiCat.setCat_id(rootId + "&prop=" + secondName + "_" + cat.getName());
                apiCat.setImage(cat.getImage());
                apiCat.setLevel(cat.getCat_path().split("\\|").length - 1);
                apiCat.setParent_id(cat.getParent_id());
                
                if (cat.getHasChildren()) {
                    for (Cat subcat : cat.getChildren()) {
                        apiCat.getChildren().add(toApiCatForMZD(subcat, rootId, cat.getName()));
                    }
                }
            }
        }
        
        return apiCat;
    }
	/**
	 * 获得商品分类
	 * 
	 * @author Sylow
	 * @param <b>parentid</b>:父级id.int型,可为空
	 * @param <b>catimage</b>:大概是
	 *            是否显示图片的参数 我也没看太懂，String型,可为空, on表示是
	 * @return 返回json串 <br />
	 *         <b>result</b>: 1表示添加成功0表示失败 ，int型 <br />
	 *         <b>message</b>: 提示信息 <br />
	 *         <b>data</b>: 商品分类数据
	 */
	public String goodsCatList() {
		try {
			HttpServletRequest request = getRequest();
			String parentIdStr = request.getParameter("parentid");
			int parentid = 0;
			if (parentIdStr != null) {
				parentid = NumberUtils.toInt(parentIdStr);
			}
			List<Cat> cat_tree = goodsCatManager.listAllChildren(parentid);
			String catimage = request.getParameter("catimage");
			boolean showimage = catimage != null && catimage.equals("on") ? true : false;

			String imgPath = "";
			if (!cat_tree.isEmpty()) {
				for (Cat cat : cat_tree) {

					if (cat.getImage() != null && !StringUtil.isEmpty(cat.getImage())) {
						imgPath = UploadUtil.replacePath(cat.getImage());
						cat.setImage(imgPath);
					}

				}
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("showimg", showimage);// 是否显示分类图片
			data.put("cat_tree", cat_tree);// 分类列表数据
			this.json = JsonMessageUtil.getMobileObjectJson(data);
		} catch (RuntimeException e) {
			this.logger.error("获取商品分类出错", e);
			this.showPlainErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
    
    public TagManager getTagManager() {
        return tagManager;
    }
    
    public void setTagManager(TagManager tagManager) {
        this.tagManager = tagManager;
    }

}
