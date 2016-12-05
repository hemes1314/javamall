package com.gomecellar.workflow.utils;
public class Constants {
	public final static String IS_AGREE_YES = "1";			//同意
	public final static String IS_AGREE_NO = "0";			//不同意
	public final static String IS_AGREE_CANCEL = "3";		//撤销
	
	//流程状态
	public final static String FLOW_STATUS_CANCEL = "-1";	//撤销
	public final static String FLOW_STATUS_DRAFT = "0";		//草稿
	public final static String FLOW_STATUS_APPROVAL = "1";	//审批中
	public final static String FLOW_STATUS_END = "2";		//审批结束
	
	//申诉申请定义
	public final static String PROCESS_DEF_APPEAL_FLOW = "appealProcess";
	
}
