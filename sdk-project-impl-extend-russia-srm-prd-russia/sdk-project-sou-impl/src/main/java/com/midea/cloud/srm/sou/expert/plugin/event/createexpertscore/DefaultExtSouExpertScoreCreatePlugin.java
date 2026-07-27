package com.midea.cloud.srm.sou.expert.plugin.event.createexpertscore;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreCreateDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.ExtSouExpertScoreCreateContext;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.IExtSouExpertScoreCreatePlugin;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.IExtSouExpertScoreCreateValidatePlugin;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 专家库 - 专家评审创建插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertScoreCreatePlugin implements IExtSouExpertScoreCreatePlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private RbacClient rbacClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertScoreCreateContext judgeCreateExpertScoreAuth(ExtSouExpertScoreCreateContext context) {
        AssertUtils.notEmpty(context.getParams(), "缺少数据");

        return context;
    }

    @Override
    @ApiOperation("数据处理")
    public ExtSouExpertScoreCreateContext prepareCreateExpertScore(ExtSouExpertScoreCreateContext context) {
        // 1: 查询专家信息
        Set<Long> expertUserIds = context.getParams().stream().map(ExtSouExpertScoreCreateDTO::getExpertUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!expertUserIds.isEmpty()) {
            Map<Long/* expertUserId */, ExtSouExpert> expertMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpert.class).in(ExtSouExpert::getExpertUserId, expertUserIds), ExtSouExpert.class)
                    .stream().collect(Collectors.toMap(ExtSouExpert::getExpertUserId, Function.identity()));
            context.setExpertMap(expertMap);
        }
        // 2: 查询用户信息
        Set<String> usernames = new HashSet<>(context.getParams().size() << 1); {
            for (ExtSouExpertScoreCreateDTO param : context.getParams()) {
                if (CollectionUtils.isNotEmpty(param.getScoreLineList())) {
                    for (ExtSouExpertScoreLine scoreLine : param.getScoreLineList()) {
                        if (scoreLine.getUsername() != null) { usernames.add(scoreLine.getUsername()); }
                    }
                }
            }
        }
        if (!usernames.isEmpty()) {
            context.setUserMap(rbacClient.getUserMapByNames(usernames));
        }
        // 3: 查询现有的指定寻源单相关的专家评审信息
        Set<Long> souProjectIds = context.getParams().stream().map(ExtSouExpertScoreCreateDTO::getSouProjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!souProjectIds.isEmpty()) {
            context.setExistScoreMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertScore.class)
                    .in(ExtSouExpertScore::getSouProjectId, souProjectIds), ExtSouExpertScore.class)
                    .stream().collect(Collectors.groupingBy(ExtSouExpertScore::getSouProjectId)));
        }

        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public ExtSouExpertScoreCreateContext beforeCreateExpertScore(ExtSouExpertScoreCreateContext context) {
        return SdkPluginProxy.proxy(IExtSouExpertScoreCreateValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertScoreCreateContext executeCreateExpertScore(ExtSouExpertScoreCreateContext context) {
        qlService.create(context.getExpertScoreEntityList());
        qlService.create(context.getExpertScoreLineEntityList());

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
