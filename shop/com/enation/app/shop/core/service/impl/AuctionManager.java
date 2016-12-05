package com.enation.app.shop.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.store.impl.StoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 拍卖业务流程
 * @author linyang
 * 2015-11
 */
@SuppressWarnings("rawtypes")
public class AuctionManager extends BaseSupport<Auction> {
    
    private MemberManager memberManager;
    private MemberAddressManager memberAddressManager;
    private IDlyTypeManager dlyTypeManager;
    private IPaymentManager paymentManager;
    private ProductManager productManager;
    private OrderPluginBundle orderPluginBundle;
    private StoreManager storeManager;
	public void add(Auction auction) {
		this.baseDaoSupport.insert("es_auction", auction);
	}
	
	public void edit(Auction auction) {
		this.baseDaoSupport.update("es_auction", auction, "id=" + auction.getId());
	}
	
	public void delete(Integer[] id) {
        if (id == null || id.equals(""))
            return;
        String id_str = StringUtil.arrayToString(id, ",");
        String sql = "delete from es_auction where id in (" + id_str + ")";
        this.baseDaoSupport.execute(sql);
	}
	
	
    public Auction get(Integer id) {
	        if(id!=null&&id!=0){
	            String sql = "select * from es_auction where id=?";
	            Auction auction = this.baseDaoSupport.queryForObject(sql, Auction.class,id);
	            return auction;
	        }else{
	            return null;
	        }
	 }
    
    public int getRecordCount(Integer id) {
        if(id!=null&&id!=0){
            String sql = "select count(*) from es_auction_record where auction_id=?";
            int count = this.baseDaoSupport.queryForInt(sql, id);
            return count;
        }else{
            return 0;
        }
 }
	
    public Page list(String order, int page, int pageSize) {
        order = order == null ? " id desc" : order;
        String sql = "select * from es_auction";
        sql += " order by  " + order;
        Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
        return webpage;
    }
    
	public List list() {
        String sql = "select * from es_auction order by id";
        List<Auction> auctionlist = this.baseDaoSupport.queryForList(sql, Auction.class);
        return auctionlist;
    }
    
    public List<Auction> listOnprocess() {
        String sql = "select * from es_auction where status='1' order by id";
        List<Auction> auctionlist = this.baseDaoSupport.queryForList(sql, Auction.class);
        return auctionlist;
    }
    
    public boolean updateStatus()
    {   
        //createOrder(87,428);
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cutime=format.format(date); 
        String sql = "select * from es_auction  order by id";
        List<Auction> auctionlist = this.baseDaoSupport.queryForList(sql, Auction.class);
        for(Auction cur:auctionlist)
        {
            String overtime = cur.getTime();
            String starttime = cur.getStart_time();
            String status = cur.getStatus();
            SimpleDateFormat dtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long stime = 0;
            long otime = 0;
            long ctime = 0;
            try {
                java.util.Date datestart = dtime.parse(starttime);
                java.util.Date dateover = dtime.parse(overtime);
                java.util.Date datecur = dtime.parse(cutime);
                stime = datestart.getTime();
                otime = dateover.getTime();
                ctime = datecur.getTime();
            } catch(ParseException e) {
                e.printStackTrace();
            } 
            //如果当前时间大于截止时间

            if(ctime>otime)
            {
                boolean bo2 = !status.equals("2");
                boolean bo3 = !status.equals("3");
                if(bo2&&bo3)
                {
                    //判断是否有人拍过
                    String sqlrecord = "select  *  from  ES_AUCTION_RECORD where auction_id=?";
                    List<Map> record = this.baseDaoSupport.queryForList(sqlrecord, cur.getId());
                    String sqlup = "";
                    if(record.size() == 0)
                    {
                        //设置流拍状态
                        sqlup = "update es_auction set status='3' where id="+cur.getId();
                    }else
                    {
                        //设置状态为已完成
                        sqlup = "update es_auction set status='2' where id="+cur.getId();  
                    }
                    
                    //出价记录表状态更新,设置当前出价最高为成交状态
                    String sqlup1 = "update es_auction_record set status=2  where status=1 and auction_id=?";
                    this.baseDaoSupport.execute(sqlup);
                    this.baseDaoSupport.execute(sqlup1,cur.getId());
                    
                    //插入订单
                    String sqlcur = "select * from es_auction_record r,es_member m,es_auction a  where r.userid = m.member_id and a.id=r.auction_id  and r.status=2 and r.auction_id=?";
                    List<Map> aurecordList = this.baseDaoSupport.queryForList(sqlcur,cur.getId());
                    
                    long memid = 0;
                    int productid = 0;
                    float goodsAmount = 0;
                    if(aurecordList.size()>0)
                    {
                        memid = (long) aurecordList.get(0).get("member_id");
                        productid = NumberUtils.toInt((String) aurecordList.get(0).get("goodsn"));
                        goodsAmount = NumberUtils.toFloat((String) aurecordList.get(0).get("price"));
                        System.out.println("拍卖创建订单");
                        createOrder(memid,productid,goodsAmount);
                        System.out.println("拍卖创建订单结束");
                    }
                }

                //List<CartItem> cartItemList = this.cartManager.listGoods(sessionid);
                //Order mainOrder = this.orderManager.add(order, new ArrayList<CartItem>(), sessionid);
                
                //order.setAddress_id(13);
                //order.setMember_id(36);
                //order.setSn("1511246153");
                //System.out.println(a);
               
            }else if((ctime<otime)&&(stime<ctime))
            {
                String sqlup = "update es_auction set status='1' where id="+cur.getId();
                this.baseDaoSupport.execute(sqlup);
            }else if(stime>ctime)
            {
                String sqlup = "update es_auction set status='0' where id="+cur.getId();
                this.baseDaoSupport.execute(sqlup); 
            }
        }
        //System.out.println("更新拍卖状态");
        return true;
    }
	
