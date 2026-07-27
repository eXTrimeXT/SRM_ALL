package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源核心 - 物料维度报价模板导出参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderItemQuoteTempDownloadDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    @ApiModelProperty("轮次(为空则默认为最新轮次)")
    private Integer round;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否下载所有数据")
    private Boolean downloadAll = false;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        if (souItemId == null) {
            throw new IllegalArgumentException("缺少souItemId参数");
        }
        souType = StringUtils.trimToNull(souType);
        if (souType == null) {
            throw new IllegalArgumentException("缺少souType参数");
        }
    }

}
