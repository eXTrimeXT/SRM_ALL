package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

/**
 * MQL - 入围/淘汰
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@ApiModel(description = "入围/淘汰")
@EqualsAndHashCode(callSuper = true)
public class MqlSouChangeWinStatusDTO extends BaseObjectX {

    @ApiModelProperty("报价行ID")
    protected Set<Long> orderItemIds;
    @ApiModelProperty("true-入围/false-淘汰")
    protected Boolean toWin;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (CollectionUtils.isEmpty(orderItemIds)) {
            throw new IllegalArgumentException("请选择要入围/淘汰的数据");
        }
    }

}
