CREATE DATABASE  IF NOT EXISTS `gomeshop` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `gomeshop`;
-- MySQL dump 10.13  Distrib 5.6.17, for osx10.6 (i386)
--
-- Host: 192.168.0.247    Database: gomeshop
-- ------------------------------------------------------
-- Server version	5.6.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `es_activity`
--

DROP TABLE IF EXISTS `es_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ES_ACTIVITY (
  ID int(11) NOT NULL,
  NAME VARCHAR(100) NOT NULL COMMENT '促销活动名称',
  START_TIME BIGINT(20) NOT NULL COMMENT '促销活动开始时间',
  END_TIME BIGINT(20) NOT NULL COMMENT '促销活动结束时间',
  PROMOTION_TYPES VARCHAR(45) NOT NULL COMMENT '1. 满减 2. 满赠  3. 折扣 4. 包邮',
  FILL_MINUS DECIMAL(6,1) DEFAULT NULL COMMENT '满减的满',
  MINUS DECIMAL(6,1) DEFAULT NULL COMMENT '满减的减',
  FILL_GIFT DECIMAL(6,1) DEFAULT NULL COMMENT '满赠的满',
  DISCOUNT DECIMAL(4,1) DEFAULT NULL COMMENT '折扣',
  IS_FREE_SHIPPING INT(1) DEFAULT NULL COMMENT '是否包邮',
  IS_ENABLE INT(1) DEFAULT NULL COMMENT '是否启用',
  DESCRIPTION TEXT COMMENT '描述',
  PRIMARY KEY (ID)
); ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `es_activity`
--

LOCK TABLES `es_activity` WRITE;
/*!40000 ALTER TABLE `es_activity` DISABLE KEYS */;
INSERT INTO `es_activity` VALUES (1,'满200减20',1441036800,1443542400,'1',200,20,NULL,NULL,NULL,1,'');
/*!40000 ALTER TABLE `es_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `es_activity_gift`
--

DROP TABLE IF EXISTS `es_activity_gift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ES_ACTIVITY_GIFT (
	ID INT(11) NOT NULL,
 	ACTIVITY_ID INT(11) NOT NULL,
  GOODS_ID INT(11) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `es_activity_gift`
--

LOCK TABLES `es_activity_gift` WRITE;
/*!40000 ALTER TABLE `es_activity_gift` DISABLE KEYS */;
/*!40000 ALTER TABLE `es_activity_gift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `es_activity_goods`
--

DROP TABLE IF EXISTS `es_activity_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ES_ACTIVITY_GOODS (
	ID INT(11) NOT NULL,
 	ACTIVITY_ID INT(11) NOT NULL,
  GOODS_ID INT(11) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `es_activity_goods`
--

LOCK TABLES `es_activity_goods` WRITE;
/*!40000 ALTER TABLE `es_activity_goods` DISABLE KEYS */;
/*!40000 ALTER TABLE `es_activity_goods` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-28 15:29:56
