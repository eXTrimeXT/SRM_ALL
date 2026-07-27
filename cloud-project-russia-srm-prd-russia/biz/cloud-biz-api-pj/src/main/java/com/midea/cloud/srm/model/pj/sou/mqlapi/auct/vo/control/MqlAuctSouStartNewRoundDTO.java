package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control.MqlSouStartNewRoundDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 竞价 MQL - 发起新一轮
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/21
 */
@Data
@ApiModel(description = "发起新一轮参数")
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouStartNewRoundDTO extends MqlSouStartNewRoundDTO {

    @ApiModelProperty("物料信息处理")
    private List<AuctSouItem> auctSouItemList;

    @Override
    public void formatParams() {
        super.formatParams();
        if (CollectionUtils.isNotEmpty(auctSouItemList)) {
            auctSouItemList.forEach(souItem -> {
                if (souItem.getSouItemId() == null) {
                    throw new IllegalArgumentException("缺少souItemList.souItemId参数");
                }
            });
        }
    }

}
