package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouItemMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouItemServiceImpl extends ServiceImpl<ExtSouItemMapper, ExtSouItem> implements IExtSouItemService {
}
