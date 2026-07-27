package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "简易询价头信息")
public class ExtInqSouProject extends BaseEntity<ExtInqSouProject> {

    @TableField("EXT_EXCLUDE_ORG_LIMIT_VENDORS")
    @ApiModelProperty("排序非本业务受限实体供应商")
    private Enable extExcludeOrgLimitVendors;

    @TableField("EXT_VENDOR_PERFORMANCE_RANK")
    @ApiModelProperty("供应商绩效前几名")
    private Integer extVendorPerformanceRank;

    @TableField("EXT_IS_RANDOM")
    @ApiModelProperty("是否随机")
    private Enable extIsRandom;

    @TableField("EXT_DEPARTMENT_ID")
    @ApiModelProperty("部门ID")
    private Long extDepartmentId;

    @TableField("EXT_DEPARTMENT_NAME")
    @ApiModelProperty("部门名称")
    private String extDepartmentName;

    @TableField("CREATE_USER_ORG_OU_ID")
    @ApiModelProperty("创建人所在公司ID")
    private Long createUserOrgOuId;

    @TableField("CREATE_USER_ORG_OU_CODE")
    @ApiModelProperty("创建人所在公司编码")
    private String createUserOrgOuCode;

    @TableField("CREATE_USER_ORG_OU_NAME")
    @ApiModelProperty("创建人所在公司名称")
    private String createUserOrgOuName;

    @TableField("CREATE_USER_ORG_BU_ID")
    @ApiModelProperty("创建人所在板块ID")
    private Long createUserOrgBuId;

    @TableField("CREATE_USER_ORG_BU_CODE")
    @ApiModelProperty("创建人所在板块编码")
    private String createUserOrgBuCode;

    @TableField("CREATE_USER_ORG_BU_NAME")
    @ApiModelProperty("创建人所在板块名称")
    private String createUserOrgBuName;

}
