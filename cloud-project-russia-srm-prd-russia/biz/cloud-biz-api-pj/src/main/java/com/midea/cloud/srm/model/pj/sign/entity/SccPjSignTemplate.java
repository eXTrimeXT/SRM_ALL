package com.midea.cloud.srm.model.pj.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 契约锁流程配置表
 * @author huangbf3
 * @date 2023-09-21
 */
@Data
@TableName("scc_pj_sign_template")
public class SccPjSignTemplate extends BaseEntity {

 @ApiModelProperty("主键ID")
 @TableId("TEMPLATE_HEAD_ID")
 private Long templateHeadId;

 @ApiModelProperty("业务类型编码")
 @TableField("ORDER_TYPE")
 private String orderType;

 @ApiModelProperty("业务类型名称")
 @TableField("ORDER_TYPE_NEME")
 private String orderTypeNeme;

 @ApiModelProperty("FEIGN的url前缀值")
 @TableField("FEIGN_URL_PATH")
 private String feignUrlPath;

 @ApiModelProperty("业务服务")
 @TableField("BUSSINESS_CLASS")
 private String bussinessClass;
}

