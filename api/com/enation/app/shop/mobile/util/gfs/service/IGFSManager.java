package com.enation.app.shop.mobile.util.gfs.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * GFS图片上传处理接口.
 * 
 * @author baoxiufeng
 */
public interface IGFSManager {

    /** 转存GFS的图片来源-商品信息 */
    public static final int IMAGE_GOODS = 1;
    /** 转存GFS的图片来源-商品相册信息 */
    public static final int IMAGE_GALLERY = 2;
    /** 转存GFS的图片来源-店铺信息 */
    public static final int IMAGE_STORE = 3;
    
    /**
     * 上传图片到GFS服务器.
     * 
     * @param file 文件对象
     * @param suffix 文件类型
     * @return 文件访问路径
     * @throws Exception 异常信息
     */
    public String[] upload(File file) throws Exception;
    
    /**
     * 上传远程图片到GFS服务器.
     * 
     * @param imageUrl 图片文件远程路径
     * @param suffix 文件类型
     * @return 文件访问路径
     * @throws Exception 异常信息
     */
    public String[] upload(String imageUrl) throws Exception;
    
    /**
     * 转存指定图片到GFS服务器，并返回新的GFS图片路径.
     * 
     * @param image 转存前图片路径
     * @return 转存后的GFS图片路径
     */
    public String handleImageToGFS(String image);
    
    /**
     * 批量转存图片到GFS服务器，并返回新的GFS图片路径集合.
     * 
     * @param mapList 转存前包含图片字段信息集合
     * @param idKey 包含图片字段信息中标识字段KEY
     * @param imageKey 包含图片字段信息中原图字段KEY
     * @param imageGfsKey 包含图片字段信息中新GFS图字段KEY（用于过滤已转存过的图片）
     * @return 转存后的GFS图片路径集合
     */
    @SuppressWarnings({ "rawtypes" })
    public Map<Integer, String> handleImageToGFS(List<Map> mapList, String idKey, String imageKey, String imageGfsKey);
}
