package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRisk;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouExpertRiskMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouExpertRiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @description scc_npm_sou_expert_risk
 * @author panmq
 * @date 2023-10-20
 */
@Slf4j
@Service
public class IExtSouExpertRiskServiceImpl extends ServiceImpl<ExtSouExpertRiskMapper, ExtSouExpertRisk> implements IExtSouExpertRiskService {
}

