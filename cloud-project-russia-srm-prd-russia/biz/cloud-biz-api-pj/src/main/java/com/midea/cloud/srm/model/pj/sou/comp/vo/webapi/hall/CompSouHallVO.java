package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall.CompSouHallItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 竞价大厅
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouHallVO extends ApiCompSouProjectVO {

    @ApiModelProperty("物料需求行信息")
    private List<CompSouHallItemVO> itemList;

    public static CompSouHallVO convertCompVO(SouProject souProject,
                                              CompSouProject compProject,
                                              List<SouItem> itemList,
                                              List<CompSouItem> compItemList,
                                              List<CompSouItemHis> compItemHisList,
                                              List<SouOrderItem> orderItemList) {
        CompSouHallVO vo = new CompSouHallVO();

        // 基本信息
        BeanUtils.copyProperties(souProject, vo);
        BeanUtils.copyProperties(compProject, vo);

        // 物料需求
        vo.setItemList(new ArrayList<>(itemList.size())); {
            Map<Long/* souItemId */, CompSouItem> compItemMap = compItemList.stream()
                    .collect(Collectors.toMap(CompSouItem::getSouItemId, Function.identity()));
            Map<Long/* souItemId */, CompSouItemHis> compItemHisMap = compItemHisList.stream()
                    .collect(Collectors.toMap(CompSouItemHis::getSouItemId, Function.identity()));
            Map<Long/* souItemId */, List<SouOrderItem>> orderItemMap = orderItemList.stream()
                    .filter(e -> SouOrderStatusEnum.SUBMISSION.equals(e.getOrderStatus()))
                    .collect(Collectors.groupingBy(SouOrderItem::getSouItemId));
            for (SouItem souItem : itemList) {
                CompSouHallItemVO itemVO = new CompSouHallItemVO();
                vo.getItemList().add(itemVO);

                BeanUtils.copyProperties(souItem, itemVO);
                BeanUtils.copyProperties(compItemMap.get(souItem.getSouItemId()), itemVO);
                BeanUtils.copyProperties(compItemHisMap.get(souItem.getSouItemId()), itemVO);

                List<SouOrderItem> orderItems = orderItemMap.get(souItem.getSouItemId());
                itemVO.setCompVendorCount(orderItems != null ? orderItems.size() : 0);
            }
        }

        return vo;
    }

}
