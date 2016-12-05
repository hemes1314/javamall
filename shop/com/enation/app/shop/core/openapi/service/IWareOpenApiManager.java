package com.enation.app.shop.core.openapi.service;

import java.util.List;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.gome.open.api.ware.client.dto.response.WareAddResponse;
import com.gome.open.api.ware.client.dto.response.WareCleanResponse;
import com.gome.open.api.ware.client.dto.response.WareDeleteResponse;
import com.gome.open.api.ware.client.dto.response.WareRevertResponse;
import com.gome.open.api.ware.client.dto.response.WareUpdateResponse;

/**
 * OpenApi商品信息管理接口.
 * 
 * @author baoxiufeng
 */
public interface IWareOpenApiManager {

    /**
     * 商品信息新增请求.
     * 
     * @param goods 商品信息
     * @return 商品信息新增请求响应
     * @throws Exception 操作异常信息
     */
    public WareAddResponse add(Goods goods) throws Exception;
    
    /**
     * 商品信息修改请求.
     * 
     * @param goods 商品信息
     * @return 商品信息修改请求响应
     * @throws Exception 操作异常信息
     */
    public WareUpdateResponse edit(Goods goods) throws Exception;
    
    /**
     * 商品信息删除请求.
     * 
     * @param goodsId 商品ID
     * @return 商品信息删除请求响应
     * @throws Exception 操作异常信息
     */
    public WareDeleteResponse delete(Integer goodsId) throws Exception;
    
    /**
     * 商品信息批量删除请求.
     * 
     * @param goodsIds 商品ID列表
     * @return 商品信息删除请求响应列表
     * @throws Exception 操作异常信息
     */
    public WareDeleteResponse batchDelete(List<Integer> goodsIds) throws Exception;
    
    /**
     * 商品信息还原请求.
     * 
     * @param goodsId 商品ID
     * @return 商品信息还原请求响应
     * @throws Exception 操作异常信息
     */
    public WareRevertResponse revert(Integer goodsId) throws Exception;
    
    
    
    /**
     * 商品信息批量还原请求.
     * 
     * @param goodsIds 商品ID列表
     * @return 商品信息还原请求响应
     * @throws Exception 操作异常信息
     */
    public WareRevertResponse batchRevert(List<Integer> goodsIds) throws Exception;
    
    /**
     * 商品信息清除（真删）请求.
     * 
     * @param goodsId 商品ID
     * @return 商品信息清除请求响应
     * @throws Exception 操作异常信息
     */
    public WareCleanResponse clean(Integer goodsId) throws Exception;
    
    /**
     * 商品信息批量清除（真删）请求.
     * 
     * @param goodsIds 商品ID列表
     * @return 商品信息清除请求响应
     * @throws Exception 操作异常信息
     */
    public WareCleanResponse batchClean(List<Integer> goodsIds) throws Exception;
    
    /**
     * 商品状态信息修改请求.
     * 
     * @param goods 商品信息
     * @return 商品信息修改请求响应
     * @throws Exception 操作异常信息
     */
    public WareUpdateResponse updateStatus(Goods goods) throws Exception;
    
    /**
     * 商品状态信息批量修改请求.
     * 
     * @param goodsList 商品信息列表
     * @return 商品信息修改请求响应列表
     * @throws Exception 操作异常信息
     */
    public List<WareUpdateResponse> batchUpdateStatus(List<Goods> goodsList) throws Exception;
    
    /**
     * 商品SKU库存信息修改请求.
     * 
     * @param product 商品SKU库存信息
     * @return 商品SKU库存信息修改请求响应
     * @throws Exception 操作异常信息
     */
    public WareUpdateResponse updateStock(Product product) throws Exception;
    
    /**
     * 商品SKU库存信息批量修改请求.
     * 
     * @param products 商品SKU库存信息列表
     * @return 商品SKU库存信息修改请求响应列表
     * @throws Exception 操作异常信息
     */
    public List<WareUpdateResponse> batchUpdateStock(List<Product> products) throws Exception;
}
