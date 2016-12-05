package com.enation.app.shop.core.openapi.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.shop.core.openapi.service.IOpenApiConfigManager;
import com.gome.open.api.common.AbstractResponse;
import com.gome.open.api.common.Constant;
import com.gome.open.api.common.GomeRequest;
import com.gome.open.api.sdk.DefaultGomeClient;
import com.gome.open.api.sdk.GomeException;

/**
 * OpenApi配置信息管理.
 * 
 * @author baoxiufeng
 */
@Component
public class OpenApiConfigManager implements IOpenApiConfigManager {
    @Value("#{configProperties['openapi.url']}")
    private String openApiUrl;
    
    @Value("#{configProperties['openapi.access_token']}")
    private String accessToken;
    
    @Value("#{configProperties['openapi.app_key']}")
    private String appKey;
    
    @Value("#{configProperties['openapi.app_secret']}")
    private String appSecret;
    
    @Value("#{configProperties['openapi.disabled']}")
    private Integer disabled;
    
    // 国美HTTP客户端工具
    private DefaultGomeClient client;
    
    @Override
    public DefaultGomeClient getClient() {
        if (client == null) {
            synchronized(DefaultGomeClient.class) {
                if (client == null) {
                    client = new DefaultGomeClient(openApiUrl, accessToken, appKey, appSecret);
                }
            }
        }
        return client;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends AbstractResponse> T execute(GomeRequest<?> request) throws GomeException {
        if (isOpen()) {
            T t = (T) getClient().execute(request);
            System.out.println(JSON.toJSON(t));
            if (Constant.CODE_OK.equals(((AbstractResponse) t).getCode())) return t;
            if (Constant.CODE_NO_ROWS_CHANGE.equals(((AbstractResponse) t).getCode())) return t;
//            if ("15".equals(((AbstractResponse) t).getCode())) return t;
            throw new GomeException(((AbstractResponse) t).getZhDesc());
        }
        return null;
    }

    /**
     * @return 是否开启OpenApi调用标识
     */
    public boolean isOpen() {
        return disabled == 0;
    }
}
