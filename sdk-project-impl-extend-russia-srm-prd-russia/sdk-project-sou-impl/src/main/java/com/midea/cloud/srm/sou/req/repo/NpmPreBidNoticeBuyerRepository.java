package com.midea.cloud.srm.sou.req.repo;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.req.PreBidNoticeVendor;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ex_liuxy46
 */
@Slf4j
@Component
public class NpmPreBidNoticeBuyerRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    /**
     * @param queryAction
     * @return
     */
    @Override
    public QlResult query(QlQueryAction queryAction) {
        return super.query(queryAction);
    }

    /**
     * @param queryAction
     * @param payload
     * @return
     */
    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        log.info("获取供应商AAABBB==={}", JSONObject.toJSONString(qlCondition));
        List<PreBidNoticeVendor> noticeVendorList = qlService.queryByWrapper(QlWrappers.query(PreBidNoticeVendor.class)
                        .eq(PreBidNoticeVendor::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId())
                , PreBidNoticeVendor.class);
        log.info("获取供应商aaa==={}", JSONObject.toJSONString(noticeVendorList));
        Set<Long> s = new HashSet<>();
        s.add(0L);
        if (CollectionUtils.isNotEmpty(noticeVendorList)) {
            s.addAll(noticeVendorList.stream().map(PreBidNoticeVendor::getBidNoticeId).collect(Collectors.toSet()));
        }
        qlCondition.in(BidNoticeDTO::getBidNoticeId, s);
        return qlCondition;
    }
}
