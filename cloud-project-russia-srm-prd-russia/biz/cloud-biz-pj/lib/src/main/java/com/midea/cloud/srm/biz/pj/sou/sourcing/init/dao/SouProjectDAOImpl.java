package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import org.springframework.stereotype.Service;

/**
 * 寻源.核心 - 寻源单据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Service
public class SouProjectDAOImpl extends BaseServiceImpl<SouProjectMapper, SouProject> implements BaseService<SouProject> {
}
