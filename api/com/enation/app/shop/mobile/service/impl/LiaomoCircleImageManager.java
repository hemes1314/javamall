package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.mobile.model.LiaomoCircleImage;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

@Component
public class LiaomoCircleImageManager extends BaseSupport<LiaomoCircleImage> {

    public void addCircleImage(long circleId, String image) {
        LiaomoCircleImage liaomoCircleImage = new LiaomoCircleImage();
        liaomoCircleImage.setCircle_id(circleId);
        liaomoCircleImage.setImage(image);
        this.baseDaoSupport.insert("es_liaomo_circle_image", liaomoCircleImage);
    }
    
    public List<LiaomoCircleImage> getByCircleId(long circleId) {
        String sql = "select * from es_liaomo_circle_image where circle_id="+ circleId;
        List<LiaomoCircleImage> list = this.baseDaoSupport.queryForList(sql, LiaomoCircleImage.class);
        
        for (LiaomoCircleImage liaomoCircleImage: list) {
            liaomoCircleImage.setImage(UploadUtil.replacePath(liaomoCircleImage.getImage()));
        }
        
        return list;
    }
    
}
