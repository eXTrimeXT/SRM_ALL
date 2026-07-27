package com.midea.cloud.srm.sou.bid.openrecords.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtNpmSouOpenBidRecordDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;

import java.util.List;

/**
 * @description scc_npm_sou_open_bid_record
 * @author panmq
 * @date 2023-11-13
 */
public interface IExtNpmSouOpenBidRecordService extends IService<ExtNpmSouOpenBidRecord> {

    /**
     * 查询技术标开标人员
     * @param projectId
     * @return
     */
    List<ExtNpmSouOpenBidRecordDto> queryTechOpenRecord(Long projectId);

    /**
     * 查询技术标开标人员
     * @param projectId 参数
     * @param round 参数
     * @return 返回
     */
    List<ExtNpmSouOpenBidRecordDto> queryBusOpenRecord(Long projectId, Integer round);

    /**
     * 操作记录
     * @param projectId
     * @param round
     * @param openType
     * @return
     */
    ExtNpmSouOpenBidRecord openRecord(Long projectId, Integer round, String openType);

    /**
     * 是否全部人员已开标
     * @param projectId
     * @param round
     * @param openType
     * @return
     */
    Boolean isOpenByAllUser(Long projectId, Integer round, String openType);

}

