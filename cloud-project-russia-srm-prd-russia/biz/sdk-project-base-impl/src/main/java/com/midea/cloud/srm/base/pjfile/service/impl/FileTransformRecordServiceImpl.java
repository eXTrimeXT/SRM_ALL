package com.midea.cloud.srm.base.pjfile.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.base.pjfile.mapper.FileTransformRecordMapper;
import com.midea.cloud.srm.base.pjfile.service.FileTransformRecordService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.pjfile.enitty.FileTransformRecord;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author luxc18
 */
@Service
@Slf4j
public class FileTransformRecordServiceImpl extends BaseServiceImpl<FileTransformRecordMapper, FileTransformRecord> implements FileTransformRecordService {

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 附件转换
     */
    @Override
    public void fileTransform() throws IOException {
        List<FileTransformRecord> list = this.list(Wrappers.lambdaQuery(FileTransformRecord.class)
                .isNull(FileTransformRecord::getSrmFileUploadId));
        CompletableFuture.runAsync(() -> {
            for (FileTransformRecord fileTransformRecord : list) {
                try {
                    MultipartFile convert = convert(fileTransformRecord);
                    String sourceType = "WEB_APP";
                    String uploadType = FileUploadType.DEF.name();
                    String fileModular = "base";
                    String fileFunction = "profile";
                    String fileType = fileTransformRecord.getFileType();
                    Fileupload fileupload = fileCenterClient.feignClientUpload(convert, sourceType, uploadType, fileModular, fileFunction, fileType);
                    this.update(Wrappers.lambdaUpdate(FileTransformRecord.class)
                            .set(FileTransformRecord::getSrmFileUploadId, fileupload.getFileuploadId())
                            .set(FileTransformRecord::getStatus, "待迁移")
                            .eq(FileTransformRecord::getRecordId, fileTransformRecord.getRecordId()));
                } catch (IOException e) {
                    log.error("图片迁移失败" + e);
                    log.error("图片迁移失败" + e.getMessage());
                    log.error("图片迁移失败:供应商lcCode{},图片url:{}", fileTransformRecord.getLcCode(), fileTransformRecord.getFileUrl());
                }
            }
        });
    }

    public MultipartFile convert(FileTransformRecord fileTransformRecord) throws IOException {
        Map<String, String> uriVariables = new HashMap<>(50);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(fileTransformRecord.getFileUrl(), HttpMethod.GET, null,
                byte[].class, uriVariables);
        byte[] bytes = responseEntity.getBody();
        MockMultipartFile multipartFile = new MockMultipartFile(
                fileTransformRecord.getFileName(),
                fileTransformRecord.getFileName(),
                "",
                bytes
        );
        return multipartFile;
    }


}
