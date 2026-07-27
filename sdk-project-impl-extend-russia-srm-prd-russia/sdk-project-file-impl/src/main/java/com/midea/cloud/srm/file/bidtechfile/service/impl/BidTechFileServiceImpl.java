package com.midea.cloud.srm.file.bidtechfile.service.impl;

import com.aliyun.oss.OSS;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.file.archivist.service.BidArchivistService;
import com.midea.cloud.srm.file.bidtechfile.service.BidTechFileService;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/8/9
 */
@Service
@Slf4j
public class BidTechFileServiceImpl implements BidTechFileService {

    @Autowired
    private OSS ossClient;

    @Value("${meicloud.paas.osca.bucket}")
    private String bucket;

    @Autowired
    private SouExtClient souExtClient;

    @Autowired
    private BidArchivistService bidArchivistService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String TECH_LOCK = "TECH_LOCK";

    @Override
    public void downloadTechFile(Long projectId, HttpServletResponse response) {
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            String lock = StringUtils.joinWith(SrmConstant.UNDER_LINE, TECH_LOCK, AppUserUtil.getUserName(), projectId);
            if(redisUtil.hasLock(lock)) {
                throw new BaseException("正在下载中，请稍后再试！");
            }
            if(!redisUtil.tryLock(lock, 60, TimeUnit.SECONDS)) {
                throw new BaseException("获取下载任务锁失败，请稍后再试！");
            }

            Map<String, Object> fileMap = souExtClient.listDownloadTechPlanFile(projectId);
            if(MapUtils.isEmpty(fileMap)) {
                throw new BaseException("技术附件为空");
            }
            String fileName = (String) fileMap.get("fileName");
            fileMap.remove("fileName");

            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");

            zipArchiveOutputStream = new ZipArchiveOutputStream(response.getOutputStream());

            String splite = "/";

            Map<String, Long> fileData = new HashMap<>(15);

            for(String key : fileMap.keySet()) {
                String filePath = key;
                downloadFile(fileMap.get(key), filePath, splite, fileData);
            }

            Map<Long, Fileupload> fileuploadMap = bidArchivistService.queryFileuploadByIds(new ArrayList<>(fileData.values()));

            for(String docName : fileData.keySet()) {
                Fileupload fileupload = fileuploadMap.get(fileData.get(docName));
                if(!Objects.isNull(fileupload)) {
                    addFile(fileupload.getFileFullname(), docName, zipArchiveOutputStream);
                }
            }

            //释放锁
            redisUtil.unLock(lock);

        } catch (Exception e) {
            log.error("downloadTechFile Exception", e);
            throw new BaseException(e.getMessage());
        } finally {
            if(!Objects.isNull(zipArchiveOutputStream)) {
                try {
                    zipArchiveOutputStream.closeArchiveEntry();
                    zipArchiveOutputStream.flush();
                    zipArchiveOutputStream.finish();
                } catch (Exception e) {
                    log.error("zipArchiveOutputStream.closeArchiveEntry Exception", e);
                }
            }
        }

    }

    /**
     * 添加压缩文件
     * @param ossFileName
     * @param fileName
     * @param zipArchiveOutputStream
     */
    private void addFile(String ossFileName, String fileName, ZipArchiveOutputStream zipArchiveOutputStream) {
        InputStream inputStream = null;

        try {
            inputStream = ossClient.getObject(bucket, ossFileName).getObjectContent();

            zipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(fileName));

            byte[] buffer = new byte[1024*1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                zipArchiveOutputStream.write(buffer, 0, len);
            }

        } catch (Exception e) {
            log.error("商务附件addFile Exception", e);
        } finally {
            if(!Objects.isNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("inputStream close IOException", e);
                }
            }
        }

    }

    private void downloadFile(Object object, String filePath, String splite, Map<String, Long> fileData) {

        if(object instanceof Collection) {
            List<Map<String, Object>> fileList = (List<Map<String, Object>>) object;
            fileList.stream().forEach(file -> {
                Long docId = (Long) file.get("docId");
                String docName = (String) file.get("docName");
                int count = 1;
                String originalDocName = docName;
                while (fileData.containsKey(StringUtils.joinWith(splite, filePath, docName))) {
                    String[] parts = originalDocName.split("\\.");
                    String prefix = StringUtils.join(Arrays.copyOfRange(parts, 0, parts.length - 1), ".");
                    String suffix = parts[parts.length - 1];
                    docName = String.format("%s(%d).%s", prefix, count, suffix);
                    count++;
                }

                fileData.put(StringUtils.joinWith(splite, filePath, docName), docId);
            });
        } else {
            Map<String, Object> fileMap = (Map<String, Object>) object;
            for(String key : fileMap.keySet()) {
                downloadFile(fileMap.get(key),StringUtils.joinWith(splite, filePath, key), splite, fileData);
            }
        }

    }
}