	public Page get(int pageNo, int pageSize) {
		return this.baseDaoSupport.queryForPage("select * from es_auction order by id", pageNo, pageSize);
	}
	
	public boolean createOrder(long memid,int productid,float goodsAmount)
	{
	    System.out.println("进入创建订单");
	    Member data_member = memberManager.get(memid);
	    
	    StoreOrder order = new StoreOrder();
	    Order orderParent = new Order();
        MemberAddress maddress = memberAddressManager.getMemberDefault(memid);
        order.setMember_id(memid);
        order.setMemberAddress(maddress);
        orderParent.setMember_id(memid);
        orderParent.setMemberAddress(maddress);
        //快递
        order.setShipping_id(61);
        orderParent.setShipping_id(61);
        //运费
        order.setShipping_amount(0.0);
        orderParent.setShipping_amount(0.0);
        //支付宝
        order.setPayment_id(6);
        orderParent.setPayment_id(6);
        
        order.setPayment_id(6);
        orderParent.setPayment_id(6);
        
        order.setShip_addr(data_member.getAddress());
        order.setShip_email(data_member.getEmail());
        order.setShip_mobile(data_member.getMobile());
        order.setShip_regionid(data_member.getRegion_id());
        
        
        //判断用户数据是否被手动删除
//        if(data_member==null){
//            throw new RuntimeException("创建订单失败，当前用户不存在，请联系平台管理员！");
//        }
        
        // 配送方式名称
 /*       try{
            DlyType dlyType = new DlyType();
            if (dlyType != null && order.getShipping_id()!=0){
                dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
            }else{
                dlyType.setName("");
            }
            order.setShipping_type(dlyType.getName());
        }catch (Exception e) {
            e.printStackTrace();
        } */  
        
        /************ 支付方式价格及名称 ************************/
        PayCfg payCfg = this.paymentManager.get(order.getPayment_id());
        //此方法实现体为空注释掉
        //order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
        order.setPayment_name(payCfg.getName());
        orderParent.setPayment_name(payCfg.getName());
        
        order.setPayment_type(payCfg.getType());
        orderParent.setPayment_type(payCfg.getType());

        /************ 创建订单 ************************/
        order.setCreate_time(com.enation.framework.util.DateUtil.getDateline());
        orderParent.setCreate_time(com.enation.framework.util.DateUtil.getDateline());
        
        order.setSn(this.createSn());
        orderParent.setSn(this.createSn());
        order.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
        orderParent.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
        order.setDisabled(0);
        orderParent.setDisabled(0);
        order.setPay_status(OrderStatus.PAY_NO);
        orderParent.setPay_status(OrderStatus.PAY_NO);
        order.setShip_status(OrderStatus.SHIP_NO);
        orderParent.setShip_status(OrderStatus.SHIP_NO);
        order.setOrderStatus("订单已生效");
        orderParent.setOrderStatus("订单已生效");
        
        //给订单添加仓库 ------仓库为默认仓库
        Integer depotId= this.baseDaoSupport.queryForInt("select id from depot where choose=1");
        order.setDepotid(depotId);
        
        /**检测商品库存  Start**/
        boolean result = true;  //用于判断购买量是否超出库存
        Product product = productManager.getByGoodId(productid);
        
        //Store store = storeManager.getStore(product.getStore());
        //order.setStore_name(store.getStore_name());
        
        int enableStore = product.getEnable_store();
        if(enableStore < 1)
            result = false;
        if(!result){
            throw new RuntimeException("创建订单失败，您购买的商品库存不足");
        }
        
        /***************** 2015/10/15 humaodong ***********************/
        Double money = order.getAdvance_pay();
        if (money != null && money > 0) {
            AdvanceLogs log = memberManager.pay(memid, money, 0, order.getSn(), "订单");
            order.setAdvance_pay(log.getExport_advance());
            order.setVirtual_pay(log.getExport_virtual());
        }
        /******************** end of update ***************************/
        
        //需要付的款
        orderParent.setNeed_pay_money(product.getPrice());
        order.setNeed_pay_money(product.getPrice());
        
        
        this.baseDaoSupport.insert("order", orderParent);
        Integer orderPId = this.baseDaoSupport.getLastId("order");
        
        order.setParent_id(orderPId);
        Integer store_id = product.getStore();
        order.setStore_id(store_id);
        this.baseDaoSupport.insert("order", order);
        Integer orderId = this.baseDaoSupport.getLastId("order");
        
        order.setOrder_id(orderId);
        saveGoodsItem(product,order);
        
       
       
        
        /************ 写入订单日志 ************************/
        OrderLog log = new OrderLog();
        log.setMessage("订单创建");
        log.setOp_name(data_member.getName());
        log.setOrder_id(orderId);
        this.addLog(log);
        order.setOrder_id(orderId);
        System.out.println("完成创建订单");
	    return true;
	}
	
