package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouFileMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouFileService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouFileServiceImpl extends ServiceImpl<ExtSouFileMapper, ExtSouFile> implements IExtSouFileService {
}
