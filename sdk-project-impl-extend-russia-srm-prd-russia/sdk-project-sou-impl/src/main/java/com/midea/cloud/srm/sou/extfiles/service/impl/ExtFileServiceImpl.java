package com.midea.cloud.srm.sou.extfiles.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.ZipUtil;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.extfiles.service.ExtFileService;
import com.midea.cloud.srm.sou.extfiles.service.mapper.ExtFileMapper;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Service
@Slf4j
public class ExtFileServiceImpl implements ExtFileService {

    @Autowired
    private ExtFileCenterClient extFileCenterClient;

    @Autowired
    private ExtFileMapper extFileMapper;

    /**
     * b单位
     */
    private BigDecimal BYTES_SIZE = new BigDecimal(1024);

    /**
     * 限制200M
     */
    private BigDecimal LIMIT_SIZE = new BigDecimal(200);

    @Override
    public void batchDownloadToZip(List<Long> fileIdList, HttpServletResponse response) throws Exception {
        if(CollectionUtils.isEmpty(fileIdList)) {
            throw new BaseException("批量下载附件接口请求参数为空！");
        }

        Map<String, Object> params = new HashMap<>(50);
        params.put("fileuploadIdList", fileIdList);
        List<Fileupload> fileUploadList = extFileMapper.listFile(params);

        if(CollectionUtils.isEmpty(fileUploadList)) {
            throw new BaseException("批量下载附件接口请求参数有误！");
        }

        AtomicReference<BigDecimal> byteSize = new AtomicReference<>(new BigDecimal(0));
        fileUploadList.stream().forEach(fileUpload -> {
            byteSize.set(byteSize.get().add(ObjectUtils.defaultIfNull(fileUpload.getFileSize(), BigDecimal.ZERO)));
        });

        BigDecimal limitSize = LIMIT_SIZE.multiply(BYTES_SIZE).multiply(BYTES_SIZE);

        if(byteSize.get().compareTo(limitSize) == 1) {
            throw new BaseException("批量下载附件超过200M，禁止操作！");
        }
        //压缩文件
        Map<String, InputStream> zipSource = new HashMap<>(16);
        String fileRoot = StringUtils.joinWith("", "批量附件压缩包[", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_10), "]");
        String fileName = URLEncoder.encode(fileRoot + ".zip", "UTF-8").replaceAll("\\+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");
        fileUploadList.stream().forEach(fileupload -> {
            String subFileName = MessageFormat.format("{0}\\{1}", fileRoot, fileupload.getFileSourceName());
            try {

                zipSource.put(subFileName, downLoadByFileId(fileupload.getFileuploadId()));
            } catch (Exception e) {
                log.error("downloadBusinessFile downLoadByFileId Exception", e);
            }
        });

        ZipUtil.toZip(zipSource, response.getOutputStream());
    }

    private InputStream downLoadByFileId(Long fileId) throws Exception{
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileId);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        InputStream inputStream = response1.body().asInputStream();
        return inputStream;
    }
}
