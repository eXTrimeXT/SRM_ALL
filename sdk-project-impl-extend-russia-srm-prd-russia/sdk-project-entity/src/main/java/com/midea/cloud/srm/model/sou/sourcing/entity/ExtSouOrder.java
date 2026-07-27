package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("投标头表")
@TableName("scc_sou_order")
public class ExtSouOrder extends SouOrder{

    @ApiModelProperty("投标人")
    private String extTenderName;

    @ApiModelProperty("投标联系电话")
    private String extTenderPhone;

    @ApiModelProperty("投标人")
    private String extTenderEmail;

    @ApiModelProperty("确认投标标识")
    private String extTenderFlag;

    @ApiModelProperty("下载标书时间")
    private Date extDownBidFileTime;

    @ApiModelProperty("投标类型：TECH-技术标；BUS-商务标")
    private String extOrderType;

    @ApiModelProperty("技术标标识")
    private String extTechFlag;

    @ApiModelProperty("投标次数")
    @TableField(exist = false)
    private Integer tenderTimes;

    /**
     * 是否查阅标书
     */
    @ApiModelProperty("是否查阅标书")
    @TableField(exist = false)
    private String extReadBidFlag;

    @ApiModelProperty("不参与原因")
    @TableField(exist = false)
    private String extNotjoinReason;

}
