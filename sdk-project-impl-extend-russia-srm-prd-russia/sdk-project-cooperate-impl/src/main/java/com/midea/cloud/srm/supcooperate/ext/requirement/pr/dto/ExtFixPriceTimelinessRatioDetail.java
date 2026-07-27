package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author srm
 * @Description: 定价及时率明细表实体
 * @date 2024/7/5
 */
@Data
public class ExtFixPriceTimelinessRatioDetail {
    @ApiModelProperty("可下单数量")
    private Integer orderQuantity;
    @ApiModelProperty("采购单位")
    private String createUserOrgOuName;
    /**
     * 头表点击进入明细表之后
     * 需要携带采购员的工号 但是因为采购员工号也作为模糊查询的条件
     * 所以再加一个字段专门作为明细表查询的条件（实际上也是采购员工号只是字段不同
     * 实体类里表示为（jobNumber）
     * 查询明细表必须携带这个参数作为参数条件
     */
    @ApiModelProperty("采购员工号")
    private String jobNumber;
    @ApiModelProperty("采购员")
    private String createdBy;
    @ApiModelProperty("采购员姓名")
    private String createdFullName;
    @ApiModelProperty("采购申请单编号")
    private String requirementHeadNum;
    @ApiModelProperty("申请单位")
    private String orgName;
    @ApiModelProperty("使用部门")
    private String extUseDepartmentName;
    @ApiModelProperty("申请单审批日期")
    private Date extApproveTime;
    @ApiModelProperty("物资编码")
    private String materialCode;
    @ApiModelProperty("物资名称")
    private String materialName;
    @ApiModelProperty("规格型号")
    private String extMaterialModel;
    @ApiModelProperty("计量单位")
    private String unit;
    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("供应商")
    private String vendorName;
    @ApiModelProperty("需求日期")
    private Date requirementDate;
    @ApiModelProperty("申请数量")
    private Double requirementQuantity;
    @ApiModelProperty("申请关闭原因")
    private String extClosedCause;
    @ApiModelProperty("单据状态")
    private String auditStatus;
    @ApiModelProperty("行状态")
    private String extPoolStatus;
    @ApiModelProperty("需求类型")
    private String extBuyType;
    @ApiModelProperty("定价单编号")
    private String fixPriceNo;
    @ApiModelProperty("定价单提交日期")
    private Date creationDate;
    /**
     * 是否按时完成:定价单提交(creationDate)-需求审批日期(extApproveTime)<10
     */
    @ApiModelProperty("是否按时完成")
    private String onTime;

}
