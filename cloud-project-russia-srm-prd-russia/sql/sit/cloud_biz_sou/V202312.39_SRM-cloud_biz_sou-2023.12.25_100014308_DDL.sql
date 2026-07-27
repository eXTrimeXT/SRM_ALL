-- --------------------------------------------------------
-- 主机:                           10.246.99.206
-- 服务器版本:                        5.7.43-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  12.0.0.6468
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_agreement 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_agreement` (
    `AGREEMENT_ID` bigint(20) NOT NULL COMMENT '协议主键',
    `DESIGN_ID` bigint(20) DEFAULT NULL COMMENT '关联提报策划方案id',
    `ADJUST_CODE` varchar(50) DEFAULT NULL COMMENT '调价申请单号',
    `AREA` varchar(50) DEFAULT NULL COMMENT '供货范围',
    `ITEM_ID` bigint(20) DEFAULT NULL COMMENT '物资ID',
    `ITEM_CODE` varchar(50) DEFAULT NULL COMMENT '物资编码',
    `ITEM_DESC` varchar(50) DEFAULT NULL COMMENT '物资名称',
    `MODEL` varchar(50) DEFAULT NULL COMMENT '规格型号',
    `UNIT` varchar(50) DEFAULT NULL COMMENT '计量单位',
    `BRAND` varchar(50) DEFAULT NULL COMMENT '品牌',
    `REMARK` varchar(50) DEFAULT NULL COMMENT '备注',
    `VENDOR_ID` bigint(20) DEFAULT NULL COMMENT '供应商ID',
    `VENDOR_CODE` varchar(50) DEFAULT NULL COMMENT '供应商编码',
    `VENDOR_NAME` varchar(50) DEFAULT NULL COMMENT '供应商名称',
    `TAX_PRICE` decimal(24,8) DEFAULT NULL COMMENT '含税',
    `NOTAX_PRICE` decimal(24,8) DEFAULT NULL COMMENT '未税价格',
    `PROJECT_ID` bigint(20) DEFAULT NULL COMMENT '项目ID',
    `PROJECT_CODE` varchar(50) DEFAULT NULL COMMENT '项目编码',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATED_FULL_NAME` varchar(50) NOT NULL COMMENT '创建人姓名',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`AGREEMENT_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调价申请-协议明细';

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_analysis 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_analysis` (
    `ANALYSIS_ID` bigint(30) NOT NULL,
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `TYPE` int(8) DEFAULT NULL COMMENT '类型,1.使用单位金额分析、2.按物流品类分析、3供方采购金额分析',
    `ORGANIZATION_ID` bigint(20) DEFAULT NULL COMMENT '组织id',
    `ORGANIZATION_CODE` varchar(255) DEFAULT NULL COMMENT '组织编码',
    `ORGANIZATION_NAME` varchar(255) DEFAULT NULL COMMENT '组织名称',
    `MAT_MONEY` decimal(10,2) DEFAULT NULL COMMENT '金额',
    `MAT_RATE` decimal(10,2) DEFAULT NULL COMMENT '占比',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    `VENDOR_ID` bigint(20) DEFAULT NULL COMMENT '供应商ID',
    `VENDOR_CODE` varchar(250) DEFAULT NULL COMMENT '供应商编码',
    `VENDOR_NAME` varchar(250) DEFAULT NULL COMMENT '供应商名称',
    `MATERIAL_ID` bigint(20) DEFAULT NULL COMMENT '物资id',
    `MATERIAL_CODE` varchar(250) DEFAULT NULL COMMENT '物资编码',
    `MATERIAL_NAME` varchar(250) DEFAULT NULL COMMENT '物资名称',
    PRIMARY KEY (`ANALYSIS_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_info 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_info` (
    `DEMAND_ID` bigint(30) NOT NULL COMMENT '需求id',
    `BRAND` varchar(255) DEFAULT NULL COMMENT '品牌',
    `ORDER_STATUS` varchar(255) DEFAULT NULL COMMENT '订单状态，多个',
    `ORDER_NUM` int(11) DEFAULT NULL COMMENT '单项物资订单数',
    `ORDER_NUM_TYPE` varchar(255) DEFAULT NULL COMMENT '单项物资订单数类型,大于、小于、等于、大于等于、小于等于',
    `BUY_MONEY_TYPE` varchar(255) DEFAULT NULL COMMENT '单项物资采购金额（未税）,大于、小于、等于、大于等于、小于等于',
    `BUY_MONEY` varchar(255) DEFAULT NULL COMMENT '单项物资采购金',
    `LAST_YEAR_ORDER_DATE_START` datetime DEFAULT NULL COMMENT '上年订单日期从',
    `LAST_YEAR_ORDER_DATE_END` datetime DEFAULT NULL COMMENT '上年订单日期到',
    `LAST_LAST_YEAR_ORDER_DATE_START` datetime DEFAULT NULL COMMENT '上上年订单日期从',
    `LAST_LAST_YEAR_ORDER_DATE_END` datetime DEFAULT NULL COMMENT '上上年订单日期到',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`DEMAND_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_other 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_other` (
    `OTHER_ID` bigint(30) NOT NULL COMMENT '其他id',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `OTHER_NUM` varchar(255) DEFAULT NULL COMMENT '序号',
    `BID_AREA` varchar(255) DEFAULT NULL COMMENT 'BID_AREA',
    `SUP` varchar(255) DEFAULT NULL COMMENT '供应商',
    `PRO_MON` varchar(255) DEFAULT NULL COMMENT '年采购额',
    `AMO_PRO` varchar(255) DEFAULT NULL COMMENT '金额占比',
    `CUR_POL` varchar(255) DEFAULT NULL COMMENT '现行政策',
    `SEL_SUP` varchar(255) DEFAULT NULL COMMENT '拟询价供方',
    `BID_STR` varchar(255) DEFAULT NULL COMMENT '招标策略',
    `TAR_SET` varchar(255) DEFAULT NULL COMMENT '目标设定',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`OTHER_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_programme 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_programme` (
    `PROGRAMME_ID` bigint(30) NOT NULL COMMENT '策划方案id',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `UNIT_FLAG` varchar(255) DEFAULT NULL COMMENT '使用单位金额',
    `BUY_FLAG` varchar(255) DEFAULT NULL COMMENT '供方采购金额',
    `CATEGORY_FLAG` varchar(255) DEFAULT NULL COMMENT '按物资品类分析Y',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`PROGRAMME_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_setting 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_setting` (
    `SETTING_ID` bigint(30) NOT NULL COMMENT '目标设定id',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `SUP_ID` bigint(30) DEFAULT NULL COMMENT '供方id',
    `SUP_CODE` varchar(255) DEFAULT NULL COMMENT '供方编码',
    `SUP_NAME` varchar(255) DEFAULT NULL COMMENT '供方编码',
    `NATURE` varchar(255) DEFAULT NULL COMMENT '经验性质',
    `CATEGORY_ID` bigint(30) DEFAULT NULL COMMENT '品类Id',
    `CATEGORY_CODE` varchar(255) DEFAULT NULL COMMENT '品类编码',
    `CATEGORY_NAME` varchar(255) DEFAULT NULL COMMENT '品类名称',
    `CUSTOMERS` varchar(255) DEFAULT NULL COMMENT '行业客户',
    `IS_NEW` varchar(255) DEFAULT NULL COMMENT '是否新引进Y/N',
    `REMARK` varchar(255) DEFAULT NULL COMMENT '备注',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`SETTING_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_strategy 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_strategy` (
    `STRATEGY_ID` bigint(30) NOT NULL COMMENT '策略id',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `BID_AREA` varchar(255) DEFAULT NULL COMMENT '招标区域',
    `YP_TYPE` varchar(255) DEFAULT NULL COMMENT '油品型号',
    `BUY_NUM` varchar(255) DEFAULT NULL COMMENT '采购量',
    `BUY_MONEY` varchar(255) DEFAULT NULL COMMENT '采购额',
    `CUR_POL` varchar(255) DEFAULT NULL COMMENT '现行政策',
    `SEL_SUP` varchar(255) DEFAULT NULL COMMENT '拟询价供方',
    `BID_STR` varchar(255) DEFAULT NULL COMMENT '招标策略',
    `TAR_SET` varchar(255) DEFAULT NULL COMMENT '目标设定',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`STRATEGY_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_sup 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_sup` (
    `DEMAND_SUP_ID` bigint(30) NOT NULL COMMENT '需求供应商信息i',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `SUP_ID` bigint(30) DEFAULT NULL COMMENT '供应商id',
    `SUP_CODE` varchar(255) DEFAULT NULL COMMENT '供应商编码',
    `SUP_NAME` varchar(255) DEFAULT NULL COMMENT '供应商名称',
    `NATURE` varchar(255) DEFAULT NULL COMMENT '性质',
    `ARRIVAL_RATE` varchar(255) DEFAULT NULL COMMENT '到货及时率',
    `REMARK` varchar(255) DEFAULT NULL COMMENT '备注',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`DEMAND_SUP_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_work 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_work` (
    `WORK_ID` bigint(30) NOT NULL COMMENT '工作日程id',
    `DESIGN_ID` bigint(30) DEFAULT NULL COMMENT '提报策划方案id',
    `DATA_STA` varchar(255) DEFAULT NULL COMMENT '数据统计',
    `REQ_STA` varchar(255) DEFAULT NULL COMMENT '需求分析',
    `SUP_DEV` varchar(255) DEFAULT NULL COMMENT '供方资源开发',
    `PLAN_WRITE` varchar(255) DEFAULT NULL COMMENT '策划方案编写',
    `INQ_PRO` varchar(255) DEFAULT NULL COMMENT '询比价环节',
    `FAC_APL` varchar(255) DEFAULT NULL COMMENT '定厂申请',
    `PRO_SIGN` varchar(255) DEFAULT NULL COMMENT '方案签批',
    `CON_SIGN` varchar(255) DEFAULT NULL COMMENT '合同签署',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`WORK_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_demand_year_data 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_demand_year_data` (
    `YEAR_ID` bigint(20) NOT NULL COMMENT '拉取的数据id',
    `DESIGN_ID` bigint(20) NOT NULL COMMENT '提报策划方案id',
    `TYPE` int(11) NOT NULL COMMENT '类型：1.上年数据，2.上上年数据，3.需求合并数据',
    `CATEGORY_ID` bigint(20) DEFAULT NULL COMMENT '四级品类id',
    `AREA_ID` bigint(20) DEFAULT NULL COMMENT '区域id',
    `AREA_CODE` varchar(50) DEFAULT NULL COMMENT '区域编码',
    `AREA_NAME` varchar(50) DEFAULT NULL COMMENT '区域名称',
    `ORGANIZATION_ID` bigint(20) DEFAULT NULL COMMENT '组织id',
    `ORGANIZATION_CODE` varchar(50) DEFAULT NULL COMMENT '组织编码',
    `ORGANIZATION_NAME` varchar(50) DEFAULT NULL COMMENT '组织名称',
    `MATERIAL_ID` bigint(20) DEFAULT NULL COMMENT '物资id',
    `MATERIAL_CODE` varchar(50) DEFAULT NULL COMMENT '物资编码',
    `MATERIAL_NAME` varchar(50) DEFAULT NULL COMMENT '物资名称',
    `MODEL` varchar(50) DEFAULT NULL COMMENT '规格型号',
    `UNIT` varchar(50) DEFAULT NULL COMMENT '计量单位',
    `UNIT_CODE` varchar(50) DEFAULT NULL COMMENT '计量单位编码',
    `ORDER_NUM` decimal(20,6) DEFAULT NULL COMMENT '订单数量',
    `BRAND` varchar(50) DEFAULT NULL COMMENT '品牌',
    `PRICE_TAX` decimal(20,6) DEFAULT NULL COMMENT '未税单价',
    `TAX_RATE_CODE` varchar(50) DEFAULT NULL COMMENT '税率编码',
    `TAX_RATE` decimal(20,6) DEFAULT NULL COMMENT '税率',
    `RATE_PRICE` decimal(20,6) DEFAULT NULL COMMENT '含税单价',
    `MONEY_AMOUNT` decimal(20,6) DEFAULT NULL COMMENT '金额',
    `PRICE_TOTAL` decimal(20,6) DEFAULT NULL COMMENT '价税合计',
    `SUP_ID` bigint(20) DEFAULT NULL COMMENT '供应商id',
    `SUP_CODE` varchar(50) DEFAULT NULL COMMENT '供应商编码',
    `SUP_NAME` varchar(50) DEFAULT NULL COMMENT '供应商名称',
    `ONE_TYPE_ID` bigint(20) DEFAULT NULL COMMENT '一级分类id',
    `ONE_TYPE_CODE` varchar(50) DEFAULT NULL COMMENT '一级分类编码',
    `ONE_TYPE_NAME` varchar(50) DEFAULT NULL COMMENT '一级分类名称',
    `TWO_TYPE_ID` bigint(20) DEFAULT NULL COMMENT '二级分类id',
    `TWO_TYPE_CODE` varchar(50) DEFAULT NULL COMMENT '二级分类编码',
    `TWO_TYPE_NAME` varchar(50) DEFAULT NULL COMMENT '二级分类名称',
    `ORDER_DATE` date DEFAULT NULL COMMENT '订单日期',
    `BUY_USER_ID` bigint(20) DEFAULT NULL COMMENT '采购员id',
    `BUY_USER_NAME` varchar(50) DEFAULT NULL COMMENT '采购员名字',
    `CONTRACT_ID` bigint(20) DEFAULT NULL COMMENT '合同编号id',
    `CONTRACT_CODE` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `CONTRACT_NUM` varchar(50) DEFAULT NULL COMMENT '合同序号',
    `BUY_TYPE` varchar(50) DEFAULT NULL COMMENT '购买类型',
    `CREATE_UNIT` bigint(20) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `CREATE_UNIT_CODE` varchar(50) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `CREATE_UNIT_NAME` varchar(50) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `DATA_SOURCE` varchar(50) DEFAULT NULL COMMENT '数据来源',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    `CREATE_UNIT_ID` bigint(50) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `HISTORY_VENDOR_CODE` varchar(250) DEFAULT NULL COMMENT '历史供应商编码',
    `HISTORY_PRICE_TAX` decimal(20,6) DEFAULT NULL COMMENT '历史未税单价',
    `HISTORY_RATE_PRICE` decimal(20,6) DEFAULT NULL COMMENT '历史含税单价',
    PRIMARY KEY (`YEAR_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拉取数据';

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_design_plan 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_design_plan` (
    `DESIGN_ID` bigint(20) NOT NULL COMMENT '提报策划方案id',
    `PROJECT_CODE` varchar(50) DEFAULT NULL COMMENT '项目编号',
    `PROJECT_ID` bigint(20) DEFAULT NULL COMMENT '项目I',
    `PROJECT_NAME` varchar(255) DEFAULT NULL COMMENT '项目名称',
    `NUM` bigint(10) DEFAULT NULL COMMENT '轮数',
    `PHONE` varchar(50) DEFAULT NULL COMMENT '联系方式',
    `DEP_ID` bigint(20) DEFAULT NULL COMMENT '部门ID',
    `DEP_CODE` varchar(50) DEFAULT NULL COMMENT '部门编码',
    `DEP_NAME` varchar(50) NOT NULL COMMENT '部门名称',
    `PROJ_MONEY` decimal(24,8) DEFAULT NULL COMMENT '项目金额（万元）',
    `AREA` varchar(50) NOT NULL COMMENT '供货区域',
    `STATUS` varchar(50) DEFAULT NULL COMMENT '状态：拟定、审核中、审核完成、审核不通过',
    `PROJ_INTRODUCE` varchar(50) DEFAULT NULL COMMENT '项目介绍',
    `PRICING_IDEAS` varchar(50) DEFAULT NULL COMMENT '定价思路',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATED_FULL_NAME` varchar(50) NOT NULL COMMENT '创建人姓名',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    `HAS_CREATE_PUR_INQ` char(1) NOT NULL DEFAULT 'N' COMMENT '是否已创建集采询比价',
    `SOU_NO` varchar(50) DEFAULT NULL COMMENT '集采询比价单号',
    PRIMARY KEY (`DESIGN_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提报策划方案-基础信息表';

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_ledger 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_ledger` (
    `LEDGER_ID` bigint(20) NOT NULL COMMENT '台账id',
    `PROJECT_NAME` varchar(50) DEFAULT NULL COMMENT '项目名称',
    `CONTRACT_START_DATE` datetime DEFAULT NULL COMMENT '合同日期从',
    `CONTRACT_END_DATE` datetime DEFAULT NULL COMMENT '合同日期到',
    `HEAD_PERSON` varchar(10) DEFAULT NULL COMMENT '负责人',
    `PROJECT_TOTAL_MONEY` decimal(24,8) DEFAULT NULL COMMENT '项目总金额（万元）',
    `DELAY_REASON` varchar(50) DEFAULT NULL COMMENT '延期原因',
    `NEXT_SUGGEST` varchar(50) NOT NULL COMMENT '下轮项目建议及注意事项',
    `ADD_NUM` varchar(50) NOT NULL COMMENT '本次新增项目数',
    `ADD_BEFORE_MONEY` decimal(24,8) DEFAULT NULL COMMENT '原临采年采购额(万元)',
    `ADD_AFTER_MONEY` decimal(24,8) NOT NULL COMMENT '集采后年采购额(万元)',
    `ADD_DECREMENT_MONEY` decimal(24,8) DEFAULT NULL COMMENT '本次递减金额(万元)',
    `ADD_DECREMENT_RATIO` decimal(24,8) DEFAULT NULL COMMENT '成本递减比例(%)',
    `ABO_BEFORE_MONEY` decimal(24,8) DEFAULT NULL COMMENT '上期/集采前采购额(万元)',
    `ABO_AFTER_MONEY` decimal(24,8) DEFAULT NULL COMMENT '集采后年采购额(万元)',
    `ABO_DECREMENT_MONEY` decimal(24,8) DEFAULT NULL COMMENT '本次递减金额(万元)',
    `ABO_DECREMENT_RATIO` decimal(24,8) DEFAULT NULL COMMENT '成本递减比例(%)',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATED_FULL_NAME` varchar(50) NOT NULL COMMENT '创建人姓名',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    `DATA_SOURCE` varchar(50) DEFAULT NULL COMMENT '数据来源',
    PRIMARY KEY (`LEDGER_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集采台账表';

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_paa_adjust 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_paa_adjust` (
    `ADJUST_ID` bigint(20) NOT NULL COMMENT '调整id',
    `ADJUST_CODE` varchar(255) DEFAULT NULL COMMENT '调价申请单号',
    `ADJUST_NAME` varchar(255) DEFAULT NULL COMMENT '调价申请名称',
    `STATUS` varchar(255) DEFAULT NULL COMMENT '状态拟定、提交、审批',
    `JC_ID` bigint(30) DEFAULT NULL COMMENT '关联集采项目id',
    `JC_CODE` varchar(255) DEFAULT NULL COMMENT '关联集采项目编号',
    `NUM` int(11) DEFAULT NULL COMMENT '次数',
    `EXECUTE_DATE_START` datetime DEFAULT NULL COMMENT '执行时间从',
    `EXECUTE_DATE_END` datetime DEFAULT NULL COMMENT '执行时间到',
    `ADJUST_TYPE` varchar(255) DEFAULT NULL COMMENT '调价形式1.询比价调整,2.市场行情调整',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    `CREATE_USER_ORG_OU_ID` varchar(50) DEFAULT NULL COMMENT '创建人所属公司ID',
    `CREATE_USER_ORG_OU_CODE` bigint(20) DEFAULT NULL COMMENT '创建人所属公司编码',
    `CREATE_USER_ORG_OU_NAME` varchar(50) DEFAULT NULL COMMENT '创建人所属公司名称',
    `CREATE_UNIT_ID` bigint(50) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `CREATE_UNIT_CODE` varchar(20) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `CREATE_UNIT_NAME` varchar(50) DEFAULT NULL COMMENT '创建单位(操作人员单位)',
    `DATA_SOURCE` varchar(50) DEFAULT NULL COMMENT '数据来源',
    `DESIGN_ID` bigint(20) NOT NULL COMMENT '关联提报策划方案id',
    PRIMARY KEY (`ADJUST_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

-- 导出  表 meicloud_usrm_cloud_biz_sou.scc_sou_ch_paa_market_situation 结构
CREATE TABLE IF NOT EXISTS `scc_sou_ch_paa_market_situation` (
    `MARKET_SITUATION_ID` bigint(30) NOT NULL COMMENT '市场行情id',
    `ADJUST_ID` bigint(30) DEFAULT NULL COMMENT '调整id',
    `ADJUST_CODE` varchar(255) DEFAULT NULL COMMENT '调价申请单号',
    `INTRODUCE` varchar(255) DEFAULT NULL COMMENT '调价介绍',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`MARKET_SITUATION_ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据导出被取消选择。

