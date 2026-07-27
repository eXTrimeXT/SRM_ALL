package com.midea.cloud.srm.biz.pj.contractlock.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignOrderMapper;
import com.midea.cloud.srm.biz.pj.contractlock.SccPjSignOrderService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class SccPjSignOrderServiceImpl extends ServiceImpl<SignOrderMapper, SccPjSignOrder> implements SccPjSignOrderService {


    @Override
    public boolean save(SccPjSignOrder entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<SccPjSignOrder> entityList) {
        return super.saveBatch(entityList);
    }
}
