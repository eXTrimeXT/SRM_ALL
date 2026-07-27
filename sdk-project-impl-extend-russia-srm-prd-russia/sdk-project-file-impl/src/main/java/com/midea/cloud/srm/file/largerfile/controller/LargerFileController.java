package com.midea.cloud.srm.file.largerfile.controller;

import com.midea.cloud.srm.file.largerfile.dto.*;
import com.midea.cloud.srm.file.largerfile.entity.FileUploadPart;
import com.midea.cloud.srm.file.largerfile.service.LargerFileService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;


/**
 * 大文件上传 - 接口层
 *
 * @author FuBiao
 * @since 2024/06/26
 */
@RestController
@RequestMapping("/largerfile")
@Api(tags = "大文件上传接口")
@Slf4j
public class LargerFileController extends BaseController {
    @Resource
    private LargerFileService largerFileService;
    /**
     * 前端传入文件MD5值到后端，进行秒传验证，如果存在则秒传
     */
    @PostMapping("/checkMd5")
    public CheckMd5ResultDto checkMd5(@RequestBody CheckMd5Dto checkMd5Dto) throws Exception {
       return largerFileService.checkMd5(checkMd5Dto);
    }
    /**
     * 分片校验接口
     */
    @PostMapping("/checkChunk")
    public List<CheckChunkResultDto> checkChunk(@RequestBody CheckMd5Dto checkMd5Dto) throws Exception {
        return largerFileService.checkChunk(checkMd5Dto);
    }
    /**
     * 分片上传接口
     */
    @PostMapping("/saveChunk")
    public FileUploadPart saveChunk(@RequestParam("file") MultipartFile file, FileUploadPart fileupload) throws Exception {
        return largerFileService.saveChunk(file,fileupload);
    }
    /**
     * 分片合并接口
     */
    @PostMapping("/mergeChunk")
    public Long mergeChunk(@RequestBody MergeChunkDto mergeChunkDto) throws Exception {
        return largerFileService.mergeChunk(mergeChunkDto);
    }
    /**
     * 文件下载接口（获取下载签名）
     */
    @GetMapping("/downloadSign")
    public DownLoadResultDto downloadSign(@RequestParam("fileuploadId") String fileuploadId) throws Exception {
        return largerFileService.downloadSign(fileuploadId,true);
    }
}
