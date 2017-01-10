/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50611
Source Host           : localhost:3306
Source Database       : seckill

Target Server Type    : MYSQL
Target Server Version : 50611
File Encoding         : 65001

Date: 2017-01-11 00:46:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `seckill`
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone6', '98', '2017-01-11 22:18:29', '2017-01-12 00:00:00', '2017-01-07 15:55:41');
INSERT INTO `seckill` VALUES ('1001', '800元秒杀ipad', '199', '2017-01-10 23:38:21', '2017-01-12 00:00:00', '2017-01-07 15:55:41');
INSERT INTO `seckill` VALUES ('1002', '6600元秒杀mac book pro', '300', '2017-01-01 00:00:00', '2017-01-02 00:00:00', '2017-01-07 15:55:41');
INSERT INTO `seckill` VALUES ('1003', '7000元秒杀iMac', '400', '2017-01-01 00:00:00', '2017-01-02 00:00:00', '2017-01-07 15:55:41');

-- ----------------------------
-- Table structure for `success_killed`
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------
INSERT INTO `success_killed` VALUES ('1000', '1575719131', '-1', '2017-01-07 23:07:11');
INSERT INTO `success_killed` VALUES ('1000', '1787632429', '0', '2017-01-08 18:24:21');
INSERT INTO `success_killed` VALUES ('1000', '17876324329', '0', '2017-01-08 18:26:51');
INSERT INTO `success_killed` VALUES ('1001', '1575719131', '0', '2017-01-07 23:14:04');
INSERT INTO `success_killed` VALUES ('1001', '15757101341', '0', '2017-01-10 23:38:21');
