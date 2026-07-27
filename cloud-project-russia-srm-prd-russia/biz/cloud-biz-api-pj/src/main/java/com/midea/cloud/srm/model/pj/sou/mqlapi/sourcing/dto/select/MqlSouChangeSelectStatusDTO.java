package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouChangeSelectStatusItemDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * MQL - 中标/落标
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@ApiModel(description = "中标/落标")
@EqualsAndHashCode(callSuper = true)
public class MqlSouChangeSelectStatusDTO extends BaseObjectX {

    @ApiModelProperty("报价行信息")
    protected List<MqlSouChangeSelectStatusItemDTO> selects;
    @ApiModelProperty("true-中标/false-落标")
    protected Boolean toWin;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (CollectionUtils.isEmpty(selects)) {
            throw new IllegalArgumentException("请选择要中标/落标的数据");
        }
        if (!toWin) {
            selects.forEach(s -> s.setWinAmount(null));
        } else {
            Set<Long> orderItemIds = new HashSet<>(selects.size());
            Iterator<MqlSouChangeSelectStatusItemDTO> iterator = selects.iterator();
            while (iterator.hasNext()) {
                MqlSouChangeSelectStatusItemDTO item = iterator.next();
                if (!orderItemIds.add(item.getOrderItemId())) {
                    iterator.remove();
                } else {
                    if (item.getWinAmount() != null && item.getWinAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("中标供应商的中标数量必须大于0");
                    }
                }
            }
        }
    }

}
