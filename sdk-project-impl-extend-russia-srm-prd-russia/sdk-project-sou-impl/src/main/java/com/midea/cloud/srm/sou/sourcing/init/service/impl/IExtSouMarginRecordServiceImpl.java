package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouMarginRecordMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @description scc_npm_sou_margin_record
 * @author panmq
 * @date 2023-10-07
 */
@Slf4j
@Service
public class IExtSouMarginRecordServiceImpl extends ServiceImpl<ExtSouMarginRecordMapper, ExtSouMarginRecord> implements IExtSouMarginRecordService {
}

