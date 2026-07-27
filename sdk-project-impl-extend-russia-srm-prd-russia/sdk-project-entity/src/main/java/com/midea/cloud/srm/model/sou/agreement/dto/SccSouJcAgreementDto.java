package com.midea.cloud.srm.model.sou.agreement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementOrg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SccSouJcAgreementDto extends SccSouJcAgreement {

    /** 操作开始时间 */
    private Date operatorStartDate;

    /** 操作结束时间 */
    private Date operatorEndDate;

    /** 采购组织 */
    private List<Long> orgIds;

    private Long materialId;
    /**
     * 物料编码
     */
    private String materialCode;
    private String materialName;
    private String unit;

    /** 新增组织 */
    private List<SccSouJcAgreementOrg> sccSouJcAgreementOrgList;

    /** 规格 */
    private String standards;

    /**
     * 组织名称
     */
    private String orgNames;

    /**
     * 集采ids
     */
    private List<Long> agreementIds;

    /**
     * 排序
     */
    private String orderByStr;

    /**
     * 采购组织
     */
    private Long buyOrgId;

    @ApiModelProperty("供应区域")
    private String supplyArea;
}
