package com.midea.cloud.srm.biz.pj.sou.comp.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.component.mphelper.service.CustomServiceImpl;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import org.springframework.stereotype.Service;

/**
 * 竞价 - 寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Service
public class CompSouProjectDAOImpl extends CustomServiceImpl<CompSouProjectMapper, CompSouProject> implements BaseService<CompSouProject> {
}
