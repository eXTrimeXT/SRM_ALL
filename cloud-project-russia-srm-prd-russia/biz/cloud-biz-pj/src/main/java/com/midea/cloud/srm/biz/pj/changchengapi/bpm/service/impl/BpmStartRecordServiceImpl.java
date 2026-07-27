package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper.BpmNewFlagMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper.BpmStartRecordMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmNewFlagService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmStartRecordService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmStartRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author huangbf3
 * 新BPM审批流标志表实现类
 */
@Slf4j
@Service
public class BpmStartRecordServiceImpl extends BaseServiceImpl<BpmStartRecordMapper, BpmStartRecord> implements IBpmStartRecordService {

}
