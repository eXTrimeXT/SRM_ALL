package com.midea.cloud.srm.file.archivist.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.alibaba.excel.EasyExcel;
import com.aliyun.oss.OSS;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.file.upload.service.IFileuploadService;
import com.midea.cloud.srm.file.archivist.enums.BidArchivistEnums;
import com.midea.cloud.srm.file.archivist.excel.writer.NpmSouBidCellWriteHandler;
import com.midea.cloud.srm.file.archivist.mapper.BidArchivistMapper;
import com.midea.cloud.srm.file.archivist.service.BidArchivistService;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreLineQueryDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/13
 */
@Slf4j
@Service
public class BidArchivistServiceImpl implements BidArchivistService {

    @Autowired
    private IFileuploadService iFileuploadService;

    @Autowired
    private OSS ossClient;

    @Value("${meicloud.paas.osca.bucket}")
    private String bucket;

    @Autowired
    private SouExtClient souExtClient;

    @Resource
    private BidArchivistMapper bidArchivistMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String ARCHIVIST_LOCK = "ARCHIVIST_LOCK";

    private static final String BATCHZIP_LOCK = "BATCHZIP_LOCK";

    @Override
    public void archivist(Long projectId, HttpServletResponse response) {

        try {

            String lock = StringUtils.joinWith(SrmConstant.UNDER_LINE, ARCHIVIST_LOCK, AppUserUtil.getUserName(), projectId);
            if(redisUtil.hasLock(lock)) {
                throw new BaseException("正在下载中，请稍后再试！");
            }
            if(!redisUtil.tryLock(lock, 60, TimeUnit.SECONDS)) {
                throw new BaseException("获取下载任务锁失败，请稍后再试！");
            }

            ExtSouProject project = bidArchivistMapper.queryProjectById(projectId);
            if(Objects.isNull(project)) {
                project = new ExtSouProject();
            }
            String fileName = MessageFormat.format("招标流程[{0}]附件归档.zip", project.getExtProjectNo());
            //替换空格 不然会变为加号
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");

            ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(response.getOutputStream());

            //查询附件信息
            Map<String, Object> fileInfo = queryFile(projectId);

            //压缩申请资料
            zipRequirement(fileInfo, zipArchiveOutputStream);

            //压缩招标文件
            zipDataSubmit(fileInfo, zipArchiveOutputStream);

            //压缩投标文件
            zipOrder(fileInfo, zipArchiveOutputStream);

            //评标文件
            zipBidScore(projectId, zipArchiveOutputStream);

            //澄清文件
            zipAnswer(fileInfo, zipArchiveOutputStream);

            //汇总文件
            zipSum(fileInfo, zipArchiveOutputStream);

            //结果文件
            zipResult(fileInfo, zipArchiveOutputStream);

            //其他文件
            zipOther(fileInfo, zipArchiveOutputStream);

            zipArchiveOutputStream.closeArchiveEntry();
            zipArchiveOutputStream.flush();
            zipArchiveOutputStream.finish();

            //释放锁
            redisUtil.unLock(lock);

        } catch (Exception e) {
            log.info("下载OSS文件异常", e);
            throw new BaseException(e.getMessage());
        } catch (Throwable throwable) {
            log.info("下载OSS文件异常", throwable);
            throw new BaseException(throwable.getMessage());
        }

    }

