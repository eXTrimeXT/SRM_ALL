package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.init.MqlBidSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouItemVO extends MqlBidSouItemDTO {

    public static List<MqlBidSouItemVO> convertBidVO(List<SouItem> souItemList,
                                                     List<BidSouItem> bidItemList) {
        if (bidItemList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* souItemId */, BidSouItem> bidItemMap = bidItemList.stream()
                .collect(Collectors.toMap(BidSouItem::getSouItemId, Function.identity()));

        List<MqlBidSouItemVO> voList = new ArrayList<>(bidItemList.size());
        for (SouItem souItem : souItemList) {
            MqlBidSouItemVO vo = new MqlBidSouItemVO();
            voList.add(vo);

            BeanUtils.copyProperties(souItem, vo);

            vo.setBidSouItem(bidItemMap.get(souItem.getSouItemId()));
        }
        return voList;
    }

}
