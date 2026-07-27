package com.midea.cloud.srm.sou.designplans.service.impl;

import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.sou.designplans.dto.PaaAdjustDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjust;
import com.midea.cloud.srm.sou.designplans.mapper.PaaAdjustMapper;
import com.midea.cloud.srm.sou.designplans.service.PaaAdjustService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: master
 * @description:
 * @author: 100014337
 * @create: 2023-12-18 16:08
 * @version 1.0
 **/
@Service
public class PaaAdjustServiceImpl   extends BaseServiceImpl<PaaAdjustMapper, SccSouChPaaAdjust> implements PaaAdjustService {

    @Resource
    private PaaAdjustMapper paaAdjustMapper;

    /**
     * 获取调价申请列表
     * @param obj 参数
     * @return 返回值
     */
    @Override
    public List<PaaAdjustDto> getAdjustList(SccSouChPaaAdjust obj) {
        return paaAdjustMapper.getAdjustList(obj);
    }
}

