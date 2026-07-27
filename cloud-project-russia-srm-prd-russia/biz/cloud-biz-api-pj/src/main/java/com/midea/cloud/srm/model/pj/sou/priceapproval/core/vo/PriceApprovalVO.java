package com.midea.cloud.srm.model.pj.sou.priceapproval.core.vo;

import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 价格审批单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalVO extends PriceApproval {

    @ApiModelProperty("中标行信息")
    private List<PriceApprovalItemVO> itemList;

    @ApiModelProperty("附件信息")
    private List<PriceApprovalFile> fileList;

    public static PriceApprovalVO convertVO(PriceApproval priceApproval,
                                            List<PriceApprovalItem> approvalItemList,
                                            List<PriceApprovalItemLadder> approvalItemLadderList,
                                            List<PriceApprovalItemPayment> approvalItemPaymentList,
                                            List<PriceApprovalFile> approvalFileList,
                                            List<PriceApprovalItemFollow> followList) {
        PriceApprovalVO vo = SouObjectXUtil.convertTargetObj(priceApproval, PriceApprovalVO.class);
        vo.setFileList(approvalFileList);
        vo.setItemList(new ArrayList<>(approvalItemList.size())); {
            Map<Long/* approvalItemId */, List<PriceApprovalItemLadder>> itemLadderMap = approvalItemLadderList.stream()
                    .collect(Collectors.groupingBy(PriceApprovalItemLadder::getApprovalItemId));
            Map<Long/* approvalItemId */, List<PriceApprovalItemPayment>> itemPaymentMap = approvalItemPaymentList.stream()
                    .collect(Collectors.groupingBy(PriceApprovalItemPayment::getApprovalItemId));
            Map<Long/* approvalItemId */, List<PriceApprovalItemFollow>> followMap = followList.stream()
                    .sorted(Comparator.comparing(PriceApprovalItemFollow::getCreationDate))
                    .collect(Collectors.groupingBy(PriceApprovalItemFollow::getApprovalItemId));

            for (PriceApprovalItem approvalItem : approvalItemList) {
                PriceApprovalItemVO item = SouObjectXUtil.convertTargetObj(approvalItem, PriceApprovalItemVO.class);
                vo.getItemList().add(item);

                item.setLadderPriceList(itemLadderMap.get(approvalItem.getApprovalItemId()));
                item.setPaymentList(itemPaymentMap.get(approvalItem.getApprovalItemId()));
                item.setFollowList(followMap.get(approvalItem.getApprovalItemId()));
            }
        }
        return vo;
    }

}
