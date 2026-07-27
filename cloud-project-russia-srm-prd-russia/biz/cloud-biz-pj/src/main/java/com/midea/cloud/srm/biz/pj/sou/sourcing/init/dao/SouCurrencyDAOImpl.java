package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import org.springframework.stereotype.Service;

/**
 * 寻源.核心 - 可用币种
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Service
public class SouCurrencyDAOImpl extends BaseServiceImpl<SouCurrencyMapper, SouCurrency> implements BaseService<SouCurrency> {
}
