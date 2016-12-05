package com.enation.app.shop.mobile.action.auction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.Auction;
import com.enation.app.shop.core.model.AuctionRecord;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.mobile.service.impl.ApiAuctionRecordManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.app.shop.mobile.service.impl.ApiAuctionManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("auction")
@Results({
	@Result(name="list", type="freemarker",location="/shop/admin/yuemo/auction_list.html"),
	@Result(name="add_auction", type="freemarker",location="/shop/admin/yuemo/auction_add.html"),
	@Result(name="detail_auction", type="freemarker",location="/shop/admin/yuemo/auction_detail.html"),
	@Result(name="edit_auction", type="freemarker",location="/shop/admin/yuemo/auction_edit.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class AuctionApiAction extends WWAction {
	private Integer[] id;
	private String title;
	private String content;
	private Auction auction;
	
    

    private String stime;
    private Integer auctionId;	
	private List<Auction> auctionList;
	private ApiAuctionManager apiAuctionManager;
	private ApiAuctionRecordManager apiAuctionRecordManager;
	private IMemberAddressManager memberAddressManager ;
	private final int PAGE_SIZE = 20;
	
    /*
    * 拍卖列表
    */
	public String list() {
        String statis = SystemSetting.getStatic_server_domain(); 
        try{   
            //HttpServletRequest request =ThreadContextHolder.getHttpRequest();    
            //int page =NumberUtils.toInt(request.getParameter("page"),1) ;
            
            //Page auctionPage = apiAuctionManager.listPage(page, PAGE_SIZE);   
            //List<Auction> list = (List<Auction>)auctionPage.getResult();
            List<Auction> list = apiAuctionManager.list();

            //替换图片路径
            for(Auction au :list)
            {
               if(au.getImage() != null)
                   au.setImage(au.getImage().replaceAll("fs:",statis)); 
               
               //时间戳
               SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               try {
                    String stime = au.getStart_time();
                    String etime = au.getTime();
                    Date d1;
                    Date d2;
                    d1 = sdf.parse(stime);
                    d2 = sdf.parse(etime);
                    long timeStemp = d1.getTime();
                    long timeEtemp = d2.getTime();
                    String str = String.valueOf(timeStemp);
                    String re_time = str.substring(0, 10);
                    au.setStime(timeStemp);
                    au.setEtime(timeEtemp);
                } catch(ParseException e) {
                    e.printStackTrace();
                }
            }
            
            this.json = JsonMessageUtil.getMobileListJson(list);
        }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	 /*
     * 拍卖详情
     */
	public String detailAuction() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        try{
                String auctionId = request.getParameter("auctionId");
        	    Map list = apiAuctionManager.detail(auctionId);
    	        this.json = JsonMessageUtil.getMobileListJson(list);
        }catch (RuntimeException e) {
                this.logger.error("数据库运行异常", e);
                this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	public String addLooks() {
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    String actionId = request.getParameter("auctionId");
	    String memId = request.getParameter("memId");
	    boolean sta = apiAuctionManager.addLooks(actionId);
        if(sta)
        {
          this.showPlainSuccessJson("成功围观");
        }else{
            this.showErrorJson("围观失败");
        }
	    return WWAction.JSON_MESSAGE;
	}
	/*
	 * 参加拍卖
	 */
	public String joinAuction() {
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    Member member = UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
	    String actionId = request.getParameter("auctionId");
	    String memId = request.getParameter("memId");
	    String add = request.getParameter("add");
	    try{
    	    boolean sta = apiAuctionManager.joinAuction(actionId,memId,add);
    	    if(sta)
    	    {
    	      this.showPlainSuccessJson("成功加价");
    	    }else{
    	        this.showErrorJson("拍卖失败");
    	    }
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showErrorJson("数据库异常");
        }
	    return WWAction.JSON_MESSAGE;
	} 
	
	 /*
     * 拍卖出价记录
     */
    public String getAuctionRecord() { 
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String actionId = request.getParameter("auctionId");
        try{
        List<AuctionRecord> auRecordList = apiAuctionRecordManager.getAuctionRecord(actionId);
        this.json = JsonMessageUtil.getMobileListJson(auRecordList);
        }
        catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
        return JSON_MESSAGE;
    } 
    public String defAddress(){
        
        try{
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            Integer memId = NumberUtils.toInt(request.getParameter("memId"));
            Member member = UserConext.getCurrentMember();
            if (member == null) {
                this.json = JsonMessageUtil.expireSession();
                return WWAction.JSON_MESSAGE;
            }
            MemberAddress address = memberAddressManager.getMemberDefault(member.getMember_id()); 
            HashMap <String ,Object > map = new HashMap<String, Object>();
            map.put("isDefAddress", address==null ? false : true);      
            this.json=JsonMessageUtil.getMobileObjectJson(map);
          
        }catch(RuntimeException e){
            this.showPlainErrorJson("运行异常");
        }
        
        return WWAction.JSON_MESSAGE;
    }
	
	public List<Auction> getAuctionList() {
        return auctionList;
    }

    public void setAuctionList(List<Auction> auctionList) {
        this.auctionList = auctionList;
    }
    

    public Integer[] getId() {
        return id;
    }

    
    public void setId(Integer[] id) {
        this.id = id;
    }

    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }

    
    public String getContent() {
        return content;
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    
    public Auction getAuction() {
        return auction;
    }

    
    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    
    public String getStime() {
        return stime;
    }

    
    public void setStime(String stime) {
        this.stime = stime;
    }

    
    public Integer getAuctionId() {
        return auctionId;
    }

    
    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    
    public ApiAuctionManager getApiAuctionManager() {
        return apiAuctionManager;
    }

    
    public void setApiAuctionManager(ApiAuctionManager apiAuctionManager) {
        this.apiAuctionManager = apiAuctionManager;
    }

    
    public ApiAuctionRecordManager getApiAuctionRecordManager() {
        return apiAuctionRecordManager;
    }

    
    public void setApiAuctionRecordManager(ApiAuctionRecordManager apiAuctionRecordManager) {
        this.apiAuctionRecordManager = apiAuctionRecordManager;
    }

    
    public IMemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    
    public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }
    
	
}
