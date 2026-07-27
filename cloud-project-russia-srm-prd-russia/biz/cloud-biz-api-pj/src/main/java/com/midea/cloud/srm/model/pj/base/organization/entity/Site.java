package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 *
 * <pre>
 *  地点 模型
 * </pre>
 *
 * @author xiejj44@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2021/10/22 14:45
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_site")
@ApiModel(description = "地点")
@QlMatchType("Site")
public class Site extends BaseEntity {

    private static final long serialVersionUID = -1740072639491692912L;

    @ApiModelProperty(value = "地址主键ID")
    @TableId("SITE_ID")
    private Long siteId;

    @ApiModelProperty(value = "地点")
    @TableField("SITE_NAME")
    private String siteName;

    @ApiModelProperty(value = "地点说明")
    @TableField("SITE_DESC")
    private String siteDesc;

    @ApiModelProperty(value = "地点类型")
    @TableField("SITE_TYPE")
    private String siteType;

    @ApiModelProperty(value = "组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;

    @ApiModelProperty(value = "组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty(value = "状态（Y：有效，N：无效）")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "联系人")
    @TableField("RECEIVER")
    private String receiver;

    @ApiModelProperty(value = "联系人电话")
    @TableField("RECEIVER_PHONE")
    private String receiverPhone;

    @ApiModelProperty(value = "ERP同步ID")
    @TableField("ERP_SITE_ID")
    private Long erpSiteId;

    @ApiModelProperty(value = "国家")
    @TableField("COUNTRY_CODE")
    private String countryCode;

    @ApiModelProperty(value = "省份")
    @TableField("PROVINCE_CODE")
    private String provinceCode;

    @ApiModelProperty(value = "城市")
    @TableField("CITY_CODE")
    private String cityCode;

    /*@ApiModelProperty(value = "城市辖区")
    @TableField(exist = false)
    private String areaCode;*/

    @ApiModelProperty(value = "地址区域")
    @TableField("ADDRESS_REGION")
    private String addressRegion;

    @ApiModelProperty(value = "是否默认(Y-是,N-否)")
    @TableField("IS_DEFAULT")
    private String isDefault;

}
