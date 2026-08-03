package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignFeignMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignOrderFileMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignFeignService;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderFileService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignFeign;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description scc_pj_sign_feign
 * @author huangbf3
 * @date 2023-09-25
 */
@Slf4j
@Service
public class ISccPjSignFeignServiceImpl extends ServiceImpl<SignFeignMapper, SccPjSignFeign> implements ISccPjSignFeignService {
}

