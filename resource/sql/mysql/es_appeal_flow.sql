/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50545
Source Host           : localhost:3306
Source Database       : gm

Target Server Type    : MYSQL
Target Server Version : 50545
File Encoding         : 65001

Date: 2015-10-09 11:10:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for es_appeal_flow
-- ----------------------------
DROP TABLE IF EXISTS `es_appeal_flow`;
CREATE TABLE `es_appeal_flow` (
  `appeal_flow_id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `applicant` varchar(10) DEFAULT NULL COMMENT '申请人ID',
  `applicant_cn` varchar(10) DEFAULT NULL COMMENT '申请人姓名',
  `reason` varchar(2000) DEFAULT NULL COMMENT '申诉原因',
  `appeal_photo` varchar(300) DEFAULT NULL COMMENT '申诉相片',
  `process_instance_id` varchar(50) DEFAULT NULL COMMENT '流程实例ID',
  `business_id` int(8) DEFAULT NULL COMMENT '申诉业务对应的主键',
  `create_time` int(8) DEFAULT NULL COMMENT '创建时间',
  `status` varchar(2) DEFAULT NULL COMMENT '单据状态',
  PRIMARY KEY (`appeal_flow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for es_wf_task_opinion
-- ----------------------------
DROP TABLE IF EXISTS `es_wf_task_opinion`;
CREATE TABLE `es_wf_task_opinion` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `process_def_id` varchar(20) DEFAULT NULL COMMENT '流程定义名称',
  `business_id` int(30) DEFAULT NULL COMMENT '审批业务ID',
  `process_instance_id` varchar(50) DEFAULT NULL COMMENT '流程实例ID',
  `task_id` varchar(50) DEFAULT NULL COMMENT '任务ID',
  `task_defination_key` varchar(20) DEFAULT NULL COMMENT '任务定义名',
  `task_name` varchar(50) DEFAULT NULL COMMENT '任务名称',
  `user_id` varchar(50) DEFAULT NULL COMMENT '处理人ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '处理人姓名',
  `create_time` int(11) DEFAULT NULL COMMENT '处理时间',
  `comments` varchar(800) DEFAULT NULL COMMENT '审批意见',
  `isagree` varchar(1) DEFAULT NULL COMMENT '审批方式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8;
