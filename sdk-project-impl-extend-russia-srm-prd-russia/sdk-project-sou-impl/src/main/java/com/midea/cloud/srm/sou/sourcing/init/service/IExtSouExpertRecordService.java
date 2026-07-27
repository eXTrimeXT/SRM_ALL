package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;

import java.util.List;

/**
 * @description scc_npm_sou_expert_record
 * @author panmq
 * @date 2023-10-20
 */
public interface IExtSouExpertRecordService extends IService<ExtSouExpertRecord> {

    /**
     * 增加抽取历史
     * @param groupList
     * @param expertRange
     * @return
     */
    public List<ExtSouExpertRecord> addRecord(List<ExtSouGroup> groupList, String expertRange);

}

