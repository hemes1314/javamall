/**
ɾ����Ʒ���ж�����ֶ�
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
��Ʒ�����м���id�ֶΣ��Ա㱣�ֹ�������
**/
ALTER TABLE `es_goods_spec`
ADD COLUMN `id`  int NOT NULL AUTO_INCREMENT FIRST ,
MODIFY COLUMN `spec_id`  int(8) NOT NULL AFTER `id`,
ADD PRIMARY KEY (`id`);

/**
�˵�������Ƿ���Ե����ֶ�
**/
ALTER TABLE `es_menu`
ADD COLUMN `canexp`  int(6) NULL DEFAULT 0  ;