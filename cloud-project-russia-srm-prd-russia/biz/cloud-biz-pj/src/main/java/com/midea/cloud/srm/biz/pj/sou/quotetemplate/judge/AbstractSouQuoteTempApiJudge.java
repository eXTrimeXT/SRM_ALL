package com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge;

import brave.internal.Nullable;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempApiRepositoryImpl;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 寻源 - 模型报价模板api引用 - 接口校验服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/17
 */
public class AbstractSouQuoteTempApiJudge {

    @Autowired
    private SouQuoteTempApiRepositoryImpl souQuoteTempApiRepository;

    /**
     * 当前操作人是否可以编辑报价模板api
     */
    public void judgeEditApiAuth(@Nullable Long apiId) {
        if (apiId == null) { return; }
        SouQuoteTempApi api = souQuoteTempApiRepository.getById(apiId);
        AssertUtils.notNull(api, LocaleHandler.getLocaleMsg("报价模板api信息[{0}]不存在"), apiId);
        AssertUtils.isTrue(SouQuoteTempApiStatusEnum.DRAFT.equals(api.getApiStatus()), "非拟定状态，不能编辑");
    }

}
