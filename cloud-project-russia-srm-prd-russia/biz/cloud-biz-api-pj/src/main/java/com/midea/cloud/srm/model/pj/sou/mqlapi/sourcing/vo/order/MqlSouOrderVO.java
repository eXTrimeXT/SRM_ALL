package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderVO extends SouOrder {

    @ApiModelProperty("立项信息")
    private MqlSouProjectVO souInitInfo;
    @ApiModelProperty("物料需求")
    private List<MqlSouOrderItemVO> orderItemList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileVO> orderFileList;

    public static MqlSouOrderVO convertMqlVO(MqlSouProjectVO souInitInfo,
                                             List<SouItem> availableItems,
                                             @Nullable SouOrder order,
                                             List<SouOrderItem> orderItemList,
                                             List<SouOrderFile> orderFileList,
                                             List<SouOrderItemHis> ladderPriceList,
                                             boolean containsAllAvailableItems) {
        MqlSouOrderVO vo = new MqlSouOrderVO();
        vo.setProjectId(souInitInfo.getProjectId());
        vo.setSouInitInfo(souInitInfo);
        if (order != null) {
            SouObjectXUtil.mergeProperties(order, vo);
        }
        // 物料需求
        vo.setOrderItemList(new ArrayList<>(availableItems.size())); {
            Map<Long/* souItemId */, SouOrderItem> souOrderItemMap = orderItemList.stream()
                    .collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
            Map<Long/* orderItemId */, List<SouOrderItemHis>> ladderPriceMap = ladderPriceList.stream()
                    .sorted(Comparator.comparing(SouOrderItemHis::getOrder_round))
                    .collect(Collectors.groupingBy(SouOrderItemHis::getOrderItemId));
            Map<Long/* souItemId */, List<SouItemLadder>> ladderMap = souInitInfo.getItemList().stream()
                    .map(MqlSouItemVO::getLadderList)
                    .filter(CollectionUtils::isNotEmpty)
                    .collect(Collectors.toMap(e -> e.get(0).getSouItemId(), Function.identity()));
            Set<Long> availableItemIds = availableItems.stream().map(SouItem::getSouItemId).collect(Collectors.toSet());
            for (MqlSouItemVO souItem : souInitInfo.getItemList()) {
                if (!availableItemIds.contains(souItem.getSouItemId())) { continue; }
                MqlSouOrderItemVO itemVO = new MqlSouOrderItemVO(); {
                    itemVO.setSouItem(souItem);
                    itemVO.setProjectId(souItem.getProjectId());
                    itemVO.setSouItemId(souItem.getSouItemId());
                    itemVO.setItemId(souItem.getItemId());
                }
                SouOrderItem orderItem = souOrderItemMap.get(souItem.getSouItemId());
                if (containsAllAvailableItems) {
                    vo.getOrderItemList().add(itemVO);
                } else {
                    if (orderItem != null) {
                        vo.getOrderItemList().add(itemVO);
                    }
                }

                if (orderItem != null) {
                    SouObjectXUtil.mergeProperties(orderItem, itemVO);

                    List<SouOrderItemHis> ladderList = ladderPriceMap.get(orderItem.getOrderItemId());
                    if (ladderList != null) {
                        itemVO.setLadderPriceList(ladderPriceMap.get(orderItem.getOrderItemId()));
                    }
                } else {
                    List<SouItemLadder> ladderList = ladderMap.get(souItem.getSouItemId());
                    if (ladderList != null) {
                        itemVO.setLadderPriceList(SouObjectXUtil.convertTargetObj(ladderList, new TypeReference<List<SouOrderItemHis>>() {}));
                    }
                }
            }
        }
        // 报价附件
        vo.setOrderFileList(new ArrayList<>(orderFileList.size() + souInitInfo.getFileConfigList().size())); {
            Map<Long/* fileConfigId */, SouOrderFile> orderFileConfigMap = orderFileList.stream()
                    .filter(e -> e.getSouFileConfigId() != null)
                    .collect(Collectors.toMap(SouOrderFile::getSouFileConfigId, Function.identity()));
            for (SouFileConfig fileConfig : souInitInfo.getFileConfigList()) {
                MqlSouOrderFileVO fileVO = SouObjectXUtil.convertTargetObj(fileConfig, MqlSouOrderFileVO.class);
                vo.getOrderFileList().add(fileVO);

                SouOrderFile orderFile = orderFileConfigMap.get(fileConfig.getSouFileConfigId());
                if (orderFile != null) {
                    SouObjectXUtil.mergeProperties(orderFile, fileVO);
                }
            }

            for (SouOrderFile orderFile : orderFileList) {
                if (orderFile.getSouFileConfigId() != null ) { continue; }

                MqlSouOrderFileVO fileVO = SouObjectXUtil.convertTargetObj(orderFile, MqlSouOrderFileVO.class);
                vo.getOrderFileList().add(fileVO);
            }
        }

        return vo;
    }

}
