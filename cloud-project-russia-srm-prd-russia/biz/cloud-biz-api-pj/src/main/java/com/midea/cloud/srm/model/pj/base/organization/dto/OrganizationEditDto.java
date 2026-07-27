package com.midea.cloud.srm.model.pj.base.organization.dto;

import com.midea.cloud.srm.model.pj.base.organization.entity.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author huangbf3
 */
@Data
@Accessors
@ApiModel(description = "组织编辑DTO")
public class OrganizationEditDto implements Serializable {

    private static final long serialVersionUID = 6771121356344055666L;

    @ApiModelProperty(value = "组织")
    private Organization organization;

    @ApiModelProperty(value = "地址列表")
    private List<Site> siteList;

    @ApiModelProperty(value = "业务实体信息")
    private Organization ouInfo;

    @ApiModelProperty(value = "公司信息")
    private OrgCompany orgCompany;

    @ApiModelProperty(value = "账户信息")
    private List<OrgCompanyBank> orgCompanyBankList;

    @ApiModelProperty(value = "地址信息")
    private List<OrgCompanyAddress> orgCompanyAddressList;

    @ApiModelProperty(value = "联系人信息")
    private List<OrgCompanyPerson> orgCompanyPersonList;

    @ApiModelProperty(value = "开票信息")
    private List<OrgInvoiceInfo> orgInvoiceInfoList;

    @ApiModelProperty(value = "收票信息")
    private List<OrgCollectInfo> orgCollectInfoList;

    private String errorMsg;
}
