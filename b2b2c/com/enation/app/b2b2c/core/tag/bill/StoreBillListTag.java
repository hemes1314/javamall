package com.enation.app.b2b2c.core.tag.bill;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺结算单列表
 *
 * @author fenlongli
 * @date 2015-5-21 上午11:44:45
 */
@Component
public class StoreBillListTag extends BaseFreeMarkerTag {
    private IStoreMemberManager storeMemberManager;
    private IBillManager billManager;
    private IStoreManager storeManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        int pageSize = 10;
        Integer pageNo = request.getParameter("page") == null ? 1 : NumberUtils.toInt(request.getParameter("page").toString());
        Map result = new HashMap();
        Page page = billManager.store_bill_detail_list(pageNo, pageSize, storeMemberManager.getStoreMember().getStore_id());
        result.put("billList", page);
        result.put("totalCount", page.getTotalCount());
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);


        //自营店隐藏掉确认结算单；
        int storeType = 1;
        //获取当前登录的会员 店铺id
        Integer store_id = storeMemberManager.getStoreMember().getStore_id();
        if (store_id != null) {
            Store store = storeManager.getStore(store_id);
            storeType = store.getStore_type();
        }
        result.put("storeType",storeType);



        return result;
    }

    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }

    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }

    public IBillManager getBillManager() {
        return billManager;
    }

    public void setBillManager(IBillManager billManager) {
        this.billManager = billManager;
    }

    public IStoreManager getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }
}
