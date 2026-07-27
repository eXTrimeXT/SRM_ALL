package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ex_liuxy46
 */
@Data
public class EdmPrLineDto implements Serializable {

    /** 物料编码 */
    private String categoryCode;
    /** 需求数量 */
    private String orderQuantity;
    /** 使用部门 */
    private String extUseDepartmentCode;
    /** 使用人 */
    private String extUserCode;
    /** 使用人联系方式 */
    private String extUserPhone;
    /** 参考价 */
    private String extPredictPrice;
    /** 币种：默认人民币 */
    private String aaa;
    /** 备注 */
    private String comments;
    /** 用途 */
    private String extUseTo;
    /** 品牌 */
    private String brand;
    /** 附件 */
    private String extAttachName;
    /** 费用科目 */
    private String extFeeSubject;

    /** 本次需求时间 */
    private String requirementDate;

    /** 租户ID */
    private String tenantId;
    /** 组织ID */
    private String edmOrgId;
    /** 外部行ID */
    private String externalId;

}
