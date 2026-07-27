package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "提报策划方案-基础信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_design_plan")
public class SccSouChDesignPlan extends BaseEntity<SccSouChDesignPlan> {

    @ApiModelProperty("提报策划方案id")
    @TableId("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("项目编号")
    @TableField("PROJECT_CODE")
    private String projectCode;

    @ApiModelProperty("项目id")
    @TableField("PROJECT_ID")
    private Long projectId;
    @ApiModelProperty("项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty("轮数")
    @TableField("NUM")
    private Integer num;

    @ApiModelProperty("联系方式")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty("部门id")
    @TableField("DEP_ID")
    private Long depId;
    @ApiModelProperty("部门编码")
    @TableField("DEP_CODE")
    private String depCode;
    @ApiModelProperty("部门名称")
    @TableField("DEP_NAME")
    private String depName;

    @ApiModelProperty("项目金额（万元）")
    @TableField("PROJ_MONEY")
    private String projMoney;

    @ApiModelProperty("供货区域")
    @TableField("AREA")
    private String area;

    @ApiModelProperty("状态：拟定、审核中、审核完成、审核不通过")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty("项目介绍")
    @TableField("PROJ_INTRODUCE")
    private String projIntroduce;

    @ApiModelProperty("定价思路")
    @TableField("PRICING_IDEAS")
    private String pricingIdeas;

    @ApiModelProperty("创建时间到")
    @TableField(exist = false)
    private String createDateEnd;

    @TableField("HAS_CREATE_PUR_INQ")
    @ApiModelProperty("是否已创建集采询比价")
    private Enable hasCreatePurInq;

    @TableField("SOU_NO")
    @ApiModelProperty("集采询比价单号")
    private String souNo;

    @TableField("ORG_BU_ID")
    @ApiModelProperty("所属板块ID")
    private Long orgBuId;
    @TableField("ORG_BU_CODE")
    @ApiModelProperty("所属板块编码")
    private String orgBuCode;
    @TableField("ORG_BU_NAME")
    @ApiModelProperty("所属板块名称")
    private String orgBuName;

    @TableField("ORG_ID")
    @ApiModelProperty("所属公司id")
    private Long orgId;
    @TableField("ORG_CODE")
    @ApiModelProperty("所属公司编码")
    private String orgCode;
    @TableField("ORG_NAME")
    @ApiModelProperty("所属公司名称")
    private String orgName;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;

}
