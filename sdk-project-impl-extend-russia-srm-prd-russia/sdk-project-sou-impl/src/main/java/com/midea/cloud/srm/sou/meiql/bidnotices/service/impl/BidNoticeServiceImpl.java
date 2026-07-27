package com.midea.cloud.srm.sou.meiql.bidnotices.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.feign.SdkPjClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.bidnotices.dto.*;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.bidnotices.enums.SignTypeEnum;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.SpirePdfUtil;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.XEasypdfUtil;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.XwpfdUtils;
import com.midea.cloud.srm.sou.meiql.inspect.dto.Inspect;
import com.midea.cloud.srm.sou.meiql.inspect.dto.InspectVendor;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouGroupMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouItemMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.mideacloud.partner.enums.Enable;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.pdf.pdfbox.core.base.Document;
import org.dromara.pdf.pdfbox.core.enums.FontStyle;
import org.dromara.pdf.pdfbox.handler.PdfHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Primary
@Service
public class BidNoticeServiceImpl implements BidNoticeService {

    @Autowired
    private QlService qlService;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Value("${bidNotice.sign.name.group:保定长城控股集团有限公司}")
    private String group;
    @Value("${bidNotice.sign.name.car:长城汽车股份有限公司}")
    private String car;
    @Value("${bidNotice.sign.name.groupFlag:NYBK}")
    private String groupFlag;

    @Autowired
    private ExtFileCenterClient extFileCenterClient;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private SdkPjClient sdkPjClient;


    @Autowired
    private ExtSouGroupMapper groupMapper;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private ExtSouItemMapper itemMapper;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private BaseClient baseClient;

    /**
     * 生成中/落标通知数据
     * @param ca
     * @return
     */
    @Override
    public BidNoticeDTO add(CaDTO ca) throws Exception {
        //查询判断是否已存在中落标通知单
        List<BidNoticeDTO> list = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
                .eq(BidNoticeDTO::getProjectId,ca.getProjectId())
                .eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
                .notEq(BidNoticeDTO::getStatus,BidNoticeStatusEnum.ABANDON.getCode())
                .select(BidNoticeDTO::getBidNoticeId),BidNoticeDTO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BaseException("已存在中/落标通知单，不能重复生成");
        }

        //1.主表信息复制
        BidNoticeDTO bidNoticeDTO = new BidNoticeDTO();
        BeanUtils.copyProperties(bidNoticeDTO,ca);
        bidNoticeDTO.setStatus(BidNoticeStatusEnum.DRAFT.getCode());

