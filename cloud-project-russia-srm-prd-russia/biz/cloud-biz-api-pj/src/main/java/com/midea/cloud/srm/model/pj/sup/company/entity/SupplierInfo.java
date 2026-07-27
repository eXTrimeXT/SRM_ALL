package com.midea.cloud.srm.model.pj.sup.company.entity;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@ApiOperation("供应商具体信息")
public class SupplierInfo {

    @ApiModelProperty("供应商编码")
    private String orgCode;

    @ApiModelProperty("供应商全称，唯一")
    private String fullName;

    @ApiModelProperty("供应商简称")
    private String shortName;

    @ApiModelProperty("社会信用代码/税号/身份证号，唯一")
    private String taxCode;

    @ApiModelProperty("供应商来源，枚举字段 COMMON：一般供应商，示例：COMMON")
    private String supSource;

    @ApiModelProperty("标题类型，枚举字段 COMPANY：公司，MEN：先生，MADAM：女士，示例：COMPANY")
    private String titleType;

    @ApiModelProperty("标题，枚举字段 COMPANY：公司，MEN：先生，MADAM：女士，示例：公司")
    private String title;

    @ApiModelProperty("标题英文名称")
    private String titleEn;

    @ApiModelProperty("供应商供货类型，枚举字段 PART：汽车零部件，OFFICE_SUPPLY：办公用品，示例：PART")
    private String supplyType;

    @ApiModelProperty("国家编码")
    private String countryCode;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("国家英文名称")
    private String countryEn;

    @ApiModelProperty("地区编码")
    private String area;

    @ApiModelProperty("地区名称")
    private String areaName;

    @ApiModelProperty("地区英文名称")
    private String areaNameEn;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("区域编码")
    private String region;

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("区域英文名称")
    private String regionNameEn;

    @ApiModelProperty("主营业务【字段类型建议设置为mediumtext】")
    private String supType;

    @ApiModelProperty("银行账户")
    private String bankAccount;

    @ApiModelProperty("账户组")
    private String accountGroup;

    @ApiModelProperty("贸易伙伴")
    private String tradePartner;

    @ApiModelProperty("法人代表")
    private String legalRepresent;

    @ApiModelProperty("证件有效期结束⽇期")
    private String endDate;

    @ApiModelProperty("证件有效期开始⽇期")
    private String startDate;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("申请人")
    private String applicant;

    @ApiModelProperty("申请⼈电话")
    private String applicantPhone;

    @ApiModelProperty("申请时间")
    private String appDate;

    @ApiModelProperty("创建时间，格式：yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiModelProperty("更新时间，格式：yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    @ApiModelProperty("公司代码")
    private String companyCode;


}
