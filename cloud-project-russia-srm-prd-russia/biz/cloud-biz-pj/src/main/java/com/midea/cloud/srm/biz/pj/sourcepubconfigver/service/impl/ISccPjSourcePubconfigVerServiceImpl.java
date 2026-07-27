package com.midea.cloud.srm.biz.pj.sourcepubconfigver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sourcepubconfigver.mapper.SccPjSourcePubconfigVerMapper;
import com.midea.cloud.srm.biz.pj.sourcepubconfigver.service.ISccPjSourcePubconfigVerService;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.pj.sourcepubconfigvers.entity.SccPjSourcePubconfigVer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ISccPjSourcePubconfigVerServiceImpl extends ServiceImpl<SccPjSourcePubconfigVerMapper, SccPjSourcePubconfigVer> implements ISccPjSourcePubconfigVerService {
    @Override
    public SccPjSourcePubconfigVer saveSccPjSourcePubconfigVer(SccPjSourcePubconfig sourcePubconfig) {

        SccPjSourcePubconfigVer sccPjSourcePubconfigVer = new SccPjSourcePubconfigVer();

        LambdaQueryWrapper<SccPjSourcePubconfigVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjSourcePubconfigVer::getConfigNumber, sourcePubconfig.getConfigNumber());
        List<SccPjSourcePubconfigVer> sccPjSourcePubconfigVers = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(sccPjSourcePubconfigVers)) {
            //插入关联关系
            sccPjSourcePubconfigVer.setPubconfigVerId(IdGenrator.generate());
            sccPjSourcePubconfigVer.setConfigVer(sourcePubconfig.getConfigVer());
            sccPjSourcePubconfigVer.setConfigNumber(sourcePubconfig.getConfigNumber());
            this.save(sccPjSourcePubconfigVer);
        } else {
            sccPjSourcePubconfigVer = sccPjSourcePubconfigVers.get(0);
            sccPjSourcePubconfigVer.setConfigVer(sourcePubconfig.getConfigVer());
            this.updateById(sccPjSourcePubconfigVer);
        }
        return sccPjSourcePubconfigVer;
    }

    @Override
    public List<SccPjSourcePubconfigVer> saveSccPjSourcePubconfigVerBatch(List<SccPjSourcePubconfig> sccPjSourcePubconfigList) {
        LambdaQueryWrapper<SccPjSourcePubconfigVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSourcePubconfigVer::getConfigNumber, sccPjSourcePubconfigList.stream().map(SccPjSourcePubconfig::getConfigNumber).distinct().collect(Collectors.toList()));
        List<SccPjSourcePubconfigVer> sccPjSourcePubconfigVers = this.list(queryWrapper);
        Map<String, SccPjSourcePubconfigVer> sourcePubconfigVerMap = sccPjSourcePubconfigVers.stream().collect(Collectors.toMap(k->k.getConfigNumber(), Function.identity(), (k1, k2)->k2));
        List<SccPjSourcePubconfigVer> saveList = new ArrayList<>();
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            SccPjSourcePubconfigVer sccPjSourcePubconfigVer = new SccPjSourcePubconfigVer();
            sccPjSourcePubconfigVer.setPubconfigVerId(IdGenrator.generate());
            sccPjSourcePubconfigVer.setConfigNumber(sourcePubconfig.getConfigNumber());

            SccPjSourcePubconfigVer save = sourcePubconfigVerMap.getOrDefault(sourcePubconfig.getConfigNumber(), sccPjSourcePubconfigVer);

            save.setConfigVer(sourcePubconfig.getConfigVer());
            saveList.add(save);
        });

        this.saveOrUpdateBatch(saveList);
        return saveList;
    }
}
