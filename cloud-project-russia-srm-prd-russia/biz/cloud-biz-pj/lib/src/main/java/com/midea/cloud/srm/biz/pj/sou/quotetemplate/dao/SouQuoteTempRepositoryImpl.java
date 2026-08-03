package com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import org.springframework.stereotype.Service;

/**
 * 寻源模型报价模板表
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
@Service
public class SouQuoteTempRepositoryImpl extends BaseServiceImpl<SouQuoteTempMapper, SouQuoteTemp> implements BaseService<SouQuoteTemp> {
}
