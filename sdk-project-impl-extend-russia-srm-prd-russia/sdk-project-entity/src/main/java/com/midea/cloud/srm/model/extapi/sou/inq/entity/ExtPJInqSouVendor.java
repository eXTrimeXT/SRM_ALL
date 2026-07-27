package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长城 - 询比价 - 邀请供应商
 * @author huangbf3
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_npm_sou_inq_vendor")
@ApiModel("寻源供应商")
public class ExtPJInqSouVendor extends BaseEntity<ExtPJInqSouVendor> {

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("ID")
    @TableId("SOU_VENDOR_ID")
    private Long souVendorId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("数据来源")
    @TableField("SOURCE_FROM_TYPE")
    private ExtPjInqSouVendorSourceFromTypeEnum sourceFromType;

    @TableField("NEW_VENDOR_TAG")
    @ApiModelProperty("是否新供应商(该字段用于发起新一轮场景)")
    private Enable newVendorTag;

}
