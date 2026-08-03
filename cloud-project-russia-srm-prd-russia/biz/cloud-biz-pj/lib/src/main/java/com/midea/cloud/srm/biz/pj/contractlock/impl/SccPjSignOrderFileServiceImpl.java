package com.midea.cloud.srm.biz.pj.contractlock.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.mapper.SignOrderFileMapper;
import com.midea.cloud.srm.biz.pj.contractlock.SccPjSignOrderFileService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Collection;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class SccPjSignOrderFileServiceImpl extends ServiceImpl<SignOrderFileMapper, SccPjSignOrderFile> implements SccPjSignOrderFileService {


    @Override
    public boolean save(SccPjSignOrderFile entity) {

        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<SccPjSignOrderFile> entityList) {

        return super.saveBatch(entityList);
    }

}
