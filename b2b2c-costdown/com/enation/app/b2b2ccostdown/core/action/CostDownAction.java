package com.enation.app.b2b2ccostdown.core.action;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 直降
 * 
 * @author Jeffrey
 *
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({ @Result(name = "list", type = "freemarker", location = "/groupbuy/costdown/list.html"),
        @Result(name = "add", type = "freemarker", location = "/groupbuy/costdown/add.html"),
        @Result(name = "edit", type = "freemarker", location = "/groupbuy/costdown/edit.html") })
@Action("cost-down")
public class CostDownAction extends WWAction {

    private static final long serialVersionUID = 1L;

    private CostDownManager costDownManager;

    private CostDownActiveManager costDownActiveManager;

    private IGoodsManager goodsManager;

    private CostDownActive costDownActive;

    private Map goods;

    private int actid;

    private int gbid;

    private Integer status;

    private CostDown costDown;

    private File image;

    private String imageFileName;

    private String image_src;
    
    private Long now;

    /**
     * 跳转至团购列表
     * 
     * @return
     */
    public String list() {
    	now = new Date().getTime();
        return "list";
    }

    /**
     * 按活动id显示团购json
     * 
     * @return
     */
    public String listJson() {
        try {
            this.webpage = this.costDownManager.listByActId(this.getPage(), this.getPageSize(), actid, status);
            this.showGridJson(webpage);
        } catch(Exception e) {
            this.logger.error("查询出错", e);
            this.showErrorJson("查询出错");
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 审核团购
     * 
     * @param gbid
     *            团购Id
     * @param status
     *            审核状态
     * @return
     */
    public String auth() {
        try {
            this.costDownManager.auth(gbid, status);
            this.showSuccessJson("操作成功");
        } catch(Exception e) {
            this.logger.error("审核操作失败", e);
            this.showErrorJson("审核操作失败" + e.getMessage());
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 
     * @Title: add
     * @Description: 跳转至添加团购页面
     * @param groupBuyActive
     *            团购活动
     * @return String 添加团购页面
     */
    public String add() {
        costDownActive = costDownActiveManager.get(actid);
        return "add";
    }

    /**
     * @Title: saveAdd
     * @Description: 添加团购商品
     * @param allowTYpe
     *            判断上传的图片类型
     * @param imageFileName
     *            图片名称
     * @param groupBuy
     *            团购 此功能有待扩展不应该将对象传输过来进行修改 应该传入的是字段然后新建对象存入进去。
     *            不应该在这里去保存团购商品图片应该在添加团购的时候去调用统一的上传图片控件、并且应该支持多图上传。
     * @return String 1为成功，0为失败
     */
    public String saveAdd() {
        try {
            if(image != null) {
                //判断文件类型
                String allowTYpe = "gif,jpg,bmp,png";
                if(!imageFileName.trim().equals("") && imageFileName.length() > 0) {
                    String ex = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
                    if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0) { throw new RuntimeException(
                            "对不起,只能上传gif,jpg,bmp,png格式的图片！"); }
                }

                //判断文件大小
                if(image.length() > 2000 * 1024) { throw new RuntimeException("图片不能大于2MB！"); }
                String imgPath = UploadUtil.upload(image, imageFileName, "groupbuy");
                costDown.setImg_url(imgPath);
            }
            costDownManager.add(costDown);
            this.showSuccessJson("添加成功");
        } catch(Exception e) {
            this.showErrorJson("添加失败");
            this.logger.error("团购添加失败：" + e);
        }
        return this.JSON_MESSAGE;

    }

    /**
     * 
     * @Title: edit
     * @Description: 跳转至团购修改页面
     * @param groupBuy
     *            团购商品
     * @param groupBuyActive
     *            团购活动
     * @param goods
     *            商品
     * @param groupbuy_cat_list
     *            团购分类列表
     * @param groupbuy_area_list
     *            团购地区列表
     * @param image_src
     *            团购商品图片
     * @return String 团购修改页面
     */
    public String edit() {
        costDown = costDownManager.get(gbid);
        costDownActive = costDownActiveManager.get(costDown.getAct_id());
        goods = goodsManager.get(costDown.getGoods_id());
        image_src = UploadUtil.replacePath(costDown.getImg_url());
        return "edit";
    }

    /**
     * 
     * @Title: saveEdit
     * @Description: 保存修改商品
     * @param groupBuy
     *            团购商品
     * @return 1为成功。0为失败
     */
    public String saveEdit() {
        try {
            costDownManager.update(costDown);
            this.showSuccessJson("修改团购成功");
        } catch(Exception e) {
            this.logger.error("修改团购失败", e);
            this.showErrorJson("修改团购失败");
        }
        return this.JSON_MESSAGE;

    }

    public int getActid() {
        return actid;
    }

    public void setActid(int actid) {
        this.actid = actid;
    }

    public int getGbid() {
        return gbid;
    }

    public void setGbid(int gbid) {
        this.gbid = gbid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public CostDownManager getCostDownManager() {
        return costDownManager;
    }

    public void setCostDownManager(CostDownManager costDownManager) {
        this.costDownManager = costDownManager;
    }

    public CostDownActiveManager getCostDownActiveManager() {
        return costDownActiveManager;
    }

    public void setCostDownActiveManager(CostDownActiveManager costDownActiveManager) {
        this.costDownActiveManager = costDownActiveManager;
    }

    public CostDownActive getCostDownActive() {
        return costDownActive;
    }

    public void setCostDownActive(CostDownActive costDownActive) {
        this.costDownActive = costDownActive;
    }

    public CostDown getCostDown() {
        return costDown;
    }

    public void setCostDown(CostDown costDown) {
        this.costDown = costDown;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public Map getGoods() {
        return goods;
    }

    public void setGoods(Map goods) {
        this.goods = goods;
    }

    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }

	public Long getNow() {
		return now;
	}

	public void setNow(Long now) {
		this.now = now;
	}
}
