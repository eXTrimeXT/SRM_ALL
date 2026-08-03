package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignOrderMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @description scc_pj_sign_order
 * @author huangbf3
 * @date 2023-09-25
 */
@Slf4j
@Service
public class ISccPjSignOrderServiceImpl extends ServiceImpl<SignOrderMapper, SccPjSignOrder> implements ISccPjSignOrderService {
    @Override
    public boolean save(SccPjSignOrder entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<SccPjSignOrder> entityList) {
        return super.saveBatch(entityList);
    }
}

