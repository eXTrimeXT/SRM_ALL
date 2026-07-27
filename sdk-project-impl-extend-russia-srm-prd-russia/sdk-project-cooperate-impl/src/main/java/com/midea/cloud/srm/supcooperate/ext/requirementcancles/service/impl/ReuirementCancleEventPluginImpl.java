package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.context.RequirementCancleContext;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.enums.ReqCancleEnum;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.factory.RequirementCancleFactory;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.cancle.event.canclerequirements.IReuirementCancleEventPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm 采购申请取消插件类
 *
 * @author srm
 * @date 2024-05-20
 */
@Service
@Slf4j
public class ReuirementCancleEventPluginImpl implements IReuirementCancleEventPlugin {

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public String matchScene() {
        return IReuirementCancleEventPlugin.super.matchScene();
    }

    @Override
    public boolean isDefaultMatchAllScene() {
        return IReuirementCancleEventPlugin.super.isDefaultMatchAllScene();
    }

    @Override
    public RequirementCancleContext judgeCancleRequirementAuth(RequirementCancleContext context) {
        return IReuirementCancleEventPlugin.super.judgeCancleRequirementAuth(context);
    }

    @Override
    public RequirementCancleContext beforeCancleRequirementAuth(RequirementCancleContext context) {
        //取消范围
        List<String> cancleRangeList = new ArrayList<>();
        /** 取消寻源需求 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_SOU.name());
        /** 取消招标资料提交 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_SUBMITE.name());
        /** 取消推荐供应商 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_RECOMM.name());
        /** 取消招标单 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_BID.name());
        /** 取消考察申请或考察报告 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_INSPECT.name());
        /** 取消质疑 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_QUESTION.name());
        /** 取消澄清 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_ANSWER.name());
        /** 取消定标 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_CA.name());
        /** 取消中落标 */
        cancleRangeList.add(ReqCancleEnum.REQ_CANCLE_NOTICE.name());
        context.getLocalCache().put("cancleRangeList", cancleRangeList);
        return IReuirementCancleEventPlugin.super.beforeCancleRequirementAuth(context);
    }

    @Override
    public RequirementCancleContext executeCancleRequirementAuth(RequirementCancleContext context) {
        //取消范围
        List<String> cancleRangeList = (List<String>)context.getLocalCache().getOrDefault("cancleRangeList", new ArrayList<>());

        for(String cancleService : cancleRangeList) {
            log.info("采购申请取消执行逻辑，逻辑编码{0}, 申请编号{1} 执行开始......", cancleService, JSON.toJSONString(context.getRequirementHeadIdList()));
            RequirementCancleFactory.instance().createCancleBusiness(cancleService).cancle(context.getRequirementHeadIdList(), context.getRequirementHeadNumMap(), context.getLocalCache());
            log.info("采购申请取消执行逻辑，逻辑编码{0}, 申请编号{1} 执行结束......", cancleService, JSON.toJSONString(context.getRequirementHeadIdList()));
        }
        return IReuirementCancleEventPlugin.super.executeCancleRequirementAuth(context);
    }

    @Override
    public RequirementCancleContext afterCancleRequirementAuth(RequirementCancleContext context) {
        return IReuirementCancleEventPlugin.super.afterCancleRequirementAuth(context);
    }
}
