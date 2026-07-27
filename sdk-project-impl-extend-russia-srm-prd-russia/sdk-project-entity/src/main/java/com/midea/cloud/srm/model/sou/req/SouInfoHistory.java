package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源单公示信息修改历史表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@ApiModel(description = "寻源单公示信息修改历史表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_info_history")
public class SouInfoHistory extends BaseEntity {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long infoHistoryId;

    /**
     * 寻源单ID
     */
    @ApiModelProperty("寻源单ID")
    private Long reqHeadId;

    /**
     * 项目名称(调整前)
     */
    @ApiModelProperty("项目名称(调整前)")
    private String beforeProjectName;

    /**
     * 项目概况与招标范围(调整前)
     */
    @ApiModelProperty("项目概况与招标范围(调整前)")
    private String beforeProjectScope;

    /**
     * 供应商资质要求(调整前)
     */
    @ApiModelProperty("供应商资质要求(调整前)")
    private String beforeVendorQualReq;

    /**
     * 技术要求(调整前)
     */
    @ApiModelProperty("技术要求(调整前)")
    private String beforeTechnicalReq;

    /**
     * 业绩要求(调整前)
     */
    @ApiModelProperty("业绩要求(调整前)")
    private String beforePerformanceReq;

    /**
     * 项目名称(调整后)
     */
    @ApiModelProperty("项目名称(调整后)")
    private String afterProjectName;

    /**
     * 项目概况与招标范围(调整后)
     */
    @ApiModelProperty("项目概况与招标范围(调整后)")
    private String afterProjectScope;

    /**
     * 供应商资质要求(调整后)
     */
    @ApiModelProperty("供应商资质要求(调整后)")
    private String afterVendorQualReq;

    /**
     * 技术要求(调整后)
     */
    @ApiModelProperty("技术要求(调整后)")
    private String afterTechnicalReq;

    /**
     * 业绩要求(调整后)
     */
    @ApiModelProperty("业绩要求(调整后)")
    private String afterPerformanceReq;

    /**
     * 归档原因
     */
    @ApiModelProperty("归档原因")
    private String updateReason;

    /**
     * 归档附件ID
     */
    @ApiModelProperty("归档附件ID")
    private Long archiveFileId;

    /**
     * 归档附件名称
     */
    @ApiModelProperty("归档附件名称")
    private String archiveFileName;

}
