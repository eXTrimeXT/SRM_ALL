package com.midea.cloud.srm.file.bidbusinessfile.service.impl;

import com.aliyun.oss.OSS;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.file.archivist.service.BidArchivistService;
import com.midea.cloud.srm.file.bidbusinessfile.service.BidBusinessFileService;
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
public class BidBusinessFileServiceImpl implements BidBusinessFileService {

    public static final int EXPIRE_TIME = 60;
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

    private static final String BUSINESS_LOCK = "BUSINESS_LOCK";

    @Override
    public void downloadBusinessFile(Long projectId, HttpServletResponse response) {
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            String lock = StringUtils.joinWith(SrmConstant.UNDER_LINE, BUSINESS_LOCK, AppUserUtil.getUserName(), projectId);
            if(redisUtil.hasLock(lock)) {
                throw new BaseException("正在下载中，请稍后再试！");
            }
            if(!redisUtil.tryLock(lock, EXPIRE_TIME, TimeUnit.SECONDS)) {
                throw new BaseException("获取下载任务锁失败，请稍后再试！");
            }

            Map<String, Object> fileMap = souExtClient.listDownloadBusinessFile(projectId);
            if(MapUtils.isEmpty(fileMap)) {
                throw new BaseException("商务附件为空");
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
            log.error("downloadBusinessFile Exception", e);
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
            List<Map<String, Object>> rounds = (List<Map<String, Object>>) object;
            for (Map<String , Object> round : rounds ){
                for (String roundName : round.keySet()) {
                    List<Map<String, Object>> fileList = (List<Map<String, Object>>)round.get(roundName);
                    fileList.stream().forEach(file -> {
                        Long docId = (Long) file.get("docId");
                        String docName = (String) file.get("docName");
                        int count = 1;
                        String originalDocName = docName;
                        while (fileData.containsKey(StringUtils.joinWith(splite, filePath,roundName, docName))) {
                            String[] parts = originalDocName.split("\\.");
                            String prefix = StringUtils.join(Arrays.copyOfRange(parts, 0, parts.length - 1), ".");
                            String suffix = parts[parts.length - 1];
                            docName = String.format("%s(%d).%s", prefix, count, suffix);
                            count++;
                        }

                        fileData.put(StringUtils.joinWith(splite, filePath,roundName,docName), docId);
                    });
                }
            }

        } else {
            Map<String, Object> fileMap = (Map<String, Object>) object;
            for(String key : fileMap.keySet()) {
                downloadFile(fileMap.get(key),StringUtils.joinWith(splite, filePath, key), splite, fileData);
            }
        }

    }
}
