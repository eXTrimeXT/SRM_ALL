package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.enums.contract.ContractType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.cm.contract.mapper.ExtPartnerMapper;
import com.midea.cloud.srm.cm.contract.service.IContractInterfceService;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.dto.*;
import com.midea.cloud.srm.model.contract.enums.ContractAgreementAttachmentType;
import com.midea.cloud.srm.model.contract.vo.ContractHeadVo;
import com.midea.cloud.srm.model.contract.vo.ContractPartnerVo;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.contract.dto.CreateContractReturnDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author luxc18
 */
@Slf4j
@Service
public class ContractInterfaceServiceImpl implements IContractInterfceService {

    @Autowired
    private QlService qlService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Value("${gw.contract.tailTypeId:846}")
    private String tailTypeId;

    @Value("${srm.inner.gateway}")
    private String gatewayUrl;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private ExtFileCenterClient extFileCenterClient;


    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private BaseClient baseClient;


    private final String QUAN_AMOUNT_LIMIT = "QUAN_AMOUNT_LIMIT";
    private final String CERTAION_AMOUNT = "CERTAION_AMOUNT";
    private final String Z001 = "Z001";
    private final String Z002 = "Z002";
    private final String Z003 = "Z003";
    private final String VENDOR_FIRST = "VENDOR_FIRST";
    private final String COMPANY_FIRST = "COMPANY_FIRST";

    private final String PARTNER_A = "甲方";

    private final String PARTNER_B = "乙方";

    @Autowired
    ExtPartnerMapper extPartnerMapper;

    /**
     * 根据id查询相关信息用来发钉钉通知
     * @param contractId
     * @return
     */
    @Override
    public ContractHeadVo selectById(Long contractId) {
        return extPartnerMapper.selectById(contractId);
    }

    /**
     * 根据合同id查询伙伴合同信息
     * @param id
     * @return
     */

    @Override
    public List<ContractPartnerVo> getById(Long id) {
        return extPartnerMapper.getById(id);
    }

    /**
     *
     * @param contractId
     * @param tenantName
     * 更新合同签署状态为已签署
     */
    @Override
    public void updateStampState(String contractId, String tenantName) {
        String id= extPartnerMapper.getByContractId(contractId);
        extPartnerMapper.updateStampState(id,tenantName);
    }
    @Override
    public Long contractFiling(Long contractHeadId) {
        AssertUtils.isTrue(ObjectUtil.isNotEmpty(contractHeadId), "合同ID不能为空");
        //查询合同信息===============
        List<Record> recordList = qlService.queryByWrapper(QlWrappers.query("ContractHead")
                .eq("contractHeadId", contractHeadId), Record.class);
        Record contractHeadRecord = recordList.get(0);
        CreateContractParamDTO request = buildContractHead(contractHeadId, contractHeadRecord);
        //查询相对方================ 乙方信息 ContractPartner.partnerType='乙方'
        buildOpposite(contractHeadId, request);
        //查询财务信息==============
        /** 计价方式、合同含税总金额、暂估金额、金额收支方向、币种*/
        buildFinance(contractHeadId, request, contractHeadRecord);
        //查询用印信息===============
        /** 是否需要用印、是否已经完成用印、已签署文件、签章方式、签章类型、用印次数、是否流程签署、签署完成时间*/
        buildSignInfo(contractHeadId, request, contractHeadRecord);
        String requestStr = JSONObject.toJSONString(request);
        log.info("归档requestStr:" + requestStr);
        CreateContractReturnDTO contract = pjProjectExtClient.createContract(JSONObject.parseObject(requestStr));
        log.info("归档contract:" + JSONObject.toJSONString(contract));
        Assert.isTrue("200".equals(contract.getCode()), "调用创建合同接口失败,返回:" + contract.getMsg() + ",请重试");
        // 更新成已归档 ContractStatus.ARCHIVED.name()
        qlService.updateByWrapper(QlWrappers.update("ContractHead")
                .eq("contractHeadId", contractHeadId)
                .set("contractStatus", ContractStatus.ARCHIVED.name()));
        return contractHeadId;
    }

