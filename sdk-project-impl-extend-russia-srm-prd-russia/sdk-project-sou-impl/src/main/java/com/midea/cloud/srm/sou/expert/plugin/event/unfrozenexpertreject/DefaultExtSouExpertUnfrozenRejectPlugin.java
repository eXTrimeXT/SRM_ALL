package com.midea.cloud.srm.sou.expert.plugin.event.unfrozenexpertreject;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertFrozenStatusEnum;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertreject.ExtSouExpertUnfrozenRejectContext;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertreject.IExtSouExpertUnfrozenRejectPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 寻源 - 专家库 - 专家拒绝解冻插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertUnfrozenRejectPlugin implements IExtSouExpertUnfrozenRejectPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertUnfrozenRejectContext judgeUnfrozenRejectAuth(ExtSouExpertUnfrozenRejectContext context) {
        context.getParam().formatParams();

        ExtSouExpert expert = qlService.readByKey(ExtSouExpert.class.getSimpleName(), context.getExpert(), ExtSouExpert.class);
        AssertUtils.notNull(expert, "专家[{0}]不存在", context.getParam().getExpertId());
        AssertUtils.isTrue(Enable.N.equals(expert.getHasQuite()), "专家[{0}]已退出", context.getParam().getExpertId());
        if (expert.getFrozenStatus() != null) {
            switch (expert.getFrozenStatus()) {
                case "UNFROZEN_UN_CONFIRM":
                    // 解冻未确认
                case "FROZEN":
                    // 已冻结
                    break;
                case "FROZEN_UN_CONFIRM":
                    // 冻结未确认
                    throw new IllegalArgumentException("专家冻结处理中，禁止该操作");
                case "UNFROZEN":
                    // 已确认解冻
                    throw new IllegalArgumentException("专家已解冻，禁止拒绝解冻操作");
                default:
                    throw new IllegalArgumentException("无法识别的专家冻结状态，请自行处理:" + expert.getFrozenStatus());
            }
        } else {
            throw new IllegalArgumentException("专家尚未被冻结，禁止拒绝解冻操作");
        }

        context.setExpert(expert);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertUnfrozenRejectContext executeUnfrozenReject(ExtSouExpertUnfrozenRejectContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpert.class)
                .set(ExtSouExpert::getFrozenStatus, ExtSouExpertFrozenStatusEnum.FROZEN)
                .set(ExtSouExpert::getFrozenRejectReason, context.getParam().getFrozenRejectReason())
                .eq(ExtSouExpert::getExpertId, context.getParam().getExpertId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
