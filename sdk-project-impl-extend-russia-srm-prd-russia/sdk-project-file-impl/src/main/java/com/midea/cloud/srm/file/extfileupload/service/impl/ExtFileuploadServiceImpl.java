package com.midea.cloud.srm.file.extfileupload.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.file.upload.service.IFileuploadService;
import com.midea.cloud.srm.file.extfileupload.service.ExtFileuploadService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/15
 */
@Service
@Slf4j
public class ExtFileuploadServiceImpl implements ExtFileuploadService {

    @Autowired
    private IFileuploadService iFileuploadService;

    @Override
    public Fileupload queryById(Long fileuploadId) {
        if(Objects.isNull(fileuploadId)) {
            throw new BaseException("请求参数不能为空");
        }
        return iFileuploadService.getById(fileuploadId);
    }
}
