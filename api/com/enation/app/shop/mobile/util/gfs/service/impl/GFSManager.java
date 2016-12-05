package com.enation.app.shop.mobile.util.gfs.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.mobile.util.gfs.service.IGFSManager;
import com.enation.eop.sdk.utils.UploadUtil;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * GFS图片上传处理接口.
 * 
 * @author baoxiufeng
 */
@Component
public class GFSManager implements IGFSManager {

    @Value("#{configProperties['gfs.api.url']}")
    private String gfsApiUrl;
    
    @Value("#{configProperties['gfs.api.token']}")
    private String gfsApiToken;

    /**
     * 图片来源类型.
     * 
     * @author baoxiufeng
     */
    public enum ImageSourceType {
        LOCAL, REMOTE
    }
    
    /** 图片路径元素名称 */
    public static final String GFS_URL_KEY = "url";
    
    @Override
    public String[] upload(File file) throws Exception {
        return upload(file.getName(), new FileInputStream(file));
    }

    @Override
    public String[] upload(String imageUrl) throws Exception {
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        return upload(filename, new URL(imageUrl).openStream());
    }

    @Override
    public String handleImageToGFS(String image) {
        if (StringUtils.isBlank(image)) return null;
        image = UploadUtil.replacePath(image);
        try {
            String[] res = upload(image);
            return JSONObject.parseObject(res[0]).getString(GFS_URL_KEY) + res[1];
        } catch(Exception e) {
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map<Integer, String> handleImageToGFS(List<Map> mapList, String idKey, String imageKey, String imageGfsKey) {
        if (mapList.isEmpty()) return Collections.emptyMap();
        String image = null;
        String imageGfs = null;
        Object key = null;
        Map<Integer, String> imageMap = new HashMap<Integer, String>(mapList.size());
        for (Map map : mapList) {
            image = (String) map.get(imageKey);
            imageGfs = (String) map.get(imageGfsKey);
            if (StringUtils.isBlank(imageGfs) && StringUtils.isNotBlank(image)) {
                key = map.get(idKey);
                imageMap.put(Long.valueOf(key.toString()).intValue(), UploadUtil.replacePath(image));
            }
        }
        int fail = 0;
        int succ = 0;
        int total = imageMap.size();
        String[] res = null;
        for (Map.Entry<Integer, String> entry : imageMap.entrySet()) {
            try {
                res = upload(entry.getValue());
                entry.setValue(JSONObject.parseObject(res[0]).getString(GFS_URL_KEY) + res[1]);
                succ++;
            } catch(Exception e) {
                entry.setValue(null);
                fail++;
            }
            System.out.println(String.format("Image2GFS processing (Success/Failure/Total): (%d/%d/%d)", succ, fail, total));
        }
        return imageMap;
    }
    
    /**
     * 上传图片到GFS服务器.
     * 
     * @param filename 文件名
     * @param is 文件输入流
     * @return 文件GFS路径
     * @throws Exception 异常信息
     */
    private String[] upload(String filename, InputStream is) throws Exception {
        String suffix = filename.substring(filename.lastIndexOf("."));
        String url = String.format(gfsApiUrl, filename, suffix);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = is.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        HttpPost post = new HttpPost(url);
        post.addHeader("token", gfsApiToken);
        post.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse httpResponse = httpClient.execute(post);
        String result = EntityUtils.toString(httpResponse.getEntity());
        is.close();
        bos.close();
        return new String[] { result, suffix.toLowerCase() };
    }
}
