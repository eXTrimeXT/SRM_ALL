package com.midea.cloud.srm.file.largerfile.service;

import com.midea.cloud.srm.file.largerfile.dto.*;
import com.midea.cloud.srm.file.largerfile.entity.FileUploadPart;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 备注
 * @author FuBiao
 */
public interface LargerFileService {
    /**
     * 根据MD5验证是否秒传
     * @param checkMd5Dto 传参
     * @return 结果
     * @throws Exception 抛出异常
     */
    CheckMd5ResultDto checkMd5(CheckMd5Dto checkMd5Dto) throws Exception;


    /**
     * 分片校验接口
     * @param checkMd5Dto 传参
     * @return 结果
     * @throws Exception 抛出异常
     */
    List<CheckChunkResultDto> checkChunk(CheckMd5Dto checkMd5Dto) throws Exception;

    /**
     * 分片上传接口
     * @param file 传参
     * @param fileupload  大文件上传分片表 模型
     * @return 结果
     * @throws Exception 抛出异常
     */

    FileUploadPart saveChunk(MultipartFile file, FileUploadPart fileupload) throws Exception;

    /**
     * 分片合并接口
     * @param mergeChunkDto 传参
     * @return 结果
     * @throws Exception 抛出异常
     */

    Long mergeChunk(MergeChunkDto mergeChunkDto) throws Exception;

    /**
     * 分片上传接口
     * @param objectName 传参
     * @return 结果
     * @throws Exception 抛出异常
     */

    DownLoadResultDto downloadSign(String objectName, boolean checkFileSize) throws Exception;

}
