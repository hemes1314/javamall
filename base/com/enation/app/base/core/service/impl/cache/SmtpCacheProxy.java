package com.enation.app.base.core.service.impl.cache;

import java.util.Date;
import java.util.List;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.util.DateUtil;

public class SmtpCacheProxy extends AbstractCacheProxy<List<Smtp>> implements
		ISmtpManager  {
	private static final String cacheName = "smtp_cache";
	private ISmtpManager smtpManager;
	
	public SmtpCacheProxy(ISmtpManager _smtpManager){
		super(cacheName);
		this.smtpManager = _smtpManager;
	}
	
	private String getKey(){
		 
		return cacheName;
	}
	
	/**
	 * 清除当前站点缓存
	 */
	private void cleanCache( ){
		String mainkey = getKey();
		this.cache.remove( mainkey);
	 
	}
	
	private void put(List<Smtp> smtpList){
		String mainkey =  getKey();
		cache.put(mainkey, smtpList);
	}
	
	private List<Smtp> get(){
		String mainkey = getKey();
		return cache.get(mainkey);
	}
	
	
	@Override
	public void add(Smtp smtp) {
		this.smtpManager.add(smtp);
		this.cleanCache();
	}

	@Override
	public void edit(Smtp smtp) {
		this.smtpManager.edit(smtp);
		this.cleanCache();
	}

	@Override
	public void delete(Integer[] idAr) {
		this.smtpManager.delete(idAr);
		this.cleanCache();
	}

	@Override
	public List<Smtp> list() {
		List<Smtp> smtpList = this.get();
		if(smtpList==null){
			smtpList = this.smtpManager.list();
			this.put(smtpList);
		}
		return smtpList;
	}

	@Override
	public void sendOneMail(Smtp currSmtp) {
		
 
		currSmtp.setLast_send_time(DateUtil.getDateline());
		currSmtp.setSend_count(currSmtp.getSend_count()+1);
		
		this.smtpManager.sendOneMail(currSmtp);
	}

	@Override
	public Smtp get(int id) {
		return this.smtpManager.get(id);
	}


	@Override
	public Smtp getCurrentSmtp() {
		
		List<Smtp> smtpList = this.list();
		if( smtpList== null ) throw  new RuntimeException("没有可用的smtp服务器");
		
		Smtp currentSmtp = null;
		
		//寻找当前的smtp  
		for(Smtp smtp:smtpList){
			if( checkCount(smtp)){
				currentSmtp= smtp;
				break;
			}
		}
		
		
		
		if(currentSmtp== null){
			this.logger.error("未寻找可用smtp");
			throw new RuntimeException("未找到可用smtp，都已达到最大发信数 ");
		}
		
		if(this.logger.isDebugEnabled()){
			this.logger.debug("找到smtp->host["+currentSmtp.getHost()+"],username["+currentSmtp.getUsername()+"]");
		}
		
		return currentSmtp;
		
	}

	private boolean checkCount(Smtp smtp){
		long last_send_time = smtp.getLast_send_time(); //最后一次发送时间
		
		if(!DateUtil.toString(new Date(last_send_time*1000), "yyyy-MM-dd").equals(DateUtil.toString(new Date(), "yyyy-MM-dd"))){ //已经不是今天
			smtp.setSend_count(0);
			
			if(this.logger.isDebugEnabled()){
				this.logger.debug("host["+smtp.getHost()+"]不是今天,此smtp发信数置为0");
			}
		}
		
		return smtp.getSend_count()< smtp.getMax_count();
	}


 


 
}
