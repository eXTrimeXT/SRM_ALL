package com.midea.cloud.srm.biz.pj.sourcing.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 竞价管理——查询DTO
 * </pre>
 *
 * @author yipeng@meicloud.com
 * @version 1.00.00
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "竞价管理DTO")
public class BiddingQueryDTO extends BaseDTO {

    /** 竞价单号 */
    @ApiModelProperty(value = "竞价单号")
    private String bidNo;

    /** 竞价状态 */
    @ApiModelProperty(value = "竞价状态")
    private String bidStatus;

    /** 审核状态 */
    @ApiModelProperty(value = "审核状态")
    private String auditStatus;

    @ApiModelProperty(value = "发布人")
    private String publisher;

}
