package com.midea.cloud.srm.sou.abnormalregs.service.impl;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/21
 */

import com.midea.cloud.srm.model.sou.abnormalregs.vo.SccNpmSouAbnormalRegVo;
import com.midea.cloud.srm.sou.abnormalregs.mapper.SccNpmSouAbnormalRegMapper;
import com.midea.cloud.srm.sou.abnormalregs.service.SccNpmSouAbnormalRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: for srm
 *业务层实现类
 * @author srm
 * @date 2024-08-08
 */
@Service
public class SccNpmSouAbnormalRegServiceImpl implements SccNpmSouAbnormalRegService {
    @Autowired
    SccNpmSouAbnormalRegMapper sccNpmSouAbnormalRegMapper;
    @Override
    public void UpdateAbandon(Long id, String instruction) {
        sccNpmSouAbnormalRegMapper.UpdateAbandon(id,instruction);
    }

    @Override
    public List<SccNpmSouAbnormalRegVo> List() {
        return sccNpmSouAbnormalRegMapper.List();
    }
}
