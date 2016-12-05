/**
删除商品表中多余的字段
**/
ALTER TABLE `es_goods`
DROP COLUMN `image_default`,
DROP COLUMN `image_file`,
DROP COLUMN `isgroup`,
DROP COLUMN `islimit`,
DROP COLUMN `alert_num`,
DROP COLUMN `istejia`,
DROP COLUMN `no_discount`;

/**
商品规格表中加入id字段，以便保持规格的排序
**/
ALTER TABLE `es_goods_spec`
ADD COLUMN `id`  int NOT NULL AUTO_INCREMENT FIRST ,
MODIFY COLUMN `spec_id`  int(8) NOT NULL AFTER `id`,
ADD PRIMARY KEY (`id`);

/**
菜单表添加是否可以导出字段
**/
ALTER TABLE `es_menu`
ADD COLUMN `canexp`  int(6) NULL DEFAULT 0  ;