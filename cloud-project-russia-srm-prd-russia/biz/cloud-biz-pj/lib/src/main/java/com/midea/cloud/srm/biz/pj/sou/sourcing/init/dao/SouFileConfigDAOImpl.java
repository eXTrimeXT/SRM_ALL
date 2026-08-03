package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: huanglj50@meicloud.com
 * @date: 2022/9/15 18:45
 */
@Service
public class SouFileConfigDAOImpl
        extends BaseServiceImpl<SouFileConfigMapper, SouFileConfig>
        implements BaseService<SouFileConfig> {
}
