package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDesignPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purinq_project")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouProject extends BaseEntity<ExtPurInqSouProject> {
    public static final String EXT_SEQ_SOU_PURINQ_NO = "EXT_SEQ_SOU_PURINQ_NO";
    public static final String EXT_SEQ_SOU_PURINQ_ORDER_NO = "EXT_SEQ_SOU_PURINQ_ORDER_NO";

    /** @see SouProject#getProjectId */
    @TableId("PROJECT_ID")
    @ApiModelProperty("ID")
    private Long projectId;

    @TableField("EXT_PROJECT_STATUS")
    @ApiModelProperty("寻源状态")
    private ExtPurInqSouProjectStatusEnum extProjectStatus;

    @TableField("ORG_DEPT_ID")
    @ApiModelProperty("采购申请部门ID")
    private Long orgDeptId;

    @TableField("ORG_DEPT_CODE")
    @ApiModelProperty("采购申请部门编码")
    private String orgDeptCode;

    @TableField("ORG_DEPT_NAME")
    @ApiModelProperty("采购申请部门名称")
    private String orgDeptName;

    /** @see SccSouChDesignPlan#getDesignId */
    @TableField("DESIGN_ID")
    @ApiModelProperty("项目策划方案ID")
    private Long designId;

    /** @see SccSouChDesignPlan#getProjectCode */
    @TableField("DESIGN_PROJECT_CODE")
    @ApiModelProperty("项目策划方案编码")
    private String designProjectCode;

    /** @see SccSouChDesignPlan#getProjectName */
    @TableField("DESIGN_PROJECT_NAME")
    @ApiModelProperty("项目策划方案名称")
    private String designProjectName;

    /** @see SccSouChDesignPlan#getNum */
    @TableField("DESIGN_NUM")
    @ApiModelProperty("项目策划轮数")
    private Integer designNum;

    /** @see SccSouChDesignPlan#getCreatedBy */
    @TableField("DESIGN_CREATE_USERNAME")
    @ApiModelProperty("项目策划创建人账号")
    private String designCreateUsername;

    /** @see SccSouChDesignPlan#getCreatedFullName */
    @TableField("DESIGN_CREATE_NICK_NAME")
    @ApiModelProperty("项目策划创建人昵称")
    private String designCreateNickName;

    @TableField("DESIGN_CREATE_PHONE")
    @ApiModelProperty("项目策划创建人联系方式")
    private String designCreatePhone;

    @TableField("DESIGN_ORG_DEPT_ID")
    @ApiModelProperty("项目策划部门ID")
    private Long designOrgDeptId;

    @TableField("DESIGN_ORG_DEPT_CODE")
    @ApiModelProperty("项目策划部门编码")
    private String designOrgDeptCode;

    @TableField("DESIGN_ORG_DEPT_NAME")
    @ApiModelProperty("项目策划部门名称")
    private String designOrgDeptName;

    @TableField("DESIGN_PROJ_MONEY")
    @ApiModelProperty("项目策划项目金额(万元)")
    private String designProjMoney;

    @ApiModelProperty("项目策划供货区域")
    @TableField("DESIGN_AREA")
    private String designArea;

    /** @see SccSouChDesignPlan#getProjIntroduce */
    @TableField("DESIGN_PROJ_INTRODUCE")
    @ApiModelProperty("项目策划介绍")
    private String designProjIntroduce;

    /** @see SccSouChDesignPlan#getPricingIdeas */
    @TableField("DESIGN_PRICING_IDEAS")
    @ApiModelProperty("项目策划定价思路")
    private String designPricingIdeas;

    @TableField("HAS_FIX_PRICE")
    @ApiModelProperty("是否已定价")
    private Enable hasFixPrice;

    @TableField("ADJUST_CODE")
    @ApiModelProperty("调价申请单编码")
    private String adjustCode;

    @TableField("ADJUST_NAME")
    @ApiModelProperty("调价申请单名称")
    private String adjustName;

    @TableField("CREATE_USER_ORG_OU_ID")
    @ApiModelProperty("创建人所在公司ID")
    private Long createUserOrgOuId;

    @TableField("CREATE_USER_ORG_OU_CODE")
    @ApiModelProperty("创建人所在公司编码")
    private String createUserOrgOuCode;

    @TableField("CREATE_USER_ORG_OU_NAME")
    @ApiModelProperty("创建人所在公司名称")
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

}
