package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order;

import com.alibaba.fastjson.TypeReference;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 供应商报价信息查看
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiSouInitDetailVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private SouOrder order;
    @ApiModelProperty("物料需求")
    private List<ApiSouOrderItemVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

    public static ApiSouOrderDetailVO convertApiVO(ApiSouInitDetailVO souInitInfo,
                                                   List<SouItem> availableItems,
                                                   @Nullable SouOrder order,
                                                   List<SouOrderItem> orderItemList,
                                                   List<SouOrderFile> orderFileList,
                                                   List<SouOrderItemHis> ladderPriceList) {
        ApiSouOrderDetailVO vo = new ApiSouOrderDetailVO();
        vo.setInitInfo(souInitInfo);
        vo.setOrder(order);
        // 物料需求
        vo.setItemList(new ArrayList<>(availableItems.size())); {
            Map<Long/* souItemId */, SouOrderItem> souOrderItemMap = orderItemList.stream()
                    .collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
            Set<Long> availableItemIds = availableItems.stream().map(SouItem::getSouItemId).collect(Collectors.toSet());
            for (ApiSouItemVO souItem : souInitInfo.getRequireInfo()) {
                if (!availableItemIds.contains(souItem.getSouItemId())) { continue; }
                ApiSouOrderItemVO itemVO = SouObjectXUtil.convertTargetObj(souItem, ApiSouOrderItemVO.class);
                vo.getItemList().add(itemVO);

                SouOrderItem orderItem = souOrderItemMap.get(souItem.getSouItemId());
                if (orderItem != null) {
                    //置空本次报价
                    orderItem.setOrderNowPrice(null);
                    itemVO.setOrderNowPrice(null);
                    SouObjectXUtil.mergeProperties(orderItem, itemVO);
                }
            }
        }
        // 报价附件
        vo.setOrderFileList(new ArrayList<>(orderFileList.size() + souInitInfo.getProjectInfo().getFileConfigList().size())); {
            Map<Long/* fileConfigId */, SouOrderFile> orderFileConfigMap = orderFileList.stream()
                    .filter(e -> e.getSouFileConfigId() != null)
                    .collect(Collectors.toMap(SouOrderFile::getSouFileConfigId, Function.identity()));
            for (SouFileConfig fileConfig : souInitInfo.getProjectInfo().getFileConfigList()) {
                ApiSouOrderFileVO fileVO = SouObjectXUtil.convertTargetObj(fileConfig, ApiSouOrderFileVO.class);
                vo.getOrderFileList().add(fileVO);

                SouOrderFile orderFile = orderFileConfigMap.get(fileConfig.getSouFileConfigId());
                if (orderFile != null) {
                    SouObjectXUtil.mergeProperties(orderFile, fileVO);
                }
            }

            for (SouOrderFile orderFile : orderFileList) {
                if (orderFile.getSouFileConfigId() != null ) { continue; }

                ApiSouOrderFileVO fileVO = SouObjectXUtil.convertTargetObj(orderFile, ApiSouOrderFileVO.class);
                vo.getOrderFileList().add(fileVO);
            }
        }

        return vo;
    }

}
