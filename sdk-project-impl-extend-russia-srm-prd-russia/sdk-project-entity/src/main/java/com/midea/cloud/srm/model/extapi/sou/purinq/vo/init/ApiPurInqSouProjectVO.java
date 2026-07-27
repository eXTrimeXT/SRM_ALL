package com.midea.cloud.srm.model.extapi.sou.purinq.vo.init;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm 集采询比价 - 询价单基本信息
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouProjectVO extends SouProject {

    /** @see ExtPurInqSouProject#getExtProjectStatus */
    @ApiModelProperty("寻源状态")
    private ExtPurInqSouProjectStatusEnum extProjectStatus;

    /** @see ExtPurInqSouProject#getOrgDeptId */
    @ApiModelProperty("采购申请部门ID")
    private Long orgDeptId;

    /** @see ExtPurInqSouProject#getOrgDeptCode */
    @ApiModelProperty("采购申请部门编码")
    private String orgDeptCode;

    /** @see ExtPurInqSouProject#getOrgDeptName */
    @ApiModelProperty("采购申请部门名称")
    private String orgDeptName;

    /** @see ExtPurInqSouProject#getDesignId */
    @ApiModelProperty("项目策划方案ID")
    private Long designId;

    /** @see ExtPurInqSouProject#getDesignProjectCode */
    @ApiModelProperty("项目策划方案编码")
    private String designProjectCode;

    /** @see ExtPurInqSouProject#getDesignProjectName */
    @ApiModelProperty("项目策划方案名称")
    private String designProjectName;

    /** @see ExtPurInqSouProject#getDesignNum */
    @ApiModelProperty("项目策划轮数")
    private Integer designNum;

    /** @see ExtPurInqSouProject#getDesignCreateUsername */
    @ApiModelProperty("项目策划创建人账号")
    private String designCreateUsername;

    /** @see ExtPurInqSouProject#getDesignCreateNickName */
    @ApiModelProperty("项目策划创建人昵称")
    private String designCreateNickName;

    /** @see ExtPurInqSouProject#getDesignCreatePhone */
    @ApiModelProperty("项目曾创建人联系方式")
    private String designCreatePhone;

    /** @see ExtPurInqSouProject#getDesignOrgDeptId */
    @ApiModelProperty("项目策划部门ID")
    private Long designOrgDeptId;

    /** @see ExtPurInqSouProject#getDesignOrgDeptCode */
    @ApiModelProperty("项目策划部门编码")
    private String designOrgDeptCode;

    /** @see ExtPurInqSouProject#getDesignOrgDeptName */
    @ApiModelProperty("项目策划部门名称")
    private String designOrgDeptName;

    /** @see ExtPurInqSouProject#getDesignProjMoney */
    @ApiModelProperty("项目策划项目金额(万元)")
    private String designProjMoney;

    /** @see ExtPurInqSouProject#getDesignArea */
    @ApiModelProperty("项目策划供货区域")
    private String designArea;

    /** @see ExtPurInqSouProject#getDesignProjIntroduce */
    @ApiModelProperty("项目策划介绍")
    private String designProjIntroduce;

    /** @see ExtPurInqSouProject#getDesignPricingIdeas */
    @ApiModelProperty("项目策划定价思路")
    private String designPricingIdeas;

    /** @see ExtPurInqSouProject#getAdjustCode */
    @ApiModelProperty("调价申请单编码")
    private String adjustCode;

    /** @see ExtPurInqSouProject#getAdjustName */
    @ApiModelProperty("调价申请单名称")
    private String adjustName;

}
