package com.midea.cloud.srm.model.sou.purfixprice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purfix_price_head")
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceHead extends BaseEntity<ExtPurFixPriceHead> {

    @TableId("PUR_FIX_PRICE_HEAD_ID")
    @ApiModelProperty("ID")
    private Long purFixPriceHeadId;

    @TableField("FIX_PRICE_STATUS")
    @ApiModelProperty("单据状态")
    private ExtPurFixPriceStatusEnum fixPriceStatus;

    /** @see ExtPurInqSouProject#getProjectId */
    @TableField("SOU_PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long souProjectId;

    /** @see SouProject#getSouNo */
    @TableField("SOU_NO")
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see ExtPurInqSouProject#getDesignId */
    @TableField("DESIGN_ID")
    @ApiModelProperty("项目策划方案ID")
    private Long designId;

    /** @see ExtPurInqSouProject#getDesignProjectCode */
    @TableField("DESIGN_PROJECT_CODE")
    @ApiModelProperty("项目策划方案编码")
    private String designProjectCode;

    /** @see ExtPurInqSouProject#getDesignProjectName */
    @TableField("DESIGN_PROJECT_NAME")
    @ApiModelProperty("项目策划方案名称")
    private String designProjectName;

    /** @see ExtPurInqSouProject#getDesignNum */
    @TableField("DESIGN_NUM")
    @ApiModelProperty("项目策划轮数")
    private Integer designNum;

    /** @see ExtPurInqSouProject#getDesignArea */
    @TableField("DESIGN_AREA")
    @ApiModelProperty("项目策划供货范围")
    private String designArea;

    @TableField("DESIGN_CREATE_PHONE")
    @ApiModelProperty("项目策划创建人联系方式")
    private String designCreatePhone;

    /** @see ExtPurInqSouProject#getDesignProjIntroduce */
    @TableField("DESIGN_PROJ_INTRODUCE")
    @ApiModelProperty("项目策划介绍")
    private String designProjIntroduce;

    @TableField("EXECUTE_TIME_FROM")
    @ApiModelProperty("执行时间从")
    private Date executeTimeFrom;

    @TableField("EXECUTE_TIME_TO")
    @ApiModelProperty("执行时间从")
    private Date executeTimeTo;

    @TableField("CAN_SUBMIT")
    @ApiModelProperty("是否可提交")
    private Enable canSubmit;

    @TableField("CREATE_USER_ORG_OU_ID")
    @ApiModelProperty("创建人所属公司ID")
    private Long createUserOrgOuId;

    @TableField("CREATE_USER_ORG_OU_CODE")
    @ApiModelProperty("创建人所属公司编码")
    private String createUserOrgOuCode;

    @TableField("CREATE_USER_ORG_OU_NAME")
    @ApiModelProperty("创建人所属公司名称")
    private String createUserOrgOuName;

    @TableField("CREATE_USER_ORG_BU_ID")
    @ApiModelProperty("创建人所在板块ID")
    private Long createUserOrgBuId;

    @TableField("CREATE_USER_ORG_BU_CODE")
    @ApiModelProperty("创建人所在板块编码")
    private String createUserOrgBuCode;

    @TableField("CREATE_USER_ORG_BU_NAME")
    @ApiModelProperty("创建人所在板块名称")
    private String createUserOrgBuName;

    @TableField("CREATE_USER_DEPT_ID")
    @ApiModelProperty("创建人所在部门ID")
    private Long createUserDeptId;

    @TableField("CREATE_USER_DEPT_CODE")
    @ApiModelProperty("创建人所在部门编码")
    private String createUserDeptCode;

    @TableField("CREATE_USER_DEPT_NAME")
    @ApiModelProperty("创建人所在部门名称")
    private String createUserDeptName;

    @TableField("APPROVAL_SUBMIT_TIME")
    @ApiModelProperty("提交审批时间")
    private Date approvalSubmitTime;

    @TableField("APPROVAL_PASS_TIME")
    @ApiModelProperty("审批通过时间")
    private Date approvalPassTime;

    /**
     * bpm发起人账号
     */
    @TableField("START_BPM_USERNAME")
    @ApiModelProperty(value = "bpm发起人账号")
    private String startBpmUsername;

    /**
     * bpm发起人名称
     */
    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty(value = "bpm发起人名称")
    private String startBpmNickname;
}
