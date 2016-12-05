package com.enation.app.shop.mobile.action.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.app.shop.core.service.IMemberVitemManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.impl.VirtualProductManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Scope("prototype")
@Action("myCenter")
public class MemberCenterApiAction extends WWAction{
    
    private IPointHistoryManager pointHistoryManager;
    private IMemberOrderItemManager memberOrderItemManager;
    //礼品卡
    private IMemberGiftcardManager memberGiftcardManager;
    private IMemberManager memberManager;
    private IBonusManager bonusManager;
    private IMemberVitemManager memberVitemManager;
    private VirtualProductManager virtualProductManager;
    private IAdvanceLogsManager advanceLogsManager;


    private String start_time; 
    private String end_time;    
    private Integer number;
    private String mobile;
    private String card_sn; //礼品卡编号
    private int PAGE_SIZE = 20;
    private Double money;
    private String password;

    public String myPoint(){
        
        Member member = UserConext.getCurrentMember();
        HttpServletRequest request=getRequest();
        if(member ==null){
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
         try{
             int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
             
             Page pointPage = pointHistoryManager.pagePointHistoryFopApp(pageNo, 20,start_time,end_time);
             Map<String,Object> param = new HashMap<String,Object>();
             param.put("pointPage", pointPage);
             param.put("point", member.getPoint());
             this.json =JsonMessageUtil.getMobileObjectJson(param);
         }catch (RuntimeException e){
             this.showPlainErrorJson("数据库运行异常");
         }
         return WWAction.JSON_MESSAGE;
    }
    /**
     * 我的礼品卡
     * 
     */
	public  String myGiftCart(){
        
        
        Member member = UserConext.getCurrentMember();
        HttpServletRequest request=getRequest();
        if(member ==null){
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
         
         try{
             int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
             
             Page GiftCart = memberGiftcardManager.getGiftcardList(pageNo, PAGE_SIZE, member.getMember_id());
             
             List<Map> list = (List<Map>) GiftCart.getResult();
             for (Map map : list){
                 map.put("type_image",UploadUtil.replacePath((String)map.get("type_image")));
             }
             this.json = JsonMessageUtil.getMobileObjectJson(GiftCart);
            
         }catch (RuntimeException e){
             this.showPlainErrorJson("数据库运行异常");;
         }
        return  WWAction.JSON_MESSAGE;
    }
    /**
     * 礼品卡充值
     * @return
     */
    public String topup(){
        
       try{
         Member member=  UserConext.getCurrentMember();
           if(member == null){
               this.json = JsonMessageUtil.expireSession();
               return WWAction.JSON_MESSAGE;
           }
           
           String err = memberGiftcardManager.topup(card_sn, password, member.getMember_id());
           if (err.equals("ok")) this.showPlainSuccessJson("充值成功");
           else this.showPlainErrorJson(err);
       }catch (RuntimeException e){
           this.showPlainErrorJson("密码错误，充值失败");
       }
     
        
        return WWAction.JSON_MESSAGE;
    }
    /**
     * 优惠券 列表
     * @return
     */
    public  String myCoupon(){
        
        try{
            HttpServletRequest request =getRequest();
            Member member=  UserConext.getCurrentMember();
          if(member == null){
              this.json = JsonMessageUtil.expireSession();
              return WWAction.JSON_MESSAGE;
          }

          int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
          Integer type =NumberUtils.toInt(request.getParameter("type"),0);
          Page coupon = bonusManager.getBonusListForApp(pageNo,PAGE_SIZE,member.getMember_id(),type,1);
          this.json = JsonMessageUtil.getMobileObjectJson(coupon);
 
          }catch (RuntimeException e){
              this.showPlainErrorJson("数据库运行异常");
          }
        
        return WWAction.JSON_MESSAGE;
    }
    /**
     * 优惠券 列表
     * @return
     */
    public  String myBouns(){
        
        try{
            HttpServletRequest request =getRequest();
            Member member=  UserConext.getCurrentMember();
          if(member == null){
              this.json = JsonMessageUtil.expireSession();
              return WWAction.JSON_MESSAGE;
          }

          int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
          Integer type =NumberUtils.toInt(request.getParameter("type"),0);
          Page coupon = bonusManager.getBonusListForApp(pageNo,PAGE_SIZE,member.getMember_id(),type,0);
          
          this.json = JsonMessageUtil.getMobileObjectJson(coupon);
 
          }catch (RuntimeException e){
              this.showPlainErrorJson("数据库运行异常");
          }
        
        return WWAction.JSON_MESSAGE;
    }
    /**
     * 我的虚拟物品
     * @return
     */
	public String myVitem(){
        try{
            HttpServletRequest request =getRequest();
            Member member=  UserConext.getCurrentMember();
              if(member == null){
                  this.json = JsonMessageUtil.expireSession();
                  return WWAction.JSON_MESSAGE;
              }
              int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
              Page webpage = memberVitemManager.getList(pageNo,PAGE_SIZE,member.getMember_id());
              List<Map> list = (List<Map>) webpage.getResult();
              for (Map map : list){
                  map.put("type_image",UploadUtil.replacePath((String)map.get("type_image")));
              }
              this.json = JsonMessageUtil.getMobileObjectJson(webpage);
 
          }catch (RuntimeException e){
              this.showPlainErrorJson("数据库运行异常"+e);
          }
        
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 虚拟物品列表
     * @return
     */
    public String vitemList(){
        try {
            HttpServletRequest request =getRequest();
            int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
            Page typeList = virtualProductManager.listForApp(pageNo,PAGE_SIZE);
            List<VirtualProduct> list = (List) typeList.getResult();
            for (VirtualProduct  map:list) {
               map.setImages(UploadUtil.replacePath(map.getImages()));
            }
            this.json = JsonMessageUtil.getMobileObjectJson(typeList);
        }catch(RuntimeException e){
            this.showPlainErrorJson("数据库运行异常");
        }
        return WWAction.JSON_MESSAGE;
    }
    /** 
     * 购买虚拟物品
     * @return
     */
    public String buyVitem(){
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        Member member= UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
        
        Integer typeId = StringUtil.toInt( request.getParameter("vitemTypeId") ,null);
        if(typeId == null ){
            this.showPlainErrorJson("必须传递vitemTypeId参数");
            return WWAction.JSON_MESSAGE;
        }
        
        VirtualProduct vp = virtualProductManager.get(typeId);
        if (vp == null) {
            this.showPlainErrorJson("无效的虚拟物品");
            return WWAction.JSON_MESSAGE;
        }
            
        Integer num = StringUtil.toInt( request.getParameter("num"), 1);
        try {
            memberManager.pay(member.getMember_id(), (double)vp.getPrice()*num, 0, vp.getName()+" x "+num, "虚拟物品");
            memberVitemManager.add(vp, num, member.getMember_id());
            this.showPlainSuccessJson("购买成功");
        }catch(Exception e) {
            if (e.getMessage().equals("账户余额不足")) this.showPlainErrorJson(e.getMessage());
            else  this.showPlainErrorJson(e.getMessage());
        }
        return WWAction.JSON_MESSAGE; 
        
    }
    /**
     * 余额账户
     * @return
     */
    public String advanceHistory(){
        try {
            Member member=  memberManager.get(UserConext.getCurrentMember().getMember_id());
            //Member member= UserConext.getCurrentMember();
            if(member == null){
                this.json = JsonMessageUtil.expireSession();
                return WWAction.JSON_MESSAGE;
            }
            HttpServletRequest request =getRequest();
            int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
            Page page = advanceLogsManager.pageAdvanceLogsForApp(pageNo, PAGE_SIZE,start_time,end_time);
           
            
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("advanceHistoryList", page);
            param.put("advance", member.getAdvance());   
            param.put("virtual", member.getVirtual());   
            param.put("total_money", member.getAdvance()+member.getVirtual());   
            this.json = JsonMessageUtil.getMobileObjectJson(param);
        }catch(RuntimeException e){
            this.showPlainErrorJson("数据库运行异常"+e);
        }
        return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 未评论
     * @return
     */
    public String waitComment(){
        
        try{
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            int pageNo =NumberUtils.toInt(request.getParameter("page"),1);
            Member member = UserConext.getCurrentMember();
            if(member == null){
                this.json = JsonMessageUtil.expireSession();
                return WWAction.JSON_MESSAGE;
            }
            int pageSize = 10;       
            Page goodsPage = memberOrderItemManager.getGoodsListForApp(member.getMember_id(), 0, pageNo, pageSize);
            List<Map> list = (List<Map>) goodsPage.getResult();
            for (Map map : list){
                map.put("thumbnail",UploadUtil.replacePath((String)map.get("thumbnail")));
            }
            this.json = JsonMessageUtil.getMobileObjectJson(goodsPage);
        }catch(Exception e){
            this.showPlainErrorJson("获取数据错误"+e); 
        }
        return WWAction.JSON_MESSAGE;
    }
    public String getStart_time() {
        return start_time ;
    }
    
    public void setStart_time(String start_time) {
        this.start_time = start_time + " 00:00:00";
    }
    
    public String getEnd_time() {
        return end_time + " 23:59:59";
    }
    
    public void setEnd_time(String end_time) {
        this.end_time = end_time + " 23:59:59";;
    }
    
    public Integer getNumber() {
        
        
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }

    
    public IPointHistoryManager getPointHistoryManager() {
        return pointHistoryManager;
    }

    
    public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
        this.pointHistoryManager = pointHistoryManager;
    }
    
    public IMemberGiftcardManager getMemberGiftcardManager() {
        return memberGiftcardManager;
    }
    
    public void setMemberGiftcardManager(IMemberGiftcardManager memberGiftcardManager) {
        this.memberGiftcardManager = memberGiftcardManager;
    }
    
    public IMemberManager getMemberManager() {
        return memberManager;
    }
    
    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public IBonusManager getBonusManager() {
        return bonusManager;
    }
    
    public void setBonusManager(IBonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }
    
    public String getCard_sn() {
        return card_sn;
    }
    
    public void setCard_sn(String card_sn) {
        this.card_sn = card_sn;
    }
    
    public IMemberVitemManager getMemberVitemManager() {
        return memberVitemManager;
    }
    
    public void setMemberVitemManager(IMemberVitemManager memberVitemManager) {
        this.memberVitemManager = memberVitemManager;
    }

    public Double getMoney() {
        return money;
    }
    
    public void setMoney(Double money) {
        this.money = money;
    }
    
    public VirtualProductManager getVirtualProductManager() {
        return virtualProductManager;
    }
    
    public void setVirtualProductManager(VirtualProductManager virtualProductManager) {
        this.virtualProductManager = virtualProductManager;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public IAdvanceLogsManager getAdvanceLogsManager() {
        return advanceLogsManager;
    }
    
    public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
        this.advanceLogsManager = advanceLogsManager;
    }
    
    public IMemberOrderItemManager getMemberOrderItemManager() {
        return memberOrderItemManager;
    }
    
    public void setMemberOrderItemManager(IMemberOrderItemManager memberOrderItemManager) {
        this.memberOrderItemManager = memberOrderItemManager;
    }


}
