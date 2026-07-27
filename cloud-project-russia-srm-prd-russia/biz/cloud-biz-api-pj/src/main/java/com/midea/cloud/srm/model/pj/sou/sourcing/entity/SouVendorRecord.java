package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouVendorTypeHandler;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouVendorAddStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 - 追加供应商记录
 * PS: 未进行物料需求变更前，不记录任何数据。
 * PS: 全量记录，新增/现有保持/删除
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@ApiModel(description = "寻源核心 - 追加供应商记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "scc_sou_vendor_record", autoResultMap = true)
public class SouVendorRecord extends BaseEntity<SouVendorRecord> {

    @TableId("RECORD_ID")
    @ApiModelProperty("ID")
    private Long recordId;

    /** @see SouProject#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("LINKMAN_NAME")
    @ApiModelProperty("联系人名称")
    private String linkmanName;

    @TableField("PHONE")
    @ApiModelProperty("电话")
    private String phone;

    @TableField("EMAIL")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("ADD_STATUS")
    @ApiModelProperty("追加状态")
    private SouVendorAddStatusEnum addStatus;

    @TableField("EXECUTE_ROUND")
    @ApiModelProperty("执行的轮次")
    private Integer executeRound;

    @TableField(value = "VENDOR_INFO", typeHandler = SouVendorTypeHandler.class)
    @ApiModelProperty("供应商信息")
    private SouVendor vendorInfo;

}
