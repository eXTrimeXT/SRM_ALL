package com.midea.cloud.srm.model.pj.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 签署业务单据关系表
 * @author huangbf3
 * @date 2023-09-21
 */
@Data
@TableName("scc_pj_sign_order")
public class SccPjSignOrder extends BaseEntity {

 @ApiModelProperty("主键ID")
 @TableId("SIGN_ORDER_ID")
 private Long signOrderId;

 @ApiModelProperty("业务单据类型")
 @TableField("ORDER_TYPE")
 private String orderType;

 @ApiModelProperty("合同文档ID")
 @TableField("SIGN_FILE_ID")
 private Long signFileId;

 @ApiModelProperty("签署单ID")
 @TableField("SIGN_ID")
 private Long signId;

 @ApiModelProperty("SRM业务单据ID")
 @TableField("SRM_ORDER_ID")
 private Long srmOrderId;

 @ApiModelProperty("盖章状态")
 @TableField("SIGN_STATUS")
 private String signStatus;
}

