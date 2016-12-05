package com.enation.app.shop.core.tag.sommelier;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.Sommelier;
import com.enation.app.shop.core.model.SommelierCDayOrder;
import com.enation.app.shop.core.model.SommelierOrder;
import com.enation.app.shop.core.model.Yuemo;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.core.service.impl.SommelierManager;
import com.enation.app.shop.core.service.impl.SommelierOrderManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品酒师预约侍酒
 * @author lin
 *2015-11-13下午7:58:00
 */
@Component
@Scope("prototype")
public class SommelierOrderTag extends BaseFreeMarkerTag {
	private SommelierOrderManager sommelierOrderManager;
	private SommelierManager sommelierManager;
	private MemberManager memberManager;
	private SommelierCDayOrder sommelierCDayOrder;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品酒师的列表 ，List<Sommelier>型
	 * {@link Sommelier}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	    String sommelierids = ThreadContextHolder.getHttpRequest().getParameter("sommelierid");
	    //Integer sommelierid=(Integer)params.get("sommelierid");
	    Integer sommelierid = Integer.valueOf(sommelierids);
	    Long daystime =  getStartTime();
	    Long dayetime =  getEndTime();
	   
	    String section = "";
		//List<SommelierOrder> sommelierOrderList  = sommelierOrderManager.get(sommelierid,daystime,dayetime);
/*		for(SommelierOrder order:sommelierOrderList)
		{
		    section = section + order.getStime();
		    section = section + "~";
		    section = section + order.getEtime();
		}*/
		
		Sommelier so = sommelierManager.get(sommelierid);
		Member m = memberManager.get(so.getUserid());
		
		sommelierCDayOrder = new SommelierCDayOrder();
		sommelierCDayOrder.setSommelier_name(so.getName());
        String statis = SystemSetting.getStatic_server_domain();
        String face = "";
        if(so.getImg_url() != null)
        {
          face =  so.getImg_url().replaceAll("fs:",statis);  
        }
		sommelierCDayOrder.setSommelier_face(face);
		sommelierCDayOrder.setOrderTimeSection(section);
		return sommelierCDayOrder;
	}
    
    public SommelierManager getSommelierManager() {
        return sommelierManager;
    }
    
    public void setSommelierManager(SommelierManager sommelierManager) {
        this.sommelierManager = sommelierManager;
    }

    
    public SommelierOrderManager getSommelierOrderManager() {
        return sommelierOrderManager;
    }

    
    public void setSommelierOrderManager(SommelierOrderManager sommelierOrderManager) {
        this.sommelierOrderManager = sommelierOrderManager;
    }

    
    public SommelierCDayOrder getSommelierCDayOrder() {
        return sommelierCDayOrder;
    }

    
    public void setSommelierCDayOrder(SommelierCDayOrder sommelierCDayOrder) {
        this.sommelierCDayOrder = sommelierCDayOrder;
    }
    
    private Long getStartTime(){  
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0);  
        return todayStart.getTime().getTime();  
    } 
    
    private Long getEndTime(){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 999);  
        return todayEnd.getTime().getTime();  
    }

    
    public MemberManager getMemberManager() {
        return memberManager;
    }

    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    } 
	
}
