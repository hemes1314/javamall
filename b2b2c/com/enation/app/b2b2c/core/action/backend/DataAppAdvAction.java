package com.enation.app.b2b2c.core.action.backend;

import java.io.File;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.DataAppAdv;
import com.enation.app.b2b2c.core.service.impl.DataAppAdvManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/**
 * 后台管理 - 移动端 广告轮播
 * @author Jeffrey
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("app-adv")
@Results({
    @Result(name="list", type="freemarker", location="/core/admin/app-adv/adv_list.html"),
    @Result(name="add", type="freemarker", location="/core/admin/app-adv/adv_input.html"), 
    @Result(name="edit", type="freemarker", location="/core/admin/app-adv/adv_edit.html") 
})
public class DataAppAdvAction extends WWAction {

    private static final long serialVersionUID = 1L;
    
    private Integer aid;
    private Integer[] aids;
    private DataAppAdv adv;
    private File pic;
    private String picFileName;
    private String imgPath;
    private List<DataAppAdv> advs;
    
    @Autowired
    private DataAppAdvManager advManager;
    
    public String list() {
        return "list";
    }
    
    public String listJson() {
        this.webpage = advManager.get(getPage(), getPageSize(), getOrder());
        this.showGridJson(webpage);
        return JSON_MESSAGE;
    }
    
    public String add() {
        return "add";
    }
    
    public String edit() {
        adv = advManager.get(aid);
        return "edit";
    }
    
    public String save() {
        if (pic != null) {
            if (FileUtil.isAllowUp(picFileName)) {
                String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
                adv.setImg_url(path);
            } else {
                this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
                return JSON_MESSAGE;
            }
        }
        try {
            this.advManager.save(adv);
            this.showSuccessJson("操作成功");
        } catch (RuntimeException e) {
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }
    
    public String update() {
        if (pic != null) {
            if (FileUtil.isAllowUp(picFileName)) {
                String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
                adv.setImg_url(path);
            } else {
                this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
                return JSON_MESSAGE;
            }
        }
        try {
            this.advManager.update(adv);
            this.showSuccessJson("操作成功");
        } catch (RuntimeException e) {
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }
    
    public String delete() {
        try {
            this.advManager.delete(aids);
            this.showSuccessJson("删除成功");
        } catch (RuntimeException e) {
            this.showErrorJson("删除失败");
        }
        return this.JSON_MESSAGE;
    }
    
    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public DataAppAdv getAdv() {
        return adv;
    }

    public void setAdv(DataAppAdv adv) {
        this.adv = adv;
    }

    public File getPic() {
        return pic;
    }

    public void setPic(File pic) {
        this.pic = pic;
    }
    
    public String getPicFileName() {
        return picFileName;
    }

    public void setPicFileName(String picFileName) {
        this.picFileName = picFileName;
    }
    
    public Integer[] getAids() {
        return aids;
    }
    
    public void setAids(Integer[] aids) {
        this.aids = aids;
    }

}
