package com.enation.app.base.core.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * @author lzf 2010-3-2 上午09:46:16 version 1.0
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("adv")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/adv/adv_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/adv/adv_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/adv/adv_edit.html") 
})
public class AdvAction extends WWAction {

	private IAdColumnManager adColumnManager;
	private IAdvManager advManager;
	private List<AdColumn> adColumnList;
	private Adv adv;
	private Long acid;
	private String advname; //搜索的广告名
	private Long advid;
	private Integer[] aid;
	private File pic;
	private String picFileName;
	private Date mstartdate;
	private Date menddate;
	private String order;
	private File url;
	private String imgPath;

	private String startTime1;
	private String endTime1;
	private String startTime2;
	private String endTime2;

	public String list() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		return "list";
	}
	
	public String listJson() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		this.webpage = advManager.search(acid, advname,startTime1,endTime1,startTime2,endTime2, this.getPage(), this.getPageSize(),this.order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	public String detail() {
		adv = this.advManager.getAdvDetail(advid);
		return "detail";
	}

	public String click() {
		if (advid == 0) {
			this.getRequest().setAttribute("gourl", "/eop/shop/adv/zhaozu.jsp");
		} else {
			adv = this.advManager.getAdvDetail(advid);
			// 避免同一客户重复点击导致点击数被重复计算
			// 以客户的session做为依据。同一session生命期内只计一次对某一广告的点击数
			if (this.getRequest().getSession().getAttribute(
					"AD" + advid.toString()) == null) {
				this.getRequest().getSession().setAttribute(
						"AD" + advid.toString(), "clicked");
				adv.setClickcount(adv.getClickcount() + 1);
				this.advManager.updateAdv(adv);
			}
			// 不管此session生命期内是否已经计过点击数，点击后的页面还是要转的
			this.getRequest().setAttribute("gourl", adv.getUrl());
		}
		return "go";
	}

	public String delete() {
		
		if(EopSetting.IS_DEMO_SITE){
			for(Integer id:aid){
				if(id<=21){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		
		try {
			this.advManager.delAdvs(aid);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
		}
		
		return this.JSON_MESSAGE;
	}

	public String add() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		return "add";
	}

	public String addSave() {
		if (pic != null) {

			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
				adv.setAtturl(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		if(menddate.getTime()<mstartdate.getTime()){
			this.showErrorJson("截止时间小于开始时间");
			return JSON_MESSAGE;
		}
		
		adv.setBegintime(mstartdate.getTime()/1000);
		adv.setEndtime(menddate.getTime()/1000);
		adv.setDisabled("false");
		try {
			this.advManager.addAdv(adv);
			this.showSuccessJson("新增广告成功");
		} catch (RuntimeException e) {
			this.showErrorJson("新增广告失败");
		}
		return JSON_MESSAGE;
	}

	public String edit() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		adv = this.advManager.getAdvDetail(advid);
		if(adv.getAtturl()!=null&&!StringUtil.isEmpty(adv.getAtturl())){			
			imgPath = UploadUtil.replacePath(adv.getAtturl());
			
		}
		return "edit";
	}

	public String editSave() {
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
				adv.setAtturl(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		adv.setBegintime(mstartdate.getTime()/1000);
		adv.setEndtime(menddate.getTime()/1000);
		this.advManager.updateAdv(adv);
		this.showSuccessJson("修改广告成功");
		return JSON_MESSAGE;
	}

	public String stop() {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(1);
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
		}
		return JSON_MESSAGE;
	}

	public String start() {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(0);
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
		}
		return JSON_MESSAGE;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
	}

	public IAdvManager getAdvManager() {
		return advManager;
	}

	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

	public Adv getAdv() {
		return adv;
	}

	public void setAdv(Adv adv) {
		this.adv = adv;
	}

	public Long getAcid() {
		return acid;
	}

	public void setAcid(Long acid) {
		this.acid = acid;
	}

	public Long getAdvid() {
		return advid;
	}

	public void setAdvid(Long advid) {
		this.advid = advid;
	}

	

	public Integer[] getAid() {
		return aid;
	}

	public void setAid(Integer[] aid) {
		this.aid = aid;
	}

	public List<AdColumn> getAdColumnList() {
		return adColumnList;
	}

	public void setAdColumnList(List<AdColumn> adColumnList) {
		this.adColumnList = adColumnList;
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

	public Date getMstartdate() {
		return mstartdate;
	}

	public void setMstartdate(Date mstartdate) {
		this.mstartdate = mstartdate;
	}

	public Date getMenddate() {
		return menddate;
	}

	public void setMenddate(Date menddate) {
		this.menddate = menddate;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getAdvname() {
		return advname;
	}

	public void setAdvname(String advname) {
		this.advname = advname;
	}

	public File getUrl() {
		return url;
	}

	public void setUrl(File url) {
		this.url = url;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getStartTime1() {
		return startTime1;
	}

	public void setStartTime1(String startTime1) {
		this.startTime1 = startTime1;
	}

	public String getEndTime1() {
		return endTime1;
	}

	public void setEndTime1(String endTime1) {
		this.endTime1 = endTime1;
	}

	public String getStartTime2() {
		return startTime2;
	}

	public void setStartTime2(String startTime2) {
		this.startTime2 = startTime2;
	}

	public String getEndTime2() {
		return endTime2;
	}

	public void setEndTime2(String endTime2) {
		this.endTime2 = endTime2;
	}
}
