package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description scc_npm_sou_expert_record
 * @author panmq
 * @date 2023-10-20
 */
@Data
@ApiModel("scc_npm_sou_expert_record")
@TableName(value = "scc_npm_sou_expert_record")
public class ExtSouExpertRecord extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 @ApiModelProperty("主键")
 private Long expertRecordId;
 /**
  * 关联招标基本信息主键ID
  */
 @ApiModelProperty("关联招标基本信息主键ID")
 private Long projectId;

 @ApiModelProperty("关联组员ID")
 private Long groupId;
 /**
  * 账号ID
  */
 @ApiModelProperty("账号ID")
 private Long userId;
 /**
  * 工（账）号
  */
 @ApiModelProperty("工（账）号")
 private String userName;
 /**
  * 名字
  */
 @ApiModelProperty("名字")
 private String fullName;
 /**
  * 专家等级
  */
 @ApiModelProperty("专家等级")
 private String expertLevel;
 /**
  * 抽取时间
  */
 @ApiModelProperty("抽取时间")
 private Date extractTime;

 @ApiModelProperty("抽取范围")
 private String expertRange;

 @ApiModelProperty("移除招标工作组原因")
 private String removeReason;
}

