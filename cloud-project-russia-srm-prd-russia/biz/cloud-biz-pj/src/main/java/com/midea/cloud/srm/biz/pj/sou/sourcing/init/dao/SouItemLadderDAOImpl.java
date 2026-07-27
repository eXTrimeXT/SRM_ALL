package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import org.springframework.stereotype.Service;

/**
 * 寻源.核心 - 阶梯价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Service
public class SouItemLadderDAOImpl
        extends BaseServiceImpl<SouItemLadderMapper, SouItemLadder>
        implements BaseService<SouItemLadder> {
}
