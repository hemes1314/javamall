
<!-- 套餐组件支持 Start -->
UPDATE es_goods SET is_pack = 0 WHERE is_pack IS NULL;

ALTER TABLE `es_product` ADD COLUMN `is_pack` INT(1) NOT NULL DEFAULT 0;

ALTER TABLE `es_sellback_goodslist` ADD COLUMN `return_type` INT(1) NOT NULL DEFAULT 0;

<!-- 套餐组件支持 End -->

<!-- 退货列表bug Start -->

ALTER TABLE es_sellback_list MODIFY COLUMN goodslist text;

<!-- 退货列表bug End -->
