package com.midea.cloud.srm.sou.expert.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertLatestApplyQueryDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertQueryDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.*;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertQueryService;
import com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo.ExtSouExpertGetApplyInfoContext;
import com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo.IExtSouExpertGetApplyInfoPlugin;
import com.midea.cloud.srm.sou.expert.spi.query.getlatestapplyinfo.ExtSouExpertGetLatestApplyInfoContext;
import com.midea.cloud.srm.sou.expert.spi.query.getlatestapplyinfo.IExtSouExpertGetLatestApplyInfoPlugin;
import com.midea.cloud.srm.sou.expert.spi.query.queryexperts.ExtSouExpertQueryContext;
import com.midea.cloud.srm.sou.expert.spi.query.queryexperts.IExtSouExpertQueryPlugin;
import com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores.ExtSouExpertScoreQueryContext;
import com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores.IExtSouExpertScoreQueryPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专家库 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Slf4j
@Service
public class ExtSouExpertQueryServiceImpl implements ExtSouExpertQueryService {

    /**
     * 查询专家申请详情
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Nullable
    @Override
    public ExtSouExpertApplyVO getApplyInfoByApplyId(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertGetApplyInfoContext context = new ExtSouExpertGetApplyInfoContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertGetApplyInfoPlugin.class, context).judgeGetApplyInfoAuth(context);
        if (context.getExpertApply() == null) { return null; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetApplyInfoPlugin.class, context).beforeGetApplyInfo(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetApplyInfoPlugin.class, context).executeGetApplyInfo(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetApplyInfoPlugin.class, context).afterGetApplyInfo(context);

        return context.getResult();
    }

    /**
     * 根据用户ID查询最新的专家申请详情
     */
    @Nullable
    @Override
    public ExtSouExpertApplyVO getLatestApplyInfoByUserId(ExtSouExpertLatestApplyQueryDTO param) {
        // 1: 初始化上下文
        ExtSouExpertGetLatestApplyInfoContext context = new ExtSouExpertGetLatestApplyInfoContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertGetLatestApplyInfoPlugin.class, context).judgeGetLatestApplyInfoAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetLatestApplyInfoPlugin.class, context).beforeGetLatestApplyInfo(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetLatestApplyInfoPlugin.class, context).executeGetLatestApplyInfo(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGetLatestApplyInfoPlugin.class, context).afterGetLatestApplyInfo(context);

        return context.getResult();
    }

    /**
     * 专家库列表查询
     */
    @Override
    public List<ExtSouExpertQueryVO> queryExperts(ExtSouExpertQueryDTO queryParam) {
        // 1: 初始化上下文
        ExtSouExpertQueryContext context = new ExtSouExpertQueryContext(queryParam);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertQueryPlugin.class, context).judgeQueryExpertsAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertQueryPlugin.class, context).beforeQueryExperts(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertQueryPlugin.class, context).executeQueryExperts(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IExtSouExpertQueryPlugin.class, context).afterQueryExperts(context);

        return context.getResult();
    }

    /**
     * 专家评审列表查询
     */
    @Override
    public List<ExtSouExpertScoreQueryVO> queryExpertScores(ExtSouExpertScoreQueryDTO queryParam) {
        // 1: 初始化上下文
        ExtSouExpertScoreQueryContext context = new ExtSouExpertScoreQueryContext(queryParam);
        // 2: 校验操作条件/权限
        log.info("queryExpertScores-judgeQueryExpertScoresAuth");
        context = SdkPluginProxy.proxy(IExtSouExpertScoreQueryPlugin.class, context).judgeQueryExpertScoresAuth(context);
        // 3: 前置处理
        log.info("queryExpertScores-beforeQueryExpertScores");
        context = SdkPluginProxy.proxy(IExtSouExpertScoreQueryPlugin.class, context).beforeQueryExpertScores(context);
        // 4: 执行处理
        log.info("queryExpertScores-executeQueryExpertScores");
        context = SdkPluginProxy.proxy(IExtSouExpertScoreQueryPlugin.class, context).executeQueryExpertScores(context);
        // 5: 后置处理
        log.info("queryExpertScores-afterQueryExpertScores");
        context = SdkPluginProxy.proxy(IExtSouExpertScoreQueryPlugin.class, context).afterQueryExpertScores(context);

        return context.getResult();
    }

}
