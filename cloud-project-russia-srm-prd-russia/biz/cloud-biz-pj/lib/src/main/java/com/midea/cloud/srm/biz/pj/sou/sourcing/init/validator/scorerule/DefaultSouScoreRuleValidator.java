package com.midea.cloud.srm.biz.pj.sou.sourcing.init.validator.scorerule;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 寻源核心 - 评分规则 - 校验实现(产品默认)
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/22
 */
@Service
public class DefaultSouScoreRuleValidator extends AbstractSouScoreRuleValidator {

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
