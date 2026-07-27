package com.midea.cloud.srm.sou.meiql.question.repo;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
import com.midea.cloud.srm.sou.meiql.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 08:34:54
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class QuestionVendorRepository extends CrudRepository {

    @Autowired
    private QuestionService questionService;

    public QuestionVendorRepository() {
        this.register("submit", this::submit, true, "提交");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        questionService.initSubmitValues(recs);
        return super.doSave(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value()),recs);
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction,List<Record> recs) {
        questionService.initDraftValues(recs);
        return super.doSave(queryAction,recs);
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        qlCondition.eq(QuestionDTO::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
        return qlCondition;
    }

}
