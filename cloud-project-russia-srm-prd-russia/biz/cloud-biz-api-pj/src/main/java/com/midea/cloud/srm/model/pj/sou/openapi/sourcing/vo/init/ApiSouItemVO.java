package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemVO extends SouItem {

    @ApiModelProperty("阶梯价模板表")
    private List<SouItemLadder> ladderList;

    public static List<ApiSouItemVO> convertApiVO(List<SouItem> itemList,
                                                  List<SouItemLadder> ladderList) {
        if (itemList.isEmpty()) { return Collections.emptyList(); }
        List<ApiSouItemVO> voList = new ArrayList<>(itemList.size());

        Map<Long/* souItemId */, List<SouItemLadder>> ladderMap = ladderList.stream()
                .sorted(Comparator.comparing(SouItemLadder::getSortIndex))
                .collect(Collectors.groupingBy(SouItemLadder::getSouItemId));

        for (SouItem souItem : itemList) {
            ApiSouItemVO vo = SouObjectXUtil.convertTargetObj(souItem, ApiSouItemVO.class);
            voList.add(vo);

            vo.setLadderList(ladderMap.get(souItem.getSouItemId()));
        }
        return voList;
    }

}
