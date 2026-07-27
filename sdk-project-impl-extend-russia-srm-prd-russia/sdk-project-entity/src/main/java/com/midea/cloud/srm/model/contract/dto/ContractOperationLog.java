package com.midea.cloud.srm.model.contract.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作记录
 * @author 100014336
 */
@Data
public class ContractOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("类型-操作Id")
    private Long operationLogId;

    @ApiModelProperty("合同头信息ID")
    private Long contractHeadId;

    @ApiModelProperty("操作类型")
    private String operationType;

    @ApiModelProperty("操作描述")
    private String operationDesc;

    @ApiModelProperty("创建人ID")
    private Long createdId;

    @ApiModelProperty("创建人")
    private String createdBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime creationDate;

    @ApiModelProperty("创建人IP")
    private String createdByIp;

    @ApiModelProperty("创建人姓名")
    private String createdFullName;

    @ApiModelProperty("最后更新人ID")
    private Long lastUpdatedId;

    @ApiModelProperty("更新人")
    private String lastUpdatedBy;

    @ApiModelProperty("最后更新时间")
    private LocalDateTime lastUpdateDate;

    @ApiModelProperty("最后更新人IP")
    private String lastUpdatedByIp;

    @ApiModelProperty("最后更新人姓名")
    private String lastUpdatedFullName;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("版本号")
    private Long version;


}
