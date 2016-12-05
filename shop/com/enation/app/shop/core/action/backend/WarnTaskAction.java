package com.enation.app.shop.core.action.backend;

import com.enation.framework.action.WWAction;

/**
 * 紧急库存任务维护
 * @author xiaokx
 *
 */
public class WarnTaskAction extends WWAction {
//	private String name;
//	private String sn;
//	private IGoodsManager goodsManager;
//	private IWarnTaskManager warnTaskManager;
//	private Integer goodsId;
//	private IDepotManager depotManager;
//	private List<Depot> listDepot;//仓库列表
//	private List<Double> listSphere;//度数列表
//	private List<Double> listCylinder;//散光列表
//	private List<Map> listColor;//颜色列表 
//	private Goods goods;
//	private Integer warn_num;//商品报警数
//	private String depotval;//仓库串
//	private String sphereval;//度数串
//	private String cylinderval;//散光串
//	private String productval;//产品串 产品和颜色是一一对应的关系
//	
//	private Integer depotId;
//	private IAdminUserManager  adminUserManager;
//	private Map taskInfo;//紧急任务列表
//	private Integer taskId;
//	
//	private List<Map> DepotStore;
//	private IPresbyopicStoreManager presbyopicStoreManager;//老花镜库存
//	private IConactLensStoreManager conactLensStoreManager;//隐形眼镜彩色隐形库存
//	private IGlassesStoreManager glassesStoreManager;//光学镜片库存
//	private IFrameManager frameManager;//镜架
//	private IGoodsStoreManager goodsStoreManager;
//	
//	private IStoreLogManager storeLogManager;
//	
//	private String[] goodsid;
//	private String[] degreeid;
//	private String[] depotid;
//	private String[] degree_store;
//	private String[] sphere;
//	private String[] productid;
//	
//	private String[] spheres;
//	private String[] cylinders;
//	
//	
//	private IDaoSupport daoSupport;
//	private IDaoSupport baseDaoSupport; 
//	//紧急任务列表
//	public String execute(){
//			this.webpage = this.warnTaskManager.listAll(this.getPage(),this.getPageSize());
//			return "list";
//	}
//	
//	//查找添加商品
//	public String goodslist(){
//		if(name!=null ) name = StringUtil.toUTF8(name);
//		if(!StringUtil.isEmpty(name) || !StringUtil.isEmpty(sn))
//			this.webpage = goodsManager.searchGoods(null,null,null, name, sn, null, null, null,  this.getPage(),this.getPageSize());
//		return "goodslist";
//	}
//	
//	//创建任务
//	public String addTask(){
//		goods = goodsManager.getGoods(goodsId);
//		listDepot = depotManager.list();
//		if(goods.getCat_id()==6){
//			//镜片库存--镜片 glasses_store
//			listSphere = StringUtil.createDegreeByScope(goods.getMax_degree(), goods.getMin_degree(), 8, -8, 0.5, 0.25);
//			listCylinder = StringUtil.createDegreeByScope(6, -6, 8, -8, 0.5, 0.5);
//		}
//		else if(goods.getCat_id()==1||goods.getCat_id()==2){
//			//隐形库存--隐形眼镜,彩色隐形眼镜 conactlens_store
//			listSphere = StringUtil.createDegreeByScope(goods.getMax_degree(), goods.getMin_degree(), 6, -6, 0.5, 0.25);
//			listSphere.remove(-0.25);
//			listSphere.remove(-0.5);
//			listSphere.remove(-0.75);
//		}
//		else if(goods.getCat_id()==3||goods.getCat_id()==4||goods.getCat_id()==12){
//			//镜架库存表--板材镜架,金属镜架,太阳镜 frame_store
//			listColor = this.warnTaskManager.listColor(goodsId);
//		}
//		else if(goods.getCat_id()==19){
//			//老花镜库存表--老花镜 presbyopic_store
//			listSphere = StringUtil.createDegreeByScope(4, 1, 1, -1, 0.5, 0.5);
//		}else{
//			//其他货品库存 product_store 
//		}
//		return "addtask";
//	}
//	/**
//	 * 保存任务
//	 * @return
//	 */
//	public String saveTask(){
//		if(sphereval.endsWith(","))
//			sphereval = sphereval.substring(0,sphereval.length()-1);
//		if(cylinderval.endsWith("|"))
//			cylinderval = cylinderval.substring(0,cylinderval.length()-1);
//		if(productval.endsWith(","))
//			productval = productval.substring(0,productval.length()-1);
//		goods = goodsManager.getGoods(goodsId);
//		if(StringUtil.isEmpty(depotval)){
//			this.showErrorJson("仓库不能为空");
//			return this.JSON_MESSAGE;
//		}
//		if(goods.getCat_id()==6){
//			//镜片库存--镜片 glasses_store
//			if(StringUtil.isEmpty(sphereval)){
//				this.showErrorJson("度数不能为空");
//				return this.JSON_MESSAGE;
//			}
//			if(StringUtil.isEmpty(cylinderval)){
//				this.showErrorJson("散光不能为空");
//				return this.JSON_MESSAGE;
//			}
//		}
//		else if(goods.getCat_id()==1||goods.getCat_id()==2){
//			//隐形库存--隐形眼镜,彩色隐形眼镜 conactlens_store
//			if(StringUtil.isEmpty(sphereval)){
//				this.showErrorJson("度数不能为空");
//				return this.JSON_MESSAGE;
//			}
//		}
//		else if(goods.getCat_id()==3||goods.getCat_id()==4||goods.getCat_id()==12){
//			//镜架库存表--板材镜架,金属镜架,太阳镜 frame_store
//			if(StringUtil.isEmpty(productval)){
//				this.showErrorJson("颜色不能为空");
//				return this.JSON_MESSAGE;
//			}
//		}
//		else if(goods.getCat_id()==19){
//			//老花镜库存表--老花镜 presbyopic_store
//			if(StringUtil.isEmpty(sphereval)){
//				this.showErrorJson("度数不能为空");
//				return this.JSON_MESSAGE;
//			}
//		}
//		Map map = new HashMap();
//		map.put("goods", goods);
//		map.put("depotval", depotval);
//		map.put("sphereval", sphereval);
//		map.put("cylinderval", cylinderval);
//		map.put("productval", productval);
//		this.warnTaskManager.saveTask(map);
//		this.showSuccessJson("保存成功");
//		return this.JSON_MESSAGE;
//	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	public String addWarn(){
//		return this.JSON_MESSAGE;
//	}
//	
//
//	/**
//	 * 保存商品报警数
//	 * @return
//	 */
//	public String saveWarn(){
//		return this.JSON_MESSAGE;
//	}
//	
//
//	//查找紧急任务商品
//	public String listdepot(){
//		AdminUser user  = this.UserConext.getCurrentAdminUser();
//		DepotUser depotUser =(DepotUser)user;
//		if(name!=null ) name = StringUtil.toUTF8(name);
//		//if(!StringUtil.isEmpty(name) || !StringUtil.isEmpty(sn))
//		this.webpage = warnTaskManager.listdepot(depotUser.getDepotid(),name,sn, this.getPage(),this.getPageSize());
//		return "listdepot";
//	}
//	//维护紧急任务商品
//	public String maintaintask(){
//			AdminUser user  = this.UserConext.getCurrentAdminUser();
//			DepotUser depotUser =(DepotUser)user;
//			String sphere = "";
//			String cylinder = "";
//			String productids = "";
//			Integer catid;
//			Integer goodsid;
//			if(taskId!=null){
//				taskInfo = this.warnTaskManager.listTask(taskId);
//				sphere = taskInfo.get("sphere")==null?"":taskInfo.get("sphere").toString();
//				cylinder = taskInfo.get("cylinder")==null?"":taskInfo.get("cylinder").toString();
//				productids = taskInfo.get("productids")==null?"":taskInfo.get("productids").toString();
//				catid = Integer.valueOf(taskInfo.get("catid").toString());
//				goodsid = Integer.valueOf(taskInfo.get("goodsid").toString());
//				goods = goodsManager.getGoods(goodsid);
//				if(catid == 1 || catid== 2){
//					//隐形库存--隐形眼镜,彩色隐形眼镜
//					DepotStore = new ArrayList<Map>();
//					if(sphere.equals("all")){
//						listSphere = StringUtil.createDegreeByScope(goods.getMax_degree(), goods.getMin_degree(), 6, -6, 0.5, 0.25);
//						listSphere.remove(-0.25);
//						listSphere.remove(-0.5);
//						listSphere.remove(-0.75);
//					}
//					else
//						listSphere = StringUtil.createDegreeByScope(sphere.split(","));
//					for(int i = 0; i < listSphere.size(); i++){
//						Map store = new HashMap();
//						List<Map> temp = conactLensStoreManager.getDegreeDepotStore(goodsid,depotUser.getDepotid(), listSphere.get(i));
//						store.put("goodsid", goodsid);
//						store.put("sphere", listSphere.get(i) );
//						store.put("depotid", depotUser.getDepotid());
//						if (temp.size()>0){
//							store.put("store", temp.get(0).get("store"));
//							store.put("id", temp.get(0).get("id"));
//						}
//						else{
//							store.put("store", 0);
//							store.put("id", 0);
//						}
//						DepotStore.add(store);
//					}
//				}else if(catid == 6){
//					//镜片库存--镜片
//					//glassesStoreManager
//					DepotStore = new ArrayList<Map>();
//					int count =0;
//					if(sphere.equals("all")){
//						listSphere = StringUtil.createDegreeByScope(goods.getMax_degree(), goods.getMin_degree(), 8, -8, 0.5, 0.25);
//						count = listSphere.size();
//					}
//					else{
//						listSphere = StringUtil.createDegreeByScope(sphere.split(","));
//					}
//					String[] tempCylinder = cylinder.split("\\|");
//					if(count == 0)
//						count = tempCylinder.length;
//					for(int i=0;i<count;i++){
//						if(tempCylinder.length == 1)
//							listCylinder = StringUtil.createDegreeByScope(tempCylinder[0].split(","));//柱镜
//						else
//							listCylinder = StringUtil.createDegreeByScope(tempCylinder[i].split(","));//柱镜
//						List<Map> temp1 = glassesStoreManager.getDegreeDepotStore(goodsid,depotUser.getDepotid(), listSphere.get(0),listCylinder.get(0));
//						if(temp1 == null || temp1.size()==0){
//							glassesStoreManager.reCreate(goodsid, depotUser.getDepotid());
//						}
//						for(int j=0;j<listCylinder.size();j++){
//							Map store = new HashMap();
//							List<Map> temp = glassesStoreManager.getDegreeDepotStore(goodsid,depotUser.getDepotid(), listSphere.get(i),listCylinder.get(j));
//							store.put("goodsid", goodsid);
//							store.put("sphere", listSphere.get(i) );
//							store.put("cylinder", listCylinder.get(j) );
//							store.put("depotid", depotUser.getDepotid());
//							if (temp.size()>0){
//								store.put("store", temp.get(0).get("store"));
//								store.put("id", temp.get(0).get("id"));
//							}
//							else{
//								store.put("store", 0);
//								store.put("id", 0);
//							}
//							DepotStore.add(store);
//						}
//					}
//				}else if(catid == 19){
//					//老花镜库存表--老花镜 
//					DepotStore = new ArrayList<Map>();
//					if(sphere.equals("all"))
//						listSphere = StringUtil.createDegreeByScope(4, 1, 1, -1, 0.5, 0.5);
//					else
//						listSphere = StringUtil.createDegreeByScope(sphere.split(","));
//					for(int i = 0; i < listSphere.size(); i++){
//						Map store = new HashMap();
//						List<Map> temp = presbyopicStoreManager.getDegreeDepotStore(goodsid,depotUser.getDepotid(), listSphere.get(i));
//						store.put("goodsid", goodsid);
//						store.put("sphere", listSphere.get(i) );
//						store.put("depotid", depotUser.getDepotid());
//						if (temp.size()>0){
//							store.put("store", temp.get(0).get("store"));
//							store.put("id", temp.get(0).get("id"));
//						}
//						else{
//							store.put("store", 0);
//							store.put("id", 0);
//						}
//						DepotStore.add(store);
//					}
//				}else if(catid == 3 || catid==4 || catid ==12){
//					//镜架库存表--板材镜架,金属镜架,太阳镜
//					listColor = new ArrayList<Map>();
//					String[] color = productids.split(",");
//					List<Map> colorList = this.warnTaskManager.listColor(goodsid);
//					for(int i=0;i<color.length;i++){
//						Map store = new HashMap();
//						List<Map> temp = frameManager.getDegreeDepotStore(goodsid, depotUser.getDepotid(), Integer.valueOf(color[i]));
//						store.put("depotid", depotUser.getDepotid());
//						store.put("goodsid", goodsid);
//						if (temp.size()>0){
//							store.put("store", temp.get(0).get("store"));
//							store.put("color", temp.get(0).get("color"));
//							store.put("id", temp.get(0).get("id"));
//							store.put("productid", temp.get(0).get("productid"));
//						}
//						else{
//							store.put("id", 0);
//							store.put("store", 0);
//							store.put("productid", color[i]);
//							for(int j=0;j<colorList.size();j++){
//								if(colorList.get(j).get("productid").toString().equals(color[i])){
//									store.put("color", colorList.get(j).get("color") );
//								}
//							}
//						}
//						listColor.add(store);
//					}
//				}else{
//					DepotStore = new ArrayList<Map>();
//					List<Map> temp = goodsStoreManager.getDegreeDepotStore(goodsid, depotUser.getDepotid());
//					Map store = new HashMap();
//					store.put("depotid", depotUser.getDepotid());
//					store.put("goodsid", goodsid);
//					if (temp.size()>0){
//						store.put("store", temp.get(0).get("store"));
//						store.put("id", temp.get(0).get("id"));
//					}
//					else{
//						store.put("store", 0);
//						store.put("id", 0);
//					}
//					DepotStore.add(store);
//				}
//			}
//			
//		return "maintaintask";
//	}
//	
//	public String maintain(){
//		int total=0;
//		if(degreeid==null || degreeid.length == 0 ){
//			//this.showSuccessJson("保存失败");
//			return "{result:0}"; 
//		}
//		goods = goodsManager.getGoods(StringUtil.toInt(goodsid[0],true));
//		
//		if(goods.getCat_id()==6){
//			//镜片库存--镜片 glasses_store
//			for(int i=0;i<degreeid.length;i++){
//				int depotid1 =StringUtil.toInt(depotid[i],true);
//				int storeid =StringUtil.toInt(degreeid[i],true);
//				int store = StringUtil.toInt( degree_store[i] ,true); 
//				double sphere = StringUtil.toDouble(spheres[i],true);
//				double cylinder = StringUtil.toDouble(cylinders[i],true);
////				if(storeid==0 ){ //没有维护过、新增
////					//.daoSupport.execute("insert  into  es_frame_store(goodsid,productid,depotid,store)values(?,?,?,?)", goods.getGoods_id(),productid1,depotid1,store);
////					GlassesStore glassesStore = new GlassesStore();
////					glassesStore.setCylinder(cylinder);
////					glassesStore.setSphere(sphere);
////					glassesStore.setGoodsid(goods.getGoods_id());
////					glassesStore.setStore(store);
////					glassesStore.setDepotid(depotid1);
////					this.baseDaoSupport.insert("glasses_store", glassesStore);
////				}else{
//					this.daoSupport.execute("update es_glasses_store set store=store+? where goodsid=? and depotid=? and sphere=? and cylinder=?",store, goods.getGoods_id(),depotid1,sphere,cylinder);
//				//} 
//				total+=store;
//			}
//			this.daoSupport.execute("update es_goods set store=(select sum(store) from es_glasses_store where goodsid=?)  where goods_id =?",goods.getGoods_id(), goods.getGoods_id());
//			this.daoSupport.execute("update es_product set store=(select sum(store) from es_glasses_store where goods_id=?) where goods_id=? ", goods.getGoods_id(),goods.getGoods_id());
//		}
//		else if(goods.getCat_id()==1||goods.getCat_id()==2){
//			//隐形库存--隐形眼镜,彩色隐形眼镜 conactlens_store
//			for(int i=0;i<degreeid.length;i++){
//				int id =StringUtil.toInt(degreeid[i],true);
//				int depotid1 =StringUtil.toInt(depotid[i],true);
//				int degree_store1 =StringUtil.toInt(degree_store[i],true);
//				double sphere1 =StringUtil.toDouble( sphere[i],true);
//				int goodsid1 = StringUtil.toInt(goodsid[i],true);
//				ConactLensStore conactLensStore = new ConactLensStore();
//				conactLensStore.setDepotid(depotid1);
//				conactLensStore.setGoodsid(goodsid1);
//				
//				conactLensStore.setSphere(sphere1);
//				conactLensStore.setStore(degree_store1);
//				conactLensStore.setId(id);
//				this.conactLensStoreManager.stock(conactLensStore);
//				total+= degree_store1;
//			}	
//		}
//		else if(goods.getCat_id()==3||goods.getCat_id()==4||goods.getCat_id()==12){
//			//镜架库存表--板材镜架,金属镜架,太阳镜 frame_store
//			for(int i=0;i<degreeid.length;i++){
//				
//				int id =StringUtil.toInt(degreeid[i],true);
//				int productid1 =StringUtil.toInt(productid[i],true);
//				int depotid1 =StringUtil.toInt(depotid[i],true);
//				int store =StringUtil.toInt(degree_store[i],true);
//				
//				
//				if(id==0 ){ //没有维护过、新增
//					this.daoSupport.execute("insert  into  es_frame_store(goodsid,productid,depotid,store)values(?,?,?,?)", goods.getGoods_id(),productid1,depotid1,store);
//				}else{
//					this.daoSupport.execute("update es_frame_store set store=store+? where id=?",store,id);
//				} 
//				total+=store;
//			}
//			//this.daoSupport.execute("update es_goods_depot set iscmpl=1 where goodsid=? and depotid=?", goods.getGoods_id(),depotid[0]);
//			//更新商品总库存
//			this.daoSupport.execute("update es_goods set store=(select sum(store) from es_frame_store where goodsid=?)  where goods_id =?",goods.getGoods_id(), goods.getGoods_id());
//			this.daoSupport.execute("update es_product set store=(select sum(store) from es_frame_store where goods_id=?) where goods_id=? ", goods.getGoods_id(),goods.getGoods_id());
//		}
//		else if(goods.getCat_id()==19){
//			//老花镜库存表--老花镜 presbyopic_store
//			for(int i=0;i<degreeid.length;i++){
//				int id =StringUtil.toInt(degreeid[i],true);
//				int depotid1 =StringUtil.toInt(depotid[i],true);
//				int degree_store1 =StringUtil.toInt(degree_store[i],true);
//				int goodsid1 = StringUtil.toInt(goodsid[i],true);
//				double sphere1 =StringUtil.toDouble( sphere[i],true);
//				
//				PresbyopicStore presbyopicStore = new PresbyopicStore();
//				presbyopicStore.setDepotid(depotid1);
//				presbyopicStore.setGoodsid(goodsid1);
//				
//				presbyopicStore.setSphere(sphere1);
//				presbyopicStore.setStore(degree_store1);
//				presbyopicStore.setId(id);
//				this.presbyopicStoreManager.stock(presbyopicStore);
//				total+=degree_store1;
//			}
//		}else{
//			//其他货品库存 product_store 
//			int pid = warnTaskManager.getProductId(goods.getGoods_id());
//			for(int i= 0;i<degreeid.length ;i ++){
//				int storeid  = StringUtil.toInt(degreeid[i],true);
//				int store = StringUtil.toInt(degree_store[i],true);
//				int depotid1= StringUtil.toInt(depotid[i],true);
//				if(storeid == 0) { //新库存
//					this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store)values(?,?,?,?)",goods.getGoods_id(),pid, depotid1,store);
//				}else{ //更新库存
//					this.daoSupport.execute("update es_product_store set store=store+? where storeid=?", store,storeid);
//				}
//				total+=store;
//			}
//			//更新商品总库存
//			this.daoSupport.execute("update es_goods set store=(select sum(store) from es_product_store where goodsid=?) where goods_id=? ", goods.getGoods_id(),goods.getGoods_id());
//			this.daoSupport.execute("update es_product set store=(select sum(store) from es_product_store where productid=?) where product_id=? ", pid,pid);
//		}
//		//库存日志
//		AdminUser adminUser = UserConext.getCurrentAdminUser();		
//		DepotUser depotUser = (DepotUser)adminUser;
//		StoreLog storeLog = new StoreLog();
//		storeLog.setGoodsid(StringUtil.toInt(goodsid[0],true));
//		storeLog.setGoodsname(goods.getName());
//		storeLog.setDepot_type(EopSetting.getDepotType(depotUser.getDepotid()));
//		storeLog.setOp_type(0);
//		storeLog.setDepotid(depotUser.getDepotid());
//		storeLog.setDateline(DateUtil.getDateline());
//		storeLog.setNum(total);
//		storeLog.setUserid(adminUser.getUserid());
//		storeLog.setUsername(adminUser.getUsername());
//		storeLogManager.add(storeLog);
//		this.showSuccessJson("保存成功");
//		return this.JSON_MESSAGE;
//	}
//	
//	public String updateMaintain(){
//		if(taskId==null || taskId == 0 ){
//			//this.showSuccessJson("保存失败");
//			return "{result:0}"; 
//		}
//		warnTaskManager.updateTask(taskId);
//		this.showSuccessJson("保存成功");
//		return this.JSON_MESSAGE;
//	}
//	
//
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getSn() {
//		return sn;
//	}
//	public void setSn(String sn) {
//		this.sn = sn;
//	}
//	public IGoodsManager getGoodsManager() {
//		return goodsManager;
//	}
//	public void setGoodsManager(IGoodsManager goodsManager) {
//		this.goodsManager = goodsManager;
//	}
//	public IWarnTaskManager getWarnTaskManager() {
//		return warnTaskManager;
//	}
//	public void setWarnTaskManager(IWarnTaskManager warnTaskManager) {
//		this.warnTaskManager = warnTaskManager;
//	}
//
//	public Integer getGoodsId() {
//		return goodsId;
//	}
//
//	public void setGoodsId(Integer goodsId) {
//		this.goodsId = goodsId;
//	}
//
//
//	public IDepotManager getDepotManager() {
//		return depotManager;
//	}
//
//	public void setDepotManager(IDepotManager depotManager) {
//		this.depotManager = depotManager;
//	}
//
//	public List<Depot> getListDepot() {
//		return listDepot;
//	}
//
//	public void setListDepot(List<Depot> listDepot) {
//		this.listDepot = listDepot;
//	}
//
//	public List<Double> getListSphere() {
//		return listSphere;
//	}
//
//	public void setListSphere(List<Double> listSphere) {
//		this.listSphere = listSphere;
//	}
//
//	public List<Double> getListCylinder() {
//		return listCylinder;
//	}
//
//	public void setListCylinder(List<Double> listCylinder) {
//		this.listCylinder = listCylinder;
//	}
//
//	public List<Map> getListColor() {
//		return listColor;
//	}
//
//	public void setListColor(List<Map> listColor) {
//		this.listColor = listColor;
//	}
//
//	public Goods getGoods() {
//		return goods;
//	}
//
//	public void setGoods(Goods goods) {
//		this.goods = goods;
//	}
//
//	public String getDepotval() {
//		return depotval;
//	}
//
//	public void setDepotval(String depotval) {
//		this.depotval = depotval;
//	}
//
//	public String getSphereval() {
//		return sphereval;
//	}
//
//	public void setSphereval(String sphereval) {
//		this.sphereval = sphereval;
//	}
//
//	public String getCylinderval() {
//		return cylinderval;
//	}
//
//	public void setCylinderval(String cylinderval) {
//		this.cylinderval = cylinderval;
//	}
//
//	public String getProductval() {
//		return productval;
//	}
//
//	public void setProductval(String productval) {
//		this.productval = productval;
//	}
//
//
//	public Integer getWarn_num() {
//		return warn_num;
//	}
//
//	public void setWarn_num(Integer warn_num) {
//		this.warn_num = warn_num;
//	}
//
//	
//	
//
//	public Integer getDepotId() {
//		return depotId;
//	}
//
//	public void setDepotId(Integer depotId) {
//		this.depotId = depotId;
//	}
//
//	public IAdminUserManager getAdminUserManager() {
//		return adminUserManager;
//	}
//
//	public void setAdminUserManager(IAdminUserManager adminUserManager) {
//		this.adminUserManager = adminUserManager;
//	}
//
//	public Map getTaskInfo() {
//		return taskInfo;
//	}
//
//	public void setTaskInfo(Map taskInfo) {
//		this.taskInfo = taskInfo;
//	}
//
//	public Integer getTaskId() {
//		return taskId;
//	}
//
//	public void setTaskId(Integer taskId) {
//		this.taskId = taskId;
//	}
//
//	public IPresbyopicStoreManager getPresbyopicStoreManager() {
//		return presbyopicStoreManager;
//	}
//
//	public void setPresbyopicStoreManager(
//			IPresbyopicStoreManager presbyopicStoreManager) {
//		this.presbyopicStoreManager = presbyopicStoreManager;
//	}
//
//	public List<Map> getDepotStore() {
//		return DepotStore;
//	}
//
//	public void setDepotStore(List<Map> depotStore) {
//		DepotStore = depotStore;
//	}
//
//	public IConactLensStoreManager getConactLensStoreManager() {
//		return conactLensStoreManager;
//	}
//
//	public void setConactLensStoreManager(
//			IConactLensStoreManager conactLensStoreManager) {
//		this.conactLensStoreManager = conactLensStoreManager;
//	}
//
//	public IGlassesStoreManager getGlassesStoreManager() {
//		return glassesStoreManager;
//	}
//
//	public void setGlassesStoreManager(IGlassesStoreManager glassesStoreManager) {
//		this.glassesStoreManager = glassesStoreManager;
//	}
//
//	public IFrameManager getFrameManager() {
//		return frameManager;
//	}
//
//	public void setFrameManager(IFrameManager frameManager) {
//		this.frameManager = frameManager;
//	}
//
//	public IGoodsStoreManager getGoodsStoreManager() {
//		return goodsStoreManager;
//	}
//
//	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
//		this.goodsStoreManager = goodsStoreManager;
//	}
//
//	public IStoreLogManager getStoreLogManager() {
//		return storeLogManager;
//	}
//
//	public void setStoreLogManager(IStoreLogManager storeLogManager) {
//		this.storeLogManager = storeLogManager;
//	}
//
//	public String[] getGoodsid() {
//		return goodsid;
//	}
//
//	public void setGoodsid(String[] goodsid) {
//		this.goodsid = goodsid;
//	}
//
//	public String[] getDegreeid() {
//		return degreeid;
//	}
//
//	public void setDegreeid(String[] degreeid) {
//		this.degreeid = degreeid;
//	}
//
//	public String[] getDepotid() {
//		return depotid;
//	}
//
//	public void setDepotid(String[] depotid) {
//		this.depotid = depotid;
//	}
//
//	public String[] getDegree_store() {
//		return degree_store;
//	}
//
//	public void setDegree_store(String[] degree_store) {
//		this.degree_store = degree_store;
//	}
//
//	public String[] getSphere() {
//		return sphere;
//	}
//
//	public void setSphere(String[] sphere) {
//		this.sphere = sphere;
//	}
//
//	public String[] getProductid() {
//		return productid;
//	}
//
//	public void setProductid(String[] productid) {
//		this.productid = productid;
//	}
//
//	public IDaoSupport getDaoSupport() {
//		return daoSupport;
//	}
//
//	public void setDaoSupport(IDaoSupport daoSupport) {
//		this.daoSupport = daoSupport;
//	}
//
//	public String[] getSpheres() {
//		return spheres;
//	}
//
//	public void setSpheres(String[] spheres) {
//		this.spheres = spheres;
//	}
//
//	public String[] getCylinders() {
//		return cylinders;
//	}
//
//	public void setCylinders(String[] cylinders) {
//		this.cylinders = cylinders;
//	}
//
//	public IDaoSupport getBaseDaoSupport() {
//		return baseDaoSupport;
//	}
//
//	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
//		this.baseDaoSupport = baseDaoSupport;
//	}
//


}
