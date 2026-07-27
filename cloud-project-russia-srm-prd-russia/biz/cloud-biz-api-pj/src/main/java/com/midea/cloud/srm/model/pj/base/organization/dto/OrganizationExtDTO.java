package com.midea.cloud.srm.model.pj.base.organization.dto;

import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.base.organization.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author huangbf3
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrganizationExtDTO extends BaseEntity {

    @ApiModelProperty(value = "公司信息")
    private OrgCompany orgCompany;
    @ApiModelProperty(value = "联系人信息")
    private List<OrgCompanyPerson> companyPersonList;
    @ApiModelProperty(value = "地址信息")
    private List<OrgCompanyAddress> companyAddressList;
    @ApiModelProperty(value = "账户信息")
    private List<OrgCompanyBank> companyBankList;
    @ApiModelProperty(value = "地址列表")
    private List<Site> siteList;
}
