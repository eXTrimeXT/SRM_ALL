package com.midea.cloud.srm.sou.expert.plugin.event.expertscore;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertDoScoreDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreGroupTypeEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreStatusEnum;
import com.midea.cloud.srm.sou.expert.spi.event.expertscore.ExtSouExpertDoScoreContext;
import com.midea.cloud.srm.sou.expert.spi.event.expertscore.IExtSouExpertDoScorePlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 专家库 - 专家评分插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertDoScorePlugin implements IExtSouExpertDoScorePlugin {

    @Autowired
    private QlService qlService;

    private static final BigDecimal B_100 = new BigDecimal(100);

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertDoScoreContext judgeDoScoreAuth(ExtSouExpertDoScoreContext context) {
        ExtSouExpertDoScoreDTO.formatParams(context.getParams());

        Set<Long> expertScoreLineIds = context.getParams().stream().map(ExtSouExpertDoScoreDTO::getExpertScoreLineId).collect(Collectors.toSet());

        Map<Long/* expertScoreLineId */, ExtSouExpertScoreLine> scoreLineMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertScoreLine.class)
                .in(ExtSouExpertScoreLine::getExpertScoreLineId, expertScoreLineIds), ExtSouExpertScoreLine.class)
                .stream().collect(Collectors.toMap(ExtSouExpertScoreLine::getExpertScoreLineId, Function.identity()));
        long expertScoreId = new ArrayList<>(scoreLineMap.values()).get(0).getExpertScoreId();
        boolean isOneExpertScore = scoreLineMap.values().stream().allMatch(e -> expertScoreId == e.getExpertScoreId());
        AssertUtils.isTrue(isOneExpertScore, "不能同时操作多个单据");
        ExtSouExpertScore expertScore = qlService.readByKey(ExtSouExpertScore.class.getSimpleName(), expertScoreId, ExtSouExpertScore.class);
        context.setExpertScore(expertScore);
        context.setExpertScoreLineList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertScoreLine.class)
                .eq(ExtSouExpertScoreLine::getExpertScoreId, expertScoreId), ExtSouExpertScoreLine.class));

        for (ExtSouExpertDoScoreDTO param : context.getParams()) {
            AssertUtils.isTrue(scoreLineMap.containsKey(param.getExpertScoreLineId()), "expertScoreLineId[{0}]不存在", param.getExpertScoreLineId());
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertDoScoreContext executeDoScore(ExtSouExpertDoScoreContext context) {
        Map<Long/* expertScoreLineId */, ExtSouExpertDoScoreDTO> dtoMap = context.getParams().stream()
                .collect(Collectors.toMap(ExtSouExpertDoScoreDTO::getExpertScoreLineId, Function.identity()));

        for (ExtSouExpertScoreLine scoreLine : context.getExpertScoreLineList()) {
            ExtSouExpertDoScoreDTO dto = dtoMap.get(scoreLine.getExpertScoreLineId());
            if (dto == null) {continue;}

            if (dto.getCurrentUserId() != null) {
                boolean canScore;
                if (scoreLine.getProxyUserId() != null) {
                    canScore = scoreLine.getProxyUserId().equals(dto.getCurrentUserId());
                } else {
                    canScore = scoreLine.getUserId().equals(dto.getCurrentUserId());
                }
                AssertUtils.isTrue(canScore, "当前用户没有[{0}]评分人的评价打分权限", scoreLine.getNickname());
            }
            AssertUtils.notNull(dto.getScore(), "缺少评分");
            AssertUtils.isTrue(dto.getScore().compareTo(BigDecimal.ZERO) >= 0 && dto.getScore().compareTo(B_100) <= 0, "评分必须是0~100");
            scoreLine.setScore(dto.getScore());
        }

        qlService.updateByWrapper(QlWrappers.update(ExtSouExpertScore.class)
                .set(ExtSouExpertScore::getScoreTime, new Date())
                .eq(ExtSouExpertScore::getExpertScoreId, context.getExpertScoreLineList().get(0).getExpertScoreId()));

        boolean hasAllScored = context.getExpertScoreLineList().stream().allMatch(e -> e.getScore() != null);
        if (hasAllScored) {
            BigDecimal finalScore = BigDecimal.ZERO;
            if (Enable.Y.equals(context.getExpertScore().getScoreForLeader())) {
                // 评分对象为组长(负责人70% + 其他人30%)
                BigDecimal managerScore = BigDecimal.ZERO;
                BigDecimal otherScore = BigDecimal.ZERO;
                int otherScoreCount = 0;
                for (ExtSouExpertScoreLine scoreLine : context.getExpertScoreLineList()) {
                    if (ExtSouExpertScoreGroupTypeEnum.SOU_MANAGER.name().equals(scoreLine.getGroupType())) {
                        // 负责人 一般只有一个人
                        managerScore = managerScore.add(scoreLine.getScore());
                    } else {
                        otherScoreCount++;
                        otherScore = otherScore.add(scoreLine.getScore());
                    }
                }
                finalScore = managerScore.multiply(new BigDecimal("0.7")).add(otherScoreCount > 0 ?
                        otherScore.divide(new BigDecimal(otherScoreCount), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("0.3"))
                        : BigDecimal.ZERO);
            } else {
                // 评分对象不是组长(负责人 + 组长 平均分)
                for (ExtSouExpertScoreLine scoreLine : context.getExpertScoreLineList()) {
                    finalScore = finalScore.add(scoreLine.getScore());
                }
                finalScore = finalScore.divide(new BigDecimal(context.getExpertScoreLineList().size()), 2, RoundingMode.HALF_UP);
            }
            qlService.updateByWrapper(QlWrappers.update(ExtSouExpertScore.class)
                    .set(ExtSouExpertScore::getScoreStatus, ExtSouExpertScoreStatusEnum.FINISH)
                    .set(ExtSouExpertScore::getScoreResult, finalScore)
                    .eq(ExtSouExpertScore::getExpertScoreId, context.getExpertScoreLineList().get(0).getExpertScoreId()));
        } else {
            String scoreStatus; {
                if (ExtSouExpertScoreGroupTypeEnum.SOU_MANAGER.name().equals(context.getExpertScoreLineList().get(0).getGroupType())) {
                    scoreStatus = ExtSouExpertScoreStatusEnum.SOU_MANAGER.name();
                } else {
                    // 评分组长，可以进行多个打分（代理打分），需要全部打完后，才能标记为组长打分完成
                    boolean isLeaderScoreOk = context.getExpertScoreLineList().stream()
                            .filter(e -> ExtSouExpertScoreGroupTypeEnum.SOU_LEADER.name().equals(e.getGroupType()))
                            .allMatch(e -> e.getScore() != null);
                    scoreStatus = isLeaderScoreOk ? ExtSouExpertScoreStatusEnum.SOU_LEADER.name() : context.getExpertScore().getScoreStatus();
                }
            }
            if (scoreStatus != null) {
                qlService.updateByWrapper(QlWrappers.update(ExtSouExpertScore.class)
                        .set(ExtSouExpertScore::getScoreStatus, scoreStatus)
                        .eq(ExtSouExpertScore::getExpertScoreId, context.getExpertScoreLineList().get(0).getExpertScoreId()));
            }
        }

        qlService.update(context.getExpertScoreLineList());

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
