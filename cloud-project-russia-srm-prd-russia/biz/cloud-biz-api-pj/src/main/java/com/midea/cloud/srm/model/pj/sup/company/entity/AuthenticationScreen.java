package com.midea.cloud.srm.model.pj.sup.company.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_sup_company_info")
@ApiModel(description = "供应商相关")
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@QlMatchType("AuthenticationScreen")
public class AuthenticationScreen extends BaseEntity{


        @TableId
        @ApiModelProperty("id")
        private Long companyId;

        /** 对应营业执照号  BUSINESS_LICENSE_NO */
        @ApiModelProperty("唯一编号（证件号）")
        private String businessLicenseNo;

        /** 对应企业名称 COMPANY_NAME */
        @ApiModelProperty("公司全称")
        private String companyName;

        /** 对应企业CODE COMPANY_CODE */
        @ApiModelProperty("企业CODE")
        private String companyCode;


        /** 对应 企业名称英文 COMPANY_EN_NAME */
        @ApiModelProperty("英文全称")
        private String companyEnName;

        /** 对应 国家SAP代码  SAP_AREA_CODE COMPANY_COUNTRY */
        @ApiModelProperty("注册国家代码")
        private String companyCountry;

        /** 对应详细地址  COMPANY_ADDRESS */
        @ApiModelProperty("注册地址")
        private String companyAddress;

        /** 对应法定代表人  LEGAL_PERSON */
        @ApiModelProperty("法定代表人/负责人姓名")
        private String legalPerson;

        @ApiModelProperty("锲约验证")
        private String contractVerification;

        @ApiModelProperty("营业执照号")
        private String lcCode;

        @ApiModelProperty("身份证号")
        private String idNumber;

        @ApiModelProperty("是否黑名单")
        private String isBacklist;

        @ApiModelProperty("黑名单更新人")
        private String backlistUpdatedBy;

        @ApiModelProperty("黑名单更新时间")
        private Date backlistUpdatedDate;

        @ApiModelProperty("重点关注")
        private String focusFlag;

        @ApiModelProperty("黑名单生效日期")
        private String blackListEffectiveDate;

}
