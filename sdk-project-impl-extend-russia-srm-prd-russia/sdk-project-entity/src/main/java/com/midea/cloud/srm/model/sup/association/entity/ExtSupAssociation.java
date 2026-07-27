package com.midea.cloud.srm.model.sup.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@TableName("ceea_sup_vendor_association")
public class ExtSupAssociation extends BaseEntity {

    @TableId("ASSOCIATION_ID")
    @ApiModelProperty("ID")
    private Long associationId;

    @TableField("VENDOR_ID_A")
    @ApiModelProperty("A供应商ID")
    private Long vendorIdA;

    @TableField("VENDOR_CODE_A")
    @ApiModelProperty("A供应商编码")
    private String vendorCodeA;

    @TableField("VENDOR_NAME_A")
    @ApiModelProperty("A供应商名称")
    private String vendorNameA;

    @TableField("VENDOR_ID_B")
    @ApiModelProperty("B供应商ID")
    private Long vendorIdB;

    @TableField("VENDOR_CODE_B")
    @ApiModelProperty("B供应商CODE")
    private String vendorCodeB;

    @TableField("VENDOR_NAME_B")
    @ApiModelProperty("B供应商NAME")
    private String vendorNameB;

    @TableField("ASSOCIATION_TYPE")
    @ApiModelProperty("类型")
    private String associationType;

    @TableField("ASSOCIATION_REMARK")
    @ApiModelProperty("关联关系备注")
    private String associationRemark;

    @ApiModelProperty("供应商A唯一社会信用代码")
    private String socialCreditCodeA;

    @ApiModelProperty("供应商B唯一社会信用代码")
    private String socialCreditCodeB;
}