    /**
     * 评标文件
     * @param projectId
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipBidScore(Long projectId, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {
        ApiExtSouTechScoreLineQueryDTO query = new ApiExtSouTechScoreLineQueryDTO();
        query.setProjectId(projectId);
        List<Map<String, Object>> bidScoreList = souExtClient.exportScoreExcelForArchivist(query);

        if(CollectionUtils.isNotEmpty(bidScoreList)) {
            for(Map<String, Object> bidScoreMap : bidScoreList) {
                addFile(bidScoreMap, zipArchiveOutputStream);
            }
        }
    }

    /**
     * 澄清文件
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipAnswer(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {
        List<Long> answerFileList = (List<Long>) fileInfo.get(BidArchivistEnums.ANSWER.name());
        if(CollectionUtils.isEmpty(answerFileList)) {
            return;
        }
        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "澄清文件/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);
        answerFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
            Fileupload fileupload = fileuploadMap.get(fileId);
            String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
            //压缩文件
            addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
        });
    }

    /**
     * 结果文件
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipResult(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {
        List<Long> noticeFileList = (List<Long>) fileInfo.get(BidArchivistEnums.NOTICE.name());
        if(CollectionUtils.isEmpty(noticeFileList)) {
            return;
        }
        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "结果文件/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);
        noticeFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
            Fileupload fileupload = fileuploadMap.get(fileId);
            String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
            //压缩文件
            addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
        });
    }

    /**
     * 申请资料
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipRequirement(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {
        List<Long> requirementFileList = (List<Long>) fileInfo.get(BidArchivistEnums.REQUIREMENT.name());
        if(CollectionUtils.isEmpty(requirementFileList)) {
            return;
        }
        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "申请资料/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);
        requirementFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
            Fileupload fileupload = fileuploadMap.get(fileId);
            String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
            //压缩文件
            addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
        });
    }

    /**
     * 其他资料
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipOther(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {

        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "其他文件/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);

        //归档上传的附件
        Map<String, List<Long>> souFileMap = (Map<String, List<Long>>) fileInfo.get(BidArchivistEnums.SOU_MAP.name());
        if(MapUtils.isNotEmpty(souFileMap)) {
            for(String key : souFileMap.keySet()) {
                if(Arrays.asList("ARCHIVE").contains(key)) {
                    souFileMap.get(key).stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                        Fileupload fileupload = fileuploadMap.get(fileId);
                        String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                        //压缩文件
                        addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
                    });
                }
            }
        }

        //推荐单
        List<Long> recomFileList = (List<Long>) fileInfo.get(BidArchivistEnums.RECOM.name());
        if(CollectionUtils.isNotEmpty(recomFileList)) {
            recomFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                Fileupload fileupload = fileuploadMap.get(fileId);
                String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                //压缩文件
                addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
            });
        }


        //标前交流
        List<Long> preFileList = (List<Long>) fileInfo.get(BidArchivistEnums.PRE.name());
        if(CollectionUtils.isNotEmpty(preFileList)) {
            preFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                Fileupload fileupload = fileuploadMap.get(fileId);
                String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                //压缩文件
                addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
            });
        }

    }

    /**
     * 招标文件
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipDataSubmit(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {
        List<Long> dataSubmitFileList = (List<Long>) fileInfo.get(BidArchivistEnums.DATA_SUBMIT.name());
        if(CollectionUtils.isEmpty(dataSubmitFileList)) {
            return;
        }
        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "招标文件/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);
        dataSubmitFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
            Fileupload fileupload = fileuploadMap.get(fileId);
            String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
            //压缩文件
            addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
        });
    }

    /**
     * 投标文件
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipOrder(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {

        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }

        //技术标+商务标
        Map<String, List<Long>> orderFileMap = (Map<String, List<Long>>) fileInfo.get(BidArchivistEnums.ORDER_MAP.name());

        String filePath = "投标文件/";
        Map<String, Set<String>> fileNameSetMap = new HashMap<>(15);
        Map<String, Map<String, Integer>> repeatIndexMap = new HashMap<>(15);

        zipVendorFile(filePath, fileNameSetMap, repeatIndexMap, orderFileMap, fileuploadMap, zipArchiveOutputStream);


        //质疑
        Map<String, List<Long>> questionFileMap = (Map<String, List<Long>>) fileInfo.get(BidArchivistEnums.QUESTION_MAP.name());
        zipVendorFile(filePath, fileNameSetMap, repeatIndexMap, questionFileMap, fileuploadMap, zipArchiveOutputStream);

        //质疑
        Map<String, List<Long>> marginFileMap = (Map<String, List<Long>>) fileInfo.get(BidArchivistEnums.MARGIN_MAP.name());
        zipVendorFile(filePath, fileNameSetMap, repeatIndexMap, marginFileMap, fileuploadMap, zipArchiveOutputStream);

    }

    /**
     * 压缩供应商附件
     * @param filePath
     * @param fileNameSetMap
     * @param repeatIndexMap
     * @param vendorFileMap
     * @param fileuploadMap
     * @param zipArchiveOutputStream
     */
    private void zipVendorFile(String filePath, Map<String, Set<String>> fileNameSetMap, Map<String, Map<String, Integer>> repeatIndexMap, Map<String, List<Long>> vendorFileMap, Map<Long, Fileupload> fileuploadMap, ZipArchiveOutputStream zipArchiveOutputStream) {
        if(MapUtils.isNotEmpty(vendorFileMap)) {
            for(String vendorName: vendorFileMap.keySet()) {
                List<Long> fileIdList = vendorFileMap.get(vendorName);
                Set<String> fileNameSet = fileNameSetMap.getOrDefault(vendorName, new HashSet<>(15));
                fileNameSetMap.put(vendorName, fileNameSet);

                Map<String, Integer> repeatIndex = repeatIndexMap.getOrDefault(vendorName, new HashMap<>(15));
                repeatIndexMap.put(vendorName, repeatIndex);

                fileIdList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                    Fileupload fileupload = fileuploadMap.get(fileId);
                    String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                    //压缩文件
                    addFile(fileupload.getFileFullname(), filePath + vendorName + "/" + fileName, zipArchiveOutputStream);
                });
            }
        }
    }

    /**
     * 获取文件名字
     * @param fileName
     * @param fileNameSet
     * @param repeatIndex
     * @return
     */
    private String fileName(String fileName, Set<String> fileNameSet, Map<String, Integer> repeatIndex) {
        String point = ".";
        String sourceFileName = fileName;
        if(fileNameSet.contains(fileName)) {
            Integer index = repeatIndex.getOrDefault(fileName, 0) + 1;
            repeatIndex.put(fileName, index);
            fileName = fileName.substring(0, fileName.lastIndexOf(point)) + "(" + index + ")" + fileName.substring(fileName.lastIndexOf(point));
        }
        fileNameSet.add(sourceFileName);
        return fileName;
    }

    /**
     * 汇总文件
     * @param fileInfo
     * @param zipArchiveOutputStream
     * @throws Exception
     */
    private void zipSum(Map<String, Object> fileInfo, ZipArchiveOutputStream zipArchiveOutputStream) throws Exception {

        Map<Long, Fileupload> fileuploadMap = (Map<Long, Fileupload>) fileInfo.get(BidArchivistEnums.FILE_MAP.name());
        if(MapUtils.isEmpty(fileuploadMap)) {
            return;
        }
        String filePath = "汇总文件/";
        Set<String> fileNameSet = new HashSet<>(15);
        Map<String, Integer> repeatIndex = new HashMap<>(15);

        List<Long> caFileList = (List<Long>) fileInfo.get(BidArchivistEnums.CA.name());

        if(CollectionUtils.isNotEmpty(caFileList)) {
            caFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                Fileupload fileupload = fileuploadMap.get(fileId);
                String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                //压缩文件
                addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
            });
        }

        //招标附件
        Map<String, List<Long>> souFileMap = (Map<String, List<Long>>) fileInfo.get(BidArchivistEnums.SOU_MAP.name());
        if(MapUtils.isNotEmpty(souFileMap)) {
            for(String key : souFileMap.keySet()) {
                if(Arrays.asList("APPLY", "BID", "TALK").contains(key)) {
                    souFileMap.get(key).stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                        Fileupload fileupload = fileuploadMap.get(fileId);
                        String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                        //压缩文件
                        addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
                    });
                }
            }
        }


        //考察附件
        List<Long> inspectFileList = (List<Long>) fileInfo.get(BidArchivistEnums.INSPECT.name());

        if(CollectionUtils.isNotEmpty(inspectFileList)) {
            inspectFileList.stream().filter(id -> fileuploadMap.containsKey(id)).forEach(fileId -> {
                Fileupload fileupload = fileuploadMap.get(fileId);
                String fileName = fileName(fileupload.getFileSourceName(), fileNameSet, repeatIndex);
                //压缩文件
                addFile(fileupload.getFileFullname(), filePath+fileName, zipArchiveOutputStream);
            });
        }

    }

    /**
     * 查询附件
     * @param projectId
     * @return
     */
    private Map<String, Object> queryFile(Long projectId) {

        Map<String, Object> info = new HashMap<>(15);
        //查询申请单号
        List<String> requirementHeadNumList = queryRequirementHeadNum(projectId);

        if(CollectionUtils.isEmpty(requirementHeadNumList)) {
            return info;
        }

        List<Long> fileIdList = new ArrayList<>();

        //申请资料附件
        List<Long> requirementFileList = queryRquirementFile(requirementHeadNumList);
        fileIdList.addAll(requirementFileList);
        info.put(BidArchivistEnums.REQUIREMENT.name(), requirementFileList);

        //招标文件附件：招标资料提交的附件
        List<Long> dataSubmitFileList = queryDataSubmitFile(requirementHeadNumList);
        fileIdList.addAll(dataSubmitFileList);
        info.put(BidArchivistEnums.DATA_SUBMIT.name(), dataSubmitFileList);

        //查询投标附件
        List<Map<String, Object>> orderFileList = queryOrderFile(projectId);
        Map<String, List<Long>> orderFileMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(orderFileList)) {
            orderFileMap = orderFileList.stream().collect(Collectors.groupingBy(k -> MapUtils.getString(k, LambdaUtil.getFieldName(ExtSouVendor::getVendorName)), Collectors.mapping(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouOrderFile::getOrderDocId)), Collectors.toList())));
            fileIdList.addAll(orderFileList.stream().map(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouOrderFile::getOrderDocId))).collect(Collectors.toList()));
        }
        info.put(BidArchivistEnums.ORDER_MAP.name(), orderFileMap);

        //质疑附件
        List<Map<String, Object>> questionFileList = queryOrderFile(projectId);
        Map<String, List<Long>> questionFileMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(questionFileList)) {
            questionFileMap = questionFileList.stream().collect(Collectors.groupingBy(k -> MapUtils.getString(k, LambdaUtil.getFieldName(ExtSouVendor::getVendorName)), Collectors.mapping(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(Fileupload::getFileuploadId)), Collectors.toList())));
            fileIdList.addAll(questionFileList.stream().map(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(Fileupload::getFileuploadId))).collect(Collectors.toList()));
        }
        info.put(BidArchivistEnums.QUESTION_MAP.name(), questionFileMap);

        //质疑附件
        List<Map<String, Object>> marginFileList = queryOrderFile(projectId);
        Map<String, List<Long>> marginFileMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(marginFileList)) {
            marginFileMap = marginFileList.stream().collect(Collectors.groupingBy(k -> MapUtils.getString(k, LambdaUtil.getFieldName(ExtSouVendor::getVendorName)), Collectors.mapping(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouMargin::getPayVoucherFileId)), Collectors.toList())));
            fileIdList.addAll(marginFileList.stream().map(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouMargin::getPayVoucherFileId))).collect(Collectors.toList()));
        }
        info.put(BidArchivistEnums.MARGIN_MAP.name(), marginFileMap);

        //查询澄清附件
        List<Long> answerFileList = queryAnswerFileId(projectId);
        fileIdList.addAll(answerFileList);
        info.put(BidArchivistEnums.ANSWER.name(), answerFileList);

        //查询定标申请附件
        List<Long> caFileList = queryCaFileId(projectId);
        fileIdList.addAll(caFileList);
        info.put(BidArchivistEnums.CA.name(), caFileList);

        //查询招标附件
        List<Map<String, Object>> souFileList = querySouFileId(projectId);
        Map<String, List<Long>> souFileMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(souFileList)) {
            souFileMap = souFileList.stream().collect(Collectors.groupingBy(k -> MapUtils.getString(k, LambdaUtil.getFieldName(ExtSouFile::getFileType)), Collectors.mapping(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouFile::getSouDocId)), Collectors.toList())));
            fileIdList.addAll(souFileList.stream().map(k -> MapUtils.getLong(k, LambdaUtil.getFieldName(ExtSouFile::getSouDocId))).collect(Collectors.toList()));
        }
        info.put(BidArchivistEnums.SOU_MAP.name(), souFileMap);

        //查询考察附件
        List<Long> inspectFileList = queryInspectFileId(projectId);
        fileIdList.addAll(inspectFileList);
        info.put(BidArchivistEnums.INSPECT.name(), inspectFileList);

        //查询中落标附件
        List<Long> noticeFileList = queryNoticeFileId(projectId);
        fileIdList.addAll(noticeFileList);
        info.put(BidArchivistEnums.NOTICE.name(), noticeFileList);

        //查询推荐单附件
        List<Long> recomFileList = queryRecomFileId(projectId);
        fileIdList.addAll(recomFileList);
        info.put(BidArchivistEnums.RECOM.name(), recomFileList);

        //查询标前交流附件
        List<Long> preFileList = queryPreFile(requirementHeadNumList);
        fileIdList.addAll(preFileList);

        info.put(BidArchivistEnums.PRE.name(), preFileList);

        Map<Long, Fileupload> fileuploadMap = queryFileuploadByIds(fileIdList);

        info.put(BidArchivistEnums.FILE_MAP.name(), fileuploadMap);

        return info;
    }

    /**
     * 查询附件
     * @param fileIdList
     * @return
     */
    @Override
    public Map<Long, Fileupload> queryFileuploadByIds(List<Long> fileIdList) {
        if(CollectionUtils.isEmpty(fileIdList)) {
            return new HashMap<>(15);
        }
        Map<String, Object> param = new HashMap<>(15);
        param.put("fileuploadIdList", fileIdList);

        return bidArchivistMapper.queryFileuploadByIds(param).stream().collect(Collectors.toMap(k -> k.getFileuploadId(), Function.identity(), (k1, k2) -> k2));
    }

    /**
     * 查询标前交流附件
     * @param requirementHeadNumList
     * @return
     */
    private List<Long> queryPreFile(List<String> requirementHeadNumList) {
        if(CollectionUtils.isEmpty(requirementHeadNumList)) {
            return new ArrayList<>();
        }
        Map<String, Object> map = new HashMap<>(15);
        map.put("requirementHeadNumList", requirementHeadNumList);
        return bidArchivistMapper.queryPreFileId(map);
    }

    /**
     * 查询推荐单附件
     * @param projectId
     * @return
     */
    private List<Long> queryRecomFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryRecomFileId(map);
    }

    /**
     * 查询中落标附件
     * @param projectId
     * @return
     */
    private List<Long> queryNoticeFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryNoticeFileId(map);
    }

    /**
     * 查询考察附件
     * @param projectId
     * @return
     */
    private List<Long> queryInspectFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryInspectFileId(map);
    }

    /**
     * 查询招标附件
     * @param projectId
     * @return
     */
    private List<Map<String, Object>> querySouFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.querySouFileId(map);
    }


    /**
     * 查询定标申请附件
     * @param projectId
     * @return
     */
    private List<Long> queryCaFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryCaFileId(map);
    }

    /**
     * 查询澄清附件
     * @param projectId
     * @return
     */
    private List<Long> queryAnswerFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryAnswerFileId(map);
    }

    /**
     * 查询保证金附件
     * @param projectId
     * @return
     */
    private List<Map<String, Object>> queryMarginFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryMarginFileId(map);
    }

    /**
     * 查询质疑附件
     * @param projectId
     * @return
     */
    private List<Map<String, Object>> queryQuestionFileId(Long projectId) {

        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryQuestionFileId(map);
    }

    /**
     * 查询投标附件
     * @param projectId
     * @return
     */
    private List<Map<String, Object>> queryOrderFile(Long projectId) {
        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        return bidArchivistMapper.queryOrderFile(map);
    }

    /**
     * 查询申请资料附件
     * @param requirementHeadNumList
     * @return
     */
    private List<Long> queryDataSubmitFile(List<String> requirementHeadNumList) {
        if(CollectionUtils.isEmpty(requirementHeadNumList)) {
            return new ArrayList<>();
        }
        Map<String, Object> map = new HashMap<>(15);
        map.put("requirementHeadNumList", requirementHeadNumList);
        return bidArchivistMapper.queryDataSubmitFileId(map);
    }

    /**
     * 查询申请资料附件
     * @param requirementHeadNumList
     * @return
     */
    private List<Long> queryRquirementFile(List<String> requirementHeadNumList) {
        if(CollectionUtils.isEmpty(requirementHeadNumList)) {
            return new ArrayList<>();
        }
        Map<String, Object> map = new HashMap<>(15);
        map.put("requirementHeadNumList", requirementHeadNumList);
        return bidArchivistMapper.queryRequirementFileId(map);
    }

    /**
     * 查询申请单号
     * @param projectId
     * @return
     */
    private List<String> queryRequirementHeadNum(Long projectId) {
        Map<String, Object> map = new HashMap<>(15);
        map.put("projectId", projectId);
        List<ExtSouDemand> souDemands = bidArchivistMapper.querySouDemand(map);
        if(CollectionUtils.isNotEmpty(souDemands)) {
            return souDemands.stream().map(ExtSouDemand::getApplicantNo).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 评标文件
     * @param bidScoreMap
     * @param zipArchiveOutputStream
     */
    private void addFile(Map<String, Object> bidScoreMap, ZipArchiveOutputStream zipArchiveOutputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            List<List<String>> headTitle = (List<List<String>>) bidScoreMap.get("headTitle");
            List<List<Object>> dataList = (List<List<Object>>) bidScoreMap.get("dataList");
            String excelName = (String) bidScoreMap.get("excelName");
            EasyExcel.write(outputStream).registerWriteHandler(new NpmSouBidCellWriteHandler()).sheet(0, "sheetName").head(headTitle).doWrite(dataList);

            String fileName = "评标文件/" + excelName;
            zipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(fileName));

            zipArchiveOutputStream.write(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("评标文件addFile Exception", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("评标文件addFile outputStream close IOException", e);
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

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                zipArchiveOutputStream.write(buffer, 0, len);
            }

        } catch (Exception e) {
            log.error("归档文件addFile Exception", e);
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

    @Override
    public void batchDownloadToZip(List<Long> fileIdList, HttpServletResponse response) throws Exception {
        ZipArchiveOutputStream zipArchiveOutputStream = null;

        try {
            if(CollectionUtils.isEmpty(fileIdList)) {
                throw new BaseException("批量下载附件接口请求参数为空！");
            }
            //基于第一个批量下载附件ID进行锁，解决大部分重复批量下载的问题
            String lock = StringUtils.joinWith(SrmConstant.UNDER_LINE, BATCHZIP_LOCK, AppUserUtil.getUserName(), fileIdList.get(0));
            if(redisUtil.hasLock(lock)) {
                throw new BaseException("当前账号存在其他正在批量下载的任务，请稍后再试！");
            }
            if(!redisUtil.tryLock(lock, 60, TimeUnit.SECONDS)) {
                throw new BaseException("获取下载任务锁失败，请稍后再试！");
            }
            Map<Long, Fileupload> fileMap = this.queryFileuploadByIds(fileIdList);
            String fileRoot = StringUtils.joinWith("", "批量附件压缩包[", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_10), "]");
            String fileName = URLEncoder.encode(fileRoot + ".zip", "UTF-8").replaceAll("\\+", "%20");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");

            zipArchiveOutputStream = new ZipArchiveOutputStream(response.getOutputStream());

            String splite = "/";
            String point = ".";

            Map<String, Integer> fileNameIndex = new HashMap<>(15);

            for(Fileupload fileupload : fileMap.values()) {
                String docName = StringUtils.joinWith(splite, fileupload.getFileSourceName());

                Integer index = fileNameIndex.getOrDefault(docName, 0);
                index++;
                fileNameIndex.put(docName, index);
                if(index.compareTo(1) != 0) {
                    docName = docName.substring(0, docName.lastIndexOf(point)) + "(" + index + ")" + docName.substring(docName.lastIndexOf(point));
                }

                addFile(fileupload.getFileFullname(), docName, zipArchiveOutputStream);
            }

            //释放锁
            redisUtil.unLock(lock);
        } catch (Exception e) {
            log.error("batchDownloadToZip Exception", e);
            throw new BaseException(e.getMessage());
        } finally {
            if(!Objects.isNull(zipArchiveOutputStream)) {
                try {
                    zipArchiveOutputStream.closeArchiveEntry();
                    zipArchiveOutputStream.flush();
                    zipArchiveOutputStream.finish();
                } catch (Exception e) {
                    log.error("batchDownloadToZip zipArchiveOutputStream.closeArchiveEntry Exception", e);
                }
            }
        }


    }

}
