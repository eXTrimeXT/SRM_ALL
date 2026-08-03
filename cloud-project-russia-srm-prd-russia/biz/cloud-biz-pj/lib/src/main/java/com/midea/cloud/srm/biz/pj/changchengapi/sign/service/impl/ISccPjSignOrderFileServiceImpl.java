package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignOrderFileMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderFileService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description scc_pj_bid_group
 * @author panmq
 * @date 2023-09-25
 */
@Slf4j
@Service
public class ISccPjSignOrderFileServiceImpl extends ServiceImpl<SignOrderFileMapper, SccPjSignOrderFile> implements ISccPjSignOrderFileService {
}

