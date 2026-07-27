package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/5
 */
@Data
@ApiModel("采购需求行表EMD附件同步记录表实体类")
public class SccPrReqEdmattachSyncEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long edmattachId;
    /**
     * 采购需求头表ID
     */
    @ApiModelProperty("采购需求头表ID")
    private Long requirementLineId;
    /**
     * 附件ID
     */
    @ApiModelProperty("附件ID")
    private Long extAttachId;
    /**
     * 附件名称
     */
    @ApiModelProperty("附件名称")
    private String extAttachName;
    /**
     * 同步状态
     */
    @ApiModelProperty("同步状态")
    private String syncStatus;
    /**
     * 同步信息
     */
    @ApiModelProperty("同步信息")
    private String syncMessage;

}
