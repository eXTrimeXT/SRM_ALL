package com.midea.cloud.srm.sou.designplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.designplans.dto.PaaAdjustDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjust;

import java.util.List;

/**
 * @program: master
 * @description: 调价申请
 * @author: 100014337
 * @create: 2023-12-18 16:05
 * @version 1.0
 **/
public interface PaaAdjustService extends IService<SccSouChPaaAdjust> {
    /**
     * 获取调价申请列表
     * @param obj 参数
     * @return 返回值
     */
    List<PaaAdjustDto> getAdjustList(SccSouChPaaAdjust obj);
}

