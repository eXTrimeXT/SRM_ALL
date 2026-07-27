package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouExpertRecordMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouExpertRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description scc_npm_sou_expert_record
 * @author panmq
 * @date 2023-10-20
 */
@Slf4j
@Service
public class IExtSouExpertRecordServiceImpl extends ServiceImpl<ExtSouExpertRecordMapper, ExtSouExpertRecord> implements IExtSouExpertRecordService {

    @Override
    public List<ExtSouExpertRecord> addRecord(List<ExtSouGroup> groupList, String expertRange) {

        List<ExtSouExpertRecord> extSouExpertRecords = new ArrayList<>();
        groupList.stream().forEach(extSouGroup -> extSouExpertRecords.add(this.buildExpertRecord(extSouGroup, expertRange)));
        this.saveBatch(extSouExpertRecords);
        return extSouExpertRecords;
    }

    protected ExtSouExpertRecord buildExpertRecord(ExtSouGroup extSouGroup, String expertRange) {
        ExtSouExpertRecord expertRecord = new ExtSouExpertRecord();

        expertRecord.setProjectId(extSouGroup.getProjectId());
        expertRecord.setGroupId(extSouGroup.getGroupId());
        expertRecord.setUserId(extSouGroup.getUserId());
        expertRecord.setUserName(extSouGroup.getUserName());
        expertRecord.setFullName(extSouGroup.getFullName());
        expertRecord.setExpertLevel(extSouGroup.getExtExpertLevel());
        expertRecord.setRemoveReason(extSouGroup.getExtRemoveReason());
        expertRecord.setExpertRange(expertRange);
        expertRecord.setExpertRecordId(IdGenrator.generate());
        expertRecord.setExtractTime(new Date());

        return expertRecord;
    }
}

