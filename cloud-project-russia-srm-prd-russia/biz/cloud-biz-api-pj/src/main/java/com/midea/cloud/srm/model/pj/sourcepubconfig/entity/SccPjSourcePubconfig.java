package com.midea.cloud.srm.model.pj.sourcepubconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 寻源公示配置表
 * @author zhengkai.blog.csdn.net
 * @date 2023-09-20
 */
@Data
@ApiModel("寻源公示配置表")
public class SccPjSourcePubconfig extends BaseEntity {

    @TableId
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long pubconfigId;

    /**
     * 模板单号  collate utf8mb4_general_ci
     */
    @ApiModelProperty("模板单号  collate utf8mb4_general_ci")
    private String configNumber;

    /**
     * 公示寻源模板名称  collate utf8mb4_general_ci
     */
    @ApiModelProperty("公示寻源模板名称  collate utf8mb4_general_ci")
    private String pubconfigName;

    /**
     * 单据状态  collate utf8mb4_general_ci
     */
    @ApiModelProperty("单据状态  collate utf8mb4_general_ci")
    private String status;

    /**
     * 开户银行  collate utf8mb4_general_ci
     */
    @ApiModelProperty("开户银行  collate utf8mb4_general_ci")
    private String bankName;

    /**
     * 开户行号  collate utf8mb4_general_ci
     */
    @ApiModelProperty("开户行号  collate utf8mb4_general_ci")
    private String bankNumber;

    /**
     * 开户账号  collate utf8mb4_general_ci
     */
    @ApiModelProperty("开户账号  collate utf8mb4_general_ci")
    private String bankAccount;

    /**
     * 开户户名  collate utf8mb4_general_ci
     */
    @ApiModelProperty("开户户名  collate utf8mb4_general_ci")
    private String bankAccountName;

    /**
     * 报名方式  collate utf8mb4_general_ci
     */
    @ApiModelProperty("报名方式  collate utf8mb4_general_ci")
    private String signType;

    /**
     * 意向金说明  collate utf8mb4_general_ci
     */
    @ApiModelProperty("意向金说明  collate utf8mb4_general_ci")
    private String earnestDescr;

    /**
     * 发布媒介  collate utf8mb4_general_ci
     */
    @ApiModelProperty("发布媒介  collate utf8mb4_general_ci")
    private String pubMedium;

    /**
     * 争议解决  collate utf8mb4_general_ci
     */
    @ApiModelProperty("争议解决  collate utf8mb4_general_ci")
    private String disputeReso;

    /**
     * 集团举报渠道  collate utf8mb4_general_ci
     */
    @ApiModelProperty("集团举报渠道  collate utf8mb4_general_ci")
    private String groupTipOff;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private Long configVer;

    /**
     * 板块组织ID
     */
    @ApiModelProperty("板块组织ID")
    private Long organizationId;

    /**
     * 板块组织编码
     */
    @ApiModelProperty("板块组织编码")
    private String organizationCode;

    /**
     * 板块组织名称
     */
    @ApiModelProperty("板块组织名称")
    private String organizationName;

    /**
     * 公示负责人账号
     */
    @ApiModelProperty("公示负责人账号")
    private String chargeUserName;

    /**
     * 公示负责人名称
     */
    @ApiModelProperty("公示负责人名称")
    private String chargeFullName;
}
