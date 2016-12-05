package com.enation.app.shop.core.action.backend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.app.shop.core.model.support.TypeAreaConfig;
import com.enation.app.shop.core.service.IAreaManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;
 
/**
 * 配送方式管理
 * @author kingapex
 *2010-3-26上午08:14:49
 *@author LiFenLong 2014-4-1;4.0版本改造
 */
@SuppressWarnings({ "serial", "rawtypes" })
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("dlyType")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/setting/dly_type_add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/setting/dly_type_edit.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/setting/dly_type_list.html") 
})
public class DlyTypeAction extends WWAction {

	private Integer typeId;
	private Integer[] type_id; // 删除用
	private List logiList;
	private DlyType type;
	private IDlyTypeManager dlyTypeManager;
	private ILogiManager logiManager;	
	private IAreaManager areaManager;
	private Integer firstunit;
	private Integer continueunit;
	private Double[] firstprice;
	private Double[] continueprice;
	private String[] areaGroupName;
	private String[] areaGroupId;
	private Integer[] has_cod;//是否支持货到付款 
	private Integer[] useexp; //是否使用公式
	private String[] expressions;
	private String exp; //异步校验所用
	private Integer defAreaFee; // 是否启用默认配置
	private Boolean isEdit;
	private Integer areacount;
	private Integer arealistsize;
	/**
	 * 添加配送方式
	 * @param logiList 物流公司列表,list
	 * @return 配送方式添加页
	 */
	public String add_type(){
		logiList = this.logiManager.list();
		return "add";
	}
	/**
	 * 修改配送方式 
	 * @param isEdit 是否为修改,Boolean
	 * @param typeId 配送方式Id,Integer
	 * @param type 配送方式,DlyType
	 * @param arealistsize 地区数量，Integer
	 * @param logiList 物流公司列表,List
	 * @return 配送方式修改页
	 */
	public String edit(){
		isEdit= true;
		type = this.dlyTypeManager.getDlyTypeById(typeId);
		arealistsize=0;
		if(type.getTypeAreaList()!=null){
			arealistsize = type.getTypeAreaList().size();
		}
		logiList = this.logiManager.list();
		return "edit";
	}
	/**
	 * 配送方式列表
	 * @param logiList 物流公司列表,List
	 * @return 配送方式列表页
	 */
	public String list(){
		logiList = this.logiManager.list();
		return "list";
	}
	/**
	 * 获取配送方式列表json
	 * @author LiFenLong
	 * @param pageNo 页码,Integer
	 * @param PageSize 每页数量,Integer
	 * @return 配送方式列表json
	 */
	public String listJson(){
		this.webpage = this.dlyTypeManager.pageDlyType( this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 保存配送方式
	 * @param type 配送方式,DlyType
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd() {
		try {
			switch (type.getIs_same().intValue()) {
			case 0: this.saveDiff(false); break;
			case 1: this.saveSame(false); break;
			}
			this.showSuccessJson("配送方式添加成功");
		} catch (Exception e) {
			this.showErrorJson("配送方式添加失败");
			logger.error("配送方式添加失败",e);
		}
		return JSON_MESSAGE;
	}
	
 
	
	/**
	 * 添加统一式配置
	 * @param config 配送方式配置,DlyTypeConfig
	 * @param isUpdate 是否为修改,boolean
	 * @param firstunit 首重,Integer
	 * @param continueunit 续重,Integer
	 * @param firstprice 首重费用,Double[]
	 * @param continueprice 续重费用,Double[]
	 * @param useexp 是否使用公式,Integer[]
	 * @return tid 添加后配送方式Id
	 */
	private Integer saveSame(boolean isUpdate){
		
		DlyTypeConfig config = new DlyTypeConfig();
		config.setFirstunit(this.firstunit); //首重 
		config.setContinueunit(this.continueunit); //续重 
	
		config.setFirstprice(this.firstprice[0]); //首重费用
		config.setContinueprice(this.continueprice[0]); //续重费用
		if(useexp[0]==null){
			useexp[0]=0;
		}
		//启用公式
		if( this.useexp[0].intValue() ==1 ){
			config.setExpression(expressions[0]);
			config.setUseexp(1);
		}else{
			config.setUseexp(0);
		}
		
		type.setHas_cod(0);
		config.setHave_cod(type.getHas_cod());
		int tid=0;
		if(isUpdate){
			this.dlyTypeManager.edit(type, config);
		}else{
			tid=this.dlyTypeManager.add(type, config);
		}
		
		return tid;
	}
	/**
	 * 地区设置
	 * @param config 配送方式配置,DlyTypeConfig
	 * @param isUpdate 是否为修改,boolean
	 * @param firstunit 首重,Integer
	 * @param continueunit 续重,Integer
	 * @param firstprice 首重费用,Double[]
	 * @param continueprice 续重费用,Double[]
	 * @param defAreaFee 默认费用设置,Integer[]
	 * @param useexp 是否使用公式,Integer[]
	 * @param areacount 地区,Integer[]
	 */
	private void saveDiff(boolean isUpdate){
		DlyTypeConfig config = new DlyTypeConfig();
		
		config.setFirstunit(this.firstunit); //首重 
		config.setContinueunit(this.continueunit); //续重 		
		config.setDefAreaFee(this.defAreaFee);//默认费用设置
		
		//启用默认费用配置,费用数组第一个元素
		if(defAreaFee!=null &&defAreaFee.intValue()==1){
			config.setFirstprice(this.firstprice[1]);
			config.setContinueprice(this.continueprice[1]);
			if(useexp[1]==null){
				useexp[1]=0;
			}
			if( this.useexp[1].intValue() ==1 ){
				config.setExpression(expressions[1]);
				config.setUseexp(1);
			}else{
				config.setUseexp(0);
			}
			
		}
		if(areacount==null){
			areacount=0;
		}
		TypeAreaConfig[] configArray= new TypeAreaConfig[areacount+1];
		HttpServletRequest request = this.getRequest();
		
		for(int i=1;i<=areacount;i++){
			if(request.getParameter("areas"+i)!=null){
				String totle_areas = request.getParameter("totle_areas"+i);
				String totle_regions = request.getParameter("totle_regions"+i);
				
				TypeAreaConfig areaConfig = new TypeAreaConfig();
				
				//首重和续重使用统一的设置
				areaConfig.setContinueunit(config.getContinueunit());
				areaConfig.setFirstunit(config.getFirstunit());
				
				String [] areass = request.getParameterValues("areas"+i);
				areaConfig.setUseexp(NumberUtils.toInt(request.getParameter("useexp" + i)));
				areaConfig.setAreaId(totle_areas+totle_regions);
				areaConfig.setAreaName(areass[0]);
				
				//启用公式
				if (NumberUtils.toInt(request.getParameter("useexp" + i)) == 1) {
					areaConfig.setExpression(StringUtil.arrayToString(request.getParameterValues("expressions"+i), ","));
					areaConfig.setUseexp(1);
				}else{
					String firstprice = StringUtil.arrayToString(request.getParameterValues("firstprice"+i), ",");
					String continueprice =  StringUtil.arrayToString(request.getParameterValues("continueprice"+i), ",");
					areaConfig.setFirstprice(NumberUtils.toDouble(firstprice));
					areaConfig.setContinueprice(NumberUtils.toDouble(continueprice));
					config.setUseexp(0);
				}
				//areaConfig.setHave_cod(this.has_cod[i]);
				configArray[i]=areaConfig;
			}
		}
		
		if(isUpdate){
			this.dlyTypeManager.edit(type, config, configArray);
		}else{
			this.dlyTypeManager.add(type, config, configArray);
		}
	}
	
	/**
	 * 修改配送方式
	 * @param type 配送方式,DlyType
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit() {
 
		try {
			if(type.getIs_same().intValue()==1){
				this.saveSame(true);
			}
			
			if(type.getIs_same().intValue()==0){
				this.saveDiff(true);
			}
			
			this.showSuccessJson("配送方式修改成功");
		} catch (Exception e) {
			this.showErrorJson("配送方式修改失败");
			logger.error("配送方式修改失败", e);
		}
		
		return JSON_MESSAGE;
	}
	/**
	 * 删除配送方式
	 * @param type_id 配送方式Id数组,Integer[]
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String delete() {
		this.dlyTypeManager.delete(type_id);
		this.showSuccessJson("删除成功");
		return JSON_MESSAGE;
	}
	
	// set get
	
	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public DlyType getType() {
		return type;
	}

	public void setType(DlyType type) {
		this.type = type;
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

	public Integer getFirstunit() {
		return firstunit;
	}

	public void setFirstunit(Integer firstunit) {
		this.firstunit = firstunit;
	}

	public Integer getContinueunit() {
		return continueunit;
	}

	public void setContinueunit(Integer continueunit) {
		this.continueunit = continueunit;
	}

	public Double[] getFirstprice() {
		return firstprice;
	}

	public void setFirstprice(Double[] firstprice) {
		this.firstprice = firstprice;
	}

	public Double[] getContinueprice() {
		return continueprice;
	}

	public void setContinueprice(Double[] continueprice) {
		this.continueprice = continueprice;
	}

	public Integer getDefAreaFee() {
		return defAreaFee;
	}

	public void setDefAreaFee(Integer defAreaFee) {
		this.defAreaFee = defAreaFee;
	}

	public String[] getAreaGroupName() {
		return areaGroupName;
	}

	public void setAreaGroupName(String[] areaGroupName) {
		this.areaGroupName = areaGroupName;
	}

	public String[] getAreaGroupId() {
		return areaGroupId;
	}

	public void setAreaGroupId(String[] areaGroupId) {
		this.areaGroupId = areaGroupId;
	}

	public Integer[] getUseexp() {
		return useexp;
	}

	public void setUseexp(Integer[] useexp) {
		this.useexp = useexp;
	}

	public String[] getExpressions() {
		return expressions;
	}

	public void setExpressions(String[] expressions) {
		this.expressions = expressions;
	}

	public Integer[] getHas_cod() {
		return has_cod;
	}

	public void setHas_cod(Integer[] hasCod) {
		has_cod = hasCod;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
	}

	public Integer getAreacount() {
		return areacount;
	}

	public void setAreacount(Integer areacount) {
		this.areacount = areacount;
	}

	public Integer getArealistsize() {
		return arealistsize;
	}

	public void setArealistsize(Integer arealistsize) {
		this.arealistsize = arealistsize;
	}

	public IAreaManager getAreaManager() {
		return areaManager;
	}

	public void setAreaManager(IAreaManager areaManager) {
		this.areaManager = areaManager;
	}


}
