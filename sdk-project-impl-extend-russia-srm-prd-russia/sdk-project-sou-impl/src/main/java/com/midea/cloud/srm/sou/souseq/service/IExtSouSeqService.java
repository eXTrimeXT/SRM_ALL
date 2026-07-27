package com.midea.cloud.srm.sou.souseq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.souseq.entity.ExtSouSeq;

/**
 * @description scc_npm_sou_seq
 * @author panmq
 * @date 2023-10-19
 */
public interface IExtSouSeqService extends IService<ExtSouSeq> {

    /**
     * 获取序号
     * @param prefix：前缀
     * @param seqControl：控制因子
     * @param dateControl：日期控制因子
     * @param digit: 位数
     * @return
     */
    public String getSerial(String prefix, String seqControl, String dateControl, Long digit);
}

