package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouApplyHandleTypeEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class ReqHeadApplyBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private SouReqApplyService reqApplyService;
    @Autowired
    private SouReqApplyService souReqApplyService;
    @Autowired
    private SouReqHeadService souReqHeadService;

    public ReqHeadApplyBuyerRepository() {
        //注册action
        this.register("handleApply", this::handleApply, this::beforeHandleApply, this::afterHandleApply, true, "报名处理");
        this.register("createVendorRecommend", this::createVendorRecommend, this::beforeVendorRecommend, this::afterVendorRecommend, true, "寻源单-生成供应商推荐单");
        this.register("getApplyInfo", this::getApplyInfo, false, "获取报名详情信息");
    }

    private QlResult getApplyInfo(QlQueryAction queryAction) {
        souReqHeadService.handleSignupDone();
        return souReqApplyService.getApplyInfo(queryAction);
    }

    private QlResult createVendorRecommend(QlQueryAction queryAction) {
        souReqHeadService.handleSignupDone();
        ApiExtSouRecommVendorInfoDTO result = reqApplyService.createVendorRecommend(queryAction);
        Record record = SouObjectXUtil.convertTargetObj(result.getProject(), Record.class);
        return ResultUtil.build(queryAction, "projectId", Collections.singletonList(record), false);
    }

    private void beforeVendorRecommend(QlQueryAction queryAction, Payload payload) {

        //校验参数
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Assert.isTrue(ObjectUtil.isNotEmpty(records), "入参不能为空");
        List<Long> ids = records.stream().map(record -> record.get(SouReqApply::getReqHeadId)).collect(Collectors.toList());
        if(ids.size() > 1) {
            throw new BaseException("只能选择一个寻原单据创建推荐供应商单据");
        }
        //寻源单校验
        SouReqHead reqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, ids.get(0), SouReqHead.class);
        Assert.isTrue(reqHead.getStatus().equals(SouReqHeadStatusEnum.SIGNUP_DONE.getCode()), "创建推荐单失败：寻源单状态为" + SouReqHeadStatusEnum.SIGNUP_DONE.getName() + "，才允许创建");
        Assert.isTrue(ObjectUtil.isEmpty(reqHead.getIsRecommend()) || reqHead.getIsRecommend().equals(Enable.N.name()), "创建推荐单失败：不允许重复创建");

    }

    private void afterVendorRecommend(QlQueryAction queryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {

    }

    private void afterHandleApply(QlQueryAction queryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        /*迟延期缴纳意向金的供应商确认报名成功后，需要推送至资金系统，资金系统返回意向金缴纳状态。
        不缴纳意向金的供应商确认报名通过后，不需要推送至资金系统，意向金缴纳状态默认不涉及。
        不允许报名：提交后，更新后，更新供应商报名状态为报名失败。
        已缴纳，直接报名：系统对接前处理方式，统一处理后，保证金缴纳状态统一不涉及处理。（后续系统上线对接资金系统后，关闭该选项）*/
    }

    private void beforeHandleApply(QlQueryAction queryAction, Payload payload) {
    }

    private QlResult handleApply(QlQueryAction queryAction) {
        souReqHeadService.handleSignupDone();
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //系统对接前处理方式，统一处理后，保证金缴纳状态统一不涉及处理。（后续系统上线对接资金系统后，关闭该选项）
        records.forEach(record -> {
            Assert.isTrue(record.get(SouReqApply::getApplyStatus).equals(SouReqApplyStatusEnum.CONFIRMING_SIGNUP.getCode()), "仅报名确认中可处理");
            boolean isFail = record.get(SouReqApply::getApplyHandleType).equals(SouApplyHandleTypeEnum.CAN_NOT_APPLY.getCode());
            //允许报名，可不缴纳意向金：逻辑不变
            //已缴纳，直接报名：校验缴纳状态=已缴纳，不修改缴纳状态
            //允许报名，可延期缴纳意向金：不修改缴纳状态
            //不允许报名：不修改缴纳状态
            if(SouApplyHandleTypeEnum.APPLY_CAN_NO_INT_DEPOSIT.getCode().equals(record.get(SouReqApply::getApplyHandleType))) {
                record.set(SouReqApply::getDepositStatus, IntDepositStatusEnum.NOT_APPLICABLE.getCode());//TODO 临时处理，后续需要接资金系统
            }

            record.set(SouReqApply::getApplyStatus, isFail ? SouReqApplyStatusEnum.FAIL_SIGNUP.getCode() : SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode());
            record.set(SouReqApply::getApplyFailReason, isFail ? record.get(SouReqApply::getApplyHandleReason) : null);
        });
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), DefaultAction.UPDATE.value(), records));
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        souReqHeadService.handleSignupDone();
        return MeiQl.newCondition();
    }
}
