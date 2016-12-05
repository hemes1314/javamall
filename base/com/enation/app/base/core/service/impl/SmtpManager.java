package com.enation.app.base.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * smtp管理
 * @author kingapex
 * @date 2011-11-1 下午12:10:30 
 * @version V1.0
 */
public class SmtpManager extends BaseSupport<Smtp> implements ISmtpManager {

	@Override
	public void add(Smtp smtp) {
		this.baseDaoSupport.insert("smtp", smtp);
	}

	@Override
	public void edit(Smtp smtp) {
		this.baseDaoSupport.update("smtp", smtp,"id="+smtp.getId());
	}

	@Override
	public void delete(Integer[] idAr) {
		
		if(idAr==null || idAr.length==0) return;
		String idstr = StringUtil.arrayToString(idAr, ",");
		
		this.baseDaoSupport.execute("delete from smtp where id in("+idstr+")");
		
	}

	@Override
	public List<Smtp> list() {
		
		return this.baseDaoSupport.queryForList("select * from smtp", Smtp.class);
	}

	@Override
	public void sendOneMail(Smtp currSmtp) {
		this.baseDaoSupport.update("smtp", currSmtp, "id="+currSmtp.getId());
	}

	
	
	public Smtp get(int id){
		return this.baseDaoSupport.queryForObject("select * from smtp where id=?", Smtp.class, id);
	}

	@Override
	public Smtp getCurrentSmtp() {
		// TODO Auto-generated method stub
		return null;
	}
}
