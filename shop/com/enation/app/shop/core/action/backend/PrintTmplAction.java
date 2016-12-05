package com.enation.app.shop.core.action.backend;

import java.util.List;

import com.enation.app.shop.core.model.PrintTmpl;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IPrintTmplManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 打印模板
 * 
 * @author lzf<br/>
 *         2010-5-4上午11:10:46<br/>
 *         version 1.0
 * @author LiFenLong 2014-4-1;4.0版本改造         
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class PrintTmplAction extends WWAction {
	
	private IPrintTmplManager printTmplManager;
	private List list;
	private List trash;
	private List listCanUse;
	private Integer[] prt_tmpl_id;
	private Integer prt_tmplId;
	private PrintTmpl printTmpl;
	
	private List logiList;
	private ILogiManager logiManager;
	/**
	 * 跳转至 快递单模板列表页
	 * @return 快递单模板列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 快递单模板列表Json
	 * @param list 快递单模板列表
	 * @return 快递单模板列表Json
	 */
	public String listJson(){
		this.showGridJson(list = printTmplManager.list());
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至快递单添加页
	 * @param logiList 配送方式列表,List
	 * @return 快递单添加页
	 */
	public String add(){
		logiList = this.logiManager.list();
		return "add";
	}
	/**
	 * 保存快递单
	 * @param printTmpl 快递单,printTmpl
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try {
			if(printTmplManager.check(printTmpl.getPrt_tmpl_title())){
				this.showErrorJson("已经存在此快递单模板");
			}else{
				printTmplManager.add(printTmpl);
				this.showSuccessJson("模板添加成功");
			}
		} catch (Exception e) {
			logger.error("模板添加失败", e);
			this.showErrorJson("模板添加失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至修改快递单
	 * @param logiList 配送方式列表,List
	 * @param prt_tmplId 快递单Id,Integer
	 * @param printTmpl 快递单,printTmpl
	 * @return 修改快递单
	 */
	public String edit(){
		logiList = this.logiManager.list();
		printTmpl = printTmplManager.get(prt_tmplId);
		return "edit";
	}
	/**
	 * 保存修改快递单
	 * @param printTmpl 快递单,printTmpl
	 * @return json 
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		try {
			printTmplManager.edit(printTmpl);
			this.showSuccessJson("模板修改成功");
		} catch (Exception e) {
			this.showErrorJson("模板修改失败");
			logger.error("模板修改失败", e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 删除快递单
	 * @param prt_tmpl_id 快递单Id,Integer
	 * @return json 
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		try {
			this.printTmplManager.clean(prt_tmpl_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
			logger.error("模板删除失败", e);
		}
		return JSON_MESSAGE;
	}
	public IPrintTmplManager getPrintTmplManager() {
		return printTmplManager;
	}
	public void setPrintTmplManager(IPrintTmplManager printTmplManager) {
		this.printTmplManager = printTmplManager;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getTrash() {
		return trash;
	}
	public void setTrash(List trash) {
		this.trash = trash;
	}
	public List getListCanUse() {
		return listCanUse;
	}
	public void setListCanUse(List listCanUse) {
		this.listCanUse = listCanUse;
	}
	public Integer[] getPrt_tmpl_id() {
		return prt_tmpl_id;
	}
	public void setPrt_tmpl_id(Integer[] prt_tmpl_id) {
		this.prt_tmpl_id = prt_tmpl_id;
	}
	public Integer getPrt_tmplId() {
		return prt_tmplId;
	}
	public void setPrt_tmplId(Integer prt_tmplId) {
		this.prt_tmplId = prt_tmplId;
	}
	public PrintTmpl getPrintTmpl() {
		return printTmpl;
	}
	public void setPrintTmpl(PrintTmpl printTmpl) {
		this.printTmpl = printTmpl;
	}
	public List getLogiList() {
		return logiList;
	}
	public void setLogiList(List logiList) {
		this.logiList = logiList;
	}
	public ILogiManager getLogiManager() {
		return logiManager;
	}
	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
}
