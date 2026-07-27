package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouTechScoreHeadMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouTechScoreHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @description scc_sou_tech_score_head
 * @author panmq
 * @date 2023-10-09
 */
@Slf4j
@Service
public class IExtSouTechScoreHeadServiceImpl extends ServiceImpl<ExtSouTechScoreHeadMapper, ExtSouTechScoreHead> implements IExtSouTechScoreHeadService {
}

