package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignTemplateMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignTemplateService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description scc_pj_sign_template
 * @author huangbf3
 * @date 2023-09-25
 */
@Slf4j
@Service
public class ISccPjSignTemplateServiceImpl extends ServiceImpl<SignTemplateMapper, SccPjSignTemplate> implements ISccPjSignTemplateService {
}

