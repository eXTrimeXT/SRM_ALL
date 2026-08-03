package com.midea.cloud.srm.biz.pj.sou.sourcing.init.judge;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 寻源 - 评分规则 - 接口权限/条件控制
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/30
 */
@Service
public class DefaultSouScoreRuleJudge extends AbstractSouScoreRuleJudge {

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
