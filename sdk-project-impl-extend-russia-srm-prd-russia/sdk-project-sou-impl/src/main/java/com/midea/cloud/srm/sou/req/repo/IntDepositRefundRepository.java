package com.midea.cloud.srm.sou.req.repo;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <pre>
 *  寻源单意向金退款
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/7 14:06
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class IntDepositRefundRepository extends CrudRepository {
    public IntDepositRefundRepository() {
        //注册action
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition condition = MeiQl.newCondition();
        condition.eq("vendorId", AppUserUtil.getLoginAppUser().getCompanyId());
        return condition;
    }
}
