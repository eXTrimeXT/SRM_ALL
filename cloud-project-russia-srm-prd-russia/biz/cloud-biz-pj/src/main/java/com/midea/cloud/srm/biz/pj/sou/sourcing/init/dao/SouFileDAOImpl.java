package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import org.springframework.stereotype.Service;

/**
 * @author huangbf3
 */
@Service
public class SouFileDAOImpl
        extends BaseServiceImpl<SouFileMapper, SouFile>
        implements BaseService<SouFile> {
}