        //获取招标提交资料
        ApiExtSouProjectInfoDTO projectInfoDTO = extSouInitQueryService.getProjectInfo(ca.getProjectId());
        String applicantNo = projectInfoDTO.getProject().getApplicantNo();
        BidDataSubmit bidDataSubmit = getBidData(applicantNo);
        String contractSignUnit = null;
        String contractSignUnitId = null;
        String contractSignUnitCode = null;
        String contractSignUnitCreditCode = null;
        if (null != bidDataSubmit ) {
            //招标技术负责人-取招标资料递交
            bidNoticeDTO.setExtTechPrincipal(bidDataSubmit.getTechPrincipal());
            //联系人电话-取招标资料递交
            bidNoticeDTO.setExtTechPhone(bidDataSubmit.getPhone());
            //合同签署单位-取招标资料递交
            contractSignUnit = bidDataSubmit.getContractSignUnit();
            contractSignUnitId = bidDataSubmit.getContractSignUnitId();
            contractSignUnitCode = bidDataSubmit.getContractSignUnitCode();
            contractSignUnitCreditCode = bidDataSubmit.getContractSignUnitCredit();
        }
        //2.供应商选定结果表
        List<CaSelectionResultDTO> caSelectionResults = qlService.queryByWrapper(QlWrappers
                .query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId,ca.getCaId()),CaSelectionResultDTO.class);
        List<BidNoticeDetailDTO> details = new ArrayList<>();

        //获取考察信息
        Map<Long,Long> inspectMap = getInspect(ca.getProjectId());
        for (CaSelectionResultDTO result : caSelectionResults) {
            BidNoticeDetailDTO detail = new BidNoticeDetailDTO();
            BeanUtils.copyProperties(detail,result);
            //是否发送
            detail.setIsSend(YesOrNo.NO.getValue());
            //中/落标通知非中标，不带出签署单位
            if (YesOrNo.YES.getValue().equals(detail.getIsWin())) {
                //合同签署单位-取招标资料递交
                detail.setContractSignUnit(contractSignUnit);
                detail.setContractSignUnitId(contractSignUnitId);
                detail.setContractSignUnitCode(contractSignUnitCode);
                detail.setContractSignUnitCredit(contractSignUnitCreditCode);
            }
            //是否现场考察-取项目关联的现场考察单，是否涉及当前供应商
            if (inspectMap.containsKey(detail.getVendorId())) {
                detail.setIsOnSiteInspected(YesOrNo.YES.getValue());
                detail.setInspectId(inspectMap.get(detail.getVendorId()));
            } else {
                detail.setIsOnSiteInspected(YesOrNo.NO.getValue());
            }

            details.add(detail);
        }
        bidNoticeDTO.setBidNoticeDetails(details);
        //3.多个需求合并招标时，按照需求生成多行内部通知结果行。
        extracted(bidNoticeDTO, applicantNo);
        //保存
        List<Serializable> ids = qlService.create(TypeEnum.BidNotice.getCode(), Arrays.asList(bidNoticeDTO));
        bidNoticeDTO.setBidNoticeId((Long) ids.get(0));
        return bidNoticeDTO;
    }

    /**
     * 多个需求合并招标时，按照需求生成多行内部通知结果行
     * @param bidNoticeDTO 参数
     * @param applicantNo 参数
     */
    private void extracted(BidNoticeDTO bidNoticeDTO, String applicantNo) {
        if (StringUtil.isNotEmpty(applicantNo)) {

            /**
             * 先从 PurchaseRequirementHead 查询采购申请表数据,通过 extBidFlag 字段判断是否招标计划。
             *  如果 extBidFlag == N，直接拿 extOrgBuId
             *   如果 extBidFlag == Y，根据 requirementHeadId 查询 ExtPrSouRequirementHead
             */

            QlOpenQueryWrapper wrapper = QlOpenWrappers.query("PurchaseRequirementHead").in("requirementHeadNum", Arrays.asList(applicantNo.split(";")));
            List<Record> requirements = qlOpenClient.query(ContextPath.SUP_CE, wrapper, Record.class);
            BidNoticeInternalDTO internalDTO = null;
            if (CollectionUtils.isNotEmpty(requirements)) {
                List<BidNoticeInternalDTO> internals = new ArrayList<>();
                for (Record record : requirements) {
                    internalDTO = new BidNoticeInternalDTO();
                    internalDTO.setIsSend(YesOrNo.NO.getValue());
                    internalDTO.setIsSign(YesOrNo.NO.getValue());
                    //板块
                    if (YesOrNo.NO.getValue().equals(record.getString("extBidFlag"))) {
                        internalDTO.setExtOrgBuId(record.getLong("extOrgBuId"));
                        internalDTO.setExtOrgBuCode(record.getString("extOrgBuCode"));
                        internalDTO.setExtOrgBuName(record.getString("extOrgBuName"));
                    } else {
                        Record extRequirement = qlOpenClient.read(ContextPath.SUP_CE,"ExtPrSouRequirementHead",requirements.get(0).getString("requirementHeadId"),Record.class);
                        if (null != extRequirement) {
                            internalDTO.setExtOrgBuId(extRequirement.getLong("orgBuId"));
                            internalDTO.setExtOrgBuCode(extRequirement.getString("orgBuCode"));
                            internalDTO.setExtOrgBuName(extRequirement.getString("orgBuName"));
                        }
                    }
                    //公司
                    internalDTO.setExtOrgOuId(record.getLong("orgId"));
                    internalDTO.setExtOrgOuCode(record.getString("orgCode"));
                    internalDTO.setExtOrgOuName(record.getString("orgName"));
                    //需求部门
                    internalDTO.setDemandDepartmentId(record.getString("ceeaDepartmentId"));
                    internalDTO.setDemandDepartmentName(record.getString("ceeaDepartmentName"));
                    internals.add(internalDTO);
                }
                bidNoticeDTO.setBidNoticeInternals(internals);
            }
        }
    }

    public Map<Long,Long> getInspect(Long projectId) {
        Map<Long,Long> map = new HashMap<>(50);
        QlType qlType = SchemaUtil.getType("InspectVendor");
        QlCondition qlCondition = MeiQl.newCondition();
        qlCondition.exists("Inspect","v", MeiQl.newCondition().eq(Inspect::getBidingId,projectId)
                .eq(Inspect::getInspectId, QlQueryFieldWrapper.field(qlType.getTableName(),InspectVendor::getInspectId)));
        QlQueryWrapper qlQueryWrapper = QlWrappers.query("InspectVendor");
        qlQueryWrapper.and(qlCondition);
        List<InspectVendor> list = qlService.queryByWrapper(qlQueryWrapper, InspectVendor.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (InspectVendor v : list) {
                map.put(v.getVendorId(),v.getInspectId());
            }
        }
        return map;
    }

    private BidDataSubmit getBidData(String applicantNo) {
        if (null != applicantNo) {
            String applicationNo = applicantNo.split(";")[0];
            List<BidDataSubmit> list = qlService.queryByWrapper(QlWrappers.query(MqlType.SUBMIT_BUYER).eq(BidDataSubmit::getRequirementHeadNum,applicationNo),BidDataSubmit.class);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public BidNoticeDTO add(Long projectId) throws Exception {
        Record record = new Record();
        record.put(CaDTO::getProjectId,projectId);
        record.put(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode());
        record.put(CaDTO::getType, CaTypeEnum.APPLY.getCode());
        List<CaDTO> caList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode())
                        .eq(CaDTO::getProjectId,projectId)
                        .eq(CaDTO::getType, CaTypeEnum.APPLY.getCode())
                        .eq(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode()),CaDTO.class);
        if (CollectionUtils.isNotEmpty(caList)) {
            return add(caList.get(0));
        } else {
            throw new BaseException("不存在已审批的定标申请单据");
        }
    }

    @Override
    public Long addLostBidFile(String extProjectNo,String vendorName,String souName) {
        Map<String,String> fieldMap = new HashMap<>(50);
        fieldMap.put("${extProjectNo}",extProjectNo);
        fieldMap.put("${vendorName}",vendorName);
        fieldMap.put("${souName}",souName);
        fieldMap.put("${now}", new SimpleDateFormat("yyyy 年 MM 月 dd 日").format(new Date()));
        return null;
    }

    public Long saveFile(MultipartFile file, String fileName) {
        String uploadType = FileUploadType.DEF.name();
        String sourceType = "WEB_APP";
        String fileModular = "base";
        String fileFunction = "commonFile";
        String fileType = "PDF";
        Fileupload upload = fileCenterClient.feignClientUpload(file,sourceType, uploadType,fileModular,fileFunction,fileType);
        return upload.getFileuploadId();
    }
    @Override
    public Long saveFile(String extProjectNo,String vendorName,String souName,String now,String fileName) {
        String uploadType = FileUploadType.DEF.name();
        String sourceType ="WEB_APP";
        String fileModular ="base";
        String fileFunction ="commonFile";
        String fileType ="PDF";
        String filePath ="/template/notWinBidnotice-template.docx";
        log.info(MessageFormat.format("BidNoticeServiceImpl.saveFile 上传文件服务器开始: {0}-{1}-{2}", extProjectNo, souName, fileName));
        /** 该方法存在不会自动换行，项目名称过长时超出显示区域的问题
        Fileupload upload = fileCenterClient.feignClientUpload(SpirePdfUtil.getFile(extProjectNo,vendorName,souName,fileName,inputStream),sourceType, uploadType,fileModular,fileFunction,fileType);
        */
        Map<String, Object> param = new HashMap<>(15);
        param.put("vendorName", vendorName);
        param.put("extProjectNo", extProjectNo);
        param.put("souName", souName);
        Fileupload upload = fileCenterClient.feignClientUpload(XwpfdUtils.instance().getFile(fileName, filePath, param),sourceType, uploadType,fileModular,fileFunction,fileType);
        log.info(MessageFormat.format("BidNoticeServiceImpl.saveFile 上传文件服务器结束: {0}-{1}-{2}", extProjectNo, souName, fileName));
        return upload.getFileuploadId();
    }
    @Override
    public Long signCar(Long fileuploadId,String companyName,String now,String fileName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getFile(fileuploadId);
            Document document = PdfHandler.getDocumentHandler().load(inputStream);
            if (Integer.compare(document.getTotalPageNumber(), SrmConstant.NUM_ZERO) == 1) {
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), now, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 385F, 110F);
            }
            document.save(outputStream);
            document.close();
            MultipartFile file = SpirePdfUtil.getFile(fileName,outputStream.toByteArray());
            return saveFile(file,fileName);
        } catch (Exception e) {
            log.error("附件【"+fileName+"】盖章失败",e);
            throw new BaseException("附件【"+fileName+"】盖章失败");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    @Override
    public Long signCarWin(Long fileuploadId,String companyName,String now,String fileName) {
        String img = "/template/汽车股份.png";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getFile(fileuploadId);
            //盖章和签字放到第一页
            //更换x-easypdf
            Document document = PdfHandler.getDocumentHandler().load(inputStream);
            if (Integer.compare(document.getTotalPageNumber(), SrmConstant.NUM_ZERO) == 1) {
                //板块名称
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), companyName, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 375F, 140F);
                //日期
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), now, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 385F, 110F);
                //追加图片
                XEasypdfUtil.writeImage(document.getPage(SrmConstant.NUM_ZERO), this.getClass().getResourceAsStream(img), 380F, 180F, 130, 95);
            }
            document.save(outputStream);
            document.close();
            MultipartFile file = SpirePdfUtil.getFile(fileName,outputStream.toByteArray());
            return saveFile(file,fileName);
        } catch (Exception e) {
            log.error("附件【"+fileName+"】盖章失败",e);
            throw new BaseException("附件【"+fileName+"】盖章失败");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @Override
    public Long signGoup(Long fileuploadId,String companyName,String now,String fileName) {
//        String img ="/template/控股2.jpg";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getFile(fileuploadId);
            Document document = PdfHandler.getDocumentHandler().load(inputStream);
            if (Integer.compare(document.getTotalPageNumber(), SrmConstant.NUM_ZERO) == 1) {
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), now, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 385F, 110F);
            }
            document.save(outputStream);
            document.close();
            MultipartFile file = SpirePdfUtil.getFile(fileName,outputStream.toByteArray());
            return saveFile(file,fileName);
        } catch (Exception e) {
            log.error("附件【"+fileName+"】盖章失败",e);
            throw new BaseException("附件【"+fileName+"】盖章失败");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @Override
    public Long signGroupWin(Long fileuploadId,String companyName,String now,String fileName) {
        String img = "/template/控股2.jpg";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getFile(fileuploadId);
            //盖章和签字放到第一页
            //更换x-easypdf
            Document document = PdfHandler.getDocumentHandler().load(inputStream);
            if (Integer.compare(document.getTotalPageNumber(), SrmConstant.NUM_ZERO) == 1) {
                //板块名称
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), companyName, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 375F, 140F);
                //日期
                XEasypdfUtil.writeText(document.getPage(SrmConstant.NUM_ZERO), now, "微软雅黑", Color.black, FontStyle.NORMAL, 14, 385F, 110F);
                //追加图片
                XEasypdfUtil.writeImage(document.getPage(SrmConstant.NUM_ZERO), this.getClass().getResourceAsStream(img), 380F, 180F, 130, 95);
            }
            document.save(outputStream);
            document.close();
            MultipartFile file = SpirePdfUtil.getFile(fileName,outputStream.toByteArray());
            return saveFile(file,fileName);
        } catch (Exception e) {
            log.error("附件【"+fileName+"】盖章失败",e);
            throw new BaseException("附件【" + fileName + "】盖章失败");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    @Override
    public Long signName(Long fileuploadId,String signPath,String now,String fileName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getFile(fileuploadId);
            Document document = PdfHandler.getDocumentHandler().load(inputStream);
            if (Integer.compare(document.getTotalPageNumber(), SrmConstant.NUM_ZERO) == 1) {
                XEasypdfUtil.writeImageByRelative(document.getPage(SrmConstant.NUM_ZERO), this.getClass().getResourceAsStream(signPath), 457F, 153.5F, 40, 24);
            }
            document.save(outputStream);
            document.close();
            MultipartFile file = SpirePdfUtil.getFile(fileName,outputStream.toByteArray());
            return saveFile(file,fileName);
        } catch (Exception e) {
            log.error("附件【"+fileName+"】签字失败",e);
            throw new BaseException("附件【"+fileName+"】签字失败");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public InputStream getFile(Long fileuploadId) throws Exception {
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileuploadId);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        InputStream inputStream = response1.body().asInputStream();
        return inputStream;
    }
    @Override
    public Long signByType(String type,String extOrgBuCode,Long fileuploadId,String fileName,String now) {
        Long fileId = null;
        //有NYBK的李小贵，（NYBK为板块编码），其他秦总签批
        if (SignTypeEnum.INNER.getCode().equals(type)) {
            if (null != extOrgBuCode && extOrgBuCode.contains(groupFlag)) {
                fileId = signName(fileuploadId,"/template/签字2.png",now,fileName);
            } else {
                fileId = signName(fileuploadId,"/template/签字1.png",now,fileName);
            }
        } else if (SignTypeEnum.LOST.getCode().equals(type)) {
            //判断板块,有NYBK的是控股的章,其他编号的用股份
            if (null != extOrgBuCode && extOrgBuCode.contains(groupFlag)) {
                fileId = signGoup(fileuploadId,group,now,fileName);
            } else {
                fileId = signCar(fileuploadId,car,now,fileName);
            }
        } else if (SignTypeEnum.WIN.getCode().equals(type)) {
            //判断板块,有NYBK的是控股的章,其他编号的用股份
            if (null != extOrgBuCode && extOrgBuCode.contains(groupFlag)) {
                fileId = signGroupWin(fileuploadId,group,now,fileName);
            } else {
                fileId = signCarWin(fileuploadId,car,now,fileName);
            }
        }
        return fileId;
    }

    @Override
    public void signByType(Long id, String signType,String extOrgBuCode) {
        String now = new SimpleDateFormat("yyyy 年 M 月 d 日").format(new Date());
        String type = null;
        if (SignTypeEnum.INNER.getCode().equals(signType)) {
            type = TypeEnum.BidNoticeInternal.getCode();
        } else if (SignTypeEnum.LOST.getCode().equals(signType) || SignTypeEnum.WIN.getCode().equals(signType)) {
            type = TypeEnum.BidNoticeDetail.getCode();
        }
        AssertUtils.notNull(type,"类型不存在");
        Record record = qlService.readByKey(type,id,Record.class);
        Long fileuploadId = null;
        if (SignTypeEnum.INNER.getCode().equals(signType)) {
            fileuploadId = signByType(signType,extOrgBuCode,record.get(BidNoticeInternalDTO::getAttachmentId)
                    ,record.get(BidNoticeInternalDTO::getAttachmentName),now);
            record.put(BidNoticeInternalDTO::getAttachmentId,fileuploadId);
            record.put(BidNoticeInternalDTO::getIsSign,YesOrNo.YES.getValue());
        } else if (SignTypeEnum.LOST.getCode().equals(signType) || SignTypeEnum.WIN.getCode().equals(signType)) {
            fileuploadId = signByType(signType,extOrgBuCode,record.get(BidNoticeDetailDTO::getNoticeAttachmentId)
                    ,record.get(BidNoticeDetailDTO::getNoticeAttachmentName),now);
            record.put(BidNoticeDetailDTO::getNoticeAttachmentId,fileuploadId);
            record.put(BidNoticeDetailDTO::getIsSign,YesOrNo.YES.getValue());
        }
        qlService.update(type,Arrays.asList(record));
    }

    /**
     * 中标发送-创建合同
     * @param internalId
     */
    @Override
    public List<Record> createContract (Long internalId) {

        //招标完成时间
        Date extInviteFinishDate = new Date();

        BidNoticeInternalDTO internalDTO = qlService.readByKey(TypeEnum.BidNoticeInternal.getCode(), internalId,BidNoticeInternalDTO.class);
        if (null != internalDTO) {
            List<BidNoticeDetailDTO> details = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                    .eq(BidNoticeDetailDTO::getBidNoticeId,internalDTO.getBidNoticeId())
                    .eq(BidNoticeDetailDTO::getIsWin,YesOrNo.YES.getValue()),BidNoticeDetailDTO.class);
            if (CollectionUtils.isNotEmpty(details)) {
                //获取定标申请
                Long bidNoticeId = internalDTO.getBidNoticeId();
                BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNotice.getCode(), bidNoticeId,BidNoticeDTO.class);
                CaDTO caDTO = qlService.readByKey(TypeEnum.Ca.getCode(),bidNoticeDTO.getCaId(),CaDTO.class);
                ApiExtSouProjectInfoDTO projectInfoDTO = extSouInitQueryService.getProjectInfo(bidNoticeDTO.getProjectId());
                List<Record> contractHeads = new ArrayList<>();
                for (BidNoticeDetailDTO detailDTO : details) {
                    Record contractHead = new Record();
                    //供应商id 供应商编码 供应商名称
                    contractHead.put("vendorId",detailDTO.getVendorId());
                    contractHead.put("vendorCode",detailDTO.getVendorCode());
                    contractHead.put("vendorName",detailDTO.getVendorName());

                    // 板块id 板块编码 板块名称
                    contractHead.put("extSectorId",internalDTO.getExtOrgBuId());
                    contractHead.put("extSectorName",internalDTO.getExtOrgBuName());
                    contractHead.put("extSectorCode",internalDTO.getExtOrgBuCode());
                    //招标负责人
                    ExtSouGroup group = this.getBidGroup(projectInfoDTO.getGroupList());
                    if (null != group) {
                        contractHead.put("extInviteHeadId",group.getUserId());
//                        contractHead.put("",group.getFullName());
                    }

                    //合同经办人
                    contractHead.put("extContractHandlerId",caDTO.getContractOperatorUserId());
                    contractHead.put("extContractHandlerName",caDTO.getContractOperatorNickname());
                    contractHead.put("extContractHandlerAccount",caDTO.getContractOperatorUsername());
                    //招标项目编号
                    contractHead.put("sourceNumber",bidNoticeDTO.getExtProjectNo());
                    //招标结束时间
                    contractHead.put("extInviteFinishDate", DateUtil.format(extInviteFinishDate, DateUtil.DATE_FORMAT_10));
                    //是否免评价
                    contractHead.put("extEvaluateFlag",detailDTO.getIsPerformanceEvaluated().equals(YesOrNo.YES.getValue())?YesOrNo.NO.getValue():YesOrNo.YES.getValue());
                    // 品类id，品类编码 品类名称
                    contractHead.put("extCategoryId",projectInfoDTO.getProject().getExtCategoryId());
                    contractHead.put("extCategoryCode",projectInfoDTO.getProject().getExtCategoryCode());
                    contractHead.put("extCategoryName",projectInfoDTO.getProject().getExtCategoryName());
                    //投资编号
                    contractHead.put("extInvestNo",projectInfoDTO.getProject().getExtInvestNo());
                    //甲方
                    contractHead.put("owner",detailDTO.getContractSignUnit());
                    //来源类型
                    contractHead.put("sourceType","BID_NOTICE");
                    contractHead.put("operationType","SAVE_TEMP");
                    contractHead.put("contractType","MIAN_CONTRACT_ADD");
                    //状态
                    contractHead.put("contractStatus","DRAFT");

                    //拆单 contractHead.put("owner",detailDTO.getContractSignUnit());
                    if(StringUtils.isNotBlank(detailDTO.getContractSignUnitId())) {
                        String[] unitIdArrarys = detailDTO.getContractSignUnitId().split(SouConstant.SIG_3);
                        String[] unitCodeArrarys = ObjectUtils.defaultIfNull(detailDTO.getContractSignUnitCode(), "").split(SouConstant.SIG_3);
                        String[] unitCreditArrarys = ObjectUtils.defaultIfNull(detailDTO.getContractSignUnitCredit(), "").split(SouConstant.SIG_3);
                        String[] unitArrarys = ObjectUtils.defaultIfNull(detailDTO.getContractSignUnit(), "").split(SouConstant.SIG_3);

                        for(int index = 0; index < unitIdArrarys.length; index++) {
                            Record record = JSON.parseObject(JSON.toJSONString(contractHead), Record.class);
                            /** 签署单位名称 */
                            record.put("owner", elementOfArrarys(unitArrarys, index));
                            /** 签署单位ID */
                            record.put("buId",Long.valueOf(unitIdArrarys[index]));
                            /** 签署单位编码 */
                            record.put("buCode",elementOfArrarys(unitCodeArrarys, index));
                            /** 签署单位名称 */
                            record.put("buName",elementOfArrarys(unitArrarys, index));
                            /** 签署单位统一社会信用代码 */
                            record.put("creditCode",elementOfArrarys(unitCreditArrarys, index));
                            contractHeads.add(record);
                        }
                    } else {
                        contractHeads.add(contractHead);
                    }

                }

                sdkPjClient.save(contractHeads);
                return contractHeads;
            }
        } else {
            throw new BaseException("内部通知internalId不存在");
        }

        return new ArrayList<>();
    }

    @Override
    public BidNoticeDetailTemplateDTO getBidNoticeDetailInfo(Long bidNoticeDetailId) {
        BidNoticeDetailDTO detailDTO = qlService.readByKey("BidNoticeDetail",bidNoticeDetailId,BidNoticeDetailDTO.class);
        if(ObjectUtil.isNull(detailDTO)){
            throw new BaseException("中落标通知书明细不存在");
        }
        BidNoticeDTO bidNoticeDTO =  qlService.readByKey("BidNotice",detailDTO.getBidNoticeId(),BidNoticeDTO.class);
        LambdaQueryWrapper<ExtSouGroup> extSouGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        extSouGroupLambdaQueryWrapper.eq(ExtSouGroup::getProjectId,bidNoticeDTO.getProjectId());
        ExtSouGroup extSouGroup = getBidGroup(groupMapper.selectList(extSouGroupLambdaQueryWrapper));
        BidNoticeDetailTemplateDTO  bidNoticeDetailTemplateDTO = BidNoticeDetailTemplateDTO.createBidNoticeDetail(bidNoticeDTO,detailDTO,extSouGroup);
        return bidNoticeDetailTemplateDTO;
    }

    @Override
    public BidNoticeInternalTemplateDTO getBidNoticeInternalTemplateInfo(Long bidNoticeInternalId) throws Exception {
        BidNoticeInternalDTO bidNoticeInternalDTO = qlService.readByKey("BidNoticeInternal",bidNoticeInternalId,BidNoticeInternalDTO.class);
        if(ObjectUtil.isNull(bidNoticeInternalDTO)){
            throw new BaseException("内部通知明细不存在");
        }
        BidNoticeDTO bidNoticeDTO =  qlService.readByKey("BidNotice",bidNoticeInternalDTO.getBidNoticeId(),BidNoticeDTO.class);
        if(ObjectUtil.isNull(bidNoticeDTO)){
            throw new BaseException("中落标通知主表不存在");
        }
        BidNoticeDetailDTO bidNoticeDetailDTO = getAvailableBidNoticeDetail(bidNoticeDTO);
        ExtSouGroup extSouGroup = null;
        ExtSouOrder extSouOrder = null;
        CaDTO caDTO = null;
        ExtSouItem extSouItem = null;
        ExtSouProject extSouProject = null;
        if(ObjectUtil.isNotNull(bidNoticeDetailDTO)){
            Long projectId = bidNoticeDTO.getProjectId();
            //查询寻源项目
            extSouProject = projectService.getById(projectId);
            if(ObjectUtil.isNull(extSouProject)){
                throw new BaseException("寻源项目不存在");
            }
            //查询招标小组信息
            LambdaQueryWrapper<ExtSouGroup> extSouGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
            extSouGroupLambdaQueryWrapper.eq(ExtSouGroup::getProjectId,projectId);
            extSouGroup = getBidGroup(groupMapper.selectList(extSouGroupLambdaQueryWrapper));
            List<ExtSouOrder> extSouOrders = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId,projectId)
                    .eq(ExtSouOrder::getVendorId,bidNoticeDetailDTO.getVendorId())
                    .eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION).list();
            if(CollUtil.isNotEmpty(extSouOrders)){
                extSouOrder = extSouOrders.get(0);
            }
            //查询定标审批单信息
            List<CaDTO> caList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode())
                    .eq(CaDTO::getProjectId,bidNoticeDTO.getProjectId())
                    .eq(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode()),CaDTO.class);
            if(CollUtil.isNotEmpty(caList)){
                caDTO = caList.get(0);
            }
            //查询报价信息
            LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
            itemQuery.eq(ExtSouItem::getProjectId, bidNoticeDTO.getProjectId());
            itemQuery.orderByAsc(ExtSouItem::getExtPackageName, ExtSouItem::getSortIndex);
            List<ExtSouItem> itemList = itemMapper.selectList(itemQuery);
            if(CollUtil.isNotEmpty(itemList)){
                extSouItem = itemList.get(0);
            }
        } else {
            throw new BaseException("没有供应商中标");
        }
        List<DictItemDTO> invoiceTypes = baseClient.listAllByDictCode("SOU_BIDPRICE_INVOICE_TYPE");
        return BidNoticeInternalTemplateDTO.create(bidNoticeDTO,bidNoticeInternalDTO,bidNoticeDetailDTO,extSouGroup,extSouProject,extSouOrder,caDTO,extSouItem,invoiceTypes);
    }

    private BidNoticeDetailDTO getAvailableBidNoticeDetail(BidNoticeDTO bidNoticeDTO) {
        List<BidNoticeDetailDTO> detailDTOS =  qlService.queryByWrapper(QlWrappers.query("BidNoticeDetail")
                .eq(BidNoticeDetailDTO::getBidNoticeId,bidNoticeDTO.getBidNoticeId()).eq(BidNoticeDetailDTO::getIsWin, Enable.Y.name()),BidNoticeDetailDTO.class);
        if(detailDTOS.size()==1){
            return detailDTOS.get(0);
        } else {
            return null;
        }
    }

    private String elementOfArrarys(String[] arrarys, int index) {
        if(index < arrarys.length) {
            return arrarys[index];
        }
        return null;
    }

    public ExtSouGroup getBidGroup(List<ExtSouGroup> groupList) {
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (ExtSouGroup group : groupList) {
                if (ExtSouGroupRoleEnum.PRINCIPAL.getCode().equals(group.getGroupRole())) {
                    return group;
                }
            }
        }
        return null;
    }

    public Date getEndTime(List<ExtSouPlan> planList) {
        if (CollectionUtils.isNotEmpty(planList)) {
            for (ExtSouPlan plan : planList) {
                if (SouBidPlanTypeEnum.ACTUAL.getCode().equals(plan.getPlanType())) {
                    return plan.getCompleteTime();
                }
            }
        }
        return null;
    }
}
