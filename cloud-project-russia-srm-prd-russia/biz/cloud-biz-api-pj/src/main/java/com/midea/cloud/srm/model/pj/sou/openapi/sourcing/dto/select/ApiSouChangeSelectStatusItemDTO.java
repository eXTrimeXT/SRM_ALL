package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.baomidou.mybatisplus.annotation.TableField;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源openAPI - 中标/落标明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeSelectStatusItemDTO extends BaseObjectX {

    @ApiModelProperty("结果行ID")
    private Long orderResultId;

    @ApiModelProperty("评选备注")
    private String orderRemark;

    @ApiModelProperty("评选备注")
    private String selectRemark;

    @ApiModelProperty("中标通知备注")
    private String winNoticeRemark;

    @ApiModelProperty("是否中标")
    private String selectStatus;

    @ApiModelProperty(value = "中标原因")
    private String winReason;

    @ApiModelProperty(value = "流标原因")
    private String failureReason;

    @ApiModelProperty(value = "是否流标")
    private String failureBidFlag;

    @ApiModelProperty(value = "中标供应商ID")
    private Long winVendorId;

    @ApiModelProperty(value = "中标供应商")
    private String winVendorName;



}
