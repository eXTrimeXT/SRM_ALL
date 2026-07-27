package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purinq_vendor")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouVendor extends BaseEntity<ExtPurInqSouVendor> {

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("ID")
    @TableId("SOU_VENDOR_ID")
    private Long souVendorId;

    /** @see SouVendor#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouVendor#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("数据来源")
    @TableField("SOURCE_FROM_TYPE")
    private ExtPurInqSouVendorSourceFromTypeEnum sourceFromType;

    @TableField("NEW_VENDOR_TAG")
    @ApiModelProperty("是否新供应商(该字段用于发起新一轮场景)")
    private Enable newVendorTag;

}
