package com.midea.cloud.srm.sou.sourcing.vendor.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.sou.sourcing.vendor.mapper.ExtSouOrderMapper;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouOrderServiceImpl extends ServiceImpl<ExtSouOrderMapper, ExtSouOrder> implements IExtSouOrderService {
}
