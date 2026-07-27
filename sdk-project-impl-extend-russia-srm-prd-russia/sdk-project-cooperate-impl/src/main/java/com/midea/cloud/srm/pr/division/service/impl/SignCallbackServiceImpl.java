package com.midea.cloud.srm.pr.division.service.impl;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class SignCallbackServiceImpl implements ISignCallbackService {

    @Override
    public void complete(Long businessId, String param, List<Fileupload> fileuploads) throws Exception {
        log.info("-------------------complete--------------------");
        log.info("businessId:{},param:{}",businessId,param);

    }
}
