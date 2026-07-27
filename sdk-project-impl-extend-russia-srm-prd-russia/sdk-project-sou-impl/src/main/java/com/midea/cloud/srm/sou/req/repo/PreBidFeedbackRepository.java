package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.req.PreBidFeedback;
import com.midea.cloud.srm.model.sou.req.PreBidFeedbackVendor;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.VendorFeedbackStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
public class PreBidFeedbackRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    public PreBidFeedbackRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存");
        this.register("submit", this::submit, true, "提交");
    }

    private QlResult submit(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        //更新附件表
        this.updateBackAttach(record);
        //获取供应商数据
        List<Record> feedbackVendorList=qlService.queryByWrapper(QlWrappers.query(PreBidFeedbackVendor.class)
                .eq(PreBidFeedbackVendor::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId())
                .eq(PreBidFeedbackVendor::getBidFeedbackId,record.get(PreBidFeedback::getBidFeedbackId)), Record.class);
        Assert.isTrue(ObjectUtil.isNotEmpty(feedbackVendorList), "提交失败：供应商数据异常");
        //更新状态为已反馈
        qlService.updateByWrapper(QlWrappers.update(PreBidFeedbackVendor.class)
                .set(PreBidFeedbackVendor::getFeedbackStatus, VendorFeedbackStatusEnum.ALREADY_FEEDBACK.getCode())
                .eq(PreBidFeedbackVendor::getBidFeedbackId,record.get(PreBidFeedback::getBidFeedbackId))
                .eq(PreBidFeedbackVendor::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()));
        return new QlResult();
    }


    private QlResult saveOrUpdate(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        //更新附件表
        this.updateBackAttach(record);
        return new QlResult();
    }

    private void updateBackAttach(Record record) {
        //获取附件列表
        List<Record> problemFeedbackListFiles = record.getSubRecords("problemFeedbackListFiles");
        //先执行删除
        qlService.deleteByWrapper(QlWrappers.update(MqlType.PRE_BID_BACK_ATTACH)
                .eq(PreBidFeedbackVendor::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId())
                .eq(PreBidFeedbackVendor::getBidFeedbackId, record.get(PreBidFeedback::getBidFeedbackId)));
        //执行附件插入
        if (ObjectUtil.isNotEmpty(problemFeedbackListFiles)) {
            qlService.create(MqlType.PRE_BID_BACK_ATTACH, problemFeedbackListFiles);
        }
    }
}
