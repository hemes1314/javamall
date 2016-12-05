package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.MemberVitem;
import com.enation.app.shop.core.model.VirtualProduct;
import com.enation.app.shop.core.service.IMemberVitemManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;

/**
 * 虚拟物品查询
 * @author humaodong
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class MemberVitemManager extends BaseSupport implements IMemberVitemManager {
   
    @Autowired
    IMemberManager memberManager;
    
    @Autowired
    VirtualProductManager virtualProductManager;
    
    VirtualProductLogManager virtualProductLogManager;
    
    
	@Override
	public Page getList(int pageNo,int pageSize,long memberid) {

        String sql = "select * from es_member_vitem where member_id="+memberid+" order by price";
		Page webPage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webPage;
	}

    @Override
    public MemberVitem getByTypeId(int type_id, long member_id) {
        String sql = "select * from es_member_vitem where type_id=? and member_id=?";
        MemberVitem vitem = (MemberVitem)this.daoSupport.queryForObject(sql, MemberVitem.class, type_id, member_id);
        return vitem;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String topup(int type_id, int num, long member_id) {
        try {
            MemberVitem vitem = this.getByTypeId(type_id, member_id);
            this.sub(type_id, num, member_id);
            memberManager.topup(member_id, 0.0, (double)vitem.getPrice()*num, vitem.getType_name()+" x "+num, "虚拟物品");
            return "ok";
        } catch(Exception e) {
            return e.getMessage();
        }
    }
	
    @SuppressWarnings("unchecked")
    public void add(VirtualProduct vp, int num, long member_id) throws Exception {
        MemberVitem vitem = this.getByTypeId(vp.getId(), member_id);
        if (vitem == null) {
            vitem = new MemberVitem();
            vitem.setMember_id(member_id);
            vitem.setType_id(vp.getId());
            vitem.setType_name(vp.getName());
            vitem.setType_image(vp.getImages());
            vitem.setPrice((double)vp.getPrice());
            vitem.setNum(num);
            this.baseDaoSupport.insert("es_member_vitem", vitem);
        } else {
            vitem.setNum(vitem.getNum()+num);
            this.baseDaoSupport.update("es_member_vitem", vitem, "item_id=" + vitem.getItem_id());
        }
        
    }
    
    public void sub(int type_id, int num, long member_id) throws Exception {
        MemberVitem vitem = this.getByTypeId(type_id, member_id);
        if (vitem == null || vitem.getNum() < num) throw new Exception("对不起，您拥有的该虚拟物品数量不足。");
        if (vitem.getNum() == num) {
            this.daoSupport.execute("delete from es_member_vitem where type_id=? and member_id=?", type_id, member_id);
        } else {
            this.daoSupport.execute("update es_member_vitem set num=num-? where type_id=? and member_id=?", num, type_id, member_id);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map> getByMemberId(long memberId) {
        String sql = "select mv.*, vp.images from es_member_vitem mv "
                + "left join es_virtual_product vp on mv.type_id=vp.id"
                + " where member_id="+memberId+" order by id";
        List<Map> list = this.baseDaoSupport.queryForList(sql);
        
        for (Map map: list) {
            if (map.get("images") != null) {
                map.put("images", UploadUtil.replacePath(map.get("images").toString()));
            }
        }
        
        return list;
    }
    
    @Transactional
    public void give(int sender, int receiver, int typeId) {
        String sql = "update es_member_vitem set num=(num-1) where member_id="+ sender +" and type_id="+ typeId;
        this.baseDaoSupport.execute(sql);
        MemberVitem sendMemberVitem = this.getByTypeId(typeId, sender);
        
        if (sendMemberVitem.getNum() == 0) {
            sql = "delete from es_member_vitem where member_id="+ sender +" and type_id="+ typeId;
            this.baseDaoSupport.execute(sql);
        }
        
        MemberVitem receiverMemberVitem = this.getByTypeId(typeId, receiver);
        
        if (receiverMemberVitem == null) {
            VirtualProduct virtualProduct = virtualProductManager.get(typeId);
            
            receiverMemberVitem = new MemberVitem();
            receiverMemberVitem.setMember_id(receiver);
            receiverMemberVitem.setNum(1);
            receiverMemberVitem.setPrice(Double.valueOf(virtualProduct.getPrice()));
            receiverMemberVitem.setType_id(typeId);
            receiverMemberVitem.setType_name(virtualProduct.getName());
            
            this.baseDaoSupport.insert("es_member_vitem", receiverMemberVitem);
        } else {
            sql = "update es_member_vitem set num=(num+1) where member_id="+ receiver +" and type_id="+ typeId;
            this.baseDaoSupport.execute(sql);
        }
        
        virtualProductLogManager.addLog(sender, receiver, typeId);
    }
    
    public VirtualProductManager getVirtualProductManager() {
        return virtualProductManager;
    }

    public void setVirtualProductManager(VirtualProductManager virtualProductManager) {
        this.virtualProductManager = virtualProductManager;
    }
    
    public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
    public VirtualProductLogManager getVirtualProductLogManager() {
        return virtualProductLogManager;
    }
    
    public void setVirtualProductLogManager(VirtualProductLogManager virtualProductLogManager) {
        this.virtualProductLogManager = virtualProductLogManager;
    }

}
