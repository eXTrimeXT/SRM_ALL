package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 物料变更记录查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@ApiModel(description = "物料变更记录查询条件")
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemRecordQueryDTO extends BasePage {

    /** @see SouItemRecord#getProjectId */
    @ApiModelProperty("寻源单ID(必填)")
    private Long projectId;

    /** @see SouItemRecord#getRefreshType */
    @ApiModelProperty("刷新类型")
    private SouItemRefreshTypeEnum refreshType;

    /** @see SouItemRecord#getRefreshStatus */
    @ApiModelProperty("刷新状态")
    private SouItemRefreshStatusEnum refreshStatus;

    /** @see SouItemRecord#getItemId */
    @ApiModelProperty("物料ID")
    private Long itemId;

    /** @see SouItemRecord#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否仅查询最新的物料更新批次记录")
    private Boolean onlyLatest = true;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        itemDesc = StringUtils.trimToNull(itemDesc);
    }

}