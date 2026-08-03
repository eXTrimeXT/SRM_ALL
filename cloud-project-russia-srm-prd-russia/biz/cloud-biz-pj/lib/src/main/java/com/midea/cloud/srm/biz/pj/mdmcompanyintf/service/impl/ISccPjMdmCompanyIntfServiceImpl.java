package com.midea.cloud.srm.biz.pj.mdmcompanyintf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.biz.pj.baseregions.mapper.BaseRegionMapper;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.mdmcompanyintf.mapper.SccPjMdmCompanyIntfMapper;
import com.midea.cloud.srm.biz.pj.mdmcompanyintf.service.ISccPjMdmCompanyIntfService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.base.region.entity.Region;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.mdm.dto.*;
import com.midea.cloud.srm.model.pj.mdm.entity.SccPjMdmCompanyIntf;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewListDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapResponseDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapResultDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.entity.SapCompanyIntf;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.info.entity.BankInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Service
@Slf4j
public class ISccPjMdmCompanyIntfServiceImpl extends ServiceImpl<SccPjMdmCompanyIntfMapper, SccPjMdmCompanyIntf> implements ISccPjMdmCompanyIntfService {
    @Resource
    private SccPjMdmCompanyIntfMapper sccPjMdmCompanyIntfMapper;
    @Resource
    private BaseRegionMapper baseRegionMapper;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private QlService qlService;
    /**
     * 社会信用码查询供应商
     */
    private static final String RESULT = "result";//社会信用码查询供应商
    @Override
    public MdmResponseDto createSupplierToMdm(List<MdmCompanyDto> mdmCompanyDtoList) {
        List<SccPjMdmCompanyIntf> sccPjMdmCompanyIntfList = JSON.parseArray(JSON.toJSONString(mdmCompanyDtoList), SccPjMdmCompanyIntf.class);
        Long groupId = IdGenrator.generate();
        sccPjMdmCompanyIntfList.stream().forEach(sccPjMdmCompanyIntf -> {
            sccPjMdmCompanyIntf.setMdmCompanyIntfId(IdGenrator.generate());
            sccPjMdmCompanyIntf.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
            sccPjMdmCompanyIntf.setProcessGroupId(groupId);
            sccPjMdmCompanyIntf.setProcessSerialNum(groupId.toString());
        });
        this.saveBatch(sccPjMdmCompanyIntfList);

        String processStatus = ProcessStatusEnum.COMPLETED.getCode();
        String msg = ProcessStatusEnum.COMPLETED.getName() + ": 申请";
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.CREATE_SUPPLIER, JSON.toJSONString(mdmCompanyDtoList), "application/json");
            MdmResponseDto<List<MdmResultDto>> mdmResponseDto = MdmResponseDto.buildResp(MdmResultDto.class, result);

            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(mdmResponseDto.getCode()) != 0) {
                throw new BaseException("请求MDM接口返回异常：" + mdmResponseDto.getMessage());
            }

            List<MdmResultDto> mdmResultDtoList = mdmResponseDto.getResult();
            for (int i = 0; i < mdmResultDtoList.size(); i++) {
                sccPjMdmCompanyIntfList.get(i).setOrgCode(mdmResultDtoList.get(i).getOrgCode());
            }
            return mdmResponseDto;
        } catch (Exception e) {
            log.error("sendSupplierToMdm Exception", e);
            msg = "申请异常：" + e.getMessage();
            processStatus = ProcessStatusEnum.ERROR.getCode();
            int num = 250;
            if (!StringUtils.isBlank(msg) && msg.length() > num) {
                msg = msg.substring(0, num);
            }
            throw new BaseException(e.getMessage());
        } finally {
            String finalMsg = msg;
            String finalProcessStatus = processStatus;
            sccPjMdmCompanyIntfList.stream().forEach(sccPjMdmCompanyIntf -> {
                sccPjMdmCompanyIntf.setProcessDate(new Date());
                sccPjMdmCompanyIntf.setProcessStatus(finalProcessStatus);
                sccPjMdmCompanyIntf.setProcessMessage(finalMsg);
            });
            sccPjMdmCompanyIntfMapper.updateBatchByIds(sccPjMdmCompanyIntfList);
        }
    }


    @Override
    public MdmResponseDto editSupplierToMdm(MdmCompanyDto mdmCompanyDto) {
        SccPjMdmCompanyIntf sccPjMdmCompanyIntf = JSON.parseObject(JSON.toJSONString(mdmCompanyDto), SccPjMdmCompanyIntf.class);
        sccPjMdmCompanyIntf.setMdmCompanyIntfId(IdGenrator.generate());
        sccPjMdmCompanyIntf.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
        sccPjMdmCompanyIntf.setProcessGroupId(IdGenrator.generate());
        sccPjMdmCompanyIntf.setProcessSerialNum(IdGenrator.generate() + "");

        this.save(sccPjMdmCompanyIntf);

        String processStatus = ProcessStatusEnum.COMPLETED.getCode();
        String msg = ProcessStatusEnum.COMPLETED.getName() + ": 更新";
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.EDIT_SUPPLIER, JSON.toJSONString(mdmCompanyDto), "application/json");
            MdmResponseDto<List<MdmResultDto>> mdmResponseDto = MdmResponseDto.buildResp(MdmResultDto.class, result);
            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(mdmResponseDto.getCode()) != 0) {
                throw new BaseException("请求MDM接口返回异常：" + mdmResponseDto.getMessage());
            }
            return mdmResponseDto;
        } catch (Exception e) {
            log.error("editSupplierToMdm Exception", e);
            processStatus = ProcessStatusEnum.ERROR.getCode();
            msg = "修改异常：" + e.getMessage();
            int num = 250;
            if (!StringUtils.isBlank(msg) && msg.length() > num) {
                msg = msg.substring(0, num);
            }
            throw new BaseException(e.getMessage());
        } finally {
            sccPjMdmCompanyIntf.setProcessDate(new Date());
            sccPjMdmCompanyIntf.setProcessStatus(processStatus);
            sccPjMdmCompanyIntf.setProcessMessage(msg);
            sccPjMdmCompanyIntfMapper.updateById(sccPjMdmCompanyIntf);
        }
    }

    @Override
    public MdmResponseDto sendCompanyInfoToMdm(CompanyInfo companyInfoParam) {
        //0代表MDM不存在该供应商 1代表MDM存在该供应商
        int flag;
        //MDM返回成功标识
        int successValue = 200;
        Long companyId = companyInfoParam.getCompanyId();
        CompanyInfo companyInfo = supplierClient.getCompanyInfoForAnon(companyId);
        /*查询银行信息 */
        BankInfo query = new BankInfo();
        query.setCompanyId(companyId);
        query.setCeeaMainAccount(YesOrNo.YES.getValue());
        BankInfo mainBankInfo = new BankInfo();
        String outText = "OUT";
        if (!outText.equals(companyInfo.getOverseasRelation())) {
            List<BankInfo> bankInfoList = supplierClient.getBankInfosByParamForAnon(query);
            mainBankInfo = CollectionUtils.isNotEmpty(bankInfoList) ? bankInfoList.get(0) : new BankInfo();
        }
        /*字段映射-开始 */
        MdmCompanyDto mdmCompanyDto = new MdmCompanyDto();
        mappingCompanyInfo(mdmCompanyDto, companyInfo,companyInfoParam);
        mappingBankInfo(mdmCompanyDto, mainBankInfo);
        /*字段映射-结束 */
        MdmResponseDto<List<MdmResultDto>> mdmResponseDtoByName;
        MdmResponseDto<List<MdmTaxCodeResultDto>> mdmResponseDtoByTaxCode;
        /*根据名称查询供应商信息-开始 */
        List<String>fullNameList=new ArrayList<>();
        fullNameList.add(companyInfo.getCompanyName());
        MdmFullNameDto mdmFullNameDto=new MdmFullNameDto();
        mdmFullNameDto.setFullName(fullNameList);
        mdmResponseDtoByName = this.findSupNameToMdm(mdmFullNameDto);
        /*根据名称查询供应商信息-结束 */

        /*根据社会信用码查询供应商信息-开始 */
        List<String>fullTaxCodeList=new ArrayList<>();
        fullTaxCodeList.add(companyInfo.getLcCode());
        MdmTaxCodeDto mdmTaxCodeDto=new MdmTaxCodeDto();
        mdmTaxCodeDto.setTaxCode(fullTaxCodeList);
        mdmResponseDtoByTaxCode = this.findSupTaxCodeToMdm(mdmTaxCodeDto);
        //如果按照名称或者社会信用码查询结果存在，则标记1 不存在则标记0
        if(mdmResponseDtoByName.getResult()!=null || mdmResponseDtoByTaxCode.getResult()!=null){
            flag=1;
        }else{
            flag=0;
        }
        /*根据社会信用码查询供应商信息-结束 */

        MdmResponseDto<List<MdmResultDto>> mdmResponseDto = null;
        List<MdmCompanyDto> mdmCompanyDtoList = new ArrayList<>();
        if (Objects.isNull(mdmCompanyDto.getOrgCode()) && flag==0) {
            mdmCompanyDtoList.add(mdmCompanyDto);
            mdmResponseDto = this.createSupplierToMdm(mdmCompanyDtoList);

            if (Integer.compare(mdmResponseDto.getCode(), successValue) == 0) {
            } else {
                throw new BaseException("申请供应商编码失败：" + mdmResponseDto.getMessage());
            }
        } else {
            //如果MDM存在该供应商，并且本地供应商编码为空，则将MDM的供应商编码更新到本地
            if(flag==1 && StringUtils.isBlank(mdmCompanyDto.getOrgCode())){
                String orgCode="";
                if(mdmResponseDtoByName.getResult()!=null){
                    orgCode=mdmResponseDtoByName.getResult().get(0).getOrgCode();
                }else if(mdmResponseDtoByTaxCode.getResult()!=null){
                    orgCode=mdmResponseDtoByTaxCode.getResult().get(0).getSupplierInfoList().get(0).getOrgCode();
                }

                mdmCompanyDto.setOrgCode(orgCode);
                //修改
                mdmResponseDto = this.editSupplierToMdm(mdmCompanyDto);
                //组装返回数据
                List<MdmResultDto>result=new ArrayList<>();
                MdmResultDto mdmResultDto=new MdmResultDto();
                mdmResultDto.setOrgCode(orgCode);
                result.add(mdmResultDto);
                mdmResponseDto.setResult(result);
            }
            else {
                mdmResponseDto = this.editSupplierToMdm(mdmCompanyDto);
            }
        }
        //添加MDM供应商信息到SAP临时表
        List<SapCompanyIntf> sapCompanyIntfList = new ArrayList<>();
        //创建人格式：姓名(工号)
        String applicant = StringUtils.joinWith("", companyInfo.getLastUpdatedFullName(), "(", companyInfo.getLastUpdatedBy(), ")");
        SapCompanyIntf sapCompanyIntf = new SapCompanyIntf();
        if (Objects.isNull(mdmCompanyDto.getOrgCode())) {
            sapCompanyIntf.setOrgCode(mdmResponseDto.getResult().get(0).getOrgCode());
            sapCompanyIntf.setSupState("ADD");
        }else{
            sapCompanyIntf.setOrgCode(mdmCompanyDto.getOrgCode());
            sapCompanyIntf.setSupState("UPDATE");
        }
        sapCompanyIntf.setCreatedBy(applicant);
        sapCompanyIntf.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
        sapCompanyIntfList.add(sapCompanyIntf);
        qlService.create("SapCompanyIntf",sapCompanyIntfList);
        return mdmResponseDto;
    }
    @Override
    public SapResponseDto createSupplierToSap(SapCreateSupViewListDto sapCreateSupViewListDto) {
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.SYN_SUPPLIER_VIEW, JSON.toJSONString(sapCreateSupViewListDto), "application/json");
            SapResponseDto<List<SapResultDto>> sapResponseDto = SapResponseDto.buildResp(SapResultDto.class, result);

            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(sapResponseDto.getCode()) != 0) {
                throw new BaseException("请求SAP接口返回异常：" + sapResponseDto.getMessage());
            }
            return sapResponseDto;
        } catch (Exception e) {
            log.error("synSupplierViewToSap Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
    private MdmResponseDto findSupNameToMdm(MdmFullNameDto mdmFullNameDto) {
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.FIND_SUPPLIERNAME, JSON.toJSONString(mdmFullNameDto), "application/json");
            MdmResponseDto<List<MdmResultDto>> mdmResponseDto = MdmResponseDto.buildResp(MdmResultDto.class, result);
            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(mdmResponseDto.getCode()) != 0) {
                throw new BaseException("请求MDM接口返回异常：" + mdmResponseDto.getMessage());
            }
            if(((JSONObject)JSON.parse(result)).getJSONArray(RESULT).size()==0){
                mdmResponseDto.setResult(null);
            }
            return mdmResponseDto;
        } catch (Exception e) {
            log.error("findSupNameToMdm Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
    private MdmResponseDto findSupTaxCodeToMdm(MdmTaxCodeDto mdmTaxCodeDto) {
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.FIND_SUPPLIERTAXCODE, JSON.toJSONString(mdmTaxCodeDto), "application/json");

            MdmResponseDto<List<MdmTaxCodeResultDto>> mdmResponseDto = MdmResponseDto.buildResp(MdmTaxCodeResultDto.class, result);
            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(mdmResponseDto.getCode()) != 0) {
                throw new BaseException("请求MDM接口返回异常：" + mdmResponseDto.getMessage());
            }
            if(((JSONObject)JSON.parse(result)).getJSONArray(RESULT).size()==0){
                mdmResponseDto.setResult(null);
            }
            return mdmResponseDto;
        } catch (Exception e) {
            log.error("findSupTaxCodeToMdm Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
    /**
     * 映射银行信息表
     *
     * @param mdmCompanyDto 参数
     * @param bankInfo 参数
     */
    private void mappingBankInfo(MdmCompanyDto mdmCompanyDto, BankInfo bankInfo) {
        if (Objects.isNull(bankInfo)) {
            return;
        }
//        银行名称
        mdmCompanyDto.setBankName(bankInfo.getBankAccountName());
//        银行账号
        mdmCompanyDto.setBankAccount(bankInfo.getBankAccount());
    }

    /**
     * 映射供应商主表
     *
     * @param mdmCompanyDto 参数
     * @param companyInfo 参数
     * @param companyInfoParam 参数
     */
    private void mappingCompanyInfo(MdmCompanyDto mdmCompanyDto, CompanyInfo companyInfo, CompanyInfo companyInfoParam) {
        Long companyId = companyInfo.getCompanyId();
        List<RecordDTO> companyRecords = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER)
                .eq("companyId", companyId)
        );
        RecordDTO recordDTO = companyRecords.get(0);

        String saText = "SA";
        if (StringUtils.isNotBlank(companyInfo.getCompanyCode()) && !companyInfo.getCompanyCode().startsWith(saText)) {
            /*更新 供应商编码 */
            mdmCompanyDto.setOrgCode(companyInfo.getCompanyCode());
        }
        /*	申请编号，OA流程使用 */
        /*	序号，OA流程使用 */

//        供应商全称 唯一
        mdmCompanyDto.setFullName(companyInfo.getCompanyName());
        /*	供应商简称 */
        mdmCompanyDto.setShortName(StringUtils.isBlank(companyInfo.getCompanyShortName()) ? companyInfo.getCompanyName() : companyInfo.getCompanyShortName());
//        社会信用代码/税号/身份证号，唯一
        mdmCompanyDto.setTaxCode(companyInfo.getLcCode());
//        供应商来源，枚举字段 COMMON：一般供应商，示例：COMMON
        mdmCompanyDto.setSupSource(OpenClientConstant.MDM_SUP_SOURCE_COMMON);
//        标题类型，枚举字段 COMPANY：公司，MEN：先生，MADAM：女士，示例：COMPANY
        mdmCompanyDto.setTitleType(OpenClientConstant.MDM_TITLE_TYPE_COMPANY);
//        供应商供货类型，枚举字段 PART：汽车零部件，OFFICE_SUPPLY：办公用品，示例：PART
        mdmCompanyDto.setSupplyType(null);
        Map<String, Region> regionMap = queryRegion(companyInfo.getCompanyCity(), companyInfo.getCompanyProvince());
//        国家编码，字典（SAP标准）【对接时需确认】
        mdmCompanyDto.setCountryCode(companyInfo.getCompanyCountry());
        Region region = new Region();
        region.setSapAreaCode("999");
        region.setAreaName("无");
//        地区编码	，字典（SAP标准）【对接时需确认】
        mdmCompanyDto.setArea(regionMap.getOrDefault(companyInfo.getCompanyProvince(), region).getSapAreaCode());
//        城市
        mdmCompanyDto.setCity(regionMap.getOrDefault(companyInfo.getCompanyCity(), region).getAreaName());
//        地址
        mdmCompanyDto.setAddress(companyInfo.getCompanyAddress());
//        区域编码，【服务商提供】
        mdmCompanyDto.setRegion(OpenClientConstant.MDM_REGION_CHN);
        String supType = companyInfo.getBusinessScope();
        int length = 500;
        if (StringUtils.isNotBlank(supType) && supType.length() > length) {
            supType = supType.substring(0, 500);
        }
        if (StringUtils.isBlank(supType)) {
            supType = "无";
        }
//        主营业务，描述控制在500个字符以内
        mdmCompanyDto.setSupType(supType);
        String partner = (String )companyInfoParam.getX("partner");
        String accountGroup = (String )companyInfoParam.getX("accountGroup");
//        账户组，字典（SAP标准）【对接时需确认】
        mdmCompanyDto.setAccountGroup(accountGroup);
//        贸易伙伴，账户组为 Z003时维护
        mdmCompanyDto.setTradePartner(partner);
        /* 如果是个人,取供应商名词 */
        String personalText = "PERSONAL";
        if (personalText.equals(companyInfo.getOverseasRelation())) {
//            法人代表
            mdmCompanyDto.setLegalRepresent(companyInfo.getCompanyName());
        } else {
//            法人代表
            mdmCompanyDto.setLegalRepresent(companyInfo.getLegalPerson());
        }
        if (Enable.Y.name().equals(companyInfo.getIfLongPeriod())) {
//            营业执照有效期开始时间，格式：yyyy-MM-dd
            mdmCompanyDto.setStartDate("1979-01-01");
//            营业执照有效期截止时间，格式：yyyy-MM-dd
            mdmCompanyDto.setEndDate("2099-01-01");
        } else {
            //营业执照有效期开始时间，格式：yyyy-MM-dd
            mdmCompanyDto.setStartDate(DateUtil.format(toDate(companyInfo.getBusinessStartDate()), DateUtil.DATE_FORMAT_10));
//          //营业执照有效期截止时间，格式：yyyy-MM-dd
            mdmCompanyDto.setEndDate(DateUtil.format(toDate(companyInfo.getBusinessEndDate()), DateUtil.DATE_FORMAT_10));
        }
//        状态，枚举字段 DRAFT：草稿，FROZEN：冻结，示例：DRAFT
        mdmCompanyDto.setStatus(OpenClientConstant.MDM_STATUS_NORMAL);
//        数据源项目代码，【服务商提供】
        mdmCompanyDto.setOperSource(OpenClientConstant.MDM_SOURCE_SRM);
//       变更生效时间，格式：yyyy-MM-dd 要求：>=当前日期
        mdmCompanyDto.setAcceptDateScheduled(DateUtil.format(companyInfo.getLastUpdateDate(), DateUtil.DATE_FORMAT_10));
        String userName = companyInfo.getCreatedBy();
        String applicant = StringUtils.joinWith("", companyInfo.getCreatedFullName(), "(", companyInfo.getCreatedBy(), ")");
        if (!Objects.isNull(mdmCompanyDto.getOrgCode())) {
            userName = companyInfo.getLastUpdatedBy();
            applicant = StringUtils.joinWith("", companyInfo.getLastUpdatedFullName(), "(", companyInfo.getLastUpdatedBy(), ")");
        }
        User applicantUser = rbacClient.getUserByUserName(userName);
        if (Objects.isNull(applicantUser)) {
            applicantUser = new User();
        }
//        申请人，格式要求：姓名(工号)
        mdmCompanyDto.setApplicant(applicant);
//        申请人电话
        mdmCompanyDto.setApplicantPhone(applicantUser.getPhone());
//        申请日期，格式：yyyy-MM-dd HH:mm:ss
        mdmCompanyDto.setAppDate(DateUtil.format(new Date(), DateUtil.DATE_FORMAT_19));
//        公司代码
        mdmCompanyDto.setCompanyCode("1000");

    }

    private Map<String, Region> queryRegion(String... regionId) {
        if (Objects.isNull(regionId) || regionId.length == 0) {
            return new HashMap<>(50);
        }
        List<String> regionIdList = Arrays.stream(regionId).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(regionIdList)) {
            return new HashMap<>(50);
        }
        Map<String, Object> param = new HashMap<>(50);
        param.put("regionIdList", regionIdList);

        List<Region> regionList = baseRegionMapper.queryRegion(param);
        if (CollectionUtils.isEmpty(regionList)) {
            return new HashMap<>(50);
        }
        return regionList.stream().collect(Collectors.toMap(k -> k.getRegionId().toString(), Function.identity(), (k1, k2) -> k2));
    }

    private Date toDate(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay(zoneId).toInstant();
        return Date.from(instant);
    }
}
