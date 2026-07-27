package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.ZipUtil;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouFileConfigTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderFileQueryDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtTechFileDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouOrderFileDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouOrderFileMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouPlanMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouOrderFileServiceImpl extends ServiceImpl<ExtSouOrderFileMapper, ExtSouOrderFile> implements IExtSouOrderFileService {

    @Autowired
    private IExtSouVendorService souVendorService;

    @Autowired
    private SouOrderDAO souOrderDAO;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtFileCenterClient extFileCenterClient;

    @Autowired
    private IExtNpmSouOrderService npmSouOrderService;

    @Override
    public List<ExtSouOrderFile> listOrderFile(ApiExtSouOrderFileQueryDto query) {
        LambdaQueryWrapper<ExtSouOrderFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(query.getProjectId()), ExtSouOrderFile::getProjectId, query.getProjectId());
        queryWrapper.eq(StringUtils.isNotBlank(query.getFileType()), ExtSouOrderFile::getFileType, query.getFileType());
        queryWrapper.in(!Objects.isNull(query.getFileTypeList()), ExtSouOrderFile::getFileType, query.getFileTypeList());
        queryWrapper.eq(!Objects.isNull(query.getVendorId()), ExtSouOrderFile::getVendorId, query.getVendorId());
        queryWrapper.in(!Objects.isNull(query.getVendorIdList()), ExtSouOrderFile::getVendorId, query.getVendorIdList());
        queryWrapper.eq(!Objects.isNull(query.getOrderId()), ExtSouOrderFile::getOrderId, query.getOrderId());
        queryWrapper.orderByAsc(ExtSouOrderFile::getOrderFileId).orderByAsc(ExtSouOrderFile::getVendorId).orderByAsc(ExtSouOrderFile::getFileType);
        List<ExtSouOrderFile> fileList = this.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(fileList)) {
            fileList.stream().forEach(file -> {
                file.formattingPackageName();
                file.formattingPackageNameToList();
            });
        }
        return fileList;
    }

    @Override
    public List<ExtSouOrderFile> getScoreTechOrderFile(Long projectId) {
        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(projectId);
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode()));

        return this.listOrderFile(queryDto);
    }

    @Override
    public List<ExtSouOrderFile> getScoreTechOrderFile(Long projectId, Long orderId) {
        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(projectId);
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode()));
        queryDto.setOrderId(orderId);
        return this.listOrderFile(queryDto);
    }

    @Override
    public List<ExtSouOrderFile> getBusOrderFile(Long projectId, Long orderId) {
        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(projectId);
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.BUS_BID.getCode(), ExtSouFileConfigTypeEnum.BUS_OTHER.getCode()));
        queryDto.setOrderId(orderId);
        return this.listOrderFile(queryDto);
    }

    @Override
    public ApiExtTechFileDto getTechPlan(Long projectId) {
        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(projectId);
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode(), ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode()));

        List<ExtSouOrderFile> fileList = this.listOrderFile(queryDto);

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, projectId);

        List<ExtSouVendor> vendorList = souVendorService.list(queryWrapper);
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2)->k2));

        //查看已投标供应商
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId).list();
        List<Long> tenderVendorIdList = orderList.stream().filter(o -> {
            return Integer.compare(ObjectUtils.defaultIfNull(o.getRound(), 1), 1) == 1 || ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType()) || Arrays.asList(SouOrderStatusEnum.SUBMISSION, SouOrderStatusEnum.CANCEL).contains(o.getOrderStatus());
        }).map(o -> o.getVendorId()).distinct().collect(Collectors.toList());

        List<ExtSouOrderFileDto> orderFileList = new ArrayList<>();

        List<ExtSouOrderFileDto> secretFileList = new ArrayList<>();

        fileList.stream().filter(f -> tenderVendorIdList.contains(f.getVendorId())).forEach(f -> {
            ExtSouOrderFileDto fileDto = new ExtSouOrderFileDto();
            BeanCopyUtil.copyProperties(fileDto, f);
            ExtSouVendor vendor = vendorMap.getOrDefault(f.getVendorId(), new ExtSouVendor());
            fileDto.setVendorName(vendor.getVendorName());
            fileDto.setVendorCode(vendor.getVendorCode());

            if(ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode().equals(f.getFileType())) {
                //属于脱敏文件
                secretFileList.add(fileDto);
            } else {
                orderFileList.add(fileDto);
            }

        });

        //返回技术方案
        ApiExtTechFileDto fileDto = new ApiExtTechFileDto();
        fileDto.setOrderFileList(orderFileList);
        fileDto.setSecretFileList(secretFileList);
        return fileDto;
    }

    @Override
    public void downloadTechPlan(Long projectId, HttpServletResponse response) throws Exception{
        ApiExtTechFileDto techFileDto = getTechPlan(projectId);
        //查询招标项目
        ExtSouProject souProject = projectService.getById(projectId);
        //查询供应商名称
        List<ExtSouVendor> vendorList = souVendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).list();
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), v -> v, (k1, k2) -> k2));

        String fileName = MessageFormat.format("招标项目[{0}]技术方案.zip", souProject.getExtProjectNo());
        //替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");
        //压缩文件
        Map<String, InputStream> zipSource = new HashMap<>(50);
        //技术文件
        List<ExtSouOrderFileDto> orderFileList = techFileDto.getOrderFileList();
        if(CollectionUtils.isNotEmpty(orderFileList)) {
            orderFileList.stream().sorted(Comparator.comparingLong(ExtSouOrderFile::getVendorId)).forEach(v -> {
                ExtSouVendor vendor = vendorMap.getOrDefault(v.getVendorId(), new ExtSouVendor());
                String subFileName = MessageFormat.format("投标文件\\{0}\\{1}", vendor.getVendorName(), v.getOrderFileName());
                try {
                    zipSource.put(subFileName, downLoadByFileId(v.getOrderDocId()));
                } catch (Exception e) {
                    log.error("downLoadByFileId Exception", e);
                }
            });
        }

        //技术文件
        List<ExtSouOrderFileDto> secretFileList = techFileDto.getSecretFileList();
        if(CollectionUtils.isNotEmpty(secretFileList)) {
            secretFileList.stream().sorted(Comparator.comparingLong(ExtSouOrderFile::getVendorId)).forEach(v -> {
                ExtSouVendor vendor = vendorMap.getOrDefault(v.getVendorId(), new ExtSouVendor());
                String subFileName = MessageFormat.format("脱敏文件\\{0}\\{1}", vendor.getVendorName(), v.getOrderFileName());
                try {
                    zipSource.put(subFileName, downLoadByFileId(v.getOrderDocId()));
                } catch (Exception e) {
                    log.error("downLoadByFileId Exception", e);
                }
            });
        }

        ZipUtil.toZip(zipSource, response.getOutputStream());

    }

    @Override
    public Map<String, Object> listDownloadTechPlanFile(Long projectId) throws Exception {
        Map<String, Object> fileMap = new HashMap<>(15);
        ApiExtTechFileDto techFileDto = getTechPlan(projectId);
        //查询招标项目
        ExtSouProject souProject = projectService.getById(projectId);
        //查询供应商名称
        List<ExtSouVendor> vendorList = souVendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).list();
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), v -> v, (k1, k2) -> k2));

        String fileName = MessageFormat.format("招标项目[{0}]技术方案.zip", souProject.getExtProjectNo());
        //替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

        fileMap.put("fileName", fileName);

        Map<String, Object> fileData = new HashMap<>(15);
        fileMap.put("投标文件", fileData);

        //技术文件
        List<ExtSouOrderFileDto> orderFileList = techFileDto.getOrderFileList();
        if(CollectionUtils.isNotEmpty(orderFileList)) {
            orderFileList.stream().sorted(Comparator.comparingLong(ExtSouOrderFile::getVendorId)).forEach(v -> {
                ExtSouVendor vendor = vendorMap.getOrDefault(v.getVendorId(), new ExtSouVendor());

                if(!fileData.containsKey(vendor.getVendorName())) {
                    fileData.put(vendor.getVendorName(), new ArrayList<>(15));
                }

                List<Map<String, Object>> fileList = (List<Map<String, Object>>) fileData.get(vendor.getVendorName());

                Map<String, Object> file = new HashMap<>(15);
                fileList.add(file);
                file.put("docId", v.getOrderDocId());
                file.put("docName", v.getOrderFileName());
            });
        }
        return fileMap;
    }

    @Override
    public void downloadBusinessFile(Long projectId, HttpServletResponse response) throws Exception {
        log.info(MessageFormat.format("downloadBusinessFile下载附件开始：{0}", projectId));
        //查询供应商列表
        List<ExtSouVendor> vendorList = souVendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(vendorList)) {
            throw new BaseException("投标供应商信息为空！");
        }
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), v -> v, (k1, k2) -> k2));

        //查询商务报价附件
        List<ExtSouOrderFile> busFileList = this.getBusOrderFile(projectId, null);
        if(CollectionUtils.isEmpty(busFileList)) {
            throw new BaseException("暂无供应商报价附件！");
        }
        //根据附件ID去重
        busFileList = busFileList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouOrderFile::getOrderDocId))), ArrayList::new)
        );

        //查询招标项目
        ExtSouProject souProject = projectService.getById(projectId);
        String fileName = MessageFormat.format("招标项目[{0}]商务附件.zip", souProject.getExtProjectNo());
        //替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");
        //压缩文件
        Map<String, InputStream> zipSource = new HashMap<>(16);

        busFileList.stream().sorted(Comparator.comparingLong(ExtSouOrderFile::getVendorId)).forEach(v -> {
            ExtSouVendor vendor = vendorMap.getOrDefault(v.getVendorId(), new ExtSouVendor());
            String subFileName = MessageFormat.format("商务附件\\{0}\\{1}", vendor.getVendorName(), v.getOrderFileName());
            try {
                log.info(MessageFormat.format("downloadBusinessFile下载附件-请求文件服务器开始：{0}-{1}", projectId, subFileName));
                zipSource.put(subFileName, downLoadByFileId(v.getOrderDocId()));
                log.info(MessageFormat.format("downloadBusinessFile下载附件-请求文件服务器结束：{0}-{1}", projectId, subFileName));
            } catch (Exception e) {
                log.error("downloadBusinessFile downLoadByFileId Exception", e);
            }
        });

        ZipUtil.toZip(zipSource, response.getOutputStream());
        log.info(MessageFormat.format("downloadBusinessFile下载附件结束：{0}", projectId));
    }

    @Override
    public Map<String, Object> listDownloadBusinessFile(Long projectId) throws Exception {
        Map<String, Object> fileMap = new HashMap<>(15);

        log.info(MessageFormat.format("listDownloadBusinessFile查询下载附件开始：{0}", projectId));
        //查询供应商列表
        List<ExtSouVendor> vendorList = souVendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(vendorList)) {
            throw new BaseException("投标供应商信息为空！");
        }
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), v -> v, (k1, k2) -> k2));

        //查询商务报价附件
        List<ExtSouOrderFile> busAllFileList = this.getBusOrderFile(projectId, null);
        //获取已报价单据
        List<ExtNpmSouOrder> extNpmSouOrderList = this.getExtBusOrder(projectId);
        if(CollectionUtils.isEmpty(busAllFileList) || CollectionUtils.isEmpty(extNpmSouOrderList)) {
            throw new BaseException("暂无供应商报价附件！");
        }
        List<String> submissionSouOrderList = extNpmSouOrderList.stream().map(item -> item.getOrderId() + "_" + item.getRound()).collect(Collectors.toList());
        List<ExtSouOrderFile> busFileList = new ArrayList<>();
        for (ExtSouOrderFile orderFile : busAllFileList) {
            //过滤已报价的供应商文件
            if (submissionSouOrderList.contains(orderFile.getOrderId()+ "_" +orderFile.getRound())){
                busFileList.add(orderFile);
            }
        }
        //根据附件ID去重
        busFileList = busFileList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouOrderFile::getOrderDocId))), ArrayList::new)
        );

        //查询招标项目
        ExtSouProject souProject = projectService.getById(projectId);
        String fileName = MessageFormat.format("招标项目[{0}]商务附件.zip", souProject.getExtProjectNo());
        //替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

        fileMap.put("fileName", fileName);

        Map<String, Object> fileData = new HashMap<>(15);
        fileMap.put("商务附件", fileData);


        busFileList.stream().sorted(Comparator.comparingLong(ExtSouOrderFile::getVendorId)).filter(v -> !Objects.isNull(v.getOrderDocId())).forEach(v -> {
            ExtSouVendor vendor = vendorMap.getOrDefault(v.getVendorId(), new ExtSouVendor());

            if(!fileData.containsKey(vendor.getVendorName())) {
                fileData.put(vendor.getVendorName(), new ArrayList<>(15));
            }

            List<Map<String, Object>> fileList = (List<Map<String, Object>>) fileData.get(vendor.getVendorName());

            Map<String, Object> file = new HashMap<>(15);
            Map<String, Object> round = new HashMap<>(15);

            file.put("docId", v.getOrderDocId());
            file.put("docName", v.getOrderFileName());

            // 将文件信息放入轮次Map中，轮次作为key，文件信息作为value（放在一个List中）
            round.put("轮次" + v.getRound(), Collections.singletonList(file));
            fileList.add(round);

        });

        log.info(MessageFormat.format("listDownloadBusinessFile查询下载附件结束：{0}", projectId));

        return fileMap;
    }

    private InputStream downLoadByFileId(Long fileId) throws Exception{
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileId);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        InputStream inputStream = response1.body().asInputStream();
        return inputStream;
    }

    @Override
    public List<ExtSouOrderFileDto> getSecretFileList(Long projectId) {

        //技术投标范围供应商
        List<ExtSouOrder> vendorRange = npmSouOrderService.techOrderRange(projectId);
        if(CollectionUtils.isEmpty(vendorRange)) {
            return new ArrayList<>();
        }

        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(projectId);
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode()));

        List<ExtSouOrderFile> fileList = this.listOrderFile(queryDto);

        Map<Long, List<ExtSouOrderFile>> fileGroup = fileList.stream().collect(Collectors.groupingBy(ExtSouOrderFile::getVendorId));

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, projectId);
        queryWrapper.in(ExtSouVendor::getVendorId, vendorRange.stream().map(o -> o.getVendorId()).collect(Collectors.toList()));
        queryWrapper.orderByAsc(ExtSouVendor::getVendorId);

        List<ExtSouVendor> vendorList = souVendorService.list(queryWrapper);


        List<ExtSouOrderFileDto> secretFileList = new ArrayList<>();

        vendorList.stream().forEach(vendor -> {
            if(fileGroup.containsKey(vendor.getVendorId())) {
                List<ExtSouOrderFile> list = fileGroup.get(vendor.getVendorId());
                List<ExtSouOrderFileDto> fileDtoList = JSON.parseArray(JSON.toJSONString(list), ExtSouOrderFileDto.class);
                fileDtoList.stream().forEach(f -> {
                    f.setVendorCode(vendor.getVendorCode());
                    f.setVendorName(vendor.getVendorName());
                });
                secretFileList.addAll(fileDtoList);
            } else {
                ExtSouOrderFileDto fileDto = new ExtSouOrderFileDto();
                fileDto.setVendorId(vendor.getVendorId());
                fileDto.setProjectId(projectId);
                fileDto.setFileType(ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode());
                fileDto.setVendorCode(vendor.getVendorCode());
                fileDto.setVendorName(vendor.getVendorName());

                secretFileList.add(fileDto);
            }
        });

        return secretFileList;
    }

    @Override
    public Long editSecretFile(ApiExtTechFileDto techFile, String souType) {
        ExtSouProject project = projectService.getById(techFile.getProjectId());
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询当前轮次：技术标
        LambdaQueryWrapper<SouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouOrder::getProjectId, techFile.getProjectId());
        queryWrapper.eq(SouOrder::getRound, project.getCurrentRound());
        List<SouOrder> souOrderList = souOrderDAO.list(queryWrapper);
        Map<Long, SouOrder> souOrderMap = souOrderList.stream().collect(Collectors.toMap(o -> o.getVendorId(), Function.identity(), (k1, k2)->k2));

        List<ExtSouOrderFile> fileList = new ArrayList<>();
        techFile.getSecretFileList().stream().forEach(fileDto -> {
            ExtSouOrderFile file = new ExtSouOrderFile();
            BeanCopyUtil.copyProperties(file, fileDto);
            SouOrder souOrder = souOrderMap.get(file.getVendorId());
            if(Objects.isNull(souOrder)) {
                file.setOrderId(-1L);
            } else {
                file.setOrderId(souOrder.getOrderId());
            }

            file.setRound(project.getCurrentRound());

            file.setFileType(ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode());

            if(Objects.isNull(file.getOrderFileId())) {
                file.setOrderFileId(IdGenrator.generate());
            }
            file.formattingPackageName();
            fileList.add(file);
        });

        this.saveOrUpdateBatch(fileList);
        return techFile.getProjectId();
    }

    private List<ExtNpmSouOrder> getExtBusOrder(Long projectId) {
        LambdaQueryWrapper<ExtSouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(projectId), ExtSouOrder::getProjectId, projectId);
        List<ExtSouOrder> souOrderList = orderService.list(queryWrapper);
        List<ExtNpmSouOrder> extNpmSouOrderList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(souOrderList)){
            List<Long> orderIdList= souOrderList.stream().map(ExtSouOrder::getOrderId).distinct().collect(Collectors.toList());
            LambdaQueryWrapper<ExtNpmSouOrder> queryExtNpmWrapper = new LambdaQueryWrapper<>();
            queryExtNpmWrapper.in(CollectionUtils.isNotEmpty(orderIdList), ExtNpmSouOrder::getOrderId, orderIdList);
            queryExtNpmWrapper.eq(ExtNpmSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION);
            extNpmSouOrderList = npmSouOrderService.list(queryExtNpmWrapper);
        }

        return extNpmSouOrderList;
    }
}
