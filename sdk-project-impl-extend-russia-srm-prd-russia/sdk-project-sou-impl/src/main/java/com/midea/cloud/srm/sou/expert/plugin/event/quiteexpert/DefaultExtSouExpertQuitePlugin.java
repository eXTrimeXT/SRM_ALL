package com.midea.cloud.srm.sou.expert.plugin.event.quiteexpert;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.sou.expert.spi.event.quiteexpert.ExtSouExpertQuiteContext;
import com.midea.cloud.srm.sou.expert.spi.event.quiteexpert.IExtSouExpertQuitePlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 寻源 - 专家库 - 专家退出插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertQuitePlugin implements IExtSouExpertQuitePlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertQuiteContext judgeQuiteExpertAuth(ExtSouExpertQuiteContext context) {
        context.getParam().formatParams();

        ExtSouExpert expert = qlService.readByKey(ExtSouExpert.class.getSimpleName(), context.getParam().getExpertId(), ExtSouExpert.class);
        AssertUtils.notNull(expert, "专家[{0}]不存在", context.getParam().getExpertId());

        context.setExpert(expert);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertQuiteContext executeQuiteExpert(ExtSouExpertQuiteContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpert.class)
                .set(ExtSouExpert::getHasQuite, Enable.Y)
                .set(ExtSouExpert::getQuiteReason, context.getParam().getQuiteReason())
                .eq(ExtSouExpert::getExpertId, context.getParam().getExpertId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
