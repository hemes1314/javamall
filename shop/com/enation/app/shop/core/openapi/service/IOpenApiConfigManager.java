package com.enation.app.shop.core.openapi.service;

import com.gome.open.api.sdk.DefaultGomeClient;

/**
 * OpenApi配置信息管理接口.
 * 
 * @author baoxiufeng
 */
public interface IOpenApiConfigManager {

    /**
     * 获取国美HTTP客户端工具.
     * 
     * @return HTTP客户端工具
     */
    public DefaultGomeClient getClient();
}
