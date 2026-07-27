package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.req.PreBidNotice;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.PreBidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouSceneModuleCodeEnum;
import com.midea.cloud.srm.sou.req.utils.SouReqCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
 *  修改日期: 2023/11/17 17:25
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class PreBidNoticeBuyerRepository extends CrudRepository {
    @Autowired
    private QlService qlService;
    @Autowired
    private SouReqCommonUtil souReqCommonUtil;

    public PreBidNoticeBuyerRepository() {
        //注册action
        this.register("submit", this::submit, true, "提交");
        this.register("getVendorList", this::getVendorList, true, "根据申请单获取寻源单报名成功的供应商");
    }

    private QlResult getVendorList(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        //获取寻源单
        List<Record> reqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).contains(SouReqHead::getRequirementHeadIdList, record.get(PreBidNotice::getRequirementHeadId)), Record.class);
        List<Record> result = new ArrayList<>();
        if (ObjectUtil.isEmpty(reqHeadList)) {
            return ResultUtil.build(queryAction, "applyId", result, false);
        }
        result = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER)
                        .eq(SouReqApply::getReqHeadId, reqHeadList.get(0).get(SouReqHead::getReqHeadId))
                        .eq(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.SUCCESS_SIGNUP)
                , Record.class);
        return ResultUtil.build(queryAction, "applyId", result, false);
    }

    private QlResult submit(QlQueryAction queryAction) {
        //移除附件
        souReqCommonUtil.deleteFiles(queryAction, SouSceneModuleCodeEnum.PRE_BID_COMM_SIGN_ATTACHMENT.toString(), "bidNoticeId");
        souReqCommonUtil.deleteFiles(queryAction, SouSceneModuleCodeEnum.PRE_BID_COMM_EDIT_ATTACHMENT.toString(), "bidNoticeId");
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        records.forEach(l->l.set(PreBidNotice::getStatus, PreBidNoticeStatusEnum.ISSUED));
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        //移除附件
        souReqCommonUtil.deleteFiles(queryAction, SouSceneModuleCodeEnum.PRE_BID_COMM_SIGN_ATTACHMENT.toString(), "bidNoticeId");
        souReqCommonUtil.deleteFiles(queryAction, SouSceneModuleCodeEnum.PRE_BID_COMM_EDIT_ATTACHMENT.toString(), "bidNoticeId");
        return super.save(queryAction);
    }
}

