package com.enation.app.shop.core.action.backend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDepotMonitorManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * 仓库监控
 * @author xiaokx
 *
 */
public class DepotMonitorAction extends WWAction {

	private IDepotMonitorManager depotMonitorManager;
	private List listTask;
	private int payTotal;//付款未确认总数
	private int pildTotal;//付款已确认总数
	private List<GoodsStores> storeList;
	private String name;
	private String sn;
	private Integer catid;
	private IGoodsManager goodsManager;
	private Goods goods;
	private int goodsid;
	private int totalCount;
	private String startDate;
	private String endDate;
	
	private int depotid;//仓库标识
	private int depotType=-1;//仓库类型
	private int opType=-1;//仓库类型
	
	private IDepotManager depotManager;
	private List list;
	private Map goodsMap;
	
	private ISettingService settingService;
	/**
	 * 仓库维护任务统计
	 * @return
	 */
	public String listTask(){
		listTask = this.depotMonitorManager.getTaskList();
		return "listtask";
	}
	/**
	 *仓库 配货任务统计
	 * @return
	 */
	public String listAllocation(){
		listTask = this.depotMonitorManager.getAllocationList();
		return "listallocation";
	}
	/**
	 * 仓库发货任务统计
	 * @return
	 */
	public String listSend(){
		listTask = this.depotMonitorManager.getSendList();
		return "listsend";
	}
	/**
	 * 仓库订单任务统计
	 * @return
	 */
	public String listOrder(){
		payTotal = this.depotMonitorManager.getTotalByStatus(1);
		pildTotal = this.depotMonitorManager.getTotalByStatus(2);
		return "listorder";
	}
	/**
	 * 库存余量列表
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String listStock(){
		goodsMap = new HashMap();
		if(name!=null ) name = StringUtil.toUTF8(name);
		
		goodsMap.put("catid", catid);
		goodsMap.put("name", name);
		goodsMap.put("sn", sn);
		
		this.webpage = goodsManager.searchGoods(goodsMap,  this.getPage(),this.getPageSize(),null,this.getSort(),this.getOrder());
		return "liststock";
	}
	/**
	 * 按商品查看各仓库库存情况
	 * @return
	 */
	public String depotStock(){
		goods = goodsManager.getGoods(goodsid);
		listTask = this.depotMonitorManager.depotidDepotByGoodsid(goodsid, goods.getCat_id());
		return "depotstock";
	}
	/**
	 * 库存余量提醒
	 * @return
	 */
	public String storeWarn(){
		String value = settingService.getSetting("shop", "goods_alerm_num");
		int num=10;
		if(!StringUtil.isEmpty(value)){
			num = StringUtil.toInt(value,true);
		}
		this.storeList = goodsManager.storeWarnGoods(num, this.getPage(),this.getPageSize());
		this.totalCount = storeList.size();
		return "storewarn";
	}
	/**
	 * 销售额统计
	 * @return
	 */
	public String listSell(){
		long start=0,end=0;
		if(startDate!=null&&!"".equals(startDate)&&endDate!=null&&!"".equals(endDate)){
			start = DateUtil.getDateline(startDate);
			end   = DateUtil.getDateline(endDate)+24*60*60;
		}
		
		listTask = this.depotMonitorManager.searchOrderSalesAmout(start, end);		
		return "listsell";
	}
	/**
	 * 销售量统计
	 * @return
	 */
	public String listSellNum(){
		if(catid==null)
			catid=0;
		long start=0,end=0;
		if(startDate!=null&&!"".equals(startDate)&&endDate!=null&&!"".equals(endDate)){
			start = DateUtil.getDateline(startDate);
			end   = DateUtil.getDateline(endDate)+24*60*60;
		}
		
		listTask = this.depotMonitorManager.searchOrderSaleNumber(start, end,catid);
		return "listsellnum";
	}
	/**
	 * 库存日志
	 * @return
	 */
	public String listStockLog(){
		list = depotManager.list();
		long start=0,end=0;
		if(startDate!=null&&!"".equals(startDate)&&endDate!=null&&!"".equals(endDate)){
			start = DateUtil.getDateline(startDate);
			end   = DateUtil.getDateline(endDate)+24*60*60;
		}
		this.webpage = this.depotMonitorManager.searchStoreLog(start, end, depotid, depotType, opType, this.getPage(),this.getPageSize());
		return "liststocklog"; 
	}
	
	public IDepotMonitorManager getDepotMonitorManager() {
		return depotMonitorManager;
	}
	public void setDepotMonitorManager(IDepotMonitorManager depotMonitorManager) {
		this.depotMonitorManager = depotMonitorManager;
	}

	public List getListTask() {
		return listTask;
	}

	public void setListTask(List listTask) {
		this.listTask = listTask;
	}
	public int getPayTotal() {
		return payTotal;
	}
	public void setPayTotal(int payTotal) {
		this.payTotal = payTotal;
	}
	public int getPildTotal() {
		return pildTotal;
	}
	public void setPildTotal(int pildTotal) {
		this.pildTotal = pildTotal;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getCatid() {
		return catid;
	}
	public void setCatid(Integer catid) {
		this.catid = catid;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public int getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}
	
	public static void main(String[] args){
//		Date d1 = DateUtil.toDate("2015-04-14", "yyyy-MM-dd");
		System.out.println(DateUtil.getDateline("2015-04-14"));
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getDepotid() {
		return depotid;
	}
	public void setDepotid(int depotid) {
		this.depotid = depotid;
	}
	public int getDepotType() {
		return depotType;
	}
	public void setDepotType(int depotType) {
		this.depotType = depotType;
	}
	public int getOpType() {
		return opType;
	}
	public void setOpType(int opType) {
		this.opType = opType;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public ISettingService getSettingService() {
		return settingService;
	}
	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}
	public List<GoodsStores> getStoreList() {
		return storeList;
	}
	public void setStoreList(List<GoodsStores> storeList) {
		this.storeList = storeList;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public Map getGoodsMap() {
		return goodsMap;
	}
	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}

	
}
