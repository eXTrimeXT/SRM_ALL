package com.midea.cloud.srm.file.largerfile.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.file.upload.mapper.FileuploadMapper;
import com.midea.cloud.file.upload.service.IFileuploadService;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlUpdateWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.file.constant.FileConstant;
import com.midea.cloud.srm.file.largerfile.dto.*;
import com.midea.cloud.srm.file.largerfile.entity.FileUploadPart;
import com.midea.cloud.srm.file.largerfile.enums.DictEnum;
import com.midea.cloud.srm.file.largerfile.service.LargerFileService;
import com.midea.cloud.srm.file.util.IpUtil;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * 备注
 * @author FuBiao
 */
@Slf4j
@Service
public class LargerFileServiceImpl implements LargerFileService {
    @Value("${meicloud.paas.osca.bucket}")
    private String bucket;
    @Value("${meicloud.paas.osca.largeFileByInUrl}")
    private String largeFileUrl;
    @Value("${meicloud.paas.osca.largeFileByExtUrl}")
    private String largeFileByExtUrl;
    @Autowired
    private OSS ossClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private FileuploadMapper fileuploadMapper;
    @Autowired
    private IFileuploadService iFileuploadService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RSALockUtil rsaLockUtil;

    public static final Long SIGN_CASH_SECONDS = 14400L;

    @Override
    public CheckMd5ResultDto checkMd5(CheckMd5Dto checkMd5Dto)  {
        CheckMd5ResultDto checkMd5ResultDto=new CheckMd5ResultDto();
        //查询scc_file_fileupload表
        Fileupload fileupload=new Fileupload();
        fileupload.setFingerprint(checkMd5Dto.getFingerprint());
        fileupload.setUploadType(checkMd5Dto.getUploadType());
        fileupload.setFileType(checkMd5Dto.getFileType());
        fileupload.setFileFunction(checkMd5Dto.getFileFunction());
        fileupload.setFileModular(checkMd5Dto.getFileModular());
        fileupload.setSourceType(checkMd5Dto.getSourceType());
        Fileupload fileuploadResult = iFileuploadService.selectFirst(new QueryWrapper<>(fileupload));
        //如果有值，则返回fileUploadId
        //如果没有值，则返回组装分片ID
        if(Objects.nonNull(fileuploadResult)){
            Fileupload fileuploadNew=new Fileupload();
            //验证 字段 创建人创建时间 等等 
            BeanUtils.copyProperties(fileuploadResult,fileuploadNew);
            fileuploadNew.setFingerprint(checkMd5Dto.getFingerprint());
            fileuploadNew.setUploadType(checkMd5Dto.getUploadType());
            fileuploadNew.setFileType(checkMd5Dto.getFileType());
            fileuploadNew.setFileFunction(checkMd5Dto.getFileFunction());
            fileuploadNew.setFileModular(checkMd5Dto.getFileModular());
            fileuploadNew.setSourceType(checkMd5Dto.getSourceType());
            //BeanUtils.copyProperties(fileupload,fileuploadNew);
            fileuploadNew.setFileuploadId(IdGenrator.generate());
            iFileuploadService.save(fileuploadNew);

            checkMd5ResultDto.setFileUploadId(fileuploadNew.getFileuploadId().toString());
            checkMd5ResultDto.setIsUpfile(YesOrNo.YES.getValue());
            return checkMd5ResultDto;
        }
        //检查分片表
        CheckChunkResultDto  dto  = checkChunk(checkMd5Dto).stream().findFirst().orElse(null);
        if(Objects.nonNull(dto)){
            checkMd5ResultDto.setIsUpfile(YesOrNo.NO.getValue());
            checkMd5ResultDto.setUploadId(dto.getUploadId());
            checkMd5ResultDto.setFileFullname(dto.getFileFullname());
            return checkMd5ResultDto;
        }

        //获取后缀名
        if(checkMd5Dto.getFileSourceName()==null){
            throw new BaseException("请传入原文件名");
        }

        String fileType = checkMd5Dto.getFileSourceName().substring(checkMd5Dto.getFileSourceName().lastIndexOf(".") + 1).toLowerCase();
        String originalFilename = UUID.randomUUID().toString();
        String uuidName=originalFilename+"."+fileType;
        //调用阿里方法获取uploadId
        String uploadId=loadUploadId(uuidName);
        checkMd5ResultDto.setIsUpfile(YesOrNo.NO.getValue());
        checkMd5ResultDto.setUploadId(uploadId);
        checkMd5ResultDto.setFileFullname(uuidName);
        return checkMd5ResultDto;
    }


