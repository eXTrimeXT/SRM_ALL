package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolQueryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo.IPrSouRequirementGetCancelInfoPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo.PrSouRequirementGetCancelInfoContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.querysouprpools.IPrSouRequirementPoolQueryPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.querysouprpools.PrSouRequirementPoolQueryContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 招标计划池 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Component
public class PrSouRequirementPoolQueryServiceImpl implements PrSouRequirementPoolQueryService {

    /**
     * 招标需求池列表查询
     */
    @Override
    public List<ExtPrSouRequirementPoolQueryVO> querySouPrPool(ExtPrSouRequirementPoolQueryDTO queryParam) {
        // 1: 初始化上下文
        PrSouRequirementPoolQueryContext context = new PrSouRequirementPoolQueryContext(queryParam);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolQueryPlugin.class, context).judgeQuerySouPoolAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolQueryPlugin.class, context).beforeQuerySouPool(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolQueryPlugin.class, context).executeQuerySouPool(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolQueryPlugin.class, context).afterQuerySouPool(context);

        return context.getResult();
    }

    /**
     * 查询招标计划取消单据信息
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    @Override
    public ExtPrSouRequirementCancelVO getCancelInfo(long requirementCancelId) {
        // 1: 初始化上下文
        PrSouRequirementGetCancelInfoContext context = new PrSouRequirementGetCancelInfoContext(requirementCancelId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementGetCancelInfoPlugin.class, context).judgeGetCancelInfoAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementGetCancelInfoPlugin.class, context).beforeGetCancelInfo(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementGetCancelInfoPlugin.class, context).executeGetCancelInfo(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementGetCancelInfoPlugin.class, context).afterGetCancelInfo(context);

        return context.getResult();
    }

}
