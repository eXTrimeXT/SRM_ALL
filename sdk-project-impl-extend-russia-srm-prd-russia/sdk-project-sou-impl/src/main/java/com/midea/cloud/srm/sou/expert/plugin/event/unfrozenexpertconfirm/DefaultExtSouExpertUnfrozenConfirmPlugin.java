package com.midea.cloud.srm.sou.expert.plugin.event.unfrozenexpertconfirm;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertFrozenStatusEnum;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertconfirm.ExtSouExpertUnfrozenConfirmContext;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertconfirm.IExtSouExpertUnfrozenConfirmPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 寻源 - 专家库 - 专家确认解冻插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertUnfrozenConfirmPlugin implements IExtSouExpertUnfrozenConfirmPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertUnfrozenConfirmContext judgeUnfrozenConfirmAuth(ExtSouExpertUnfrozenConfirmContext context) {
        ExtSouExpert expert = qlService.readByKey(ExtSouExpert.class.getSimpleName(), context.getExpertId(), ExtSouExpert.class);
        AssertUtils.notNull(expert, "专家[{0}]不存在", context.getExpertId());
        AssertUtils.isTrue(Enable.N.equals(expert.getHasQuite()), "专家[{0}]已退出", context.getExpertId());
        if (expert.getFrozenStatus() != null) {
            switch (expert.getFrozenStatus()) {
                case "UNFROZEN":
                    // 已确认解冻
                case "UNFROZEN_UN_CONFIRM":
                    // 解冻未确认
                    break;
                case "FROZEN":
                    // 已冻结
                    throw new IllegalArgumentException("专家已冻结，禁止解冻操作");
                case "FROZEN_UN_CONFIRM":
                    // 冻结未确认
                    throw new IllegalArgumentException("专家冻结处理中，禁止解冻操作");
                default:
                    throw new IllegalArgumentException("无法识别的专家冻结状态，请自行处理:" + expert.getFrozenStatus());
            }
        }

        context.setExpert(expert);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertUnfrozenConfirmContext executeUnfrozenConfirm(ExtSouExpertUnfrozenConfirmContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpert.class)
                .set(ExtSouExpert::getFrozenStatus, ExtSouExpertFrozenStatusEnum.UNFROZEN)
                .set(ExtSouExpert::getHasFrozen, Enable.N)
                .set(ExtSouExpert::getFrozenRejectReason, null)
                .eq(ExtSouExpert::getExpertId, context.getExpertId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
