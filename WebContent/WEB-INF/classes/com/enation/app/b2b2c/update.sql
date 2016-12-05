alter table es_order add bill_status int;
alter table es_order add bill_sn int;
alter table es_sellback_list add bill_status int;
alter table es_sellback_list add bill_sn int;

alter table es_bonus_type add belong int default 0;


/**
 * 兼容orcale 设置int 类型 默认值 为 0
**/
 
alter table es_store_logi_rel MODIFY logi_id int default '0';
alter table es_store_logi_rel MODIFY store_id int default '0';

