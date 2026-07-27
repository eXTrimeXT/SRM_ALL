package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApprovalFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 价格审批单 - 附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/18
 */
@Data
@ApiModel("价格审批单-附件")
@TableName("scc_price_approval_file")
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalFile extends ExtPriceApprovalFile {

    @TableId("APPROVAL_FILE_ID")
    @ApiModelProperty("ID")
    private Long approvalFileId;

    /** @see PriceApprovalItem#getApprovalId */
    @TableField("APPROVAL_ID")
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID(非必填)")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码(非必填)")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称(非必填)")
    private String vendorName;

    @TableField("FILE_DOC_ID")
    @ApiModelProperty("文件ID")
    private Long fileDocId;

    @TableField("FILE_NAME")
    @ApiModelProperty("文件名称")
    private String fileName;

    @TableField("REMARK")
    @ApiModelProperty("备注")
    private String remark;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
