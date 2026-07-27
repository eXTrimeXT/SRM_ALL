package com.midea.cloud.srm.supcooperate.meiql.requirement.repo.init;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.PjProjectBidExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.perf.vendorlevel.dto.OrgCategorySearchDto;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementApprovalUnPassDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlRequirementAbandonDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.enums.SouHandlerRoleType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitEventService;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitQueryService;
import com.midea.cloud.srm.supcooperate.utils.DingTalkSender;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * mql - 采购申请
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/24
 */
@Component
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrRequirementForBuyerRepository extends CrudRepository {

    @Autowired
    private MqlPrRequirementInitQueryService mqlPrRequirementInitQueryService;
    @Autowired
    private MqlPrRequirementInitEventService mqlPrRequirementInitEventService;
    @Autowired
    private QlService qlService;
    @Resource
    private BaseClient baseClient;

    @Resource
    private RbacClient rbacClient;

    @Autowired
    private PjProjectBidExtClient pjProjectBidExtClient;

    @Value("${dingtalk.samebu.receiver:GW00045618}")
    private String chargeUsername;

    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    public PrRequirementForBuyerRepository() {
        super();
        // 立项-业务查询
        this.register("listRequirements", this::listRequirements, false, "采购申请列表查询");
        this.registerBefore("listRequirements", this::beforeListRequirements);
        this.registerAfter("listRequirements", this::afterListRequirements);

        this.register("getRequirementInfo", this::getRequirementInfo, false, "查看采购申请详情");
        // 立项-业务事件
        this.register("tempSaveRequirement", this::tempSaveRequirement, true, "暂存采购申请单");
        this.register("submitRequirement", this::submitRequirement, true, "提交采购申请单");
        this.register("removeRequirement", this::removeRequirement, true, "删除采购申请单");
        this.register("abandonRequirement", this::abandonRequirement, true, "废弃采购申请单");
        this.register("releaseBudget", this::releaseBudget, true, "释放采购申请剩余预算");
        this.register("copyRequirement", this::copyRequirement, true, "复制采购申请单");
        this.register("approvalCallbackAfterReqInitSubmit", this::approvalCallbackAfterReqInitSubmit, true, "立项审批提交后的回调");
        this.register("approvalCallbackAfterReqInitPass", this::approvalCallbackAfterReqInitPass, true, "立项审批通过后的回调");
        this.register("approvalCallbackAfterReqInitUnPass", this::approvalCallbackAfterReqInitUnPass, true, "立项审批未通过后的回调");
    }

    @ApiOperation("采购申请列表查询")
    private QlResult listRequirements(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        return super.query(ProxyQlQueryAction.proxy(queryAction, "query"));
    }

    @ApiOperation("前置处理: 采购申请列表查询")
    private void beforeListRequirements(QlQueryAction var1, Payload var2) {}

    @ApiOperation("后置处理: 采购申请列表查询")
    private void afterListRequirements(QlQueryAction queryAction, QlResult result, Map<String/* mqlType */, Collection<Record>> repoData) {}

    @ApiOperation("查看采购申请详情")
    private QlResult getRequirementInfo(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }

        MqlPrRequirementHeadVO reqHeadVO = mqlPrRequirementInitQueryService.getRequirementInfo(requirementHeadId);
        Record record = SouObjectXUtil.convertTargetObj(reqHeadVO, Record.class);

        return ResultUtil.build(queryAction, "requirementHeadId", Collections.singletonList(record), false);
    }

    @ApiOperation("暂存采购申请单")
    private QlResult tempSaveRequirement(QlQueryAction queryAction) {
        return this.editRequirement(queryAction, true);
    }

    @ApiOperation("提交采购申请单")
    private QlResult submitRequirement(QlQueryAction queryAction) {

        QlResult result =  this.editRequirement(queryAction, false);
        Long businessId =  (Long) result.getRecords().get(0);
        PurchaseRequirementHeadDTO purchaseRequirementHeadDTO = qlService.readByKey(MqlType.PURCHASE_REQUIREMENT_HEAD,businessId,PurchaseRequirementHeadDTO.class);
        handleSubmitted(purchaseRequirementHeadDTO);
        return result;
    }

    private QlResult editRequirement(QlQueryAction queryAction, boolean isTempSave) {
        SouUserTypeCheckUtils.checkIsBuyer();

        MqlPrRequirementHeadDTO param; {
            List<MqlPrRequirementHeadDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<MqlPrRequirementHeadDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
            param.setTempSave(isTempSave);

            param.setApplyById(AppUserUtil.getLoginAppUser().getUserId());
            param.setApplyBy(AppUserUtil.getUserName());
            param.setApplyByNickname(AppUserUtil.getLoginAppUser().getNickname());
        }

        param = mqlPrRequirementInitEventService.editRequirement(param);
        return ResultUtil.build(queryAction, "requirementHeadId", Collections.singletonList(param), false);
    }

    @ApiOperation("删除采购申请单")
    private QlResult removeRequirement(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>> () {});
            AssertUtils.notNull(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }
        PurchaseRequirementHeadDTO purReq = purchaseRequirementMapper.selectById(requirementHeadId);
        log.info("--------==============" + JSONObject.toJSONString(purReq));
        if (StringUtils.isNotBlank(purReq.getEdmExNo())) {
            List<Record> purList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                    .eq(PurchaseRequirementLineDTO::getRequirementHeadNum, purReq.getRequirementHeadNum()), Record.class);
            for (Record record1 : purList) {
                if (StringUtils.isNotBlank(record1.getString("externalId"))) {
                    //edm整单删除
                    JSONObject jo = new JSONObject();
                    jo.put("applyOutsideCode", purReq.getEdmExNo());
                    jo.put("outerItemCode", record1.getString("externalId"));
                    jo.put("apporderNumber", purReq.getRequirementHeadNum());
                    jo.put("draftStatus", "删除");
                    jo.put("tenantId", record1.get("tenantId"));
                    jo.put("edmOrgId", record1.get("edmOrgId"));
                    JSONObject reStr = pjProjectExtClient.edmDraftOrderBackHaul(jo.toString());
                    log.info("--------------------------" + reStr);
                    if (!"200".equals(String.valueOf(reStr.get("code")))) {
                        throw new BaseException(reStr.get("msg").toString());
                    }
                }
            }
        }
        mqlPrRequirementInitEventService.removeRequirement(requirementHeadId);

        return QlResult.empty();
    }

    @ApiOperation("废弃采购申请单")
    private QlResult abandonRequirement(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        MqlRequirementAbandonDTO param; {
            List<MqlRequirementAbandonDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<MqlRequirementAbandonDTO>> () {});
            AssertUtils.notNull(params, "缺少数据");
            param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            param.setCurrentUserId(AppUserUtil.getLoginAppUser().getUserId());
        }

        mqlPrRequirementInitEventService.abandonRequirement(param);

        return QlResult.empty();
    }

    @ApiOperation("释放采购申请剩余预算")
    private QlResult releaseBudget(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>> () {});
            AssertUtils.notNull(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }

        mqlPrRequirementInitEventService.releaseRequirementBudget(requirementHeadId);

        return QlResult.empty();
    }

    @ApiOperation("复制采购申请单")
    private QlResult copyRequirement(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>> () {});
            AssertUtils.notNull(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }

        long newRequirementHeadId = mqlPrRequirementInitEventService.copyRequirement(requirementHeadId, AppUserUtil.getLoginAppUser());
        PrRequirementHead reqHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), newRequirementHeadId, PrRequirementHead.class);
        Record record = SouObjectXUtil.convertTargetObj(reqHead, Record.class);

        return ResultUtil.build(queryAction, "requirementHeadId", Collections.singletonList(record), false);
    }

    @ApiOperation("立项审批提交后的回调")
    private QlResult approvalCallbackAfterReqInitSubmit(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>> () {});
            AssertUtils.notNull(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }

        mqlPrRequirementInitEventService.callbackAfterApprovalSubmit(requirementHeadId);

        return QlResult.empty();
    }

    @ApiOperation("立项审批通过后的回调")
    private QlResult approvalCallbackAfterReqInitPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long requirementHeadId; {
            List<PrRequirementHead> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<PrRequirementHead>> () {});
            AssertUtils.notNull(params, "缺少数据");
            PrRequirementHead param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
            requirementHeadId = param.getRequirementHeadId();
        }

        mqlPrRequirementInitEventService.callbackAfterApprovalPass(requirementHeadId);

        return QlResult.empty();
    }

    @ApiOperation("立项审批未通过后的回调")
    private QlResult approvalCallbackAfterReqInitUnPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        MqlPrRequirementApprovalUnPassDTO param; {
            List<MqlPrRequirementApprovalUnPassDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<MqlPrRequirementApprovalUnPassDTO>> () {});
            AssertUtils.notNull(params, "缺少数据");
            param = params.get(0);
            AssertUtils.notNull(param.getRequirementHeadId(), "缺少requirementHeadId参数");
        }

        mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(param);

        return QlResult.empty();
    }

    public DictItemDTO getDictItemDTO(String dictCode, String va) {
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode(dictCode);
        for (DictItemDTO e : gyqyList) {
            if (e.getDictItemCode().equals(va)) {
                return e;
            }
        }
        return null;
    }

    private boolean checkAndSendSameCategory(PurchaseRequirementHeadDTO requirementHeadDTO,ExtPrSouRequirementHead extPrSouRequirementHead) {

        if(ObjectUtil.isEmpty(requirementHeadDTO.getCategoryCode())||ObjectUtil.isEmpty(extPrSouRequirementHead.getOrgBuCode())){
            return false;
        }
        LocalDate startDate= LocalDate.now().minus(60L, ChronoUnit.DAYS);
        QlQueryWrapper ql = QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                .eq(PurchaseRequirementHeadDTO::getCategoryCode,requirementHeadDTO.getCategoryCode())
                .gt(PurchaseRequirementHeadDTO::getApplyDate,startDate)
                .notIn(PurchaseRequirementHeadDTO::getAuditStatus, Arrays.asList(RequirementApproveStatus.DRAFT,RequirementApproveStatus.WITHDRAW,RequirementApproveStatus.REJECTED,RequirementApproveStatus.ABANDONED))
                .notEq(PurchaseRequirementHeadDTO::getRequirementHeadId,requirementHeadDTO.getRequirementHeadId());
        List<Long> ids = qlService.queryByWrapper(ql,Record.class).stream().map(e->e.get(PurchaseRequirementHeadDTO::getRequirementHeadId)).collect(Collectors.toList());
        int countRepeatBu = 0;
        log.info(MessageFormat.format("相同的品类{0}，{1}个",requirementHeadDTO.getCategoryCode(),ids.size()));
        List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId,ids),Record.class);
        for (Record record:records){
            if(extPrSouRequirementHead.getOrgBuCode().equals(record.get(ExtPrSouRequirementHead::getOrgBuCode))){
                countRepeatBu++;
            }
        }
        log.info(MessageFormat.format("相同的板块{0}，{1}个",extPrSouRequirementHead.getOrgBuCode(),countRepeatBu));
        return ids.size()>=2&&countRepeatBu>=2;
    }

    private void handleSubmitted(PurchaseRequirementHeadDTO requirementHeadDTO) {
        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD,requirementHeadDTO.getRequirementHeadId(), ExtPrSouRequirementHead.class);
        if(checkAndSendSameCategory(requirementHeadDTO,extPrSouRequirementHead)){
            DingTalkSender dingTalkSender = DingTalkSender.create(baseClient,pjProjectBidExtClient);
            String content = String.format("【%S】60天内已递交【%S】需求≥2次，请关注！",extPrSouRequirementHead.getOrgBuName(),requirementHeadDTO.getCategoryName());
            /*
            Optional<SccPjSourcePubconfig> optionalSccPjSourcePubconfig = Optional.empty();
            try {
                optionalSccPjSourcePubconfig = pjExtClient.queryByBuCode(requirementHeadDTO.getExtOrgBuCode());
            } catch (Exception e){
                log.error(e.getMessage());
                e.printStackTrace();
            }
            */

            String chargeUserName = getChargeUsername(requirementHeadDTO,extPrSouRequirementHead);
            if(ObjectUtil.isNotEmpty(chargeUserName)){
                dingTalkSender.sendDingTalk(Collections.singletonList(chargeUserName),null,null,content);
            } else {
                log.info("找不到负责人");
            }


        }
    }

    private String getChargeUsername(PurchaseRequirementHeadDTO requirementHeadDTO, ExtPrSouRequirementHead extPrSouRequirementHead){
        return this.chargeUsername;
    }


}
