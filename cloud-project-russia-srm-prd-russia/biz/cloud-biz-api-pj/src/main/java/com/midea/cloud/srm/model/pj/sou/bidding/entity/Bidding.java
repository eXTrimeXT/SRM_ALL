package com.midea.cloud.srm.model.pj.sou.bidding.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreTemplate;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源核心-询价单信息
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_project")
@ApiModel(description = "寻源-竞价单信息")
@SuppressWarnings("JavadocReference")
public class Bidding extends BaseEntity<Bidding> {

    @TableId("PROJECT_ID")
    @ApiModelProperty("ID")
    private Long projectId;


    /**
     * 竞价管理
     */
    @TableField("BID_NO")
    @ApiModelProperty("竞价单号")
    private String bidNo;

    @TableField("BID_STATUS")
    @ApiModelProperty("竞价状态")
    private String bidStatus;

    @TableField("AUDIT_STATUS")
    @ApiModelProperty("审核状态")
    private String auditStatus;

    @TableField("PUBLISHER")
    @ApiModelProperty("发布人")
    private String publisher;

    @TableField("PUBLISHER_DATE")
    @ApiModelProperty("发布时间")
    private Date publisherDate;

    @TableField("BIDDING_START_DATE")
    @ApiModelProperty("竞价开始时间")
    private Date biddingStartDate;

    @TableField("BIDDING_END_DATE")
    @ApiModelProperty("竞价结束时间")
    private Date biddingEndDate;
    //------------------------------
}
