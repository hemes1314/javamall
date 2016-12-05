
/**************************************
*  2014年11月17日 变更-xulipeng
*  新增is_parent
**************************************/
ALTER TABLE `es_goods_cat` ADD COLUMN `is_parent` int(1) NULL DEFAULT 0;

/**************************************
*  2014年12月19日 增加可用库存的逻辑
*  kingapex
**************************************/
ALTER TABLE `es_goods` ADD COLUMN `enable_store` int NOT NULL DEFAULT '0' ;
ALTER TABLE `es_product` ADD COLUMN `enable_store` int NOT NULL DEFAULT '0';

ALTER TABLE `es_product_store` ADD COLUMN `enable_store` int NOT NULL DEFAULT '0';
ALTER TABLE `es_store_log` ADD COLUMN `enable_store` int NOT NULL DEFAULT '0';

update es_product set store=0 where store is null;
update es_goods set enable_store=store;
update es_product set enable_store=store;
UPDATE es_product_store set enable_store=store;