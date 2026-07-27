package com.midea.cloud.srm.sou.req.repo;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.req.SouInfoHistory;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *  寻源单公示信息修改历史
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
public class InfoHistoryBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private SouReqHeadService souReqHeadService;

    public InfoHistoryBuyerRepository() {
        //注册action
        this.register("submit", this::submit, true, "提交");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(records, "数据不能为空。");
        QlResult qlResult = super.save(ProxyQlQueryAction.proxy(queryAction, "save"));
        //保存到寻源单头
        SouReqHead souReqHead = SouReqHead.builder()
                .reqHeadId(records.get(0).get(SouInfoHistory::getReqHeadId))
                .projectName(records.get(0).get(SouInfoHistory::getAfterProjectName))
                .projectScope(records.get(0).get(SouInfoHistory::getAfterProjectScope))
                .vendorQualReq(records.get(0).get(SouInfoHistory::getAfterVendorQualReq))
                .technicalReq(records.get(0).get(SouInfoHistory::getAfterTechnicalReq))
                .performanceReq(records.get(0).get(SouInfoHistory::getAfterPerformanceReq))
                .build();
        souReqHeadService.updateById(souReqHead);
        return qlResult;
    }
}