    @Override
    public String getUrlById(Long contractHeadId, String extStampSignSeq) {
        Long contractId = getContractIdByContractHeadId(contractHeadId, extStampSignSeq);
        String url = null;
        try {
            url = pjProjectExtClient.preSignUrl(contractId);
        } catch (Exception e) {
            log.error("获取电子签章url失败:" + e);
            log.error("获取电子签章url失败:" + e.getMessage());
            throw new BaseException("获取电子签章url失败:" + e.getMessage());
        }
        return url;
    }

    @Override
    public void confirm(Long contractHeadId, String extStampSignSeq) {
        Long contractId = getContractIdByContractHeadId(contractHeadId, extStampSignSeq);
        try {
            pjProjectExtClient.send(contractId);
        } catch (Exception e) {
            log.error("调用合同发起接口更改状态失败:" + e);
            log.error("调用合同发起接口更改状态失败:" + e.getMessage());
            throw new BaseException("调用合同发起接口更改状态失败:" + e.getMessage());
        }

        // 把状态改为签章中
        qlService.updateByWrapper(QlWrappers.update("ContractHead")
                .eq("contractHeadId", contractHeadId)
                .set("contractStatus", ContractStatus.SIGNATUREING.name()));
    }

    private Long getContractIdByContractHeadId(Long contractHeadId, String extStampSignSeq) {
        // 如已生成合同则不需重复生成
        List<Record> contractHeadRecordList = qlService.queryByWrapper(QlWrappers.query("ContractHead")
                .eq("contractHeadId", contractHeadId), Record.class);
        String contractName = contractHeadRecordList.get(0).getString("contractName");
        String stampContractFileuploadId = contractHeadRecordList.get(0).getString("stampContractFileuploadId");
        if (stampContractFileuploadId != null) {
            return Long.valueOf(stampContractFileuploadId);
        }
        // 1.根据附件生成合同文件
        List<Record> annexRecordList = qlService.queryByWrapper(QlWrappers.query("Annex")
                .eq("contractHeadId", contractHeadId), Record.class);
        Assert.isTrue(CollectionUtils.isNotEmpty(annexRecordList), "当前合同无附件信息,请维护");
        List<Long> fileuploadIdList = annexRecordList.stream().map(item -> item.getLong("fileuploadId")).collect(Collectors.toList());
        /*Long fileId = null;
        try {
            fileId = pjProjectExtClient.createbyfiles(fileuploadIdList, contractName);
        } catch (Exception e) {
            log.error("调用附件生成合同文档失败:" + e);
            log.error("调用附件生成合同文档失败:" + e.getMessage());
            throw new BaseException("调用附件生成合同文档失败:" + e.getMessage());
        }*/
        List<Signatory> signatoryList = new ArrayList<>();
        List<Record> contractPartnerRecordList = qlService.queryByWrapper(QlWrappers.query("ContractPartner")
                .eq("contractHeadId", contractHeadId), Record.class);
        Record companyRecord = contractPartnerRecordList.stream().filter(item -> PARTNER_A.equals(item.getString("partnerType"))).collect(Collectors.toList()).get(0);
        Record vendorRecord = contractPartnerRecordList.stream().filter(item -> PARTNER_B.equals(item.getString("partnerType"))).collect(Collectors.toList()).get(0);
        if (VENDOR_FIRST.equals(extStampSignSeq)) {
            // 对方先签 乙方
            buildSignAction(vendorRecord, signatoryList, 1);
            buildSignAction(companyRecord, signatoryList, 2);
        }
        if (COMPANY_FIRST.equals(extStampSignSeq)) {
            // 我方先签 甲方
            buildSignAction(companyRecord, signatoryList, 1);
            buildSignAction(vendorRecord, signatoryList, 2);
        }
        Long contractId = null;
        try {
            Map<String, Object> questParm = new HashMap<>(16);
            questParm.put("fileIdList", fileuploadIdList);
            questParm.put("orderId", contractHeadId);
            questParm.put("orderType", "SIGN_CALLBACK");
            questParm.put("contractName", contractName);
            questParm.put("signatoryList", signatoryList);
            log.info("调用生成合同接口入参:" + JSONObject.toJSONString(questParm));
            contractId = pjProjectExtClient.createContractByCategoryForContract(JSONObject.parseObject(JSONObject.toJSONString(questParm)));
            log.info("调用生成合同接口返回:" + contractId);
        } catch (Exception e) {
            log.error("调用生成合同接口失败:" + e);
            log.error("调用生成合同接口失败:" + e.getMessage());
            throw new BaseException("调用生成合同接口失败:" + e.getMessage());
        }
        // 把id更新回STAMP_CONTRACT_FILEUPLOAD_ID
        qlService.updateByWrapper(QlWrappers.update("ContractHead")
                .eq("contractHeadId", contractHeadId)
                .set("stampContractFileuploadId", contractId)
                .set("extStampSignSeq", extStampSignSeq));
        return contractId;
    }

