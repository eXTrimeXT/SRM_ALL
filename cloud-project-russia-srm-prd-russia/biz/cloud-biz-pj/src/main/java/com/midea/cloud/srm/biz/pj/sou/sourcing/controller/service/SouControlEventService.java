package com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import java.util.List;

/**
 * 寻源核心 - 业务控制 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/05
 */
public interface SouControlEventService {

    /**
     * 修改报价开始时间(立即开始/延迟开始)
     *
     * @param param   报价时间修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void changeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType);

    /**
     * 立修改报价截止时间(立即截止/延迟报价)
     *
     * @param param   报价时间修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void changeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType);

    /**
     *
     * 最早开标时间修改（仅未开标可前可修改）
     * @param param
     * @param souType
     */
    void changeEarliestBusinessOpenTime(ApiSouChangeEarliestBusinessOpenTimeDTO param, String souType);

    /**
     * 生成开标密码
     *
     * @param param   请求数据
     * @param souType 寻源类型
     */
    void generateBidPwd(ApiSouBidPwdGenerateDTO param, String souType);

    /**
     * 确认开标密码
     *
     * @param param   确认信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void confirmOpeningBid(ApiSouOpenBidDTO param, String souType);

    /**
     * 商务开标
     *
     * @param param   开标信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void businessOpen(ApiSouBusinessOpenDTO param, String souType);

    /**
     * 报价解密
     *
     * @param param   解密数据
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void decryptPrice(ApiSouDecryptPriceDTO param, String souType);

    /**
     * 发起新一轮
     *
     * @param param   发起新一轮填写的信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void startNewRound(ApiSouStartNewRoundDTO param, String souType);

    /**
     * 根据寻源的时间节点信息，调整寻源单的状态等信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     */
    void refreshProjectBySouTime(long projectId);

    /**
     * 根据竞价单ID，调整单据状态等信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     */
    void refreshProjectByWin(long projectId);

    /**
     * 根据竞价单ID，调整单据状态等信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     */
    void refreshProjectBySelect(long projectId);

    /**
     * 记录物料变更情况
     *
     * @param param   新的物料需求集合
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void recordSouItemRefreshInfo(ApiSouItemRecordDTO param, String souType);

    /**
     * 执行物料变更
     *
     * @param param   物料变更执行信息
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 执行的记录
     */
    List<SouItemRecord> executeSouItemRefresh(ApiSouItemRefreshDTO param, String souType);

    /**
     * 记录追加供应商情况
     *
     * @param param   新增供应商信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void recordSouVendorAddInfo(ApiSouVendorRecordDTO param, String souType);

    /**
     * 执行追加供应商
     *
     * @param param   追加供应商执行信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void executeSouVendorAdd(ApiSouVendorAddDTO param, String souType);

}
