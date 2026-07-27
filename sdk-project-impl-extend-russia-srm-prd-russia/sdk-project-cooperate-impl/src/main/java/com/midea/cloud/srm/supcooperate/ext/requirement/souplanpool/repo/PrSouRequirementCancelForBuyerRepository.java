package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.service.DefaultQlCondition;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolEventService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolQueryService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mql - 招标计划 - 项目计划
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/05
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementCancelForBuyerRepository extends CrudRepository {

    @Autowired
    private PrSouRequirementPoolQueryService prSouRequirementPoolQueryService;
    @Autowired
    private PrSouRequirementPoolEventService prSouRequirementPoolEventService;

    @Autowired
    private QlService qlService;

    public PrSouRequirementCancelForBuyerRepository() {
        super();
        // 业务查询
        this.register("queryCancels", this::queryCancels, false, "计划取消列表查询");
        this.register("getCancelInfo", this::getCancelInfo, false, "查询招标计划取消单据");
        // 业务事件
        this.register("tempSaveReqCancel", this::tempSaveReqCancel, true, "暂存计划取消单据");
        this.register("submitReqCancel", this::submitReqCancel, true, "提交计划取消单据");
        this.register("removeReqCancel", this::removeReqCancel, true, "删除计划取消单据");
    }

    @ApiOperation("计划取消列表查询")
    private QlResult queryCancels(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        return super.query(ProxyQlQueryAction.proxy(queryAction, "query"));
    }

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        if(CollectionUtils.isNotEmpty(records) && MqlType.PR_SOU_REQUIREMENT_CANCEL_FOR_BUYER.equals(queryAction.getType())) {
            List<Long> requirementCancelIdList = records.stream().map(r -> r.get(ExtPrSouRequirementCancel::getRequirementCancelId)).collect(Collectors.toList());
            List<Record> lineList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_CANCEL_LINE).in(ExtPrSouRequirementCancel::getRequirementCancelId, requirementCancelIdList), Record.class);
            if(CollectionUtils.isNotEmpty(lineList)) {
                List<Long> requirementHeadIdList = lineList.stream().map(r -> r.get(RequirementHead::getRequirementHeadId)).collect(Collectors.toList());

                List<Record> requirementHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadId, requirementHeadIdList), Record.class);
                List<Record> extRequirementHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadId, requirementHeadIdList), Record.class);

                Map<Long, Record> requirementMap = requirementHeadList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));
                Map<Long, Record> extRequirementMap = extRequirementHeadList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));

                Map<Long, List<Record>> lineGroup = lineList.stream().collect(Collectors.groupingBy(r -> r.get(ExtPrSouRequirementCancel::getRequirementCancelId)));

                records.forEach(cancleHead -> {
                    if(lineGroup.containsKey(cancleHead.get(ExtPrSouRequirementCancel::getRequirementCancelId))) {
                        List<Record> details = lineGroup.get(cancleHead.get(ExtPrSouRequirementCancel::getRequirementCancelId));
                        List<String> orgBuNameList = new ArrayList<>(50);
                        List<String> orgNameList = new ArrayList<>(50);
                        List<String> projectNameList = new ArrayList<>(50);
                        List<String> requirementHeadNumList = new ArrayList<>(50);

                        details.stream().forEach(detail -> {
                            Record requirementHead = requirementMap.getOrDefault(detail.get(RequirementHead::getRequirementHeadId), new Record());
                            Record extRequirementHead = extRequirementMap.getOrDefault(detail.get(RequirementHead::getRequirementHeadId), new Record());

                            orgBuNameList.add(extRequirementHead.get(ExtPrSouRequirementHead::getOrgBuName));
                            orgNameList.add(requirementHead.get(RequirementHead::getOrgName));
                            projectNameList.add(extRequirementHead.get(ExtPrSouRequirementHead::getProjectName));
                            requirementHeadNumList.add(requirementHead.get(RequirementHead::getRequirementHeadNum));

                        });

                        cancleHead.put("orgBuName", orgBuNameList.stream().filter(s -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
                        cancleHead.put("orgName", orgNameList.stream().filter(s -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
                        cancleHead.put("projectName", projectNameList.stream().filter(s -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
                        cancleHead.put("requirementHeadNum", requirementHeadNumList.stream().filter(s -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));

                    }
                });
            }
        }
        super.afterQuery(queryAction, records);
    }

    @ApiOperation("查询招标计划取消单据")
    private QlResult getCancelInfo(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementCancelId; {
            List<ExtPrSouRequirementCancel> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementCancel>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtPrSouRequirementCancel param = params.get(0);
            AssertUtils.notNull(param.getRequirementCancelId(), "缺少requirementCancelId参数");
            requirementCancelId = param.getRequirementCancelId();
        }

        ExtPrSouRequirementCancelVO result = prSouRequirementPoolQueryService.getCancelInfo(requirementCancelId);
        return ResultUtil.build(queryAction, "requirementCancelId", Collections.singletonList(result), false);
    }

    @ApiOperation("暂存计划取消单据")
    private QlResult tempSaveReqCancel(QlQueryAction queryAction) {
        return this.editReqCancel(queryAction, true);
    }

    @ApiOperation("提交计划取消单据")
    private QlResult submitReqCancel(QlQueryAction queryAction) {
        return this.editReqCancel(queryAction, false);
    }

    private QlResult editReqCancel(QlQueryAction queryAction, boolean isTempSave) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouRequirementCancelDTO param; {
            List<ExtPrSouRequirementCancelDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementCancelDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
            param.setTempSave(isTempSave);
            param.setApplyById(AppUserUtil.getLoginAppUser().getUserId());
            param.setApplyBy(AppUserUtil.getLoginAppUser().getUsername());
            param.setApplyByNickname(AppUserUtil.getLoginAppUser().getNickname());
        }

        param = prSouRequirementPoolEventService.editReqCancel(param);
        return ResultUtil.build(queryAction, "requirementCancelId", Collections.singletonList(param), false);
    }

    @ApiOperation("删除计划取消单据")
    private QlResult removeReqCancel(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementCancelId; {
            List<ExtPrSouRequirementCancel> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementCancel>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtPrSouRequirementCancel param = params.get(0);
            AssertUtils.notNull(param.getRequirementCancelId(), "缺少requirementCancelId参数");
            requirementCancelId = param.getRequirementCancelId();
        }

        ExtPrSouRequirementCancelVO result = prSouRequirementPoolEventService.removeReqCancel(requirementCancelId);
        if (result != null) {
            return ResultUtil.build(queryAction, "requirementCancelId", Collections.singletonList(result), false);
        } else {
            return QlResult.empty();
        }
    }

}