	private void saveGoodsItem(Product product, Order order) {
	        
	        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
	        Integer order_id = order.getOrder_id();

            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(product.getPrice());
            orderItem.setName(product.getName());
            //orderItem.setNum(product);

            orderItem.setGoods_id(product.getGoods_id());
            orderItem.setShip_num(0);
            orderItem.setProduct_id(product.getProduct_id());
            orderItem.setOrder_id(order_id);
            
            //需要设值
            orderItem.setGainedpoint(0);
            orderItem.setCat_id(0);
            orderItem.setNum(1);
            orderItem.setImage("http://www.gomecellar.com/statics/attachment//store/20/goods/201510212132289257_thumbnail.jpg");
            //orderItem.setAddon();
            
            //3.0新增的三个字段
            orderItem.setSn(product.getSn());
            //orderItem.setImage();
            //orderItem.setCat_id();
            
            //orderItem.setUnit();
            
            this.baseDaoSupport.insert("order_items", orderItem);
            int itemid = this.baseDaoSupport.getLastId("order_items");
            orderItem.setItem_id(itemid);
            orderItemList.add(orderItem);
            this.orderPluginBundle.onItemSave(order,orderItem);

	        
	        String itemsJson  = JSONArray.fromObject(orderItemList).toString();
	        this.daoSupport.execute("update es_order set items_json=? where order_id=?", itemsJson,order_id);    
	    }
	
	   /**
     * 创建订单号（日期+两位随机数）
     */
    public String createSn() {
        boolean isHave = true;  //数据库中是否存在该订单
        String sn = "";         //订单号
        
        //如果存在当前订单
        while(isHave) {
            StringBuffer  snSb = new StringBuffer(DateUtil.getDateline()+"") ;
            snSb.append(getRandom());
            String sql = "SELECT count(order_id) FROM es_order WHERE sn = '" + snSb.toString() + "'";
            int count = this.baseDaoSupport.queryForInt(sql);
            if(count == 0) {
                sn = snSb.toString();
                isHave = false;
            }
        }
        

        return sn;
    }
    
    /**
     * 获取随机数
     * @return
     */
    public  int getRandom(){
        Random random=new Random();
        int num=Math.abs(random.nextInt())%100;
        if(num<10){
            num=getRandom();
        }
        return num;
    }

    /**
     * 添加订单日志
     * 
     * @param log
     */
    private void addLog(OrderLog log) {
        log.setOp_time(com.enation.framework.util.DateUtil.getDateline());
        this.baseDaoSupport.insert("order_log", log);
    }
    
    public MemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    
    public void setMemberAddressManager(MemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    
    public IDlyTypeManager getDlyTypeManager() {
        return dlyTypeManager;
    }

    
    public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
        this.dlyTypeManager = dlyTypeManager;
    }

    
    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    
    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    
    public ProductManager getProductManager() {
        return productManager;
    }

    
    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    
    public OrderPluginBundle getOrderPluginBundle() {
        return orderPluginBundle;
    }

    
    public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
        this.orderPluginBundle = orderPluginBundle;
    }

    
    public StoreManager getStoreManager() {
        return storeManager;
    }

    
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    
	
}
