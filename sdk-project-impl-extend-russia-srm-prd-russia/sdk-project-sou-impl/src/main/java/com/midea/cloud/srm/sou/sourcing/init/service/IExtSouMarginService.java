package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;

import java.util.List;
import java.util.Map;

/**
 * @description scc_npm_sou_margin
 * @author panmq
 * @date 2023-10-07
 */
public interface IExtSouMarginService extends IService<ExtSouMargin> {

    /**
     * 拷贝年度保证金信息
     * @param souMarginList
     */
    void copyYearMarginInfo(List<ExtSouMargin> souMarginList);

    /**
     * 查询年度保证金
     * @param souMarginList
     * @return
     */
    Map<Long, ExtSouMargin> queryYearMarginInfo(List<ExtSouMargin> souMarginList);
}

