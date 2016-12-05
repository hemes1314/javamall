package com.gomecellar.workflow.model;

import com.enation.framework.database.PrimaryKeyField;

public class AppealFlow {
	private Integer appeal_flow_id;	//主键
	private Long member_id;		//购买人主键
	private String member_name;		//购买人
	private String applicant;		//流程发起人id
	private String applicant_cn;	//流程发起人
	private Long appeal_date;		//申诉时间
	private String reason;			//申诉原因
	private String appeal_photo;		//申诉图片
	private String process_instance_id;	//流程实例ID
	private String business_id;			//实际申诉业务主键
	private Long create_time;		//流程创建时间
	private String status;			//审批状态		-1：撤销，0:草稿，1审批中，2审批结束
	
	private String tradeno;         //退货单  add by lxl 
	@PrimaryKeyField
	public Integer getAppeal_flow_id() {
		return appeal_flow_id;
	}

	public void setAppeal_flow_id(Integer appeal_flow_id) {
		this.appeal_flow_id = appeal_flow_id;
	}
	
	
	public String getApplicant() {
		return applicant;
	}
	
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public Long getAppeal_date() {
		return appeal_date;
	}
	public void setAppeal_date(Long appeal_date) {
		this.appeal_date = appeal_date;
	}
	public String getReason() {
		return reason==null?"退货":reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProcess_instance_id() {
		return process_instance_id;
	}
	public void setProcess_instance_id(String process_instance_id) {
		this.process_instance_id = process_instance_id;
	}
	public String getBusiness_id() {
		return business_id;
	}
	public void setBusiness_id(String businessId) {
		this.business_id = businessId;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getApplicant_cn() {
		return applicant_cn;
	}

	public void setApplicant_cn(String applicant_cn) {
		this.applicant_cn = applicant_cn;
	}
	
	public String getAppeal_photo() {
		return appeal_photo;
	}

	public void setAppeal_photo(String appeal_photo) {
		this.appeal_photo = appeal_photo;
	}
	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getMember_name() {
		return member_name;
	}

	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}

    
    public String getTradeno() {
        return tradeno;
    }

    
    public void setTradeno(String tradeno) {
        this.tradeno = tradeno;
    }
	
}
