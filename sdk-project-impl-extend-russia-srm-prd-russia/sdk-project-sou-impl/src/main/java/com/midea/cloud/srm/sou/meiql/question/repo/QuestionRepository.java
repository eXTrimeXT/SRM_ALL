package com.midea.cloud.srm.sou.meiql.question.repo;

import com.midea.cloud.common.enums.YesOrNo;
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
public class QuestionRepository extends CrudRepository {

    @Autowired
    private QuestionService questionService;

    public QuestionRepository() {
        this.register("submit", this::submit, true, "提交");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        questionService.initReplayValues(recs);
        //发送短信
        questionService.sendSmsReplayValues(recs);
        return super.doSave(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value()),recs);
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        //供应商推荐审批通过前，所有提交的质疑，仅供应商负责人可见，可点击质疑回复。
        //供应商推荐审批通过后，所有提交的质疑，仅招标负责人可查看并质疑回复。（注：以发起质疑时的状态作为判断）
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        return qlCondition;
    }

}
