package com.midea.cloud.srm.sou.expert.plugin.event.createexpertscore;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreCreateDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.ExtSouExpertScoreCreateContext;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.IExtSouExpertScoreCreateValidatePlugin;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 寻源 - 专家库 - 专家评审创建校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Component
public class DefaultExtSouExpertScoreCreateValidatePlugin implements IExtSouExpertScoreCreateValidatePlugin {

    @Override
    @ApiModelProperty("校验及转化专家评审")
    public ExtSouExpertScoreCreateContext validateAndConvertExpertScore(ExtSouExpertScoreCreateContext context) {
        // 1: 数据校验
        List<ExtSouExpertScoreCreateDTO> params = context.getParams(); {
            AssertUtils.notEmpty(params, "缺少数据");
            Set<String/* souProjectId_expertUserId */> uniqueSet = new HashSet<>(params.size());
            for (ExtSouExpertScoreCreateDTO param : params) {
                // 1.1: ID
                param.setExpertScoreId(IdGenrator.generate());
                // 1.2: 寻源ID
                AssertUtils.notNull(param.getSouProjectId(), "缺少souProjectId参数");
                // 1.3: 寻源单号
                param.setSouNo(StringUtils.trimToNull(param.getSouNo()));
                AssertUtils.notNull(param.getSouNo(), "缺少souNo参数");
                // 1.4: 寻源标题
                param.setSouName(StringUtils.trimToNull(param.getSouName()));
                AssertUtils.notNull(param.getSouName(), "缺少souName参数");
                // 1.5: 项目所在地
                param.setProjectAddress(StringUtils.trimToNull(param.getProjectAddress()));
                if (param.getProjectAddress() != null) {
                    AssertUtils.isTrue(param.getProjectAddress().length() <= 255, "项目所在地输入长度不能超过255");
                }
                // 1.6: 概算金额(万元)(略)
                // 1.7: 专家库信息
                AssertUtils.notNull(param.getExpertUserId(), "缺少expertUserId(专家用户ID)");
                ExtSouExpert expert = context.getExpertMap().get(param.getExpertUserId());
                AssertUtils.notNull(expert, "专家[expertUserId={0}]不存在", param.getExpertUserId());
                param.setExpertId(expert.getExpertId());
                param.setExpertUserId(expert.getExpertUserId());
                param.setExpertUsername(expert.getExpertUsername());
                param.setExpertFullName(expert.getExpertFullName());
                // 1.8: 评价状态
                param.setScoreStatus(ExtSouExpertScoreStatusEnum.DRAFT.name());
                // 1.9: 评价结果(置空)
                param.setScoreResult(null);

                AssertUtils.isTrue(uniqueSet.add(param.getSouProjectId() + "_" + param.getExpertUserId()), "[{0}] + [{1}]不能重复",
                        param.getSouName(), param.getExpertUsername());

                List<ExtSouExpertScore> existScoreList = context.getExistScoreMap().get(param.getSouProjectId());
                if (CollectionUtils.isNotEmpty(existScoreList)) {
                    existScoreList.forEach(score -> AssertUtils.isFalse(score.getExpertUserId().equals(param.getExpertUserId()),
                            "[{0}] + [{1}]不能重复", param.getSouName(), param.getExpertUsername()));
                }
            }
        }
        // 2: 数据转化
        List<ExtSouExpertScore> entityList = new ArrayList<>(params.size()); {
            for (ExtSouExpertScoreCreateDTO param : params) {
                ExtSouExpertScore entity = SouObjectXUtil.convertTargetObj(param, ExtSouExpertScore.class);
                entityList.add(entity);

                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertScore.class.getSimpleName());

                SouObjectXUtil.mergeProperties(entity, param);
            }
        }

        context.setExpertScoreEntityList(entityList);
        return context;
    }

    @Override
    @ApiModelProperty("校验及转化专家评审详情")
    public ExtSouExpertScoreCreateContext validateAndConvertExpertScoreLines(ExtSouExpertScoreCreateContext context) {
        // 1: 数据校验
        List<ExtSouExpertScoreCreateDTO> params = context.getParams(); {
            AssertUtils.notEmpty(params, "缺少数据");
            for (ExtSouExpertScoreCreateDTO param : params) {
                AssertUtils.notEmpty(param.getScoreLineList(), "缺少scoreLineList数据");
                for (ExtSouExpertScoreLine scoreLine : param.getScoreLineList()) {
                    // 1.1: ID
                    scoreLine.setExpertScoreLineId(IdGenrator.generate());
                    // 1.2: 专家评审ID
                    scoreLine.setExpertScoreId(param.getExpertScoreId());
                    // 1.3: 用户信息
                    AssertUtils.notNull(scoreLine.getUsername(), "专家评审明细缺少username信息");
                    User user = context.getUserMap().get(scoreLine.getUsername());
                    AssertUtils.notNull(user, "用户[{0}]不存在", scoreLine.getUsername());
                    scoreLine.setUserId(user.getUserId());
                    scoreLine.setUsername(user.getUsername());
                    scoreLine.setNickname(user.getNickname());
                    // 1.4: 用户职责
                    AssertUtils.notNull(scoreLine.getGroupType(), "专家评审明细缺少groupType信息");
                    // 1.5: 评分
                    scoreLine.setScore(null);
                    // 1.6: 代理评分人员(略)
                }
            }
        }
        // 2: 数据转化
        List<ExtSouExpertScoreLine> entityList = new ArrayList<>(params.size() << 2); {
            for (ExtSouExpertScoreCreateDTO param : params) {
                for (ExtSouExpertScoreLine scoreLine : param.getScoreLineList()) {
                    ExtSouExpertScoreLine entity = SouObjectXUtil.convertTargetObj(scoreLine, ExtSouExpertScoreLine.class);
                    entityList.add(entity);
                }
            }
        }

        context.setExpertScoreLineEntityList(entityList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
