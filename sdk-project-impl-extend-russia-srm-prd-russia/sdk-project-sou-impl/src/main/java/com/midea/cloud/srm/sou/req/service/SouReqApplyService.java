package com.midea.cloud.srm.sou.req.service;


import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.req.SouReqApply;

import java.util.Map;

/**
 * 寻源需求单报名表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
public interface SouReqApplyService extends BaseService<SouReqApply> {

    /**
     * 寻源单-推荐供应商
     * @param queryAction 参数
     * @return 返回
     */
    ApiExtSouRecommVendorInfoDTO createVendorRecommend(QlQueryAction queryAction);

    /**
     * 备注
     * @param queryAction 参数
     * @return 返回
     */
    QlResult getApplyInfo(QlQueryAction queryAction);

    /**
     * 统计公示和不公示供应商推荐数量
     * @param param
     * @return
     */
    Map<String, Object> countRecomm(Map<String, Object> param);

    /**
     * 存在关联关系时发送钉钉消息给供应商负责人
     * @param souReqApply
     */
    void dingTalkNotice(Record souReqApply);
}
