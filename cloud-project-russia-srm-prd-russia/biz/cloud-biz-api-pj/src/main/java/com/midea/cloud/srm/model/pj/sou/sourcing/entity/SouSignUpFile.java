package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 - 供应商报名附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_sign_up_file")
@ApiModel("寻源核心-供应商报名附件")
public class SouSignUpFile extends BaseEntity<SouSignUpFile> {

    @ApiModelProperty("ID")
    @TableId("SIGN_UP_FILE_ID")
    private Long signUpFileId;

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("供应商表ID")
    @TableField("SOU_VENDOR_ID")
    private Long souVendorId;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouFile#getSouFileId */
    @ApiModelProperty("关联报名附件要求ID")
    @TableField("SOU_FILE_ID")
    private Long souFileId;

    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty("文件ID")
    @TableField("SIGN_UP_DOC_ID")
    private Long signUpDocId;

    @ApiModelProperty("文件名称")
    @TableField("SIGN_UP_FILE_NAME")
    private String signUpFileName;

    @ApiModelProperty("备注")
    @TableField("SIGN_UP_REMARK")
    private String signUpRemark;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
