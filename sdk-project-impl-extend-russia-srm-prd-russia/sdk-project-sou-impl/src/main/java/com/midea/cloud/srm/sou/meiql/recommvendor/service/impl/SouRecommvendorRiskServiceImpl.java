package com.midea.cloud.srm.sou.meiql.recommvendor.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.ExtSupplierClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.enums.DictCodeEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorTypeEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.*;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RiskItemType;
import com.midea.cloud.srm.model.sou.req.*;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.PreBidFeedbackStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.PreBidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.VendorFeedbackStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.*;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.meiql.recommvendor.mapper.RecommvendorMapper;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.factory.VendorRiskFactory;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor.*;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class SouRecommvendorRiskServiceImpl implements SouRecommvendorRiskService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private QlService qlService;

    @Autowired
    private PjSouClient pjSouClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ExtSupplierClient extSupplierClient;

    @Autowired
    private RecommvendorMapper recommvendorMapper;

    private static final String API_REDIS_FLAG = "Redis:N";

    /** 供应商-主数据 */
    public static final String SUPPLIER = "CompanyInfo";

    public static final String PR_SOU_REQUIREMENT_POOL_FOR_BUYER = "PrSouRequirementPoolForBuyer";

    public static final String EXT_PR_SOU_REQUIREMENT_HEAD = "ExtPrSouRequirementHead";

    /** GSCP缓存 */
    public static final String GSCP_COMPANY_KEY = "GSCP_COMPANY";

    /** -主要人员， */
    private static final String TYPE_DIRECTORS = "DIRECTORS";

    /** 主要股东 */
    private static final String TYPE_SHAREHOLDERS = "SHAREHOLDERS";

    private static final String SUNSHINE_CREDIT = "SUNSHINE_CREDIT";

    /**
     * 统计风险数量
     * @param riskDto 参数
     * @param value 参数
     */
    protected void riskItemCount(RecommvendorRiskDto riskDto, String... value) {
        Arrays.stream(value).forEach(v -> {
            if(YesOrNo.YES.getValue().equals(v)) {
                riskDto.count(1);
            }
        });
    }

    @Override
    public RecommvendorRiskDto vendorRiskNew(RecommvendorProjectDto param) {
        //获取字典
        List<DictItemDTO> dictItemsAllList = baseClient.listByDictCode(Arrays.asList(DictCodeEnum.RECOMMVENOR_SERVICE.getCode(), DictCodeEnum.RISK_LEVEL.getCode(), DictCodeEnum.RISK_TYPE.getCode()));

        Map<String, List<DictItemDTO>> dictMap = dictItemsAllList.stream().collect(Collectors.groupingBy(DictItemDTO::getDictCode));

        List<DictItem> dictItems = JSON.parseArray(JSON.toJSONString(dictMap.getOrDefault(DictCodeEnum.RECOMMVENOR_SERVICE.getCode(), new ArrayList<>(16))), DictItem.class);

        //构造数据
        Map<String, RiskResponse> riskResponseMap = new HashMap<>(15);
        //
        RiskRequest generatorRequest = buildGenerateRequest(param, dictItems);
        generatorRequest.setDictMap(dictMap);

        RiskResponse generatorResponse = VendorRiskFactory.getInstance().riskDataGenerator().todo(generatorRequest);
        riskResponseMap.put(VendorRiskDataGenerator.class.getSimpleName(), generatorResponse);

        //准备数据
        RiskRequest preRequest = new RiskRequest();
        preRequest.setVendorIdList(generatorRequest.getVendorIdList());
        preRequest.setDictMap(dictMap);
        //联系人
        RiskResponse responseContact = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentContact.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentContact.class.getSimpleName(), responseContact);
        //爬虫
        RecommvendorRiskDto recommvendorRiskDto = (RecommvendorRiskDto) generatorResponse.getData();
        preRequest.setVendorRiskList(recommvendorRiskDto.getVendorRiskList());
        preRequest.setRecommvenorServiceDictItems(dictItems);
        RiskResponse responseCrawler = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentCrawler.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentCrawler.class.getSimpleName(), responseCrawler);
        //供应商推荐
        preRequest.setApplicantNo(param.getApplicantNo());
        RiskResponse responseRecomm = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentRecomm.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentRecomm.class.getSimpleName(), responseRecomm);

        //供应商历史报名重复信息-联系人 电话 邮箱
        RiskRequest hisReg = new RiskRequest();
        hisReg.setData(responseRecomm.getData());
        RiskResponse riskResponseHistoryInfo = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentRecommHistoryReg.class.getSimpleName()).todo(hisReg);
        riskResponseMap.put(VendorRiskPreTreatmentRecommHistoryReg.class.getSimpleName() , riskResponseHistoryInfo);

        //供应商品类受限详细内容
        RiskResponse riskResponseGeneratorDtl = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskDataCategoryRestriction.class.getSimpleName()).todo(generatorRequest);
        riskResponseMap.put(VendorRiskDataCategoryRestriction.class.getSimpleName(),riskResponseGeneratorDtl);

        /*//关联供应商 -- 复杂版  关联关系会穿透，例如 A-C关联， B-C关联， 未维护A-B关联关系，也会识别为 A-B关联
        RiskResponse responseRelation = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentRelation.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentRelation.class.getSimpleName(), responseRelation);*/
        //关联供应商--简单版 关联关系不穿透，例如 A-C关联， B-C关联， 未维护A-B关联关系，那么 A-B不关联
        RiskResponse responseRelation = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentSimpleRelation.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentSimpleRelation.class.getSimpleName(), responseRelation);
        //异常名录
        builExceptionRequest(preRequest, responseContact, responseCrawler, responseRecomm);
        RiskResponse responseExceptionVendor = VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentExceptionVendor.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentExceptionVendor.class.getSimpleName(), responseExceptionVendor);
        //供应商风险管理
        RiskResponse responseMonitor= VendorRiskFactory.getInstance().riskPreTreatment(VendorRiskPreTreatmentMonitor.class.getSimpleName()).todo(preRequest);
        riskResponseMap.put(VendorRiskPreTreatmentMonitor.class.getSimpleName(), responseMonitor);
        //处理数据
        RiskRequest treatingRequest = new RiskRequest();
        treatingRequest.setData(riskResponseMap);

        RiskResponse treatingResponse = VendorRiskFactory.getInstance().riskDataTreating().todo(treatingRequest);
        return (RecommvendorRiskDto) treatingResponse.getData();
    }

    private void builExceptionRequest(RiskRequest preRequest, RiskResponse responseContact, RiskResponse responseCrawler, RiskResponse responseRecomm) {
        preRequest.setContactNameList(new ArrayList<>());
        preRequest.setCeeaContactMethodList(new ArrayList<>());
        preRequest.setEmailList(new ArrayList<>());

        Map<Long, List<ContactInfoDto>> vendorContactMap = (Map<Long, List<ContactInfoDto>>) responseContact.getData();
        if(MapUtils.isNotEmpty(vendorContactMap)) {
            for(List<ContactInfoDto> contactInfoDtoList : vendorContactMap.values()) {
                for(ContactInfoDto contactInfoDto : contactInfoDtoList) {
                    addList(preRequest.getContactNameList(), contactInfoDto.getContactName());
                    addList(preRequest.getCeeaContactMethodList(), contactInfoDto.getCeeaContactMethod());
                    addList(preRequest.getEmailList(), contactInfoDto.getEmail());
                }
            }
        }

        Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap = (Map<String, CompanyAQCApiDTO>) responseCrawler.getData();
        if(MapUtils.isNotEmpty(companyAqcApiDtoMap)) {
            for(CompanyAQCApiDTO aqc : companyAqcApiDtoMap.values()) {
                addList(preRequest.getContactNameList(), aqc.getData().getLegalPerson());
                addList(preRequest.getContactNameList(), aqc.getData().getShareholdersData());
                addList(preRequest.getContactNameList(), aqc.getData().getDirectorsData());
            }
        }

        Map<Long, RecommvendorDto> vendorRecommMap = (Map<Long, RecommvendorDto>) responseRecomm.getData();
        if(MapUtils.isNotEmpty(vendorRecommMap)) {
            for(RecommvendorDto recommvendorDto : vendorRecommMap.values()) {
                addList(preRequest.getContactNameList(), recommvendorDto.getLinkmanName());
                addList(preRequest.getCeeaContactMethodList(), recommvendorDto.getPhone());
                addList(preRequest.getEmailList(), recommvendorDto.getEmail());
            }
        }
    }

    private void addList(List<String> list, List<String> value) {
        if(CollectionUtils.isNotEmpty(value)) {
            value.stream().forEach(v -> addList(list, v));
        }
    }

    private void addList(List<String> list, String value) {
        if(StringUtils.isNotBlank(value) && !list.contains(value)) {
            list.add(value);
        }
    }

    private RiskRequest buildGenerateRequest(RecommvendorProjectDto param, List<DictItem> dictItems) {
        //启用阳光诚信接口标识
        Boolean sunshineCreditFlag = dictItems.stream().filter(d -> SUNSHINE_CREDIT.equals(d.getDictItemCode()) && YesOrNo.YES.getValue().equals(d.getItemDescription())).findAny().isPresent();
        RiskRequest request = new RiskRequest();
        request.setSunshineCreditFlag(sunshineCreditFlag);
        request.setVendorIdList(param.getRecommvendorList().stream().map(v->v.getVendorId()).distinct().collect(Collectors.toList()));
        return request;
    }



    @Override
    public RecommvendorRiskDto vendorRisk(RecommvendorProjectDto param) {

        List<DictItem> dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.RECOMMVENOR_SERVICE.getCode());

        //启用阳光诚信接口标识
        Boolean sunshineCreditFlag = dictItems.stream().filter(d -> "SUNSHINE_CREDIT".equals(d.getDictItemCode()) && YesOrNo.YES.getValue().equals(d.getItemDescription())).findAny().isPresent();


        List<Long> vendorIdList = param.getRecommvendorList().stream().map(p -> p.getVendorId()).distinct().collect(Collectors.toList());
        //查询黑名单
        List<RecordDTO> recordList = queryBlackList(vendorIdList, sunshineCreditFlag);

        //查询供应商默认联系人
        List<ContactInfoDto> vendorContactList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, vendorIdList).eq(ContactInfoDto::getCeeaDefaultContact, YesOrNo.YES.getValue()), ContactInfoDto.class);
        Map<Long, ContactInfoDto> vendorContactMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(vendorContactList)) {
            vendorContactMap = vendorContactList.stream().collect(Collectors.toMap(c->c.getCompanyId(), Function.identity(), (k1, k2)->k2));
        }
        //取寻源报名供应商
        Map<Long, SouReqApply> vendorReqApplyMap = new HashMap<>(50);
        if(StringUtils.isNotBlank(param.getApplicantNo())) {
            List<String> applicantNoList = Arrays.asList(param.getApplicantNo().split(";"));
            List<SouReqHead> souReqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).in(SouReqHead::getRequirementHeadNo, applicantNoList), SouReqHead.class);
            if(CollectionUtils.isNotEmpty(souReqHeadList)) {
                //查询报名成功的供应商联系人
                List<SouReqApply> souReqApplyList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER).in(SouReqApply::getReqHeadId, souReqHeadList.stream().map(h -> h.getReqHeadId()).collect(Collectors.toList())).eq(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode()), SouReqApply.class);
                if(CollectionUtils.isNotEmpty(souReqApplyList)) {
                    vendorReqApplyMap = souReqApplyList.stream().collect(Collectors.toMap(c->c.getVendorId(), Function.identity(), (k1, k2)->k2));
                }
            }
        }

        List<RecommvendorDto> recommvendorDtoList = new ArrayList<>();

        recordList.stream().forEach(recordDTO -> {
            recommvendorDtoList.add(buildRecommvendor(recordDTO));
        });

        if (CollectionUtils.isEmpty(recommvendorDtoList)) {
            return new RecommvendorRiskDto();
        }

        //爬虫
        Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap = crawler(recommvendorDtoList, true, dictItems);

        RecommvendorRiskDto riskDto = new RecommvendorRiskDto();
        riskDto.setVendorRiskList(recommvendorDtoList);
        RecommvendorRiskParamDto riskParamDto = new RecommvendorRiskParamDto();
        riskParamDto.setVendorContactMap(vendorContactMap);
        riskParamDto.setVendorReqApplyMap(vendorReqApplyMap);
        //计算风险
        this.caculateRisk(companyAqcApiDtoMap, riskParamDto, riskDto);

        //关联关系 和 黑名单
        relationsRisk(riskDto, sunshineCreditFlag);

        //统计风险数量
        recommvendorDtoList.stream().forEach(recommvendorDto -> {
            riskItemCount(riskDto, recommvendorDto.getTimeLimitFlag(), recommvendorDto.getExtIsMainPoint(), recommvendorDto.getGroupBlacklistFlag(),
                    recommvendorDto.getPositionLimitFlag(), recommvendorDto.getCategoryLimitFlag(), recommvendorDto.getKeySupervisionFlag(), recommvendorDto.getExtIsDishonesty(),
                    recommvendorDto.getExtIsBizAnomaly());
        });

        return riskDto;
    }

    /**
     * 异常名录风险
     * 获取推荐供应商表：法人、股东、主要联系人、联系人电话、联系人邮箱、联系人信息与异常名单&推荐单位的关联关系供应商的
     * 法人、联系人、联系人电话、联系人邮箱进行对比。
     * 推荐单的法人、股东、主要联系人、主要人员与异常名单供应商的法人、联系人进行对比。
     * 推荐单的联系人电话、联系人邮箱与异常名单供应商的联系人电话、联系人邮箱进行对比。
     * 若对比出现相同，则提示：XXX供应商法人与异常名录XXX供应商联系人重复，重复内容为：XXX。
     * 异常名录的条件：
     * 异常名录：是否重点关注、是否重点监督、是否限制单位、是否黑名单、是否限制时间、是否限制品类为任一为是的数据
     **/
    protected void exceptionSupplierRisk(RecommvendorRiskDto riskDto, RecommvendorRiskParamDto riskParamDto) {

        if(ObjectUtils.anyNull(riskDto) || CollectionUtils.isEmpty(riskDto.getVendorRiskList())) {
            return;
        }

        //查询异常名录
        Map<String, Object> param = new HashMap<>(15);
        //联系人名字 or 法人名字
        List<String> contactNameList = new ArrayList<>();
        //联系人方式
        List<String> ceeaContactMethodList = new ArrayList<>();
        //联系人邮箱
        List<String> emailList = new ArrayList<>();

        riskDto.getVendorRiskList().stream().forEach(recommvendorDto -> {
            //供应商联系人
            ContactInfoDto contactInfoDto = riskParamDto.getVendorContactMap().getOrDefault(recommvendorDto.getVendorId(), new ContactInfoDto());
            //供应商联系人
            SouReqApply souReqApply = riskParamDto.getVendorReqApplyMap().getOrDefault(recommvendorDto.getVendorId(), new SouReqApply());

            addToList(contactNameList, contactInfoDto.getContactName());
            addToList(contactNameList, souReqApply.getApplyContactName());
            addToList(contactNameList, recommvendorDto.getExtLegal());
            addToList(ceeaContactMethodList, contactInfoDto.getCeeaContactMethod());
            addToList(ceeaContactMethodList, souReqApply.getApplyPhone());
            addToList(emailList, contactInfoDto.getEmail());
            addToList(emailList, souReqApply.getApplyEmail());
        });

        //防止查询报错
        if(CollectionUtils.isEmpty(contactNameList)) {
            contactNameList.add("");
        }

        param.put("contactNameList", contactNameList);
        param.put("ceeaContactMethodList", ceeaContactMethodList);
        param.put("emailList", emailList);

        List<ExceptionSupplierDto> exceptionSupplierDtoList = recommvendorMapper.queryExceptionSupplier(param);
        if(CollectionUtils.isEmpty(exceptionSupplierDtoList)) {
            return;
        }

        /**异常名录-联系人 */
        Map<String, List<ExceptionSupplierDto>> linkmanNameMap = new HashMap<>(15);
        /**异常名录-法人 */
        Map<String, List<ExceptionSupplierDto>> legalMap = new HashMap<>(15);
        /**异常名录-联系人方式 */
        Map<String, List<ExceptionSupplierDto>> linkmanTelMap = new HashMap<>(15);
        /**异常名录-联系人邮箱 */
        Map<String, List<ExceptionSupplierDto>> linkmanMailMap = new HashMap<>(15);
        exceptionSupplierDtoList.stream().forEach(exceptionSupplierDto -> {
            addToMap(linkmanNameMap, exceptionSupplierDto.getContactName(), exceptionSupplierDto);
            addToMap(legalMap, exceptionSupplierDto.getLegalPerson(), exceptionSupplierDto);
            addToMap(linkmanTelMap, exceptionSupplierDto.getCeeaContactMethod(), exceptionSupplierDto);
            addToMap(linkmanMailMap, exceptionSupplierDto.getEmail(), exceptionSupplierDto);
        });

        //解析风险
        riskDto.getVendorRiskList().stream().forEach(recommvendorDto -> {

            //供应商联系人
            ContactInfoDto contactInfoDto = riskParamDto.getVendorContactMap().getOrDefault(recommvendorDto.getVendorId(), new ContactInfoDto());
            //供应商联系人
            SouReqApply souReqApply = riskParamDto.getVendorReqApplyMap().getOrDefault(recommvendorDto.getVendorId(), new SouReqApply());

            /**异常名录-联系人 {0}供应商联系人与异常名录{1}供应商联系人重复，重复内容为：{2}*/
            List<String> userNameList = new ArrayList<>();
            addToList(userNameList, contactInfoDto.getContactName());
            addToList(userNameList, souReqApply.getApplyContactName());
            if(CollectionUtils.isNotEmpty(userNameList)) {
                userNameList.stream().forEach(name -> {
                    appendExceptionSupplierRisk(riskDto, name, recommvendorDto.getVendorName(), RiskItemType.LINKMAN_NAME, "{0}供应商联系人与异常名录{1}供应商联系人重复，重复内容为：{2}", linkmanNameMap);
                });
            }

            /**异常名录-联系人 {0}供应商法人与异常名录{1}供应商联系人重复，重复内容为：{2}*/
            appendExceptionSupplierRisk(riskDto, recommvendorDto.getExtLegal(), recommvendorDto.getVendorName(), RiskItemType.LINKMAN_NAME, "{0}供应商法人与异常名录{1}供应商联系人重复，重复内容为：{2}", linkmanNameMap);

            /**异常名录-法人 */
            appendExceptionSupplierRisk(riskDto, recommvendorDto.getExtLegal(), recommvendorDto.getVendorName(), RiskItemType.LEGAL, "{0}供应商法人与异常名录{1}供应商法人重复，重复内容为：{2}", legalMap);

            /**异常名录-联系人方式 */
            List<String> telList = new ArrayList<>();
            addToList(telList, contactInfoDto.getCeeaContactMethod());
            addToList(telList, souReqApply.getApplyPhone());
            if(CollectionUtils.isNotEmpty(telList)) {
                telList.stream().forEach(tel -> {
                    appendExceptionSupplierRisk(riskDto, tel, recommvendorDto.getVendorName(), RiskItemType.LINKMAN_TEL, "{0}供应商联系人电话与异常名录{1}供应商联系人电话重复，重复内容为：{2}", linkmanTelMap);
                });
            }
            /**异常名录-联系人邮箱 */
            List<String> mailList = new ArrayList<>();
            addToList(mailList, contactInfoDto.getEmail());
            addToList(mailList, souReqApply.getApplyEmail());
            if(CollectionUtils.isNotEmpty(mailList)) {
                mailList.stream().forEach(mail -> {
                    appendExceptionSupplierRisk(riskDto, mail, recommvendorDto.getVendorName(), RiskItemType.LINKMAN_MAIL, "{0}供应商联系人邮箱与异常名录{1}供应商联系人邮箱重复，重复内容为：{2}", linkmanMailMap);
                });
            }
        });

    }

    /**
     *
     * @param riskDto
     * @param value
     * @param companyName
     * @param type
     * @param msg
     * @param exceptionMap
     */
    private void appendExceptionSupplierRisk(RecommvendorRiskDto riskDto, String value, String companyName, RiskItemType type, String msg, Map<String, List<ExceptionSupplierDto>> exceptionMap) {
        if(StringUtils.isBlank(value)) {
            return;
        }
        List<String> exceptionSupplierNameList = new ArrayList<>();
        if(exceptionMap.containsKey(value)) {
            exceptionMap.get(value).stream().forEach(exceptionSupplierDto -> {
                addToList(exceptionSupplierNameList, exceptionSupplierDto.getContactName());
            });
        }
        if(CollectionUtils.isEmpty(exceptionSupplierNameList)) {
            return;
        }

        riskDto.count(1);
        riskDto.appendRiskItem(type.getIndex(), MessageFormat.format(msg, companyName, exceptionSupplierNameList.stream().collect(Collectors.joining("、")), value));
    }

    private void addToMap(Map<String, List<ExceptionSupplierDto>> map, String key, ExceptionSupplierDto value) {
        if(StringUtils.isBlank(key)) {
            return;
        }
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    private void addToList(List<String> list, String value) {
        if(StringUtils.isBlank(value)) {
            return;
        }
        Arrays.stream(value.split(";")).forEach(v -> {
            if(!list.contains(v)) {
                list.add(v);
            }
        });
    }

    protected List<RecordDTO> queryBlackList(List<Long> vendorIdList, Boolean sunshineCreditFlag) {
        if(sunshineCreditFlag) {
            List<Record> recordList = extSupplierClient.querySupplierRiskBlacklist(vendorIdList);
            if(CollectionUtils.isEmpty(recordList)) {
                return new ArrayList<>();
            }
            return JSON.parseArray(JSON.toJSONString(recordList), RecordDTO.class);
        } else {
            QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(SUPPLIER).in(CompanyInfo::getCompanyId, vendorIdList);
            List<RecordDTO> recordList = qlOpenClient.query(ContextPath.SUP, qlOpenQueryWrapper, RecordDTO.class);
            return recordList;
        }
    }

    /**
     * 关联关系风险
     * @param riskDto 参数
     * @param sunshineCreditFlag 参数
     */
    protected void relationsRisk(RecommvendorRiskDto riskDto, Boolean sunshineCreditFlag) {
        if(CollectionUtils.isEmpty(riskDto.getVendorRiskList())) {
            return;
        }
        List<Long> vendorIdList = riskDto.getVendorRiskList().stream().map(v -> v.getVendorId()).collect(Collectors.toList());
        //关联关系
        Map<Long, List<Long>> relationsMap = queryVendorRelations(vendorIdList);
        //查询关联关系黑名单
        Set<Long> relationsVendorIdSet = new HashSet<>();
        relationsMap.values().forEach(v -> relationsVendorIdSet.addAll(v));
        relationsVendorIdSet.removeAll(vendorIdList);
        Map<Long, RecordDTO> relationVendorMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(relationsVendorIdSet)) {
            List<RecordDTO> recordList = queryBlackList(new ArrayList<>(relationsVendorIdSet), sunshineCreditFlag);
            if(CollectionUtils.isNotEmpty(recordList)) {
                recordList.stream().forEach(recordDTO -> {
                    relationVendorMap.put(recordDTO.get(CompanyInfo::getCompanyId), recordDTO);
                });
            }
        }

        //4 关联关系黑名单
        List<String> relationsBlackList = new ArrayList<>();
        //7 关联关系供应商
        List<String> relationsVendorErrorList = new ArrayList<>();
        //关联链路最小关联ID分组
        Map<Long, List<RecommvendorDto>> relationsRecommvendorMap = new HashMap<>(50);

        //关联关系黑名单
        riskDto.getVendorRiskList().stream().forEach(recommvendorDto -> {
            //默认不是关联关系黑名单
            recommvendorDto.setRelBlacklistRepeatFlag(YesOrNo.NO.getValue());
            //黑名单供应商名称
            List<String> blackList = new ArrayList<>();
            //关联关系
            for(Long id: relationsMap.getOrDefault(recommvendorDto.getVendorId(), new ArrayList<>())) {
                if(Long.compare(id, recommvendorDto.getVendorId()) == 0) {
                    continue;
                }
                if(!relationVendorMap.containsKey(recommvendorDto.getVendorId())) {
                    continue;
                }

                RecordDTO recordDTO = relationVendorMap.get(recommvendorDto.getVendorId());

                if(YesOrNo.YES.getValue().equals(recordDTO.get(CompanyInfo::getIsBacklist))) {
                    recommvendorDto.setRelBlacklistRepeatFlag(YesOrNo.YES.getValue());
                    blackList.add(recordDTO.get(CompanyInfo::getCompanyName));
                }

            }

            if(CollectionUtils.isNotEmpty(blackList)) {
                relationsBlackList.add(MessageFormat.format("{0}关联关系供应商存在黑名单，黑名单为：{1}", recommvendorDto.getVendorName(), blackList.stream().collect(Collectors.joining(", "))));
            }

            //分组
            if(relationsMap.containsKey(recommvendorDto.getVendorId()) && CollectionUtils.isNotEmpty(relationsMap.get(recommvendorDto.getVendorId()))) {
                putRelationsVendorMap(relationsRecommvendorMap, relationsMap.get(recommvendorDto.getVendorId()).get(0), recommvendorDto);
            }
        });

        for(List<RecommvendorDto> list : relationsRecommvendorMap.values()) {
            relationsVendorErrorList.add(MessageFormat.format("{0}为关联关系供应商", list.stream().map(r -> r.getVendorName()).collect(Collectors.joining("与"))));
        }

        if(CollectionUtils.isNotEmpty(relationsBlackList)) {
            riskDto.addRiskItem(4, relationsBlackList.stream().collect(Collectors.joining("; ")));
        }
        if(CollectionUtils.isNotEmpty(relationsVendorErrorList)) {
            riskDto.addRiskItem(7, relationsVendorErrorList.stream().collect(Collectors.joining("; ")));
        }

    }

    protected void putRelationsVendorMap(Map<Long, List<RecommvendorDto>> relationsRecommvendorMap, Long vendorId, RecommvendorDto recommvendorDto) {
        if(!relationsRecommvendorMap.containsKey(vendorId)) {
            relationsRecommvendorMap.put(vendorId, new ArrayList<>());
        }
        relationsRecommvendorMap.get(vendorId).add(recommvendorDto);
    }

    protected List<String> addAllList(List<String>... list) {
        List<String> dataList = new ArrayList<>();
        Arrays.stream(list).forEach(l -> {
            if(CollectionUtils.isNotEmpty(l)) {
                dataList.addAll(l);
            }
        });
        return dataList.stream().filter(s -> StringUtils.isNotBlank(s)).sorted(Comparator.comparing(s ->s)).distinct().collect(Collectors.toList());
    }

    protected void caculateRisk(Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap, RecommvendorRiskParamDto riskParamDto, RecommvendorRiskDto riskDto) {
        //{"联系人重复", "联系人电话重复", "联系人邮箱重复", "法人重复",
        //            "关联关系黑名单", "股东重复", "主要人员重复", "关联关系供应商"}
        Map<String, String> checkRepeatMap = new HashMap<>(50);
        Map<String, RecommvendorDto> vendorMap = new HashMap<>(50);

        if(ObjectUtils.anyNull(riskParamDto)) {
            riskParamDto = new RecommvendorRiskParamDto();
        }
        if(ObjectUtils.anyNull(riskParamDto.getVendorContactMap())) {
            riskParamDto.setVendorContactMap(new HashMap<>(50));
        }
        if(ObjectUtils.anyNull(riskParamDto.getVendorReqApplyMap())) {
            riskParamDto.setVendorReqApplyMap(new HashMap<>(50));
        }
        extracted(companyAqcApiDtoMap, riskParamDto, riskDto, checkRepeatMap, vendorMap);

        //合并重复项
        Map<String, String> checkRepeatNewMap = mergeRepeatCheckMap(checkRepeatMap);

        //生成风险
        extracted(riskDto, vendorMap, checkRepeatNewMap);
    }

    /**
     *
     * @param companyAqcApiDtoMap
     * @param riskParamDto
     * @param riskDto
     * @param checkRepeatMap
     * @param vendorMap
     */
    private void extracted(Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap, RecommvendorRiskParamDto riskParamDto, RecommvendorRiskDto riskDto, Map<String, String> checkRepeatMap, Map<String, RecommvendorDto> vendorMap) {
        for(int i = 0; i < riskDto.getVendorRiskList().size() -1; i++) {
            RecommvendorDto baseCompany = riskDto.getVendorRiskList().get(i);
            CompanyAQCApiDTO companyAqcApiDto = companyAqcApiDtoMap.get(baseCompany.getVendorName());

            vendorMap.put(baseCompany.getVendorId().toString(), baseCompany);
            for(int j = i+1; j < riskDto.getVendorRiskList().size(); j++) {
                RecommvendorDto compareCompany = riskDto.getVendorRiskList().get(j);
                vendorMap.put(compareCompany.getVendorId().toString(), compareCompany);

                //联系人重复
                List<String> baseLinkManList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(baseCompany.getVendorId(), new ContactInfoDto()).getContactName()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(baseCompany.getVendorId(), new SouReqApply()).getApplyContactName()));
                List<String> compareLinkManList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(compareCompany.getVendorId(), new ContactInfoDto()).getContactName()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(compareCompany.getVendorId(), new SouReqApply()).getApplyContactName()));
                baseCompany.setLinkmanName(baseLinkManList.stream().collect(Collectors.joining(";")));
                compareCompany.setLinkmanName(compareLinkManList.stream().collect(Collectors.joining(";")));
                String key0 = StringUtils.joinWith("_", 0, baseCompany.getVendorId(), compareCompany.getVendorId());
                checkRepeatMap.put(key0, compareRepeatName(baseLinkManList, compareLinkManList));
                if(StringUtils.isNotBlank(checkRepeatMap.get(key0))) {
                    baseCompany.setContackRepeatFlag(YesOrNo.YES.getValue());
                    compareCompany.setContackRepeatFlag(YesOrNo.YES.getValue());
                }

                //联系人电话重复
                List<String> basePhoneList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(baseCompany.getVendorId(), new ContactInfoDto()).getCeeaContactMethod()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(baseCompany.getVendorId(), new SouReqApply()).getApplyPhone()));
                List<String> comparePhoneList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(compareCompany.getVendorId(), new ContactInfoDto()).getCeeaContactMethod()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(compareCompany.getVendorId(), new SouReqApply()).getApplyPhone()));
                baseCompany.setPhone(basePhoneList.stream().collect(Collectors.joining(";")));
                compareCompany.setPhone(comparePhoneList.stream().collect(Collectors.joining(";")));
                String key1 = StringUtils.joinWith("_", 1, baseCompany.getVendorId(), compareCompany.getVendorId());
                checkRepeatMap.put(key1, compareRepeatName(basePhoneList, comparePhoneList));
                if(StringUtils.isNotBlank(checkRepeatMap.get(key1))) {
                    baseCompany.setTelRepeatFlag(YesOrNo.YES.getValue());
                    compareCompany.setTelRepeatFlag(YesOrNo.YES.getValue());
                }

                //联系人邮箱重复
                List<String> baseEmailList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(baseCompany.getVendorId(), new ContactInfoDto()).getEmail()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(baseCompany.getVendorId(), new SouReqApply()).getApplyEmail()));
                List<String> compareEmailList = addAllList(strToList(riskParamDto.getVendorContactMap().getOrDefault(compareCompany.getVendorId(), new ContactInfoDto()).getEmail()), strToList(riskParamDto.getVendorReqApplyMap().getOrDefault(compareCompany.getVendorId(), new SouReqApply()).getApplyEmail()));
                baseCompany.setEmail(baseEmailList.stream().collect(Collectors.joining(";")));
                compareCompany.setEmail(compareEmailList.stream().collect(Collectors.joining(";")));
                String key2 = StringUtils.joinWith("_", 2, baseCompany.getVendorId(), compareCompany.getVendorId());
                checkRepeatMap.put(key2, compareRepeatName(baseEmailList, compareEmailList));
                if(StringUtils.isNotBlank(checkRepeatMap.get(key2))) {
                    baseCompany.setEmailRepeatFlag(YesOrNo.YES.getValue());
                    compareCompany.setEmailRepeatFlag(YesOrNo.YES.getValue());
                }

                CompanyAQCApiDTO compareCompanyAqcApiDto = companyAqcApiDtoMap.get(compareCompany.getVendorName());
                if(ObjectUtils.allNotNull(companyAqcApiDto, compareCompanyAqcApiDto)) {
                    //法人重复
                    String key3 = StringUtils.joinWith("_", 3, baseCompany.getVendorId(), compareCompany.getVendorId());
                    checkRepeatMap.put(key3, compareRepeatName(strToList(companyAqcApiDto.getData().getLegalPerson()), strToList(compareCompanyAqcApiDto.getData().getLegalPerson())));
                    if(StringUtils.isNotBlank(checkRepeatMap.get(key3))) {
                        baseCompany.setLegalRepeatFlag(YesOrNo.YES.getValue());
                        compareCompany.setLegalRepeatFlag(YesOrNo.YES.getValue());
                    }
                    //股东重复
                    String key5 = StringUtils.joinWith("_", 5, baseCompany.getVendorId(), compareCompany.getVendorId());
                    checkRepeatMap.put(key5, compareRepeatName(companyAqcApiDto.getData().getShareholdersData(), compareCompanyAqcApiDto.getData().getShareholdersData()));
                    if(StringUtils.isNotBlank(checkRepeatMap.get(key5))) {
                        baseCompany.setHolderRepeatFlag(YesOrNo.YES.getValue());
                        compareCompany.setHolderRepeatFlag(YesOrNo.YES.getValue());
                    }
                    //主要人员重复
                    String key6 = StringUtils.joinWith("_", 6, baseCompany.getVendorId(), compareCompany.getVendorId());
                    checkRepeatMap.put(key6, compareRepeatName(companyAqcApiDto.getData().getDirectorsData(), compareCompanyAqcApiDto.getData().getDirectorsData()));
                    if(StringUtils.isNotBlank(checkRepeatMap.get(key6))) {
                        baseCompany.setMainPeopleRepeatFlag(YesOrNo.YES.getValue());
                        compareCompany.setMainPeopleRepeatFlag(YesOrNo.YES.getValue());
                    }
                }

            }
        }

        //合并重复项
        Map<String, String> checkRepeatNewMap = mergeRepeatCheckMap(checkRepeatMap);

        //生成风险
        extracted(riskDto, vendorMap, checkRepeatNewMap);
    }

    /**
     * 生成风险
     * @param riskDto 参数
     * @param vendorMap 参数
     * @param checkRepeatNewMap 参数
     */
    private void extracted(RecommvendorRiskDto riskDto, Map<String, RecommvendorDto> vendorMap, Map<String, String> checkRepeatNewMap) {
        if(MapUtils.isNotEmpty(checkRepeatNewMap)) {
            Map<Integer, List<String>> errorMap = new HashMap<>(50);
            for(String key : checkRepeatNewMap.keySet()) {
                List<String> keyList = Arrays.asList(key.split("_"));
                Integer type = Integer.valueOf(keyList.get(0));
                List<String> companyNameList = new ArrayList<>();
                for(int i = 1; i < keyList.size(); i++) {
                    RecommvendorDto recommvendorDto = vendorMap.get(keyList.get(i));
                    companyNameList.add(recommvendorDto.getVendorName());
                }
                if(!errorMap.containsKey(type)) {
                    errorMap.put(type, new ArrayList<>());
                }
                errorMap.get(type).add(MessageFormat.format("{0}{1}, 重复内容为：{2}", companyNameList.stream().collect(Collectors.joining("与")), riskDto.buildRiskItem(type).getType(), checkRepeatNewMap.get(key)));
            }

            addRisk(riskDto, errorMap, 0, 1, 2, 3, 5, 6);
        }
    }

    protected void addRisk(RecommvendorRiskDto riskDto, Map<Integer, List<String>> errorMap, Integer... type) {
        Arrays.stream(type).forEach(t -> {
            if(errorMap.containsKey(t)) {
                riskDto.addRiskItem(t, errorMap.get(t).stream().collect(Collectors.joining("; ")));
                riskDto.count(errorMap.get(t).size());
            }
        });
    }

    private List<String> strToList(String str) {
        if(StringUtils.isNotBlank(str)) {
            return new ArrayList<>(Collections.singletonList(str));
        }

        return new ArrayList<>();
    }

    private Map<String, String> mergeRepeatCheckMap(Map<String, String> checkMap) {
        Map<String, String> newCheckMap = new HashMap<>(50);

        Map<String, List<String>> checkListMap = new HashMap<>(50);

        for(String key: checkMap.keySet()) {
            String value = checkMap.get(key);
            if(StringUtils.isNotBlank(value)) {
                if(!checkListMap.containsKey(value)) {
                    checkListMap.put(value, new ArrayList<>());
                }
                checkListMap.get(value).add(key);
            }
        }

        for(String key : checkListMap.keySet()) {
            List<String> value = checkListMap.get(key);
            Map<String, Set<String>> newKeyMap = new HashMap<>(50);
            for(String oldKey : value) {
                List<String> toOldKeyList = Arrays.asList(oldKey.split("_"));
                Set<String> newKeyList = newKeyMap.getOrDefault(toOldKeyList.get(0), new HashSet<>());
                newKeyList.addAll(toOldKeyList.subList(1, toOldKeyList.size()));
                newKeyMap.put(toOldKeyList.get(0), newKeyList);
            }
            for(String newKey: newKeyMap.keySet()) {
                newCheckMap.put(StringUtils.joinWith("_", newKey, newKeyMap.get(newKey).stream().sorted(Comparator.comparing(s->s)).collect(Collectors.joining("_"))), key);
            }
        }
        return newCheckMap;
    }

    private String compareRepeatName(List<String> baseFullNameList, List<String> compareFullNameList) {
        if(CollectionUtils.isEmpty(baseFullNameList) || CollectionUtils.isEmpty(compareFullNameList)) {
            return "";
        }
        List<String> baseList = new ArrayList<>(baseFullNameList);
        List<String> compareList = new ArrayList<>(compareFullNameList);
        baseList.retainAll(compareList);
        if(CollectionUtils.isEmpty(baseList)) {
            return "";
        }
        return baseList.stream().sorted(Comparator.comparing(s -> s)).collect(Collectors.joining(", "));
    }

    private Boolean isUserContainsOtherCompany(Map<String, List<String>> userMap, String company, List<String> userNameList, RecommvendorRiskDto.RiskItem riskItem, List<String> errorList) {
        if (CollectionUtils.isEmpty(userNameList)) {
            return false;
        }
        Boolean check = false;
        Map<String, List<String>> companyRepeatMap = new HashMap<>(NumConstant.SIXTEEN);
        for (String user : userNameList) {
            if (userMap.containsKey(user) && userMap.get(user).contains(company) && userMap.get(user).size() > 0) {
                check = true;
                userMap.get(user).stream().filter(c -> !c.equals(company)).forEach(c -> {
                    if (!companyRepeatMap.containsKey(c)) {
                        companyRepeatMap.put(c, new ArrayList<>());
                    }
                    if (!companyRepeatMap.get(c).contains(user)) {
                        companyRepeatMap.get(c).add(user);
                    }
                });
            }
        }

        if (check) {
            for (String c : companyRepeatMap.keySet()) {
                errorList.add(MessageFormat.format("{0}与{1}{2}, 重复内容为：{3}", company, c, riskItem.getType(), companyRepeatMap.get(c).stream().collect(Collectors.joining("、"))));
            }
        }
        return check;
    }

    private void userCompanyMap(Map<String, List<String>> userMap, String company, List<String> userNameList) {
        if (CollectionUtils.isEmpty(userNameList)) {
            return;
        }
        userNameList.stream().forEach(user -> {
            if (!userMap.containsKey(user)) {
                userMap.put(user, new ArrayList<>());
            }
            if (!userMap.get(user).contains(company)) {
                userMap.get(user).add(company);
            }
        });
    }

    @Override
    public Map<Long, RecordDTO> queryCompanyInfo(List<RecommvendorDto> recommvendorDtoList) {
        if(CollectionUtils.isEmpty(recommvendorDtoList)) {
            return new HashMap<>(50);
        }

        QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(SUPPLIER).in(CompanyInfo::getCompanyId, recommvendorDtoList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList()));
        List<RecordDTO> recordList = qlOpenClient.query(ContextPath.SUP, qlOpenQueryWrapper, RecordDTO.class);
        Map<Long, RecordDTO> companyInfoMap = recordList.stream().collect(Collectors.toMap(v -> v.get(CompanyInfo::getCompanyId), Function.identity(), (k1, k2)->k2));

        recommvendorDtoList.stream().filter(v -> companyInfoMap.containsKey(v.getVendorId())).forEach(recommvendorDto -> {
            RecordDTO recordDTO = companyInfoMap.get(recommvendorDto.getVendorId());
            //是否重点关注
            recommvendorDto.setExtIsMainPoint(recordDTO.getString("focusFlag"));
        });

        return companyInfoMap;
    }

    /**
     * 大数据爬虫接口
     *
     * @param recommvendorDtoList
     */
    @Override
    public Map<String, CompanyAQCApiDTO> crawler(List<RecommvendorDto> recommvendorDtoList, Boolean ignoreGscp, List<DictItem> dictItems) {

        //查询接口配置
        if(Objects.isNull(dictItems)) {
            dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.RECOMMVENOR_SERVICE.getCode());
        }
        AtomicReference<String> gscpFlag = new AtomicReference<>(YesOrNo.YES.getValue());
        AtomicReference<String> aqcApiFlag = new AtomicReference<>(YesOrNo.YES.getValue());
        //Redis:N
        AtomicReference<String> gscpRedisFlag = new AtomicReference<>(YesOrNo.YES.getValue());
        AtomicReference<String> aqcApiRedisFlag = new AtomicReference<>(YesOrNo.YES.getValue());
        if(CollectionUtils.isNotEmpty(dictItems)) {
            String gscp = "GSCP";
            String aqcApi = "AQCApi";
            dictItems.stream().forEach(d -> {
                if(gscp.equals(d.getDictItemCode())) {
                    gscpFlag.set(d.getItemDescription());
                    if(API_REDIS_FLAG.equals(d.getDictItemMark())) {
                        gscpRedisFlag.set(YesOrNo.NO.getValue());
                    }
                }
                if(aqcApi.equals(d.getDictItemCode())) {
                    aqcApiFlag.set(d.getItemDescription());
                    if(API_REDIS_FLAG.equals(d.getDictItemMark())) {
                        aqcApiRedisFlag.set(YesOrNo.NO.getValue());
                    }
                }
            });
        }

        /** 不启用gscp查询接口 */
        if(ignoreGscp) {
            gscpFlag.set(YesOrNo.NO.getValue());
        }

        //批量获取GSCP
        Map<Long, String> gscp = queryGscpBtach(recommvendorDtoList.stream().map(r -> r.getVendorId()).distinct().collect(Collectors.toList()), gscpFlag.get(), gscpRedisFlag.get());

        Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap = queryCompanyAqcApi(recommvendorDtoList, aqcApiFlag.get(), aqcApiRedisFlag.get());
        //查询供应商数据
        recommvendorDtoList.stream().forEach(recommvendorDto -> {
            CompanyAQCApiDTO companyAqcApiDto = companyAqcApiDtoMap.get(recommvendorDto.getVendorName());
            if (!Objects.isNull(companyAqcApiDto)) {
                //是否失信
                recommvendorDto.setExtIsDishonesty(booleanYesOrNo(companyAqcApiDto.getData().getBrokenPromises()));
                //是否经营异常
                recommvendorDto.setExtIsBizAnomaly(booleanYesOrNo(companyAqcApiDto.getData().getAbnormalOperation()));
                //股东是否重复
                recommvendorDto.setHolderRepeatFlag(YesOrNo.NO.getValue());
                //法人是否重复
                recommvendorDto.setLegalRepeatFlag(YesOrNo.NO.getValue());
                //主要人员是否重复
                recommvendorDto.setMainPeopleRepeatFlag(YesOrNo.NO.getValue());
                //爬虫接口 注册资金 成立时间 GSCP 公司地址 是否失信 是否经营异常 法人 主要人员 主要股东
                recommvendorDto.setExtRegisterFund(companyAqcApiDto.getData().getRegCapital());
                try {
                    if(StringUtils.isNotBlank(companyAqcApiDto.getData().getStartDate())) {
                        recommvendorDto.setExtFounded(DateUtil.parseDate(companyAqcApiDto.getData().getStartDate()));
                    }
                } catch (ParseException e) {
                    log.error("crawler ParseException", e);
                }

                recommvendorDto.setExtCompanyAddr(companyAqcApiDto.getData().getRegAddr());

                recommvendorDto.setExtIsDishonesty(booleanYesOrNo(companyAqcApiDto.getData().getBrokenPromises()));
                recommvendorDto.setExtIsBizAnomaly(booleanYesOrNo(companyAqcApiDto.getData().getAbnormalOperation()));

                recommvendorDto.setExtLegal(companyAqcApiDto.getData().getLegalPerson());
                recommvendorDto.setExtMainPeople(listToStr(companyAqcApiDto.getData().getDirectorsData()));
                recommvendorDto.setExtStockholder(listToStr(companyAqcApiDto.getData().getShareholdersData()));

            }
            recommvendorDto.setExtGscp(gscp.get(recommvendorDto.getVendorId()));
        });

        return companyAqcApiDtoMap;
    }

    /**
     * 批量从供应商库查询gscp
     * @param companyInfoIdList 参数
     * @param gscpFlag 参数
     * @param redisFlag 参数
     * @return 返回
     */
    private Map<Long, String> queryGscpBtach(List<Long> companyInfoIdList, String gscpFlag, String redisFlag) {

        if(CollectionUtils.isEmpty(companyInfoIdList)) {
            return new HashMap<>(50);
        }

        Map<Long, String> gscp = new HashMap<>(50);

        companyInfoIdList.stream().forEach(companyId -> {
            String gscpStatus = this.queryGscp(companyId, gscpFlag, redisFlag);
            gscp.put(companyId, gscpStatus);
        });

        //接口开启，且不读取缓存时，存供应商库
        if(YesOrNo.YES.getValue().equals(gscpFlag) && YesOrNo.NO.getValue().equals(redisFlag) && MapUtils.isNotEmpty(gscp)) {
            //将gscp结果反写供应商库
            Map<String, List<Long>> updateCompanyInfoMap = new HashMap<>(15);
            for(Long companyId : gscp.keySet()) {
                /** 接口请求异常时不写入供应商库 */
                if(SrmConstant.GSCP_EXCEPTION.equals(gscp.get(companyId))) {
                    continue;
                }
                if(!updateCompanyInfoMap.containsKey(gscp.get(companyId))) {
                    updateCompanyInfoMap.put(gscp.get(companyId), new ArrayList<>());
                }
                updateCompanyInfoMap.get(gscp.get(companyId)).add(companyId);
            }
            for(String gscpStatus: updateCompanyInfoMap.keySet()) {
                qlOpenClient.update(ContextPath.SUP, QlOpenWrappers.update(MqlType.SUPPLIER).set("gscpStatus", gscpStatus).in(CompanyInfo::getCompanyId, updateCompanyInfoMap.get(gscpStatus)));
            }
        }

        return gscp;
    }

    protected String listToStr(List<String> list) {
        if(CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.stream().collect(Collectors.joining(";"));
    }

    @Override
    public Map<String, CompanyAQCApiDTO> queryCompanyAqcApi(List<RecommvendorDto> recommvendorDtoList, String aqcApiFlag, String redisFlag) {
        Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap = new HashMap<>(50);

        Map<String, RecommvendorDto> vendorMap = recommvendorDtoList.stream().collect(Collectors.toMap(v -> v.getVendorName(), Function.identity(), (k1, k2) -> k2));

        if(!YesOrNo.NO.getValue().equals(redisFlag)) {
            //读取缓存
            //读取缓存
            List<Long> vendorIdList = recommvendorDtoList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //先查询缓存
            List<Record> vendorBigdataList = qlService.queryByWrapper(QlWrappers.query(MqlType.VENDOR_BIGDATA).in("vendorId", vendorIdList).ge("creationDate", calendar.getTime()), Record.class);
            List<Record> vendorBiglinkList = qlService.queryByWrapper(QlWrappers.query(MqlType.VENDOR_BIGLINK).in("vendorId", vendorIdList), Record.class);

            if (CollectionUtils.isNotEmpty(vendorBigdataList)) {
                Map<Long, List<Record>> linkMap = vendorBiglinkList.stream().collect(Collectors.groupingBy(r -> r.getLong("vendorId")));
                for (Record vendorBigData : vendorBigdataList) {
                    CompanyAQCApiDTO apiDTO = new CompanyAQCApiDTO();
                    apiDTO.setCompany(vendorBigData.getString("entName"));
                    apiDTO.setData(new CompanyAQCApiDTO.ItemData());
                    recordToCompanyAqcApiDto(vendorBigData, linkMap.get(vendorBigData.getLong("vendorId")), apiDTO);

                    companyAqcApiDtoMap.put(apiDTO.getCompany(), apiDTO);
                }
            }
        }

        List<String> allVendorNameList = vendorMap.keySet().stream().collect(Collectors.toList());
        List<String> cacheVendorNameList = companyAqcApiDtoMap.keySet().stream().collect(Collectors.toList());

        //全集减去缓存集，剩下的需要查询大数据接口
        allVendorNameList.removeAll(cacheVendorNameList);

        callCompanyAqcApi(companyAqcApiDtoMap, allVendorNameList, vendorMap, aqcApiFlag);
        return companyAqcApiDtoMap;
    }

    protected void callCompanyAqcApi(Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap, List<String> companyNameList, Map<String, RecommvendorDto> vendorMap, String aqcApiFlag) {
        if(!YesOrNo.YES.getValue().equals(aqcApiFlag)) {
            log.info("callCompanyAQCApi 接口未启用");
            return;
        }
        try {
            if (CollectionUtils.isEmpty(companyNameList)) {
                return;
            }
            log.info("callCompanyAQCApi开始爬虫接口：" + JSON.toJSONString(companyNameList));
            List<CompanyAQCApiDTO> companyAqcApiDtos = pjSouClient.findAqcByNames(companyNameList);
            List<Record> vendorBigdataList = new ArrayList<>();
            List<Record> vendorBiglinkList = new ArrayList<>();
            List<Long> vendorIdList = new ArrayList<>();
            for (CompanyAQCApiDTO companyAqcApiDto : companyAqcApiDtos) {
                if (!vendorMap.containsKey(companyAqcApiDto.getCompany())) {
                    continue;
                }
                companyAqcApiDtoMap.put(companyAqcApiDto.getCompany(), companyAqcApiDto);
                Record record = new Record();
                record.put("vendorId", vendorMap.get(companyAqcApiDto.getCompany()).getVendorId());
                vendorIdList.add(vendorMap.get(companyAqcApiDto.getCompany()).getVendorId());
                List<Record> subLinkList = new ArrayList<>();
                companyAqcApiDtoToRecord(record, subLinkList, companyAqcApiDto, vendorMap);
                vendorBigdataList.add(record);
                vendorBiglinkList.addAll(subLinkList);
            }
            if (CollectionUtils.isNotEmpty(vendorBigdataList)) {
                //先删后存
                qlService.deleteByWrapper(QlWrappers.update(MqlType.VENDOR_BIGDATA).in("vendorId", vendorIdList));
                qlService.deleteByWrapper(QlWrappers.update(MqlType.VENDOR_BIGLINK).in("vendorId", vendorIdList));

                qlService.save(MqlType.VENDOR_BIGDATA, vendorBigdataList);
                qlService.save(MqlType.VENDOR_BIGLINK, vendorBiglinkList);
            }
        } catch (Exception e) {
            log.error("callCompanyAQCApi Exception", e);
        }
    }

    private void companyAqcApiDtoToRecord(Record record, List<Record> linkRecordList, CompanyAQCApiDTO apiDTO, Map<String, RecommvendorDto> vendorMap) {
        record.put("entName", apiDTO.getData().getEntName());
        record.put("legalPerson", apiDTO.getData().getLegalPerson());
        if(StringUtils.isNotBlank(apiDTO.getData().getStartDate())) {
            record.put("startDate", apiDTO.getData().getStartDate());
        }
        record.put("regAddr", apiDTO.getData().getRegAddr());
        linkRecordList.addAll(linkRecordList(apiDTO.getData().getDirectorsData(), TYPE_DIRECTORS, apiDTO.getCompany(), vendorMap));
        linkRecordList.addAll(linkRecordList(apiDTO.getData().getShareholdersData(), TYPE_SHAREHOLDERS, apiDTO.getCompany(), vendorMap));
        record.put("brokenPromises", booleanYesOrNo(apiDTO.getData().getBrokenPromises()));
        record.put("abnormalOperation", booleanYesOrNo(apiDTO.getData().getAbnormalOperation()));
    }

    private String booleanYesOrNo(Boolean value) {
        if(ObjectUtils.anyNull(value)) {
            return null;
        }
        if (value) {
            return YesOrNo.YES.getValue();
        }
        return YesOrNo.NO.getValue();
    }

    private List<Record> linkRecordList(List<String> fullNameList, String type, String companyName, Map<String, RecommvendorDto> vendorMap) {
        if (CollectionUtils.isEmpty(fullNameList) || !vendorMap.containsKey(companyName)) {
            new ArrayList<>();
        }
        List<Record> recordList = new ArrayList<>();
        fullNameList.stream().forEach(fullName -> {
            Record link = new Record();
            link.put("vendorId", vendorMap.get(companyName).getVendorId());
            link.put("fullName", fullName);
            link.put("type", type);

            recordList.add(link);
        });

        return recordList;
    }


    private void recordToCompanyAqcApiDto(Record record, List<Record> linkRecordList, CompanyAQCApiDTO apiDTO) {

        Map<String, List<Record>> linkRecordMap = CollectionUtils.isEmpty(linkRecordList) ? new HashMap<>(50) : linkRecordList.stream().collect(Collectors.groupingBy(r -> r.getString("type")));
        apiDTO.getData().setEntName(record.getString("entName"));
        apiDTO.getData().setLegalPerson(record.getString("legalPerson"));
        apiDTO.getData().setStartDate(record.getString("startDate"));
        //注册资金
        apiDTO.getData().setRegCapital(record.getString("regCapital"));
        apiDTO.getData().setRegAddr(record.getString("regAddr"));
        apiDTO.getData().setDirectorsData(fullName(linkRecordMap.get(TYPE_DIRECTORS)));
        apiDTO.getData().setShareholdersData(fullName(linkRecordMap.get(TYPE_SHAREHOLDERS)));
        apiDTO.getData().setBrokenPromises(yesOrNo(record.getString("brokenPromises")));
        apiDTO.getData().setAbnormalOperation(yesOrNo(record.getString("abnormalOperation")));
    }

    private List<String> fullName(List<Record> linkList) {
        if (CollectionUtils.isEmpty(linkList)) {
            return new ArrayList<>();
        }
        return linkList.stream().map(r -> r.getString("fullName")).distinct().collect(Collectors.toList());
    }

    protected Boolean yesOrNo(String value) {
        if(ObjectUtils.anyNull(value)) {
            return null;
        }
        if (YesOrNo.YES.getValue().equals(value)) {
            return true;
        }
        return false;
    }


    @Override
    public String queryGscp(Long companyId, String gscpFlag, String redisFlag) {
        try {
            //先读取缓存
            if(!YesOrNo.NO.getValue().equals(redisFlag)) {
                String gscp = redisUtil.get(StringUtils.joinWith("_", GSCP_COMPANY_KEY, companyId));
                if(StringUtils.isNotBlank(gscp)) {
                    return gscp;
                }
            }
            return queryGscpFromPj(companyId, gscpFlag);
        } catch (Exception e) {
            log.error("queryGscp Exception", e);
        }
        return SrmConstant.GSCP_EXCEPTION;
    }

    protected Map<Long, List<Long>> queryVendorRelations(List<Long> vendorIdList) {
        //过程关联记录
        Map<Long, List<Long>> processMap = new HashMap<>(50);
        //关联ID查询记录
        Set<Long> relationsRecord = new HashSet<>(vendorIdList);
        //返回结果集
        Map<Long, List<Long>> dataMap = new HashMap<>(50);

        List<Long> queryVendorIdList = new ArrayList<>(vendorIdList);
        while (true) {
            List<Long> finalQueryVendorIdList = queryVendorIdList;
            List<ExtSupAssociation> associations = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER).select(ExtSupAssociation::getVendorIdA, ExtSupAssociation::getVendorIdB)
                    .in(ExtSupAssociation::getVendorIdA, vendorIdList).or(q -> q.in(ExtSupAssociation::getVendorIdB, finalQueryVendorIdList)), ExtSupAssociation.class);
            if(CollectionUtils.isEmpty(associations)) {
                //无关联关系，退出循环
                break;
            }
            //去重
            associations = associations.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StringUtils.joinWith("_", o.getVendorIdA(), o.getVendorIdB())))), ArrayList::new));

            Map<Long, List<Long>> relationMapAsc = new HashMap<>(50);
            Map<Long, List<Long>> relationMapDesc = new HashMap<>(50);
            Set<Long> associationsVendorIds = new HashSet<>();
            associations.stream().forEach(a -> {
                putMapWithSortAsc(relationMapAsc, a.getVendorIdA(), a.getVendorIdB());
                putMapWithSortDesc(relationMapDesc, a.getVendorIdA(), a.getVendorIdB());
                associationsVendorIds.add(a.getVendorIdA());
                associationsVendorIds.add(a.getVendorIdB());
            });

            queryVendorIdList.stream().forEach(vendorId -> {
                if(relationMapAsc.containsKey(vendorId)) {
                    processMap.put(vendorId, addValue(processMap.get(vendorId), relationMapAsc.get(vendorId)));
                }
                if(relationMapDesc.containsKey(vendorId)) {
                    processMap.put(vendorId, addValue(processMap.get(vendorId), relationMapDesc.get(vendorId)));
                }
            });

            //移除查询数据
            associationsVendorIds.removeAll(relationsRecord);

            if(CollectionUtils.isEmpty(associationsVendorIds)) {
                break;
            }

            queryVendorIdList = new ArrayList<>(associationsVendorIds);
            relationsRecord.addAll(queryVendorIdList);
        }

        vendorIdList.stream().forEach(vendorId -> {
            List<Long> resultList = new ArrayList<>();
            getRelationsList(processMap, new HashSet<>(), resultList, vendorId);
            dataMap.put(vendorId, resultList.stream().distinct().sorted(Comparator.comparingLong(v -> v)).collect(Collectors.toList()));
        });

        return dataMap;
    }

    /**
     * 构造关联关系网络
     * @param processMap 参数
     * @param searchRecord 参数
     * @param resultList 参数
     * @param vendorId 参数
     */
    protected void getRelationsList(Map<Long, List<Long>> processMap, Set<Long> searchRecord, List<Long> resultList, Long vendorId) {
        searchRecord.add(vendorId);
        List<Long> vendorIdList = processMap.getOrDefault(vendorId, new ArrayList<>());
        if(CollectionUtils.isEmpty(vendorIdList)) {
            return;
        }
        for(Long id: vendorIdList) {
            if(Long.compare(id, vendorId) != 0) {
                resultList.add(id);
                if(!searchRecord.contains(id)) {
                    //递归
                    getRelationsList(processMap, searchRecord, resultList, id);
                }
            }
        }
    }

    /**
     * 组装关联Map对象 小：大
     * @param relationMap
     * @param vendorIdA
     * @param vendorIdB
     */
    protected void putMapWithSortAsc(Map<Long, List<Long>> relationMap, Long vendorIdA, Long vendorIdB) {
        if(Long.compare(vendorIdA, vendorIdB) == -1) {
            relationMap.put(vendorIdA, addValue(relationMap.get(vendorIdA), vendorIdB));
        } else {
            relationMap.put(vendorIdB, addValue(relationMap.get(vendorIdB), vendorIdA));
        }
    }

    /**
     * 组装关联Map对象 大：小
     * @param relationMap
     * @param vendorIdA
     * @param vendorIdB
     */
    protected void putMapWithSortDesc(Map<Long, List<Long>> relationMap, Long vendorIdA, Long vendorIdB) {
        if(Long.compare(vendorIdA, vendorIdB) == 1) {
            relationMap.put(vendorIdA, addValue(relationMap.get(vendorIdA), vendorIdB));
        } else {
            relationMap.put(vendorIdB, addValue(relationMap.get(vendorIdB), vendorIdA));
        }
    }

    protected List<Long> addValue(List<Long> valueList, List<Long> value) {
        if(CollectionUtils.isEmpty(valueList)) {
            return value;
        }
        valueList.addAll(value);
        return valueList.stream().distinct().collect(Collectors.toList());
    }

    protected List<Long> addValue(List<Long> valueList, Long value) {
        if(CollectionUtils.isEmpty(valueList)) {
            valueList = new ArrayList<>();
        }
        valueList.add(value);
        return valueList;
    }

    private String queryGscpFromPj(Long companyId, String gscpFlag) {
        if(!YesOrNo.YES.getValue().equals(gscpFlag)) {
            log.info("queryGscpFromPj 接口未启用");
            return null;
        }
        try {
            String gscp = pjSouClient.importScreening(companyId);
            //写入缓存 12 小时
            redisUtil.set(StringUtils.joinWith("_", GSCP_COMPANY_KEY, companyId), gscp, 60*60*12);
            return gscp;
        } catch (Exception e) {
            log.error("queryGscpFromPj Exception", e);
        }
        return SrmConstant.GSCP_EXCEPTION;
    }

    protected RecommvendorDto buildRecommvendor(RecordDTO recordDTO) {
        RecommvendorDto recommvendorDto = new RecommvendorDto();
        //供应商ID
        recommvendorDto.setVendorId(recordDTO.get(CompanyInfo::getCompanyId));
        //供应商编码
        recommvendorDto.setVendorCode(recordDTO.get(CompanyInfo::getCompanyCode));
        //供应商名称
        recommvendorDto.setVendorName(recordDTO.get(CompanyInfo::getCompanyName));
        //时间受限
        recommvendorDto.setTimeLimitFlag(recordDTO.getString("timeLimitFlag"));
        //重点关注
        recommvendorDto.setExtIsMainPoint(recordDTO.getString("focusFlag"));
        //是否集团黑名单
        recommvendorDto.setGroupBlacklistFlag(recordDTO.get(CompanyInfo::getIsBacklist));
        //是否单位受限
        recommvendorDto.setPositionLimitFlag(recordDTO.getString("positionLimitFlag"));
        //是否品类受限
        recommvendorDto.setCategoryLimitFlag(recordDTO.getString("categoryLimitFlag"));
        //是否重点监督
        recommvendorDto.setKeySupervisionFlag(recordDTO.getString("keySupervisionFlag"));
        //联系人是否重复: 报名联系人&供应商主数据
        recommvendorDto.setContackRepeatFlag(YesOrNo.NO.getValue());
        //联系人电话是否重复: 报名联系人&供应商主数据
        recommvendorDto.setTelRepeatFlag(YesOrNo.NO.getValue());
        //联系人邮箱是否重复: 报名联系人&供应商主数据
        recommvendorDto.setEmailRepeatFlag(YesOrNo.NO.getValue());
        // @ApiModelProperty("是否失信")
        recommvendorDto.setExtIsDishonesty(YesOrNo.NO.getValue());
        //    @ApiModelProperty("是否经营异常")
        recommvendorDto.setExtIsBizAnomaly(YesOrNo.NO.getValue());

        // @ApiModelProperty("法人是否重复")
        recommvendorDto.setLegalRepeatFlag(YesOrNo.NO.getValue());
        //    @ApiModelProperty("股东是否重复")
        recommvendorDto.setHolderRepeatFlag(YesOrNo.NO.getValue());
        //    @ApiModelProperty("主要人员是否重复")
        recommvendorDto.setMainPeopleRepeatFlag(YesOrNo.NO.getValue());

        return recommvendorDto;
    }

    @Override
    public void rollbackPlanPool(List<RecommvendorProjectDto> recommvendorProjectDtoList) {
        //来源推荐供应商
        List<RecommvendorProjectExtendDto> recommvendorProjectExtendDtos = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProjectExtend.name()).in(RecommvendorProjectExtendDto::getProjectId, recommvendorProjectDtoList.stream().map(o->o.getProjectId()).collect(Collectors.toList())), RecommvendorProjectExtendDto.class);
        recommvendorProjectExtendDtos = recommvendorProjectExtendDtos.stream().filter(o -> SouRecommvendorTypeEnum.RECOMM.getCode().equals(o.getRcommendType())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(recommvendorProjectExtendDtos)) {
            return;
        }
        //查询申请单号
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouDemand::getProjectId, recommvendorProjectExtendDtos.stream().map(o->o.getProjectId()).collect(Collectors.toList()));
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        List<ExtSouDemand> demandList = demandService.list(queryWrapper);

        if(CollectionUtils.isEmpty(demandList)) {
            return;
        }

        List<String> applicantNoList = demandList.stream().map(o->o.getApplicantNo()).distinct().collect(Collectors.toList());

        //查询招标计划池
        QlOpenQueryWrapper qlOpenWrappers = QlOpenWrappers.query(PR_SOU_REQUIREMENT_POOL_FOR_BUYER).in("requirementHeadNum", applicantNoList);

        List<RecordDTO> recordDtos = qlOpenClient.query(ContextPath.SUP_CE, qlOpenWrappers, RecordDTO.class);
        if(CollectionUtils.isEmpty(recordDtos)) {
            return;
        }

        List<Long> requirementHeadIdList = new ArrayList<>();
        recordDtos.forEach(recordDTO -> {
            requirementHeadIdList.add(recordDTO.getLong("requirementHeadId"));
        });

        QlOpenUpdateWrapper qlOpenUpdateWrapper = QlOpenWrappers.update(EXT_PR_SOU_REQUIREMENT_HEAD).set("hasCreateVendorRecommend", Enable.N.name()).in("requirementHeadId", requirementHeadIdList);
        qlOpenClient.update(ContextPath.SUP_CE, qlOpenUpdateWrapper);

        //回写寻源单
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_HEAD_BUYER)
                .set(SouReqHead::getIsRecommend, Enable.N.name())
                .in(SouReqHead::getRequirementHeadNoList, applicantNoList));

    }

    @Override
    public void rollbackPlanPoolForBid(List<ExtSouProject> projectList) {
        //查询申请单号
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouDemand::getProjectId, projectList.stream().map(o->o.getProjectId()).collect(Collectors.toList()));
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        List<ExtSouDemand> demandList = demandService.list(queryWrapper);

        if(CollectionUtils.isEmpty(demandList)) {
            return;
        }

        //查询招标计划池
        QlOpenQueryWrapper qlOpenWrappers = QlOpenWrappers.query(PR_SOU_REQUIREMENT_POOL_FOR_BUYER).in("requirementHeadNum", demandList.stream().map(o->o.getApplicantNo()).distinct().collect(Collectors.toList()));

        List<RecordDTO> recordDtos = qlOpenClient.query(ContextPath.SUP_CE, qlOpenWrappers, RecordDTO.class);
        if(CollectionUtils.isEmpty(recordDtos)) {
            return;
        }

        List<Long> requirementHeadIdList = new ArrayList<>();
        recordDtos.forEach(recordDTO -> {
            requirementHeadIdList.add(recordDTO.getLong("requirementHeadId"));
        });

        QlOpenUpdateWrapper qlOpenUpdateWrapper = QlOpenWrappers.update(EXT_PR_SOU_REQUIREMENT_HEAD).set("hasCreateSou", Enable.N.name()).in("requirementHeadId", requirementHeadIdList);
        qlOpenClient.update(ContextPath.SUP_CE, qlOpenUpdateWrapper);
    }

    @Override
    public Long vendorUpdateAsSouReq(Long projectId) {
        //查询寻源单号
        List<RecommvendorProjectExtendDto> recommvendorProjectExtendDtos = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProjectExtend.name()).eq(RecommvendorProjectExtendDto::getProjectId, projectId), RecommvendorProjectExtendDto.class);
        recommvendorProjectExtendDtos = recommvendorProjectExtendDtos.stream().filter(o -> SouRecommvendorTypeEnum.RECOMM.getCode().equals(o.getRcommendType()) && !Objects.isNull(o.getSouRequirementId())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(recommvendorProjectExtendDtos)) {
            return projectId;
        }

        List<RecommvendorDto> recommvendorDtoList = qlService.queryByWrapper(QlWrappers.query(RecommType.Recommvendor.name())
                .eq(RecommvendorDto::getProjectId, projectId)
                .orderByDesc(RecommvendorDto::getSortIndex), RecommvendorDto.class);
        //获取包名成功的供应商
        QlCondition queryCondition = MeiQl.newCondition();
        queryCondition.in("reqHeadId", recommvendorProjectExtendDtos.stream().map(o->o.getSouRequirementId()).distinct().collect(Collectors.toList()))
                .eq("applyStatus", SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode());
        if(CollectionUtils.isNotEmpty(recommvendorDtoList)) {
            queryCondition.notIn("vendorId", recommvendorDtoList.stream().map(RecommvendorDto::getVendorId).distinct().collect(Collectors.toList()));
        }


        List<Record> recors = qlService.query(MqlType.SOU_REQ_APPLY_BUYER, queryCondition, Record.class);
        if(CollectionUtils.isEmpty(recors)) {
            return projectId;
        }


        //生成数据
        AtomicInteger index = new AtomicInteger(0);
        if(CollectionUtils.isNotEmpty(recommvendorDtoList) && !Objects.isNull(recommvendorDtoList.get(0).getSortIndex())) {
            index.set(recommvendorDtoList.get(0).getSortIndex()+1);
        }

        Map<Long, RecommvendorDto> saveMap = new HashMap<>(50);
        recors.stream().forEach(record -> {
            RecommvendorDto recommvendorDto = new RecommvendorDto();
            recommvendorDto.setProjectId(projectId);
            recommvendorDto.setVendorId(record.getLong("vendorId"));
            recommvendorDto.setVendorCode(record.getString("vendorCode"));
            recommvendorDto.setVendorName(record.getString("vendorName"));
            recommvendorDto.setSortIndex(index.getAndAdd(1));
            recommvendorDto.setLinkmanName(record.getString("applyContactName"));
            recommvendorDto.setPhone(record.getString("applyPhone"));
            recommvendorDto.setEmail(record.getString("applyEmail"));

            saveMap.put(recommvendorDto.getVendorId(), recommvendorDto);
        });

        if(MapUtils.isNotEmpty(saveMap)) {
            List<RecommvendorDto> saveList = new ArrayList<>(saveMap.values());
            /**大数据爬虫&GSCP*/
            this.crawler(saveList, false, null);
            /**取供应商主数据 是否重点关注*/
            this.queryCompanyInfo(saveList);
            qlService.save(RecommType.Recommvendor.name(), saveList);
        }

        return projectId;
    }

    @Override
    public Long vendorUpdateAsPreBid(Long projectId) {

        /** 查询申请单号 */
        List<Record> demandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).eq(ExtSouDemand::getProjectId, projectId).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO), Record.class);

        if(CollectionUtils.isEmpty(demandList)) {
            return projectId;
        }

        /** 查询已发布的标前交流通知单 */
        List<Record> preBidNoticeList = qlService.queryByWrapper(QlWrappers.query(MqlType.PRE_BID_NOTICE_BUYER).in(PreBidNotice::getRequirementHeadNo, demandList.stream().map(r -> r.get(ExtSouDemand::getApplicantNo)).distinct().collect(Collectors.toList())).eq(PreBidNotice::getStatus, PreBidNoticeStatusEnum.ISSUED.getCode()), Record.class);
        if(CollectionUtils.isEmpty(preBidNoticeList)) {
            return projectId;
        }

        /** 查询已完成的标前交流反馈单 */
        List<Record> preBidFeedbackList = qlService.queryByWrapper(QlWrappers.query(MqlType.PRE_BID_FEEDBACK_BUYER).in(PreBidFeedback::getBidNoticeId, preBidNoticeList.stream().map(r -> r.get(PreBidNotice::getBidNoticeId)).distinct().collect(Collectors.toList())).eq(PreBidFeedback::getStatus, PreBidFeedbackStatusEnum.FINISHED.getCode()), Record.class);
        if(CollectionUtils.isEmpty(preBidFeedbackList)) {
            return projectId;
        }

        /** 查询已完成的标前交流反馈供应商信息 */
        List<Record> preBidFeedbackVendorList = qlService.queryByWrapper(QlWrappers.query(MqlType.PRE_BID_FEEDBACK_VENDOR_BUYER).in(PreBidFeedbackVendor::getBidFeedbackId, preBidFeedbackList.stream().map(r -> r.get(PreBidFeedback::getBidFeedbackId)).distinct().collect(Collectors.toList())).eq(PreBidFeedbackVendor::getFeedbackStatus, VendorFeedbackStatusEnum.ALREADY_FEEDBACK.getCode()).eq(PreBidFeedbackVendor::getIsSelected, YesOrNo.YES.getValue()).groupBy(PreBidFeedbackVendor::getVendorId), Record.class);
        if(CollectionUtils.isEmpty(preBidFeedbackVendorList)) {
            return projectId;
        }

        List<RecommvendorDto> recommvendorDtoList = qlService.queryByWrapper(QlWrappers.query(RecommType.Recommvendor.name())
                .eq(RecommvendorDto::getProjectId, projectId)
                .orderByDesc(RecommvendorDto::getSortIndex), RecommvendorDto.class);

        AtomicInteger index = new AtomicInteger(0);
        if(CollectionUtils.isNotEmpty(recommvendorDtoList) && !Objects.isNull(recommvendorDtoList.get(0).getSortIndex())) {
            index.set(recommvendorDtoList.get(0).getSortIndex()+1);
        }

        Map<Long, RecommvendorDto> recommvendorDtoMap = recommvendorDtoList.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2)->k2));

        List<RecommvendorDto> insertList = new ArrayList<>();

        preBidFeedbackVendorList.stream().filter(vendor -> !recommvendorDtoMap.containsKey(vendor.get(PreBidFeedbackVendor::getVendorId))).forEach(vendor -> {
            RecommvendorDto recommvendorDto = new RecommvendorDto();
            recommvendorDto.setProjectId(projectId);
            recommvendorDto.setVendorId(vendor.get(PreBidFeedbackVendor::getVendorId));
            recommvendorDto.setVendorCode(vendor.get(PreBidFeedbackVendor::getVendorCode));
            recommvendorDto.setVendorName(vendor.get(PreBidFeedbackVendor::getVendorName));
            recommvendorDto.setSortIndex(index.getAndAdd(1));
            recommvendorDto.setLinkmanName(vendor.get(PreBidFeedbackVendor::getContactName));
            recommvendorDto.setPhone(vendor.get(PreBidFeedbackVendor::getPhone));
            recommvendorDto.setExtVendorAttr(vendor.get(PreBidFeedbackVendor::getVendorAttribute));
            recommvendorDto.setExtPreBid(YesOrNo.YES.getValue());

            insertList.add(recommvendorDto);
        });

        if(CollectionUtils.isNotEmpty(insertList)) {

            /**大数据爬虫&GSCP*/
            this.crawler(insertList, false, null);
            /**取供应商主数据 是否重点关注*/
            this.queryCompanyInfo(insertList);

            qlService.create(RecommType.Recommvendor.name(), insertList);
        }
        return projectId;
    }
}

