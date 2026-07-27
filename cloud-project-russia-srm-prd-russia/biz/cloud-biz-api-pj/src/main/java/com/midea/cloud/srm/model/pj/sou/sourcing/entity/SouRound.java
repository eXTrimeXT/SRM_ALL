package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouPwdInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouRoundOpenPwdInfoTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Map;

/**
 * 寻源.核心表 - 轮次信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_round")
@ApiModel("寻源项目轮次")
public class SouRound extends BaseEntity<SouRound> {

    @ApiModelProperty("ID")
    @TableId("ROUND_ID")
    private Long roundId;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    @ApiModelProperty("报价开始时间")
    @TableField("ORDER_START_TIME")
    private Date orderStartTime;

    @ApiModelProperty("报价截止时间")
    @TableField("ORDER_END_TIME")
    private Date orderEndTime;

    @ApiModelProperty("调整报价截止时间原因")
    @TableField("CHANGE_ORDER_END_TIME_REASON")
    private String changeOrderEndTimeReason;

    @ApiModelProperty("是否已公开本轮结果")
    @TableField("HAS_PUBLISH_RESULT")
    private Enable hasPublishResult;

    @ApiModelProperty("公开本轮结果时间")
    @TableField("PUBLISH_RESULT_TIME")
    private Date publishResultTime;

    @ApiModelProperty("本轮应报价供应商数量")
    @TableField("INVITE_COUNT")
    private Integer inviteCount;

    @ApiModelProperty("本轮已报价供应商数量")
    @TableField("ORDER_COUNT")
    private Integer orderCount;

    @ApiModelProperty("商务开标标识")
    @TableField("BUSINESS_OPEN")
    private Enable businessOpen;

    @ApiModelProperty("商务开标时间")
    @TableField("BUSINESS_OPEN_TIME")
    private Date businessOpenTime;

    @ApiModelProperty("报价解密标识")
    @TableField("PRICE_DECRYPT")
    private Enable priceDecrypt;

    @ApiModelProperty("报价解密时间")
    @TableField("PRICE_DECRYPT_TIME")
    private Date priceDecryptTime;

    @ApiModelProperty("开标密码信息")
    @TableField(value = "OPEN_PWD_INFO", typeHandler = SouRoundOpenPwdInfoTypeHandler.class)
    private Map<String, SouPwdInfoVO> openPwdInfo;

    @TableField("EARLIEST_BUSINESS_OPEN_TIME")
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;

}
