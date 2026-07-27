package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;

import java.util.List;

/**
 * @description ext_scc_sou_order
 * @author panmq
 * @date 2023-11-08
 */
public interface IExtNpmSouOrderService extends IService<ExtNpmSouOrder> {

    /**
     * 扩展报价表
     * @param souOrderList
     * @return
     */
    public List<ExtNpmSouOrder> extendSouOrder(List<ExtSouOrder> souOrderList);

    /**
     * 更新读标标识
     * @param projectId
     */
    public void updateReadBid(Long projectId);

    /**
     * 更新下载标书时间
     * @param projectId
     */
    public void updateDownloadTime(Long projectId);

    /**
     * 技术已投保供应商范围
     * @param projectId
     * @return
     */
    List<ExtSouOrder> techOrderRange(Long projectId);

    /**
     * 查询最新一轮次的投标信息
     * @param projectId
     * @return
     */
    List<ExtSouOrder> queryNewestOrder(Long projectId);

    /**
     * 查询最新一轮次的商务已投标供应商
     * @param projectId
     * @return
     */
    List<ExtSouOrder> queryNewestBusSubmissionOrder(Long projectId);
}

