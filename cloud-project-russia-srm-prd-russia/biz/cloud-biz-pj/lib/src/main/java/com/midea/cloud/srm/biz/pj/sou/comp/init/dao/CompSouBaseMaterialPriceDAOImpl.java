package com.midea.cloud.srm.biz.pj.sou.comp.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouBaseMaterialPrice;
import org.springframework.stereotype.Service;

/**
 * 竞价 - 基材价格缓存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/13
 */
@Service
public class CompSouBaseMaterialPriceDAOImpl extends BaseServiceImpl<CompSouBaseMaterialPriceMapper, CompSouBaseMaterialPrice> implements BaseService<CompSouBaseMaterialPrice> {
}
