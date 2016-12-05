package com.enation.app.b2b2c.core.service.bill.impl;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.bill.Bill;
import com.enation.app.b2b2c.core.model.bill.BillAccount;
import com.enation.app.b2b2c.core.model.bill.BillDetail;
import com.enation.app.b2b2c.core.model.bill.BillStatusEnum;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.shop.core.model.Order;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class BillManager extends BaseSupport implements IBillManager {
    private IDaoSupport daoSupport;
    private IStoreManager storeManager;

	@Override
    public Page bill_list(Integer pageNo, Integer pageSize, String keyword) {
        //update by lxl
        String sql = "SELECT * FROM es_bill WHERE 1=1 ";
        if (keyword != null && !StringUtil.isEmpty(keyword)) {
            sql += " and name like '%" + keyword + "%'";
        }
        sql += " order by bill_id desc";
        Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
        List<Map> list = (List<Map>) webpage.getResult();
        DecimalFormat df = new DecimalFormat("#.00");
        for (Map bill : list) {
            List<BillDetail> billDetails = this.daoSupport.queryForList("select * from es_bill_detail where bill_id = ?", BillDetail.class, bill.get("bill_id"));
            for (BillDetail bd : billDetails) {
                String sn = bd.getSn();
                
                if (bill.get("bill_price") != null) {
                    //订单金额 实际付款金额
                    bill.put("bill_price", df.format(NumberUtils.toDouble(bill.get("bill_price").toString()) + getPaymoney(sn)));
                } else {
                    bill.put("bill_price", df.format(getPaymoney(sn)));
                }

                if (bill.get("commi_price") != null) {
                    //佣金金额
                    //去掉.00的情况 chenzhongwei add
                    String commi_price = df.format(NumberUtils.toDouble(bill.get("commi_price").toString()));
                    bill.put("commi_price", commi_price.equals(".00") ? "0" : commi_price);
                } else {
                    String commi_price = df.format(getCommiPrice(sn));
                    bill.put("commi_price", commi_price.equals(".00") ? "0" : df.format(commi_price));
                }

                if (bill.get("returned_price") != null) {
                    //退单金额
                    bill.put("returned_price", 0.0);//NumberUtils.toDouble(bill.get("returned_price").toString()) + getReturnedPrice(sn)
                } else {
                    bill.put("returned_price", 0.0);//getReturnedPrice(sn)
                }

                if (bill.get("returned_commi_price") != null) {
                    //退单佣金
                    bill.put("returned_commi_price", 0.0);//NumberUtils.toDouble(bill.get("returned_commi_price").toString()) + getReturnedCommiPrice(sn)
                } else {
                    bill.put("returned_commi_price", 0.0);//getReturnedCommiPrice(sn)
                }

                if (bill.get("red_packets_price") != null) {
                    //正向红包
                    bill.put("red_packets_price", df.format(NumberUtils.toDouble(bill.get("red_packets_price").toString()) + getRedPacketsPrice(sn)));
                } else {
                    bill.put("red_packets_price", df.format(getRedPacketsPrice(sn)));
                }
                if (bill.get("returned_red_packets_price") != null) {
                    //退还红包
                    bill.put("returned_red_packets_price", 0.0);//NumberUtils.toDouble(bill.get("returned_red_packets_price").toString()) + getReturnedRedPacketsPrice(sn)
                } else {
                    bill.put("returned_red_packets_price", 0.0);//getReturnedRedPacketsPrice(sn)
                }
                
                if (bill.get("order_price") != null) {
                    bill.put("order_price", df.format(NumberUtils.toDouble(bill.get("bill_price").toString())));
                }else{
                    bill.put("order_price", df.format(NumberUtils.toDouble(bill.get("order_price") + "") + NumberUtils.toDouble(bill.get("bill_price").toString())));
                }
            }
            //去掉.00的情况 chenzhongwei add
            String price = df.format(NumberUtils.toDouble(bill.get("bill_price").toString()) - NumberUtils.toDouble(bill.get("commi_price").toString()) - NumberUtils.toDouble(bill.get("returned_price").toString()) - NumberUtils.toDouble(bill.get("returned_commi_price").toString()) + NumberUtils.toDouble(bill.get("red_packets_price").toString()) - NumberUtils.toDouble(bill.get("returned_red_packets_price").toString()));
            bill.put("price", price.equals(".00") ? "0" : price);
        }
        return webpage;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.bill.IBillManager#(java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public Page bill_detail_list(Integer pageNo, Integer pageSize, Integer bill_id, String keyword) {
        String sql = "SELECT * FROM es_bill_detail WHERE bill_id = ?";
        if (keyword != null || !StringUtil.isEmpty(keyword)) {
            sql += " and store_name like '%" + keyword + "%'";
        }
        sql += " order by bill_id desc";
        DecimalFormat df = new DecimalFormat("#.00");  
        Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize, bill_id);
        List<Map> list = (List<Map>) webpage.getResult();
        for (Map bill : list) {
            String sn = bill.get("sn").toString();
            //订单金额 实际付款金额
            //当等于.00的时候赋值为0 chenzhongwei add
            String bill_price = df.format(getPaymoney(sn));
            bill.put("bill_price", ".00".equals(bill_price) ? "0" : NumberUtils.toDouble(bill_price));
            //佣金金额
            bill.put("commi_price", getCommiPrice(sn));
            //退单金额
            bill.put("returned_price", 0.0);//getReturnedPrice(sn)
            //退还佣金
            bill.put("returned_commi_price", 0.0);//getReturnedCommiPrice(sn)
            //正向红包
            bill.put("red_packets_price", getRedPacketsPrice(sn));
            //退还红包
            bill.put("returned_red_packets_price", 0.0);//getReturnedRedPacketsPrice(sn)
            
            //当等于.00的时候赋值为0 chenzhongwei add
            String price = df.format(NumberUtils.toDouble(bill.get("bill_price").toString())
                    - NumberUtils.toDouble(bill.get("commi_price").toString())
                    - NumberUtils.toDouble(bill.get("returned_price").toString())
                    - NumberUtils.toDouble(bill.get("returned_commi_price").toString())
                    + NumberUtils.toDouble(bill.get("red_packets_price").toString())
                    - NumberUtils.toDouble(bill.get("returned_red_packets_price").toString()));
            bill.put("price", ".00".equals(price) ? "0" : NumberUtils.toDouble(price));
        }
        //TODO MONSOON 自营店不允许结算操作，但是要显示
        return webpage;
    }

	@Override
    public Page store_bill_detail_list(Integer pageNo, Integer pageSize, Integer store_id) {
        Page webpage = this.daoSupport.queryForPage("SELECT * FROM es_bill_detail WHERE store_id=? order by bill_time desc", pageNo, pageSize, store_id);
        List<Map> list = (List<Map>) webpage.getResult();
        DecimalFormat df = new DecimalFormat("0.00");  
        for (Map bill : list) {
            String sn = bill.get("sn").toString();
            //订单金额 实际付款金额
            bill.put("bill_price", df.format(getPaymoney(sn)));
            //佣金金额
            bill.put("commi_price", getCommiPrice(sn));
            //退单金额
            bill.put("returned_price", 0.0);//getReturnedPrice(sn)
            //退还佣金
            bill.put("returned_commi_price", 0.0);//getReturnedCommiPrice(sn)
            //正向红包
            bill.put("red_packets_price", getRedPacketsPrice(sn));
            //退还红包
            bill.put("returned_red_packets_price", 0.0);//getReturnedRedPacketsPrice(sn)

            bill.put("price", df.format(NumberUtils.toDouble(bill.get("bill_price").toString()) - NumberUtils.toDouble(bill.get("commi_price").toString()) - NumberUtils.toDouble(bill.get("returned_price").toString()) - NumberUtils.toDouble(bill.get("returned_commi_price").toString()) + NumberUtils.toDouble(bill.get("red_packets_price").toString()) - NumberUtils.toDouble(bill.get("returned_red_packets_price").toString())));
        }
        //TODO MONSOON 自营店不允许结算操作，但是要显示
        return webpage;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.bill.IBillManager#add_bill(com.enation.app.b2b2c.core.model.bill.Bill)
     */
	@Override
    public void add_bill(Integer order_id, double price) {
        this.logger.info("确认结算：add_bill");
        //如果是父id
        if (this.daoSupport.queryForInt("select count(0) from es_order where parent_id = ?", order_id) > 0) {
            this.logger.info("确认结算：father");
            List<StoreOrder> orders = this.daoSupport.queryForList("select * from es_order where parent_id = ?", StoreOrder.class, order_id);
            for (StoreOrder order : orders) {
                //获取店铺id
                int storeid = this.daoSupport.queryForInt("select store_id from es_order where order_id = ?", order.getOrder_id());
                Bill bill = (Bill) this.daoSupport.queryForObject("select * from es_bill where start_time = ?", Bill.class, DateUtil.getMonthFirstDay());
                //如果结算单为空
                if (bill == null) {
                    this.editBill(order.getStore_id(), order.getNeed_pay_money(),order.getCommission());
                } else {
                    this.editPortionBill(storeid, order.getNeed_pay_money(),order.getCommission());
                }
            }
        } else {
            this.logger.info("确认结算：child");
            StoreOrder order = (StoreOrder) this.daoSupport.queryForObject("select * from es_order where order_id = ?", StoreOrder.class, order_id);
            //获取店铺id
            int storeid = this.daoSupport.queryForInt("select store_id from es_order where order_id = ?", order_id);
            Bill bill = (Bill) this.daoSupport.queryForObject("select * from es_bill where start_time = ?", Bill.class, DateUtil.getMonthFirstDay());
            //如果结算单为空
            if (bill == null) {
                this.editBill(storeid, price, com.enation.app.shop.mobile.util.NumberUtils.toDouble(order.getCommission()));
            } else {
                this.editPortionBill(storeid, price, com.enation.app.shop.mobile.util.NumberUtils.toDouble(order.getCommission()));
            }
        }
    }

    /**
     * 创建结算单
     * 循环店铺表算出每家店铺的结算信息，创建出结算的详细单
     * 然后进行相加算出此期的结算金额信息.
     */
    public void editBill(Integer store_id, double price,double commission) {
        //如果本期尚未生成结算单，则生成结算单
        Bill bill = new Bill();
        bill.setCommi_price(0.0);
        bill.setOrder_price(0.0);
        bill.setPrice(0.0);
        bill.setReturned_commi_price(0.0);
        bill.setReturned_price(0.0);
        Long start_time = DateUtil.getMonthFirstDay();
        Long end_time = DateUtil.getMonthLastDay();
        bill.setName(DateUtil.getDateline() + "");
        bill.setStart_time(start_time);
        bill.setEnd_time(end_time);
        this.daoSupport.insert("es_bill", bill);
        bill.setBill_id(this.daoSupport.getLastId("es_bill"));
        this.editPortionBill(store_id, price,commission);
    }

    /**
     * 结算单
     * 循环店铺表算出每家店铺的结算信息，创建出结算的详细单
     * 然后进行相加算出此期的结算金额信息.
     */
    public void editPortionBill(Integer store_id, double price,double commission) {
        //获取最新一期的结算单
        Bill bill = (Bill) this.daoSupport.queryForObject("select * from  es_bill where start_time = ?", Bill.class, DateUtil.getMonthFirstDay());

        //结算详细单价格信息
        //创建结算详细
        Store store = storeManager.getStore(store_id);
        BillDetail billDetail = (BillDetail) daoSupport.queryForObject(
                "select * from es_bill_detail where store_id = ? and bill_id = ?",
                BillDetail.class, store_id, bill.getBill_id());
        //如果 订单详情为空
        if (billDetail == null) {
            billDetail = new BillDetail();
            billDetail.setStore_name(store.getStore_name());
            billDetail.setBill_id(bill.getBill_id());
            // 2016-04-01 monsoon改变 结算单编码格式为 如:201601-6041-42
            SimpleDateFormat time = new SimpleDateFormat("yyyyMM");
            billDetail.setSn(time.format(bill.getStart_time()*1000) + "-" + bill.getBill_id() + "-" + store_id);
            billDetail.setStore_id(store.getStore_id());
            billDetail.setStatus(0);
            billDetail.setStart_time(bill.getStart_time());
            billDetail.setEnd_time(bill.getEnd_time());
            billDetail.setBill_price(0.0);
            billDetail.setPrice(0.0);
            billDetail.setReturned_commi_price(0.0);
            billDetail.setReturned_price(0.0);
            billDetail.setCommi_price(0.0);
        }

        billDetail.setBill_time(DateUtil.getDateline());
        //创建结算详细单
        getBillDetail(billDetail, price,commission);
        //如果金额大于0 则是收款
        if (price > 0) {
            //收款，修改佣金，修改总价，修改订单价格
            String sql = "update es_bill set price = price+?+? , order_price = order_price+? ,commi_price = commi_price+? where bill_id = ?";
            this.daoSupport.execute(sql,price,-commission,price,commission,bill.getBill_id());
        } else {
            //退款，修改 总金额 退还金额，退还佣金
            String sql = "update es_bill set price = price+?+? ,  " +
                    "returned_price=returned_price+? ,returned_commi_price = returned_commi_price+? where bill_id = ?";
            this.daoSupport.execute(sql,price,-commission,price,commission,bill.getBill_id());
        }
    }


    /**
     * 修改订单信息
     *
     * @param bill_sn  结算编号
     * @param store_id 店铺Id
     * @param end_time 结束时间
     */
    private void update_order_info(String bill_sn, Integer store_id, Long end_time) {
        this.daoSupport.execute("update es_order set bill_status=1,bill_sn=? where store_id=? and create_time<? and bill_status=0", bill_sn, store_id, end_time);
        this.daoSupport.execute("update es_sellback_list set bill_status=1, bill_sn=? where store_id=? and regtime<? and bill_status=0", bill_sn, store_id, end_time);
    }

    /**
     * 添加结算详细单
     *
     * @param billDetail
     */
    public void getBillDetail(BillDetail billDetail, double payprice,double commission) {
        //如果没有这个结算单详情 那么添加
        if (billDetail.getId() == null) {
            this.add_bill_detail(billDetail);
            billDetail.setId(daoSupport.getLastId("es_bill_detail"));
        }
        Store store = storeManager.getStore(billDetail.getStore_id());
        //纪录订单的结算信息
        this.update_order_info(billDetail.getSn(), store.getStore_id(), billDetail.getEnd_time());

        /**这里要注意
         * 查询结果只包含本月操作，例如：上月的订单，这个月签收的，
         * 那么结算订单的结果将设置在这个月的出账单里，
         * 如果这个月的商品，下个月退货了，同样也只出现在下月的退款单里
         */
//		如果钱大于0 代表是 付款，小于 等于0 代表是退款
        if (payprice > 0) {
            //收款，修改佣金，修改总价，修改订单价格
            String sql = "update es_bill_detail set price = price+? , bill_price = bill_price+?+? ,commi_price = commi_price+? where id = ?";
            this.daoSupport.execute(sql, payprice,commission,payprice,commission,billDetail.getId());
        } else {
            //退款，修改 总金额 店铺金额，退还金额，退还佣金
            String sql = "update es_bill_detail set  bill_price = bill_price+?+? , " +
                    "returned_price=returned_price+? ,returned_commi_price = returned_commi_price+? where id = ?";
            this.daoSupport.execute(sql,commission,payprice,-payprice,commission,billDetail.getId());
        }
    }

    @Override
    public void add_bill_detail(BillDetail billDetail) {
        this.daoSupport.insert("es_bill_detail", billDetail);
    }

    @Override
    public BillDetail get_bill_detail(Integer id) {
        return (BillDetail) this.daoSupport.queryForObject("select * from es_bill_detail where id=?", BillDetail.class, id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void edit_billdetail_status(Integer id, Integer status) {
        String sql = "update es_bill_detail set status=? where id=?";
        this.daoSupport.execute(sql, status, id);
        if (status == BillStatusEnum.PAY.getIndex()) {
            this.bill(id);
        }
    }

    /**
     * 结算订单
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void bill(Integer id) {
        BillDetail billDetail = this.get_bill_detail(id);
    }

    /**
     * 查询结算的订单列表
     */
    @Override
    public Page bill_order_list(Integer pageNo, Integer pageSize, String sn) {
        //显示妥投月份的数据 chenzhongwei add
        if(StringUtils.isNotEmpty(sn)) {
            StringBuffer sql = new StringBuffer();
            String[] snArray = sn.split("-");
            String month = snArray[0];
            String[] params = { month, sn };
            sql.append("select order1.* from")
                    .append(" (select t.*, (select to_date('1970/01/01', 'yyyy/mm/dd') + t.signing_time / (60 * 60 * 24) from dual) signing_date from es_order t) order1")
                    .append(" left join (select order_id from es_order) order2 on order1.order_id = order2.order_id")
                    .append(" where order1.status = 7").append(" and to_char(order1.signing_date, 'yyyymm') =?")
                    .append(" and order1.bill_sn = ?").append("  order by order1.create_time desc");
           
           //添加主订单号 chenzhongwei add (参照Page com.enation.app.b2b2c.core.service.order.impl.StoreOrderManager.listOrder(Map map, int page, int pageSize, String other, String order)) 
           Page webPage =this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, params);
          //统一获取支付方式,之前的sql太复杂了,一次性获取有难度,改为分两次, 一次性找出所有的父订单,统计支付方式放入map,然后倒腾回子订单map
            if (webPage.getResult() != null) {
                List list = (List)webPage.getResult();
                if (!list.isEmpty()) {
                    Set<Integer> pidSet = new HashSet<>();
                    for (Object o : list) {
                        Map m = (Map) o;
                        Integer v = (Integer)m.get("parent_id");
                        if (v != null) {
                            pidSet.add(v);
                        }
                    }
//                    Integer[] array = new Integer[pidSet.size()];
//                    int i = 0;
//                    for (Integer pid : pidSet) {
//                        array[i++] = pid;
//                    }
//                    String ids = StringUtil.arrayToString(array, ",");
                    String ids = StringUtil.arrayToString(pidSet.toArray(), ",");
                    //String sql1 = "select order_id,need_pay_money,payment_name,advance_pay,bonus_pay where order_id in (" + ids + ")";
                    String sql1 = "select * from es_order where order_id in (" + ids + ")";
                    List<Order> parentOrders = baseDaoSupport.queryForList(sql1, Order.class);
                    Map<Integer, String> paymentMethodMap = new HashedMap();
                    Map<Integer, String> parentSnMap = new HashMap<Integer, String>();
                    for (Order po : parentOrders) {
                        paymentMethodMap.put(po.getOrder_id(), po.getPaymentMethod());
                        parentSnMap.put(po.getOrder_id(), po.getSn());
                    }
                    for (Object o : list) {
                        Map m = (Map) o;
                        Integer v = (Integer)m.get("parent_id");
                        if (v != null) {
                            //m.put("paymentMethod", paymentMethodMap.get(v));
                            //直接放入这里,不用修改页面了
                            m.put("payment_name", paymentMethodMap.get(v));
                            m.put("parent_sn", parentSnMap.get(v));
                        }
                    }

                }
            }
            return webPage;
        }else{
            return null;
        }
    }

    /**
     * 获取结算的退货单列表
     *
     * @param pageNo
     * @param pageSize
     * @param sn
     * @return
     */
    @Override
    public Page bill_sell_back_list(Integer pageNo, Integer pageSize, String sn) {
        //瞎写的，不读取退货单 目前
        String sql = "select * from es_sellback_list where tradestatus = 300000 and  bill_sn=? order by id desc";
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, sn);
    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public IStoreManager getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }


    /**
     * 根据结算单号查出 正向红包
     *
     * @param sn
     * @return
     */
    @Override
    public Double getPaymoney(String sn) {
        Double result=null;
      //显示妥投月份的数据 chenzhongwei add
        if(StringUtils.isNotEmpty(sn)) {
            StringBuffer sql = new StringBuffer();
            String[] snArray = sn.split("-");
            String month = snArray[0];
            String[] params = { month, sn };
           /* sql.append("select sum(order1.need_pay_money) + sum(order1.advance_pay) paymoney from")
                    .append(" (select t.*, (select to_date('1970/01/01', 'yyyy/mm/dd') + t.signing_time / (60 * 60 * 24) from dual) signing_date from es_order t) order1")
                    .append(" left join (select order_id from es_order) order2 on order1.order_id = order2.order_id")
                    .append(" where order1.status = 7").append(" and to_char(order1.signing_date, 'yyyymm') =?")
                    .append(" and order1.bill_sn = ?");*/
            
            sql.append("select sum(order1.need_pay_money) + sum(order1.advance_pay) paymoney from")
            .append(" (select t.*, (select to_date('1970/01/01', 'yyyy/mm/dd') + t.signing_time / (60 * 60 * 24) from dual) signing_date from es_order t) order1")
            .append(" left join (select order_id from es_order) order2 on order1.order_id = order2.order_id")
            .append(" where order1.status = 7").append(" and to_char(order1.signing_date, 'yyyymm') =").append("'").append(month).append("' ")
            .append(" and order1.bill_sn = ").append("'").append(sn).append("'");
            
            String value  = this.daoSupport.queryForString(sql.toString());
            value = value==null?"0":value;
            result= Double.valueOf(value.toString()); 
         /*   Map<String, Object> resultMap = this.daoSupport.queryForMap(sql.toString(), params);
            if(null != resultMap && resultMap.size() > 0) {
                Object object = resultMap.get("paymoney");
                if(null != object) { 
                    result= Double.valueOf(object.toString()); 
                }
            }*/
        } 
        return result;
    }

    /**
     * 根据结算单号查出 正向佣金
     *
     * @param sn
     * @return
     */
    @Override
    public Double getCommiPrice(String sn) {
        String sql = "select sum(commission) from es_order where status = 7 and   bill_sn = ?";
        return (Double) this.baseDaoSupport.queryForObject(sql, new DoubleMapper(), sn);
    }


    /**
     * 根据结算单号查出 正向红包
     *
     * @param sn
     * @return
     */
    @Override
    public Double getRedPacketsPrice(String sn) {
        String sql = "select sum(bonus_pay) from es_order where status = 7 and  bill_sn = ?";
        return (Double) this.baseDaoSupport.queryForObject(sql, new DoubleMapper(), sn);
    }

    /**
     * 根据结算单号查出 退款金额
     *
     * @param sn
     * @return
     */
    @Override
    public Double getReturnedPrice(String sn) {
        String sql = "SELECT DISTINCT sum(REGEXP_SUBSTR (return_price,'[^,]+',1,LEVEL)) from es_sellback_list where bill_sn = ? CONNECT BY REGEXP_SUBSTR (return_price,'[^,]+',1,LEVEL) IS NOT NULL order by 1";
        return (Double) this.baseDaoSupport.queryForObject(sql, new DoubleMapper(), sn);
    }


    /**
     * 根据结算单号查出 退还佣金
     *
     * @param sn
     * @return
     */
    @Override
    public Double getReturnedCommiPrice(String sn) {
        String sql = "select sum(o.commission) from  es_order o  where o.order_id in (select sb.orderid from es_sellback_list sb where sb.tradestatus = 3 and  sb.BILL_SN = ?)";
        return (Double) this.baseDaoSupport.queryForObject(sql, new DoubleMapper(), sn);
    }


    /**
     * 根据结算单号查出 退还红包
     *
     * @param sn
     * @return
     */
    @Override
    public Double getReturnedRedPacketsPrice(String sn) {
        String sql = "select sum(o.bonus_pay) from  es_order o  where o.order_id in (select sb.orderid from es_sellback_list sb where sb.tradestatus = 3 and sb.BILL_SN = ?)";
        return (Double) this.baseDaoSupport.queryForObject(sql, new DoubleMapper(), sn);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveBillAccountInfo(BillAccount billAccount) {
        this.daoSupport.insert("ES_BILL_ACCOUNT", billAccount);
    }

    @Override
    public BillAccount getBillAccountInfo(Integer bill_id) {
        return (BillAccount) this.daoSupport.queryForObject("select * from es_bill_account where bill_id=?", BillAccount.class, bill_id);
    }

}
