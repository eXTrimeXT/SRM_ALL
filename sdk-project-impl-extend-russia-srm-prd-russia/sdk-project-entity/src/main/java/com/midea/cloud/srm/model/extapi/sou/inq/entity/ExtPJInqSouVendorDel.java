package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长城 - 询比价 - 邀请供应商删除记录
 * @author huangbf3
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_npm_sou_inq_vendor_del")
@ApiModel("寻源供应商")
public class ExtPJInqSouVendorDel extends BaseEntity<ExtPJInqSouVendorDel> {

    @ApiModelProperty("ID")
    @TableId("SOU_VENDOR_DEL_ID")
    private Long souVendorDelId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;

    @ApiModelProperty("联系人名称")
    @TableField("LINKMAN_NAME")
    private String linkmanName;

    @ApiModelProperty("电话")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;

    @ApiModelProperty("数据来源")
    @TableField("SOURCE_FROM_TYPE")
    private ExtPjInqSouVendorSourceFromTypeEnum sourceFromType;

    @ApiModelProperty("删除原因")
    @TableField("DEL_REASON")
    private String delReason;

}
