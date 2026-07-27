package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemVO extends MqlSouItemDTO {

    public static List<MqlSouItemVO> convertMqlVO(List<SouItem> itemList,
                                                  List<SouItemLadder> ladderList) {
        if (itemList.isEmpty()) { return Collections.emptyList(); }
        List<MqlSouItemVO> voList = new ArrayList<>(itemList.size());

        Map<Long/* souItemId */, List<SouItemLadder>> ladderMap = ladderList.stream()
                .sorted(Comparator.comparing(SouItemLadder::getSortIndex))
                .collect(Collectors.groupingBy(SouItemLadder::getSouItemId));

        for (SouItem souItem : itemList) {
            MqlSouItemVO vo = SouObjectXUtil.convertTargetObj(souItem, MqlSouItemVO.class);
            voList.add(vo);

            vo.setLadderList(ladderMap.get(souItem.getSouItemId()));
        }
        return voList;
    }

}
