package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panmq
 * @description scc_npm_sou_open_bid_record
 * @date 2023-11-13
 */
@Data
@ApiModel("scc_npm_sou_open_bid_record")
@TableName(value = "scc_npm_sou_open_bid_record")
public class ExtNpmSouOpenBidRecord extends BaseEntity {
    @TableId
    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long openId;
    /**
     * 关联招标基本信息主键ID
     */
    @ApiModelProperty("关联招标基本信息主键ID")
    private Long projectId;
    /**
     * 开标类型，TECH-技术标, BUS-商务标
     */
    @ApiModelProperty("开标类型，TECH-技术标, BUS-商务标")
    private String openType;
    /**
     * 操作人账号ID
     */
    @ApiModelProperty("操作人账号ID")
    private Long userId;
    /**
     * 操作人账号
     */
    @ApiModelProperty("操作人账号")
    private String userName;
    /**
     * 操作人账号名字
     */
    @ApiModelProperty("操作人账号名字")
    private String fullName;
    /**
     * 轮次
     */
    @ApiModelProperty("轮次")
    private Integer round;
    /**
     * 开标状态，COMPLETED-完成
     */
    @ApiModelProperty("开标状态，COMPLETED-完成")
    private String openStatus;
}

