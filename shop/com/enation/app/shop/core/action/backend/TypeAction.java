package com.enation.app.shop.core.action.backend;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.GoodsType;
import com.enation.app.shop.core.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.model.support.TypeSaveState;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

/**
 * 类型action 负责商品类型的添加和修改 <br/>
 * 负责类型插件相关业务管理
 * 
 * @author apexking
 * @author LiFenLong 2014-4-1;4.0版本改造
 * 
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("type")
@Results({
	@Result(name="step1", type="freemarker", location="/shop/admin/type/type_add_step1.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/type/type_list.html"),
	@Result(name="add_props", type="freemarker", location="/shop/admin/type/type_props.html"),
	@Result(name="add_parms", type="freemarker", location="/shop/admin/type/type_params.html"),
	@Result(name="join_brand", type="freemarker", location="/shop/admin/type/type_brand.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/type/type_edit_step1.html"),
	@Result(name="props_input", type="freemarker", location="/com/enation/app/shop/plugin/standard/type/props_input.html"),
	@Result(name="params_input", type="freemarker", location="/com/enation/app/shop/plugin/standard/type/params_input.html"),
	@Result(name="brand_list", type="freemarker", location="/com/enation/app/shop/plugin/standard/type/brand_input.html"),
	@Result(name="field", type="freemarker", location="/shop/admin/type/type_field.html"),
	@Result(name="param_item", type="freemarker", location="/shop/admin/type/param_input_item.html")
})
public class TypeAction extends WWAction {

	private IGoodsTypeManager goodsTypeManager;
	private IBrandManager brandManager;
	private List brandlist;
	private GoodsTypeDTO goodsType;

	/** ****用于属性信息的读取***** */
	private String[] propnames; // 参数名数组

	private int[] proptypes; // 属性类型数组

	private String[] options; // 可选值列表
	
	private String[] datatype; //校验类型
	private int[] required; //是否必填
	private String[] unit; //单位
	
	

	/** ****用于参数信息的读取***** */
	private String paramnum; // 参数组中参数个数信息

	private String[] groupnames; // 参数组名数组

	private String[] paramnames; // 参数名数组

	private Integer typeId;

	private int is_edit;

	private Integer[] type_id;// 清空商品类型使用

	// 用户选择关联的品牌
	private Integer[] chhoose_brands;

	private static String GOODSTYPE_SESSION_KEY = "goods_type_in_session";

	private static String GOODSTYPE_STATE_SESSION_KEY = "goods_type_state_in_session";

	private String order;
	
	private Integer otherType;
	

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * 查看类型名称是否存在
	 * @return json 
	 * return 1.操作成功.0.操作失败
	 */
	public String checkname(){
		if(EopSetting.IS_DEMO_SITE){
			if(goodsType.getType_id()!=null && goodsType.getType_id()<=48){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		 if(this.goodsTypeManager.checkname( this.goodsType.getName(),goodsType.getType_id() )){
			 this.showErrorJson("类型名称已存在");
		 }else{
			 this.goodsTypeManager.save(goodsType);
			 this.showSuccessJson("保存成功");
		 }
		 return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至类型列表
	 * @return 类型列表
	 */
	public String list() {
		return "list";
	}
	/**
	 * 获取类型列表Json
	 * @author LiFenLong
	 * @return 类型列表Json
	 */
	public String listJson(){
		this.webpage = this.goodsTypeManager.pageType(order, this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	/**
	 * 跳转设置类型页面第一步
	 * @return 设置类型页面
	 */
	public String step1() {
		return "step1";
	}
	/**
	 * 跳转至设置类型页面第二步
	 * @return json
	 * @param 商品类型 GoodsTypeDTO
	 * result 1.操作成功.0.操作失败
	 */
	public String step2() {

		// *步骤的状态，存在session中，在每步进行更改这个状态*//
		TypeSaveState saveState = new TypeSaveState();
		this.session.put(GOODSTYPE_STATE_SESSION_KEY, saveState);

		GoodsType tempType = getTypeFromSession();
		if (tempType == null) {

			this.session.put(GOODSTYPE_SESSION_KEY, goodsType); // 用页面上收集的信息
		} else { // 用于编辑的时候，先从session取出从数据库里读取的类型信息，然后根据页面收集用户选择的情况改变session中的信息。

			if (is_edit == 1) {
				tempType.setHave_parm(goodsType.getHave_parm());
				tempType.setHave_prop(goodsType.getHave_prop());
				tempType.setJoin_brand(goodsType.getJoin_brand());
				tempType.setIs_physical(goodsType.getIs_physical());
				tempType.setHave_field(goodsType.getHave_field());
				tempType.setName(goodsType.getName());
			} else {
				this.session.put(GOODSTYPE_SESSION_KEY, goodsType);
			}
		}

		String result = getResult();
		if (result == null) {
			this.renderText("参数不正确！");
		}
		this.showSuccessJson("添加成功");
		return this.JSON_MESSAGE;
	}

	/**
	 * 编辑类型
	 * @param typeId 商品类型Id
	 * @return
	 */
	public String edit() {
		this.goodsType = this.goodsTypeManager.get(typeId);
		return "edit";
	}
	public String editJson(){
		this.goodsType = this.goodsTypeManager.get(typeId);
		this.session.put(GOODSTYPE_SESSION_KEY, goodsType);
		if(goodsType.getProps()!=null){
			JSONArray jsonar=JSONArray.fromObject(goodsType.getProps());
			Object[] objar= jsonar.toArray();
			int i=0;
			for (Object object : objar) {
				JSONObject obj= (JSONObject)object;
				obj.put("id", i);
				i++;
			}
			json =(JSONArray.fromObject(objar).toString());
		}
		this.is_edit = 1;
		if(json==null){
			this.json="[]";
		}
		return JSON_MESSAGE;
	}

	public String editOther(){
		this.goodsType=this.goodsTypeManager.get(typeId);
		if(otherType==2){
			return "add_props";
		}
		if(otherType==3){
			return "add_parms";
		}
		if(otherType==4){
			brandlist= this.brandManager.list();
			return "join_brand";
		}
		return null;
	}
	
	public String paramItem(){
		return "param_item";
	}
	
	/**
	 * 保存参数信息
	 * @return
	 */
	public String saveParams() {
		String[] paramnums = new String[] {};
		if (paramnum != null&&!StringUtil.isEmpty(paramnum)) {
			if (paramnum.indexOf(",-1") >= 0) {// 检查是否有删除的参数组
				paramnum = paramnum.replaceAll(",-1", "");
			}
			paramnums = paramnum.split(",");
		}

		String params = this.goodsTypeManager.getParamString(paramnums,
				groupnames, paramnames, null);
		GoodsType prop= this.goodsTypeManager.getById(typeId);
		prop.setParams(params);
		this.goodsTypeManager.save(prop);
		this.showSuccessJson("保存成功");
		return JSON_MESSAGE;
		
		/*GoodsType tempType = getTypeFromSession();
		TypeSaveState saveState = getSaveStateFromSession();
		tempType.setParams(params);
		saveState.setDo_save_params(1);
		return getResult();*/
	}

	/**
	 * 保存属性信息
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String saveProps() throws UnsupportedEncodingException {

		HttpServletRequest req=getRequest();
		//设置请求编码
		req.setCharacterEncoding("UTF-8");
		//获取编辑数据 这里获取到的是json字符串
		String inserted = req.getParameter("inserted");
		String deleted = req.getParameter("deleted");
		String updated = req.getParameter("updated");
		
		if(inserted != null){
			//把json字符串转换成对象
			GoodsType inprop= this.goodsTypeManager.getById(typeId);
			
			JSONArray json = JSONArray.fromObject(inserted);
			List<Attribute> list = (List) JSONArray.toCollection(json,Attribute.class);
			String str=null;
			if(inprop.getProps()!=null&&!StringUtil.isEmpty(inprop.getProps())){				
				JSONArray propjson = JSONArray.fromObject(inprop.getProps());
				List<Attribute> proplist = (List) JSONArray.toCollection(propjson,Attribute.class);
				proplist.addAll(list);
				str = JSONArray.fromObject(proplist).toString();
			}else{
				str=JSONArray.fromObject(list).toString();
			}
			inprop.setProps(str);
			this.goodsTypeManager.save(inprop);
			this.showSuccessJson("添加成功");
		}
		
		if(deleted != null){
			GoodsType dataprop= this.goodsTypeManager.getById(typeId);
			String datastr=dataprop.getProps();
			JSONArray datajson=JSONArray.fromObject(datastr);
			Object[] dataobj= datajson.toArray();
			int i=0;
			for (Object daobj : dataobj) {
				JSONObject obj= (JSONObject)daobj;
				obj.put("id", i);
				i++;
			}
			JSONArray detejson = JSONArray.fromObject(deleted);
			Object[] detobj= detejson.toArray();
			for(Object dobj : dataobj){
				for (Object uobj : detobj) {
					JSONObject d_obj= (JSONObject)dobj;
					JSONObject u_obj= (JSONObject)uobj;
					if(d_obj.get("id").equals(u_obj.get("id"))){				
						datajson.remove(dobj);
					}
				}
			}
			Object[] dedatajson = datajson.toArray();
			for (Object object : dedatajson) {
				JSONObject updata_obj= (JSONObject)object;
				updata_obj.remove("id");
			}
			dataprop.setProps(JSONArray.fromObject(dedatajson).toString());
			this.goodsTypeManager.save(dataprop);
			this.showSuccessJson("删除成功");
		}
		
		if(updated != null){
			GoodsType dataprop= this.goodsTypeManager.getById(typeId);
			String datastr=dataprop.getProps();
			JSONArray datajson=JSONArray.fromObject(datastr);
			Object[] dataobj= datajson.toArray();
			int i=0;
			for (Object daobj : dataobj) {
				JSONObject obj= (JSONObject)daobj;
				obj.put("id", i);
				i++;
			}
			JSONArray upjson = JSONArray.fromObject(updated);
			Object[] upobj= upjson.toArray();
			for(Object dobj : dataobj){
				for (Object uobj : upobj) {
					JSONObject d_obj= (JSONObject)dobj;
					JSONObject u_obj= (JSONObject)uobj;
					if(d_obj.get("id").equals(u_obj.get("id"))){				
						datajson.remove(dobj);
						datajson.add((Integer) u_obj.get("id"),u_obj);
					}
				}
			}
			Object[] updatajson = datajson.toArray();
			for (Object object : updatajson) {
				JSONObject updata_obj= (JSONObject)object;
				updata_obj.remove("id");
			}
			dataprop.setProps(JSONArray.fromObject(updatajson).toString());
			this.goodsTypeManager.save(dataprop);
			this.showSuccessJson("修改成功");
		}
		return JSON_MESSAGE;
	}

	/**.
	 * 保存品牌数据
	 * 
	 * @return
	 */
	public String saveBrand() {
		
		GoodsType prop= this.goodsTypeManager.getById(typeId);
		prop.setBrand_ids(this.chhoose_brands);

		this.goodsTypeManager.saveTypeBrand(prop);
		this.showSuccessJson("保存成功");
		return JSON_MESSAGE;
		
		/*GoodsType tempType = getTypeFromSession();
		tempType.setBrand_ids(this.chhoose_brands);

		// *标志流程品牌保存状态为已保存
		TypeSaveState saveState = getSaveStateFromSession();
		saveState.setDo_save_brand(1);
		return getResult();*/
	}
	
	
	

	public String save() {
		GoodsTypeDTO tempType = getTypeFromSession();
		tempType.setDisabled(0);
		tempType.setBrandList(null);
		tempType.setPropList(null);
		tempType.setParamGroups(null);
		
		this.typeId =this.goodsTypeManager.save(tempType);
		this.session.remove(GOODSTYPE_SESSION_KEY);
		
		//没有自定义商品字段直接保存,并提示
		if(tempType.getHave_field() ==0){
			this.msgs.add("商品类型保存成功");
			this.urls.put("商品类型列表", "type!list.do");	
			return this.MESSAGE;
		}else{//定义了商品字段，到商品字段定义页面
			return "field";	
		}
		
	}

	
	
	//
	private GoodsTypeDTO getTypeFromSession() {
		Object obj = this.session.get(GOODSTYPE_SESSION_KEY);

		if (obj == null) {
			// this.renderText("参数不正确");
			return null;
		}

		GoodsTypeDTO tempType = (GoodsTypeDTO) obj;

		return tempType;
	}

	//
	/**
	 * 当前流程的保存状态
	 * 
	 * @return
	 */
	private TypeSaveState getSaveStateFromSession() {

		// *从session中取出收集的类型数据*//
		Object obj = this.session.get(GOODSTYPE_STATE_SESSION_KEY);
		if (obj == null) {
			this.renderText("参数不正确");
			return null;
		}
		TypeSaveState tempType = (TypeSaveState) obj;
		return tempType;
	}

	//
	/**
	 * 根据流程状态以及在第一步时定义的
	 * 
	 * @return
	 */
	private String getResult() {

		GoodsType tempType = getTypeFromSession();
		this.goodsType = getTypeFromSession();
		TypeSaveState saveState = getSaveStateFromSession();
		String result = null;

		if (tempType.getHave_prop() != 0 && saveState.getDo_save_props() == 0) { // 用户选择了此类型有属性，同时还没有保存过
			result = "add_props";
		} else if (tempType.getHave_parm() != 0
				&& saveState.getDo_save_params() == 0) { // 用户选择了此类型有参数，同时还没有保存过
			result = "add_parms";
		} else if (tempType.getJoin_brand() != 0
				&& saveState.getDo_save_brand() == 0) { // 用户选择了此类型有品牌，同时还没有保存过
			brandlist= this.brandManager.list();
			result = "join_brand";
		} else {

			result = save();
		}

		return result;
	}

	/**
	 * 将商品类型放入回收站
	 * 
	 * @return
	 */
	public String delete() {
		try {
			if(EopSetting.IS_DEMO_SITE){
				for(Integer tid:type_id){
					if(tid<=48){
						this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
						return JSON_MESSAGE;
					}
				}
			}
			
			int result  = goodsTypeManager.delete(type_id);
			if(result ==1)
			this.showSuccessJson("删除成功");
			else
			this.showErrorJson("此类型存在与之关联的商品或类别，不能删除。");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 清空商品类型
	 * 
	 * @return
	 */
	public String clean() {
		try {
			goodsTypeManager.clean(type_id);
			this.json = "{'result':0,'message':'清除成功'}";
		} catch (RuntimeException e) {
			this.json = "{'result':1,'message':'清除失败'}";
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 从回收站中还原商品类型
	 * 
	 * @return
	 */
	public String revert() {
		try {
			goodsTypeManager.revert(type_id);
			this.json = "{'result':0,'message':'还原成功'}";
		} catch (RuntimeException e) {
			this.json = "{'result':1,'message':'还原失败'}";
		}
		return this.JSON_MESSAGE;
	}

	private List attrList;// 某个类型的属性定义列表
	private ParamGroup[] paramAr; // 某个类型的参数定义列表
	//

	// 读取某个类型下的商品属性定义并形成输入html
	// 被ajax抓取用
	public String disPropsInput() {
		attrList = this.goodsTypeManager.getAttrListByTypeId(typeId);
		attrList =attrList==null || attrList.isEmpty() ?null:attrList;
		return "props_input"; 
	}

	//
	// 读取某个类型下的商品参数定义并形成输入html
	// 被ajax抓取用
	public String disParamsInput() {
		this.paramAr = this.goodsTypeManager.getParamArByTypeId(typeId);
		return "params_input";
	}


	//添加或修改商品时异步读取品牌列表
	public String listBrand(){
		this.brandlist = this.goodsTypeManager.listByTypeId(typeId);
		return "brand_list";
	}	

	public List getAttrList() {
		return attrList;
	}

	public void setAttrList(List attrList) {
		this.attrList = attrList;
	}

	public ParamGroup[] getParamAr() {
		return paramAr;
	}

	public void setParamAr(ParamGroup[] paramAr) {
		this.paramAr = paramAr;
	}

	public GoodsTypeDTO getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(GoodsTypeDTO goodsType) {
		this.goodsType = goodsType;
	}

	public String[] getPropnames() {
		return propnames;
	}

	public void setPropnames(String[] propnames) {
		this.propnames = propnames;
	}

	public int[] getProptypes() {
		return proptypes;
	}

	public void setProptypes(int[] proptypes) {
		this.proptypes = proptypes;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}

	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}

	public String[] getGroupnames() {
		return groupnames;
	}

	public void setGroupnames(String[] groupnames) {
		this.groupnames = groupnames;
	}

	public String[] getParamnames() {
		return paramnames;
	}

	public void setParamnames(String[] paramnames) {
		this.paramnames = paramnames;
	}

	public String getParamnum() {
		return paramnum;
	}

	public void setParamnum(String paramnum) {
		this.paramnum = paramnum;
	}

	public Integer[] getChhoose_brands() {
		return chhoose_brands;
	}

	public void setChhoose_brands(Integer[] chhoose_brands) {
		this.chhoose_brands = chhoose_brands;
	}

	

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public int getIs_edit() {
		return is_edit;
	}

	public void setIs_edit(int is_edit) {
		this.is_edit = is_edit;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

	public List getBrandlist() {
		return brandlist;
	}

	public void setBrandlist(List brandlist) {
		this.brandlist = brandlist;
	}

	public String[] getDatatype() {
		return datatype;
	}

	public void setDatatype(String[] datatype) {
		this.datatype = datatype;
	}

	public int[] getRequired() {
		return required;
	}

	public void setRequired(int[] required) {
		this.required = required;
	}

	public String[] getUnit() {
		return unit;
	}

	public void setUnit(String[] unit) {
		this.unit = unit;
	}

	public Integer getOtherType() {
		return otherType;
	}

	public void setOtherType(Integer otherType) {
		this.otherType = otherType;
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	
	
}