    protected String loadUploadId(String name) {
        String uploadId;
        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucket, name);
            // 初始化分片。
            InitiateMultipartUploadResult upResult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId。
            uploadId = upResult.getUploadId();
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        return uploadId;
    }

    @Override
    public List<CheckChunkResultDto> checkChunk(CheckMd5Dto checkMd5Dto)  {
        List<CheckChunkResultDto> checkChunkList;
        QlQueryWrapper qlQueryWrapper = QlWrappers.query("FileUploadPart");
        qlQueryWrapper.eq(CheckChunkResultDto::getFingerprint, checkMd5Dto.getFingerprint());
        checkChunkList= qlService.queryByWrapper(qlQueryWrapper,CheckChunkResultDto.class);
        return checkChunkList;
    }

    /**
     * @param file 传参
     * @param fileUploadPart 传参
     * @return FileUploadPart
     * @throws Exception 异常信息
     */
    @Override
    public FileUploadPart saveChunk(MultipartFile file, FileUploadPart fileUploadPart) throws Exception {
        //上传分片
        this.uploadPart(fileUploadPart, file);
        //添加分片列表
        fileUploadPart.setFilePureName(bucket+fileUploadPart.getFileFullname());
        fileUploadPart.setFilePath(bucket + "|" + fileUploadPart.getFileFullname());
        fileUploadPart.setUploadStatus(YesOrNo.YES.getValue());
        fileUploadPart.setUploadType(fileUploadPart.getUploadType());
        fileUploadPart.setFileExtendType(fileUploadPart.getFileSourceName().substring(fileUploadPart.getFileSourceName().lastIndexOf(".") + 1).toLowerCase());
        List<FileUploadPart> fileUploadPartList=new ArrayList<>();
        fileUploadPartList.add(fileUploadPart);
        qlService.save("FileUploadPart",fileUploadPartList);
        return fileUploadPart;
    }
    private void uploadPart(FileUploadPart fileUploadPart, MultipartFile file) throws Exception {
        InputStream is = null;
        try {
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucket);
            uploadPartRequest.setKey(fileUploadPart.getFileFullname());
            uploadPartRequest.setUploadId(fileUploadPart.getUploadId());
            // 设置上传的分片流。
            is = file.getInputStream();
            uploadPartRequest.setInputStream(is);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
            uploadPartRequest.setPartNumber(fileUploadPart.getChunkNum().intValue());
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            ossClient.uploadPart(uploadPartRequest);
        } finally {
            org.apache.commons.compress.utils.IOUtils.closeQuietly(is);
        }
    }

    @Override
    public Long mergeChunk(MergeChunkDto mergeChunkDto)  {
        //获取所有分片
        List<PartETag> partETags = listParts(mergeChunkDto);
        //分片合并
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucket, mergeChunkDto.getFileFullname(), mergeChunkDto.getUploadId(), partETags);
        // 完成分片上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        //新增附件主表
        //验证分片是否上传过，没有则进行上传
        QlQueryWrapper qlQueryWrapper = QlWrappers.query("FileUploadPart");
        qlQueryWrapper.eq(FileUploadPart::getFingerprint, mergeChunkDto.getFingerprint());
        qlQueryWrapper.eq(FileUploadPart::getUploadId, mergeChunkDto.getUploadId());
        List<FileUploadPart> fileUploadList= qlService.queryByWrapper(qlQueryWrapper,FileUploadPart.class);
        Fileupload fileupload=new Fileupload();
        if(fileUploadList.size()>0){
            BigDecimal fileSize=new BigDecimal(fileUploadList.get(0).getFileSize());
            fileupload.setFileSize(fileSize);
            fileupload.setFingerprint(fileUploadList.get(0).getFingerprint());
            fileupload.setUploadType(fileUploadList.get(0).getUploadType());
            fileupload.setFileType(fileUploadList.get(0).getFileType());
            fileupload.setFileFunction(fileUploadList.get(0).getFileFunction());
            fileupload.setFileModular(fileUploadList.get(0).getFileModular());
            fileupload.setSourceType(fileUploadList.get(0).getSourceType());
            fileupload.setFileFullname(fileUploadList.get(0).getFileFullname());
            fileupload.setFileSourceName(fileUploadList.get(0).getFileSourceName());

        }else{
            throw new ServiceException("未查询到分片记录");
        }
        Long resultId= addMainFileInfo(fileupload);
        // 删除分片列表
        QlUpdateWrapper qlUpdateWrapper = QlWrappers.update("FileUploadPart");
        qlUpdateWrapper.eq(FileUploadPart::getFingerprint, mergeChunkDto.getFingerprint());
        qlUpdateWrapper.eq(FileUploadPart::getUploadId, mergeChunkDto.getUploadId());
        qlService.deleteByWrapper(qlUpdateWrapper);
        return resultId;


    }
    protected List<PartETag> listParts(MergeChunkDto mergeChunkDto) {
        List<PartETag> partETags = new ArrayList<>();
        try {
            // 列举所有已上传的分片。
            PartListing partListing;
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucket, mergeChunkDto.getFileFullname(), mergeChunkDto.getUploadId());

            do {
                partListing = ossClient.listParts(listPartsRequest);

                for (PartSummary part : partListing.getParts()) {
                    PartETag partETag = new PartETag(part.getPartNumber(),part.getETag());
                    partETags.add(partETag);
                }
                // 指定List的起始位置，只有分片号大于此参数值的分片会被列出。
                listPartsRequest.setPartNumberMarker(partListing.getNextPartNumberMarker());
            } while (partListing.isTruncated());
        } catch (Exception e) {
            log.error("OSS列举分片",e);
            throw new BaseException(e.getMessage());
        }
        return partETags;
    }

    /**
     * 新增file主表数据
     * @param fileupload 传参
     * @return 结果
     */
    protected Long addMainFileInfo(Fileupload fileupload) {
        Long mainId=IdGenrator.generate();
        fileupload.setFileuploadId(mainId);
        fileupload.setFingerprint(fileupload.getFingerprint());
        fileupload.setUploadType(fileupload.getUploadType());
        fileupload.setFileType(fileupload.getFileType());
        fileupload.setFileFunction(fileupload.getFileFunction());
        fileupload.setFileModular(fileupload.getFileModular());
        fileupload.setSourceType(fileupload.getSourceType());
        fileupload.setFileFullname(fileupload.getFileFullname());
        fileupload.setFilePureName(bucket+fileupload.getFileFullname());
        fileupload.setFileSize(fileupload.getFileSize());
        fileupload.setFilePath(bucket + "|" + fileupload.getFileFullname());
        fileupload.setFileExtendType(fileupload.getFileSourceName().substring(fileupload.getFileSourceName().lastIndexOf(".") + 1).toLowerCase());
        this.fileuploadMapper.insert(fileupload);
        return mainId;
    }
    @Override
    public DownLoadResultDto downloadSign(String fileuploadId, boolean checkFileSize) throws Exception {
        String fileUrl;
        String ip = getIp();
        if(IpUtil.isInnerIP(ip)){
            fileUrl=largeFileUrl;
        }else{
            fileUrl=largeFileByExtUrl;
        }
        DownLoadResultDto downLoadResultDto=new DownLoadResultDto();
        String sign;
        Fileupload fileuploadEntity = new Fileupload();
        fileuploadEntity.setFileuploadId(Long.parseLong(rsaLockUtil.decrypt(fileuploadId)));
        Fileupload fileuploadResult = iFileuploadService.selectFirst(new QueryWrapper<>(fileuploadEntity));
        //验证是否使用大文件下载
        if(checkFileSize){
            if(YesOrNo.NO.getValue().equals(downLoadFileType(fileuploadResult))){
                downLoadResultDto.setIsTrue(YesOrNo.NO.getValue());
                return downLoadResultDto;
            }
        }

        //读取redis的值，如果有则直接返回
        String redisSign = redisUtil.get(FileConstant.OSS_STS_SIGN_PREFIX+fileuploadResult.getFileFullname());
        if(StringUtils.isNotEmpty(redisSign)){
            downLoadResultDto.setSign(redisSign);
            downLoadResultDto.setIsTrue(YesOrNo.YES.getValue());
            downLoadResultDto.setFileFullname(fileuploadResult.getFileFullname());
            downLoadResultDto.setFileUrl(fileUrl+fileuploadResult.getFileFullname()+"?"+redisSign);

            return downLoadResultDto;
        }
        URL url = getSignCoverFileNameUrl(fileuploadResult);

        int index = url.toString().indexOf('?');
        if (index != -1) {
            sign = url.toString().substring(index + 1);
            downLoadResultDto.setSign(sign);
            downLoadResultDto.setIsTrue(YesOrNo.YES.getValue());
            downLoadResultDto.setFileFullname(fileuploadResult.getFileFullname());
            downLoadResultDto.setFileUrl(fileUrl+fileuploadResult.getFileFullname()+"?"+sign);
            //写入redis
            redisUtil.set(FileConstant.OSS_STS_SIGN_PREFIX+fileuploadResult.getFileFullname(), sign, SIGN_CASH_SECONDS);
        }
        return downLoadResultDto;
    }

    private URL getSignCoverFileNameUrl(Fileupload fileuploadResult) {
        String objectName;
        objectName = fileuploadResult.getFileFullname();
        // 设置签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000L * 9);
        //  填写Bucket名称，例如examplebucket。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectName);
        //设置过期时间
        request.setExpiration(expiration);
        //方法一： 直接覆盖请求头
        ResponseHeaderOverrides Headers=new ResponseHeaderOverrides();
        Headers.setContentDisposition(String.format("attachment;filename=%s", fileuploadResult.getFileSourceName()));
        request.setResponseHeaders(Headers);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        return ossClient.generatePresignedUrl(request);
    }

    protected String downLoadFileType(Fileupload fileuploadResult) {
        //200兆转换成字节
        //根据功能code判定是否可以用大附件上传
        List<DictItem> dictList = baseClient.listDictItemByDictCode(DictEnum.FILE_SIZE.getCode());
        long minSize=0L;
        for(DictItem sictItem:dictList){
            if(sictItem.getDictItemCode().equals(DictEnum.LARGE_FILE_MIN_SIZE.getCode())){
                minSize=Long.parseLong(sictItem.getDictItemMark());
                break;
            }
        }
        BigDecimal fileSize =BigDecimal.valueOf(minSize*1024*1024);
        //对比当前字节数，如果大于200兆，采用流式下载，小于等于200兆采用普通下载
        if (fileuploadResult.getFileSize().compareTo(fileSize) > 0) {
            return YesOrNo.YES.getValue();
        } else {
            return YesOrNo.NO.getValue();
        }
    }
    private static String getIp() {
        HttpServletRequest request = HttpServletHolder.getRequest();
        return request == null ? "127.0.0.1" : IPUtil.getRemoteIpAddr(request);
    }
}