    private void buildSignAction(Record vendorRecord, List<Signatory> signatories, Integer serialNo) {
        Signatory signatory = new Signatory();
        List<Action> actionList = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        Action action = new Action();
        Operator operator = new Operator();
        signatory.setTenantName(vendorRecord.getString("partnerName"));
        signatory.setSerialNo(serialNo);
        action.setSerialNo(1);
        String partnerType = "partnerType";
        signatory.setReceiverNumber(vendorRecord.getString("extEmployeeNumber"));
        signatory.setContact(vendorRecord.getString("phone"));
        if (PARTNER_A.equals(vendorRecord.getString(partnerType))) {
            operator.setOperatorNumber(vendorRecord.getString("extEmployeeNumber"));
        }
        if (PARTNER_B.equals(vendorRecord.getString(partnerType))) {
            operator.setOperatorContact(vendorRecord.getString("phone"));
        }
        operators.add(operator);
        action.setActionOperators(operators);
        Action actionNew = new Action();
        BeanUtils.copyProperties(action,actionNew);
        // 23-12-20 特意重复
        actionList.add(action);
        actionList.add(actionNew);
        signatory.setActions(actionList);
        signatories.add(signatory);
    }

    private void buildSignInfo(Long contractHeadId, CreateContractParamDTO request, Record contractHeadRecord) {
        ContractSignInfoDTO contractSignInfoDTO = new ContractSignInfoDTO();
        // 是否需要用印
        contractSignInfoDTO.setIsSign(1);
        // 是否已经完成用印
        contractSignInfoDTO.setIsSignComplete(1);
        // 是否流程签署
        contractSignInfoDTO.setIsProcessSign(1);
        // 签章类型
        contractSignInfoDTO.setSealType("合同章");
        // 签章方式 1.电子签章 2.纸质签章
        contractSignInfoDTO.setSignType("ELECTRONIC_CONTRACT".equals(contractHeadRecord.getString("formal")) ? 1 : 2);
        //StampAnnex 签章附件的创建日期 contractHeadId
        List<Record> stampAnnexRecordList = qlService.queryByWrapper(QlWrappers.query("StampAnnex")
                .eq("contractHeadId", contractHeadId), Record.class);
        List<Map<String, String>> signFiles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(stampAnnexRecordList)) {
            List<Long> fileuploadIdList = stampAnnexRecordList.stream().map(item -> item.getLong("fileuploadId")).collect(Collectors.toList());
            List<Fileupload> fileuploads = fileCenterClient.find(fileuploadIdList);
            Map<Long, Fileupload> fileuploadMap = fileuploads.stream().collect(Collectors.toMap(Fileupload::getFileuploadId, Function.identity()));
            // 签署完成时间 "2021-10-01 10:00:00"
            contractSignInfoDTO.setSignTime(DateUtil.parseDateToStr(stampAnnexRecordList.get(0).getDate("creationDate"), "yyyy-MM-dd"));
            for (Record record : stampAnnexRecordList) {
                String fileSourceName = record.getString("fileSourceName");
                Long fileuploadId = record.getLong("fileuploadId");
                String url = gatewayUrl + "/cloud-srm/api-pj/external/file/download?fileSourceName=%s&fileuploadId=%s";
                Map<String, String> signFile = new HashMap<>(16);
                signFile.put("fileName", fileSourceName);
                signFile.put("docType", fileuploadMap.get(fileuploadId).getFileExtendType());
                signFile.put("fileUrl", String.format(url, fileSourceName, fileuploadId));
                signFiles.add(signFile);
            }
        }
        // 已签署文件
        contractSignInfoDTO.setSignFiles(JSONArray.toJSONString(signFiles));
        // 用印信息
        request.setContractSignInfo(contractSignInfoDTO);
    }

    private CreateContractParamDTO buildContractHead(Long contractHeadId, Record contractHeadRecord) {
        // 合同经办人
        Long contractManagerId = contractHeadRecord.getLong("extContractHandlerId");
        List<Record> contractPartnerRecordList = qlService.queryByWrapper(QlWrappers.query("ContractPartner")
                .eq("contractHeadId", contractHeadId)
                .eq("partnerType", PARTNER_A), Record.class);

        // 组装CreateContractParamDTO,
        CreateContractParamDTO request = new CreateContractParamDTO();
        // 合同分类全路径
        request.setAllContractType("采购类>通用类");
        // 业务唯一标识
        request.setBusinessId(contractHeadId.toString());
        // 合同编号
        request.setContractCode(contractHeadRecord.getString("contractCode"));
        // 合同种类
        request.setContractKind(ContractType.MIAN_CONTRACT_ADD.name().equals(contractHeadRecord.getString("contractType")) ? 1 : 2);
        // 合同名称
        request.setContractName(contractHeadRecord.getString("contractName"));
        if (contractManagerId != null) {
            User contractManager = rbacClient.getUserByIdAnon(contractManagerId);
            // 经办人
            request.setCreateAccount(contractManager.getUsername());
        }
        // 创建时间
        request.setCreateTime(DateUtil.parseDateToStr(contractHeadRecord.getDate("creationDate"), "yyyy-MM-dd HH:mm:ss"));
        // 是否审核完成
        request.setIsCreateComplete(1);
        // 是否为正式合同
        request.setOfficialFileFlag(0);
        if (CollectionUtils.isNotEmpty(contractPartnerRecordList)) {
            // 我方签约主体
            request.setSignCompanyName(contractPartnerRecordList.get(0).getString("partnerName"));
            // 我方签署主体社会信用代码
            request.setSignTaxCode(contractPartnerRecordList.get(0).getString("taxPayer"));
        }
        // 合同来源
        request.setSource("SRM");
        // 合同状态
        request.setState("4");
        // 合同分类
        request.setTailContractType("通用类");
        // 期限类型
        request.setTimeLimit(1);
// 最后修改人
//        request.setUpdateAccount(contractHeadRecord.getString("lastUpdatedBy"));
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        // 最后修改人
        request.setUpdateAccount(loginAppUser.getUsername());
        // 更新时间
        request.setUpdateTime(DateUtil.parseDateToStr(contractHeadRecord.getDate("lastUpdateDate"), "yyyy-MM-dd HH:mm:ss"));
        // 是否免审，默认为1
        request.setAuditProcess(1);
        // 特殊类型合同，默认为0
        request.setSpeciaType(0);
        // 期限开始时间
        request.setTimeStart(DateUtil.parseDateToStr(contractHeadRecord.getDate("effectiveDateFrom"), "yyyy-MM-dd"));
        // 期限截止时间
        request.setTimeEnd(DateUtil.parseDateToStr(contractHeadRecord.getDate("effectiveDateTo"), "yyyy-MM-dd"));
        // 无固定期限说明
        request.setLimitDesc(null);
        // 原合同id，默认为null
        request.setRelateContractId(contractHeadRecord.getLong("contractOldId"));
        // 末级分类ID 测试：1065；正式：846
        request.setTailTypeId(Integer.valueOf(tailTypeId));
        String stampContractFileuploadId = contractHeadRecord.getString("stampContractFileuploadId");
        if (stampContractFileuploadId != null) {
            // 契约锁合同id STAMP_CONTRACT_FILEUPLOAD_ID
            request.setSignContractId(stampContractFileuploadId.toString());
        }
        Long modelHeadId = contractHeadRecord.getLong("modelHeadId");
        if (modelHeadId != null) {
            List<Record> modelHeadRecordList = qlService.queryByWrapper(QlWrappers.query("ModelHead")
                    .eq("modelHeadId", modelHeadId), Record.class);
            Record modelHeadRecord = modelHeadRecordList.get(0);
            // 是否法务审核 NO_EXAMINE_FLAG
            request.setLawyerFlag(Enable.Y.name().equals(modelHeadRecord.getString("NO_EXAMINE_FLAG")) ? 0 : 1);
        } else {
            // 是否法务审核
            request.setLawyerFlag(1);
        }
        return request;
    }

    private void buildOpposite(Long contractHeadId, CreateContractParamDTO request) {
        List<Record> contractPartnerRecordList = qlService.queryByWrapper(QlWrappers.query("ContractPartner")
                .eq("contractHeadId", contractHeadId)
                .eq("partnerType", PARTNER_B), Record.class);
        Record contractPartner = contractPartnerRecordList.get(0);
        String companyName = contractPartner.getString("partnerName");
        List<Record> oppositeCompanyInfoList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER)
                .eq("companyName", companyName), Record.class);
        Assert.isTrue(CollectionUtils.isNotEmpty(oppositeCompanyInfoList), "乙方供应商信息获取失败");
        Record oppositeCompanyInfo = (Record) oppositeCompanyInfoList.get(0);
        List<OppositeCollectDTO> oppositeCollectDTOList = new ArrayList<>();
        OppositeCollectDTO oppositeCollectDTO = new OppositeCollectDTO();
        // 是否签署完成
        oppositeCollectDTO.setSignState(1);
        // 相对方名称
        oppositeCollectDTO.setOppositeName(companyName);
        // 相对方社会信用代码
        oppositeCollectDTO.setTaxCode(contractPartner.getString("taxPayer"));
        // 相对方编码
        oppositeCollectDTO.setSupplierCode(oppositeCompanyInfo.getString("companyCode"));
        // 签章类型 按供应商类型来OVERSEAS_RELATION
        oppositeCollectDTO.setSealType("PERSONAL".equals(oppositeCompanyInfo.getString("overseasRelation")) ? 2 : 1);
        String accountGroup = oppositeCompanyInfo.getString("accountGroup");
        // 单位类型 0.内部  1.关联 2.外部（Z001：外部，Z002：关联，Z003:内部）
        if (Z001.equals(accountGroup)) {
            oppositeCollectDTO.setUnitType("2");
        }
        if (Z002.equals(accountGroup)) {
            oppositeCollectDTO.setUnitType("1");
        }
        if (Z003.equals(accountGroup)) {
            oppositeCollectDTO.setUnitType("0");
        }
        // 默认赋值
        // 是否涉及美国成分分析
        oppositeCollectDTO.setIsAmericaIngredient(0);
        // 是否签订阳光协议
        oppositeCollectDTO.setIsJusticeAgreement(0);
        // 是否签订保密协议
        oppositeCollectDTO.setIsSecretAgreement(0);
        // StampAnnex,scc_contract_annex
        List<Record> annexRecordList = qlService.queryByWrapper(QlWrappers.query("StampAnnex")
                .eq("contractHeadId", contractHeadId), Record.class);
        List<ContractFileDTO> contractFileDTOList = new ArrayList<>();
        for (Record record : annexRecordList) {
            Long fileuploadId = record.getLong("fileuploadId");
            String fileType = record.getString("fileType");
            if (fileuploadId == null) {
                continue;
            }
            ContractFileDTO contractFileDTO = buildOppositeFileDto(oppositeCollectDTO, fileuploadId, fileType);
            // 文件详细信息
            contractFileDTOList.add(contractFileDTO);
        }
        // 文件列表`
        oppositeCollectDTO.setContractFiles(contractFileDTOList);
        oppositeCollectDTOList.add(oppositeCollectDTO);
        // 相对方
        request.setContractOppositeCollects(oppositeCollectDTOList);
    }

    private ContractFileDTO buildOppositeFileDto(OppositeCollectDTO oppositeCollectDTO,Long fileuploadId,String fileType) {
        Fileupload fileupload = extFileCenterClient.queryById(fileuploadId);
        ContractFileDTO contractFileDTO = new ContractFileDTO();
        FileDTO fileDTO = new FileDTO();
        // 创建方式
        contractFileDTO.setCreateType(2);
        // 是否用印
        contractFileDTO.setIsSign(0);
        if (ContractAgreementAttachmentType.SUNSHINE_PROTOCOL.getCode().equals(fileType)) {
            // 附件类型 2.阳光协议 3.保密协议 4.美国成分分析表 99.其他
            contractFileDTO.setFileType(2);
            // 附件类型名称
            contractFileDTO.setFileTypeName(ContractAgreementAttachmentType.SUNSHINE_PROTOCOL.getDesc());
            // 是否签订保密协议
            oppositeCollectDTO.setIsJusticeAgreement(1);
        } else if (ContractAgreementAttachmentType.SECRECY_PROTOCOL.getCode().equals(fileType)) {
            // 附件类型 2.阳光协议 3.保密协议 4.美国成分分析表 99.其他
            contractFileDTO.setFileType(3);
            // 附件类型名称
            contractFileDTO.setFileTypeName(ContractAgreementAttachmentType.SECRECY_PROTOCOL.getDesc());
            // 是否签订保密协议
            oppositeCollectDTO.setIsSecretAgreement(1);
        } else if (ContractAgreementAttachmentType.AMERICAN_COMPOSITION.getCode().equals(fileType)) {
            // 附件类型 2.阳光协议 3.保密协议 4.美国成分分析表 99.其他
            contractFileDTO.setFileType(4);
            // 附件类型名称
            contractFileDTO.setFileTypeName(ContractAgreementAttachmentType.AMERICAN_COMPOSITION.getDesc());
            // 是否涉及美国成分分析
            oppositeCollectDTO.setIsAmericaIngredient(1);
        }  else if (ContractAgreementAttachmentType.CONTRACT_AGREEMENT.getCode().equals(fileType)) {
            // 附件类型 1. 合同正文 2.阳光协议 3.保密协议 4.美国成分分析表 99.其他
            contractFileDTO.setFileType(1);
            // 附件类型名称
            contractFileDTO.setFileTypeName(ContractAgreementAttachmentType.CONTRACT_AGREEMENT.getDesc());
        } else {
            // 附件类型 2.阳光协议 3.保密协议 4.美国成分分析表 99.其他
            contractFileDTO.setFileType(99);
            // 附件类型名称
            contractFileDTO.setFileTypeName("其他");
        }
        // fileuploadId
        fileDTO.setDocType(fileupload.getFileExtendType());
        fileDTO.setFileName(fileupload.getFileSourceName());
        String url = gatewayUrl + "/cloud-srm/api-pj/external/file/download?fileSourceName=%s&fileuploadId=%s";
        fileDTO.setFileUrl(String.format(url, fileupload.getFileSourceName(), fileupload.getFileuploadId()));
        contractFileDTO.setFiles(Arrays.asList(fileDTO));
        return contractFileDTO;
    }

        private void buildFinance(Long contractHeadId, CreateContractParamDTO request, Record contractHeadRecord) {
        ContractFinanceDTO contractFinance = new ContractFinanceDTO();
        String isFrameworkAgreement = contractHeadRecord.getString("isFrameworkAgreement");
        String ceeaControlMethod = contractHeadRecord.getString("ceeaControlMethod");
        if (QUAN_AMOUNT_LIMIT.equals(ceeaControlMethod)) {
            // 计价方式：1.固定总价 2.无固定总价 3.无金额
            contractFinance.setPriceType(1);
        }
        if (CERTAION_AMOUNT.equals(ceeaControlMethod)) {
            // 计价方式：1.固定总价 2.无固定总价 3.无金额
            contractFinance.setPriceType(2);
            // 暂估金额,无固定总价传0,其他传空
            contractFinance.setEstimateAmount(0);
        }
        if (Enable.Y.name().equals(isFrameworkAgreement)) {
            // 计价方式：1.固定总价 2.无固定总价 3.无金额
            contractFinance.setPriceType(3);
        }
        BigDecimal includeTaxAmount = contractHeadRecord.getBigDecimal("includeTaxAmount");
        // 合同金额
        contractFinance.setAmount(includeTaxAmount != null ? includeTaxAmount.setScale(2, RoundingMode.HALF_UP) : null);
        // 币种
        List<DictItem> items = baseClient.listDictItemByDictCode("CONTRACT_BI");
        HashMap<String,String>hashmap=new HashMap<>(15);
        for(int i=0;i<items.size();i++){
                hashmap.put(items.get(i).getDictItemCode(),items.get(i).getDictItemName());
        }
        Integer name=0;
        if(!StringUtils.isBlank(contractHeadRecord.getString("currencyCode")))
            Integer.valueOf(hashmap.get(contractHeadRecord.getString("currencyCode")));
        contractFinance.setCurrency(name);
        // 金额收支方向 1.我方付款 2.我方收款
        String  income=contractHeadRecord.getString("extIncome");
        //我方收款
        Integer incomeCode = 1;
        if(income.equals("in")) {
           incomeCode=2;
        }
        contractFinance.setPayDirection(incomeCode);
        //PayPlan.PAY_EXPLAIN 是id
        List<Record> payPlanRecordList = qlService.queryByWrapper(QlWrappers.query("PayPlan")
                .eq("contractHeadId", contractHeadId), Record.class);
        if (CollectionUtils.isNotEmpty(payPlanRecordList)) {
            List<Map<String, String>> paymentTypes = new ArrayList<>();
            List<Long> payExplainIdList = payPlanRecordList.stream().map(item -> Long.valueOf(item.getString("payExplain"))).collect(Collectors.toList());
            //PayType ,根据
            List<Record> payTypeRecordList = qlService.queryByWrapper(QlWrappers.query("PayType")
                    .in("payTypeId", payExplainIdList), Record.class);
            Map<String, String> payTypeMap = payTypeRecordList.stream().collect(Collectors.toMap(item -> String.valueOf(item.getLong("payTypeId")), item -> item.getString("payExplain")));

            for (Record payPlanRecord : payPlanRecordList) {
                Map<String, String> paymentType = new HashMap<>(16);
                BigDecimal stagePaymentAmount = payPlanRecord.getBigDecimal("stagePaymentAmount");
                paymentType.put("condition", payTypeMap.get(payPlanRecord.getString("payExplain")));
                if (stagePaymentAmount != null) {
                    paymentType.put("amount", stagePaymentAmount.toString());
                } else {
                    paymentType.put("amount", "0");
                }
                paymentTypes.add(paymentType);
            }
            // 付款方式,金额收支方向1时必填 [{"condition":"甲方验收合格后，乙方在30个工作日内开具全额增值税发票","amount":"672.5"}]
            contractFinance.setPaymentType(JSONArray.toJSONString(paymentTypes));
        }
        // 财务信息
        request.setContractFinance(contractFinance);
    }

}
