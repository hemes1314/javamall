package com.enation.app.b2b2c.core.action.api.storesite;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.Navigation;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.INavigationManager;
import com.enation.framework.action.WWAction;


@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("navigation")
@Results({
	 @Result(name="edit", type="freemarker", location="/themes/default/b2b2c/storesite/navication_edit.html") 
})
public class NavigationApiAction extends WWAction {
	private INavigationManager navigationManager;
	private IStoreMemberManager storeMemberManager;
	private String name;
	private Integer disable;
	private Integer sorts;
	private String contents;
	private String nav_url;
	private Integer target;
	private Integer nav_id;
	private Navigation navication;
	
	public String getList(){
		
		return null;
	};
	
	public String add(){
		StoreMember storeMember = storeMemberManager.getStoreMember();

		Navigation navigation = new Navigation();
		navigation.setName(name);
		navigation.setDisable(disable);
		navigation.setSort(sorts);
		navigation.setContents(contents);
		navigation.setNav_url(nav_url);
		navigation.setTarget(target);
		navigation.setStore_id(storeMember.getStore_id());
		if(nav_url!=null && !nav_url.equals("")){
		    if(nav_url.indexOf("http://www.gomecellar.com/")!=0){
		        this.showErrorJson("链接请以http://www.gomecellar.com/开头！");
		        return JSON_MESSAGE;
		    }
		}
		try {
			this.navigationManager.save(navigation);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return JSON_MESSAGE;
	}
	
	public String getNavcation(){
		navication = this.navigationManager.getNavication(nav_id);
		return "edit";
	}
	
	public String edit(){
		StoreMember storeMember =storeMemberManager.getStoreMember();

		Navigation navigation = new Navigation();
		navigation.setName(name);
		navigation.setDisable(disable);
		navigation.setSort(sorts);
		navigation.setContents(contents);
		navigation.setNav_url(nav_url);
		navigation.setTarget(target);
		navigation.setId(nav_id);
		navigation.setStore_id(storeMember.getStore_id());
		
		if(nav_url!=null && !nav_url.equals("")){
            if(nav_url.indexOf("http://www.gomecellar.com/")!=0){
                this.showErrorJson("链接请以http://www.gomecellar.com/开头！");
                return JSON_MESSAGE;
            }
        }
		
		try {
			this.navigationManager.edit(navigation);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return JSON_MESSAGE;
	}
	
	public String delete(){
		try {
			this.navigationManager.delete(nav_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return JSON_MESSAGE;
	}
	
	
	//set get 

	public String getName() {
		return name;
	}

	public INavigationManager getNavigationManager() {
		return navigationManager;
	}

	public void setNavigationManager(INavigationManager navigationManager) {
		this.navigationManager = navigationManager;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDisable() {
		return disable;
	}

	public void setDisable(Integer disable) {
		this.disable = disable;
	}


	public Integer getSorts() {
		return sorts;
	}

	public void setSorts(Integer sorts) {
		this.sorts = sorts;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getNav_url() {
		return nav_url;
	}

	public void setNav_url(String nav_url) {
		this.nav_url = nav_url;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Integer getNav_id() {
		return nav_id;
	}

	public void setNav_id(Integer nav_id) {
		this.nav_id = nav_id;
	}

	public Navigation getNavication() {
		return navication;
	}

	public void setNavication(Navigation navication) {
		this.navication = navication;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
