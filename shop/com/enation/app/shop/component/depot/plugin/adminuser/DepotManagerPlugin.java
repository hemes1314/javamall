package com.enation.app.shop.component.depot.plugin.adminuser;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.IAdminUserDeleteEvent;
import com.enation.app.base.core.plugin.user.IAdminUserInputDisplayEvent;
import com.enation.app.base.core.plugin.user.IAdminUserLoginEvent;
import com.enation.app.base.core.plugin.user.IAdminUserOnAddEvent;
import com.enation.app.base.core.plugin.user.IAdminUserOnEditEvent;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 库管员插件
 * @author kingapex
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class DepotManagerPlugin  extends AutoRegisterPlugin implements IAdminUserOnAddEvent,
		IAdminUserOnEditEvent, IAdminUserInputDisplayEvent,IAdminUserDeleteEvent,IAdminUserLoginEvent {
	
	private IDepotManager depotManager;
	private IDaoSupport baseDaoSupport;
	@Override
	public String getInputHtml(AdminUser user) {
		List<Depot> roomList = depotManager.list();
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();		
		freeMarkerPaser.putData("roomList" ,roomList);
		
		if(user!=null){
			Integer  depotid =(Integer)this.baseDaoSupport.queryForObject("select depotid from depot_user where userid=?", new IntegerMapper(),user.getUserid());
			freeMarkerPaser.putData("depotid",depotid);
		}
		
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public void onEdit(Long userid) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String depotid = request.getParameter("depotid");
		if(!StringUtil.isEmpty(depotid)){
			String sql ="select count(0) from depot_user where  userid=?";
			int count  = this.baseDaoSupport.queryForInt(sql, userid);
			if(count>0)
				this.baseDaoSupport.execute("update depot_user set depotid=? where userid=?", depotid,userid);
			else
				this.baseDaoSupport.execute("insert into depot_user(userid,depotid)values(?,?)", userid,depotid);
		} 
	}

	@Override
	public void onAdd(Long userid) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		if(request!=null){
			String depotid = request.getParameter("depotid");
			if(!StringUtil.isEmpty(depotid)){
				this.baseDaoSupport.execute("insert into depot_user(userid,depotid)values(?,?)", userid,depotid);
			}
		}
	}

	
	@Override
	public void onDelete(long userid) {
		 this.baseDaoSupport.execute("delete from depot_user where userid=?", userid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLogin(AdminUser user) {
			WebSessionContext<AdminUser>  sessonContext = ThreadContextHolder.getSessionContext();			
				Integer  depotid =(Integer)this.baseDaoSupport.queryForObject("select depotid from depot_user where userid=?", new IntegerMapper(),user.getUserid());
				 DepotUser stockUser = new DepotUser();
				 stockUser.setFounder(user.getFounder());
				 stockUser.setPassword(user.getPassword());
				 stockUser.setRealname(user.getRealname());
				 stockUser.setRemark(user.getRemark());
				 stockUser.setRoleids(user.getRoleids());
				 stockUser.setSiteid(user.getSiteid());
				 stockUser.setState(user.getState());
				 stockUser.setUserdept(user.getUserdept());
				 stockUser.setUserid(user.getUserid());
				 stockUser.setUsername(user.getUsername());
				 stockUser.setUserno(user.getUserno());
				 stockUser.setDateline(user.getDateline());
				 if(depotid!= null)
					 stockUser.setDepotid(depotid); 
				 stockUser.setAuthList(user.getAuthList());
				 sessonContext.setAttribute(UserConext.CURRENT_ADMINUSER_KEY,stockUser); 
	}
	
	
 

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

}
