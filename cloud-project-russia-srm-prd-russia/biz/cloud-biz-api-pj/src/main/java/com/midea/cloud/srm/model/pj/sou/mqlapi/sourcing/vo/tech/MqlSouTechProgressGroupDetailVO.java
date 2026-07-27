package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechProgressGroupDetailItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechVendorOrderVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MQL - 评委对供应商的评分详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechProgressGroupDetailVO extends com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechVendorOrderVO {

    @ApiModelProperty("技术评分头信息")
    private SouTechScoreHead techScoreHead;
    @ApiModelProperty("评分详情")
    private List<MqlSouTechProgressGroupDetailItemVO> scoreDetailList;

    /**
     * 便捷方法
     */
    public static MqlSouTechProgressGroupDetailVO convertApiVO(SouVendor vendor,
                                                               CompanyInfo companyInfo,
                                                               List<SouFileConfig> techFileConfigList,
                                                               List<SouOrderFile> orderFileList,
                                                               List<SouScoreRuleLine> scoreRuleTempLineList,
                                                               @Nullable SouTechScoreHead techScoreHead,
                                                               List<SouTechScoreLine> techScoreLineList) {
        com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechVendorOrderVO tempVO = MqlSouTechVendorOrderVO.convertApiVO(vendor, companyInfo, techFileConfigList, orderFileList);

        MqlSouTechProgressGroupDetailVO vo = SouObjectXUtil.convertTargetObj(tempVO, MqlSouTechProgressGroupDetailVO.class);
        // 技术评分头信息
        vo.techScoreHead = techScoreHead;
        // 技术评分详情
        vo.scoreDetailList = doConvertSouScoreDetail(scoreRuleTempLineList, techScoreLineList);

        return vo;
    }

    private static List<MqlSouTechProgressGroupDetailItemVO> doConvertSouScoreDetail(List<SouScoreRuleLine> scoreRuleTempLineList,
                                                                                     List<SouTechScoreLine> techScoreLineList) {
        if (scoreRuleTempLineList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* ruleLineId */, SouTechScoreLine> techScoreLineMap = techScoreLineList.stream()
                .collect(Collectors.toMap(SouTechScoreLine::getScoreRuleLineId, Function.identity()));

        List<MqlSouTechProgressGroupDetailItemVO> voList = new ArrayList<>(scoreRuleTempLineList.size());
        MqlSouTechProgressGroupDetailItemVO vo;
        SouTechScoreLine techScoreLine;
        for (SouScoreRuleLine scoreRuleTempLine : scoreRuleTempLineList) {
            vo = new MqlSouTechProgressGroupDetailItemVO();
            voList.add(vo);
            techScoreLine = techScoreLineMap.get(scoreRuleTempLine.getScoreRuleLineId());

            // 评分规则模板行ID
            vo.setScoreRuleLineId(scoreRuleTempLine.getScoreRuleLineId());
            // 评分项
            vo.setScoreItem(scoreRuleTempLine.getScoreItem());
            // 评分标准
            vo.setScoreStandard(scoreRuleTempLine.getScoreStandard());
            // 权重
            vo.setScoreWeight(scoreRuleTempLine.getScoreWeight());
            // 满分值
            vo.setTotalScore(scoreRuleTempLine.getTotalScore());
            // 分数
            vo.setScore(techScoreLine != null ? techScoreLine.getScore() : null);
        }

        return voList;
    }

}
