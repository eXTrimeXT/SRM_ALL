package com.midea.cloud.srm.model.sou.agreement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采协议组织信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_jc_agreement_org")
public class SccSouJcAgreementOrg extends BaseEntity<SccSouJcAgreementOrg> {

    @ApiModelProperty("协议采购组织id")
    @TableId("AGREEMENT_ORG_ID")
    private Long agreementOrgId;

    @ApiModelProperty("协议id")
    @TableField("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("采购组织id,组织类型为公司的全部组织")
    @TableField("BUY_ORG_ID")
    private Long buyOrgId;
    @ApiModelProperty("采购组编码")
    @TableField("BUY_ORG_CODE")
    private String buyOrgCode;
    @ApiModelProperty("采购组织名称")
    @TableField("BUY_ORG_NAME")
    private String buyOrgName;
}
