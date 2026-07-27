package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressDetailQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 技术标评标进度
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechProgressQueryVO extends BaseObjectX {

    @ApiModelProperty("需评审供应商数量")
    private Integer needReviewVendorCount;
    @ApiModelProperty("已评审供应商数量")
    private Integer alreadyReviewVendorCount;
    @ApiModelProperty("评分进度信息")
    private List<ApiSouTechProgressDetailQueryVO> techProgressList;

    public static ApiSouTechProgressQueryVO convertApiVO(List<SouGroup> groupList,
                                                         List<SouVendor> vendorList,
                                                         List<SouOrder> orderList,
                                                         List<SouTechScoreHead> techScoreHeadList,
                                                         @Nullable SouTechScoreStatusEnum scoreStatus) {
        ApiSouTechProgressQueryVO vo = new ApiSouTechProgressQueryVO();
        // 技术评分信息
        vo.techProgressList = new ArrayList<>(vendorList.size());

        Map<Long/* vendorId */, SouOrder> orderMap = orderList.stream().collect(Collectors.toMap(SouOrder::getVendorId, Function.identity()));
        Map<String/* vendorId_groupId */, SouTechScoreHead> techScoreHeadMap = techScoreHeadList.stream()
                .collect(Collectors.toMap(e -> e.getVendorId() + "_" + e.getGroupId(), Function.identity()));
        SouTechScoreHead techScoreHead;
        boolean hasReviewed;
        ApiSouTechProgressDetailQueryVO techProgress;
        BigDecimal totalScore;
        for (SouVendor vendor : vendorList) {
            hasReviewed = true;
            techProgress = new ApiSouTechProgressDetailQueryVO();
            vo.techProgressList.add(techProgress);
            techProgress.setVendorId(vendor.getVendorId());
            techProgress.setVendorName(vendor.getVendorName());
            totalScore = BigDecimal.ZERO;

            for (SouGroup group : groupList) {
                techScoreHead = techScoreHeadMap.get(vendor.getVendorId() + "_" + group.getGroupId());
                if (techScoreHead == null || SouTechScoreStatusEnum.UNFINISHED.equals(techScoreHead.getScoreStatus())) {
                    hasReviewed = false;
                    break;
                } else {
                    hasReviewed = true;
                    totalScore = totalScore.add(techScoreHead.getTotalScore());
                }
            }
            if (hasReviewed) {
                techProgress.setScoreStatus(SouTechScoreStatusEnum.FINISHED);
                techProgress.setTechScore(totalScore.divide(new BigDecimal(groupList.size()), 2, RoundingMode.HALF_UP));
            } else {
                techProgress.setScoreStatus(SouTechScoreStatusEnum.UNFINISHED);
                techProgress.setTechScore(null);
            }

            SouOrder order = orderMap.get(vendor.getVendorId());
            techProgress.setOrderId(order.getOrderId());
            techProgress.setOrderNo(order.getOrderNo());
        }

        if (scoreStatus != null) {
            vo.techProgressList = vo.techProgressList.stream()
                    .filter(e -> e.getScoreStatus().equals(scoreStatus))
                    .collect(Collectors.toList());
        }

        // 需评审供应商数量
        vo.needReviewVendorCount = vo.techProgressList.size();
        // 已评审供应商数量
        vo.alreadyReviewVendorCount = (int) vo.techProgressList.stream()
                .filter(e -> e.getScoreStatus().equals(SouTechScoreStatusEnum.FINISHED))
                .count();

        return vo;
    }

}
