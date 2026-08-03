package com.midea.cloud.srm.biz.pj.file.anon.service.impl;

import com.google.common.base.Objects;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.file.anon.service.FileAnonService;
import com.midea.cloud.srm.biz.pj.utils.XwpfdUtils;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.pj.file.ExtFileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import feign.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileAnonServiceImpl implements FileAnonService {
    @Autowired
    private ExtFileCenterClient extFileCenterClient;
    @Autowired
    private FileCenterClient fileCenterClient;



    @Override
    public Fileupload wordToPdf(Long fileId, String fileName) throws Exception {
        Fileupload fileupload = extFileCenterClient.queryById(fileId);
        AssertUtils.notNull(fileupload, "文件不存在");
        AssertUtils.isTrue(Objects.equal(FilenameUtils.getBaseName(fileupload.getFileSourceName()),FilenameUtils.getBaseName(fileName)), "文件名错误");
        MockMultipartFile pdfFile = XwpfdUtils.instance().covertFile(fileName, getFile(fileId), fileId);

        String uploadType = FileUploadType.DEF.name();
        String sourceType = "WEB_APP";
        String fileModular = "sou";
        String fileFunction = "aiReviewFile";
        String fileType = "PDF";

        return fileCenterClient.feignClientUpload(
                pdfFile,
                sourceType,
                uploadType,
                fileModular,
                fileFunction,
                fileType);
    }


    private InputStream getFile(Long fileuploadId) throws Exception {
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileuploadId);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        return response1.body().asInputStream();
    }
}
