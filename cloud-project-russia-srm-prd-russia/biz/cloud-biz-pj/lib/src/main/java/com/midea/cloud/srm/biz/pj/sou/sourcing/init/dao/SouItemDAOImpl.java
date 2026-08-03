package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import org.springframework.stereotype.Service;

/**
 * 寻源.核心 - 物料需求
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Service
public class SouItemDAOImpl extends BaseServiceImpl<SouItemMapper, SouItem> implements BaseService<SouItem> {
}
