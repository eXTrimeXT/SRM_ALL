package com.midea.cloud.srm.model.sou.souseq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description scc_npm_sou_seq
 * @author panmq
 * @date 2023-10-19
 */
@Data
@ApiModel("scc_npm_sou_seq")
@TableName(value = "scc_npm_sou_seq")
public class ExtSouSeq extends BaseEntity {

   @TableId
   /**
    * 主键
    */
   @ApiModelProperty("主键")
   private Long seqId;
 /**
  * 单号前缀
  */
 @ApiModelProperty("单号前缀")
 private String seqPrefix;
 /**
  * 序号控制因子
  */
 @ApiModelProperty("序号控制因子")
 private String seqControl;
 /**
  * 日期控制因子
  */
 @ApiModelProperty("日期控制因子")
 private String dateControl;
 /**
  * 流水号位数
  */
 @ApiModelProperty("流水号位数")
 private Long digit;
 /**
  * 当前流水
  */
 @ApiModelProperty("当前流水")
 private Long serial;
}

