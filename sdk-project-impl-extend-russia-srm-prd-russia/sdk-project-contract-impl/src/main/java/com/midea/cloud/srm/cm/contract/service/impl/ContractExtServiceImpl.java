package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.midea.cloud.common.enums.contract.ContractPartnerType;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.enums.contract.ContractType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;

import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.cm.contract.service.IContractExtService;
import com.midea.cloud.srm.cm.contract.utils.BasicDataUtil;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.ContractSouExtClient;
import com.midea.cloud.srm.feign.ContractSupExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.cm.contract.entity.ContractMaterial;
import com.midea.cloud.srm.model.cm.contract.entity.ContractPartner;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.contract.dto.ContractMaterialExt;
import com.midea.cloud.srm.model.contract.dto.ContractNoSqDto;
import com.midea.cloud.srm.model.contract.dto.ExcelContractMaterialDTO;
import com.midea.cloud.srm.model.contract.enums.ContractOperationType;
import com.midea.cloud.srm.model.contract.enums.ContractSourceTypeEnums;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgInvoiceInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.base.organization.entity.Site;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLineContractDTO;
import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import com.midea.cloud.srm.model.supplier.info.entity.BankInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.SiteInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Contract 扩展服务实现
 * @author 100014336
 */
@Slf4j
@Service
public class ContractExtServiceImpl implements IContractExtService {

    private static final String SEQ_LOCK_CONTRACT_NO = "SEQ_LOCK_CONTRACT_NO";
    @Autowired
    private QlService qlService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ContractPjExtClient pjContractClient;
    @Autowired
    private ContractSupExtClient contractSupExtClient;

    @Autowired
    private ContractSouExtClient contractSouExtClient;



    @Autowired
    private BaseClient baseClient;




    @Override
    public String getGenerateExtCode(Long contractHeadId) throws InterruptedException {
        //以contractHeadId为粒度锁
        String lockKey = StringUtils.joinWith("_", SEQ_LOCK_CONTRACT_NO, String.valueOf(contractHeadId));
        AtomicInteger count = new AtomicInteger(1);
        while (redisUtil.hasLock(lockKey)) {
            Thread.sleep(1000);
            int times = count.getAndAdd(1);
            if(times > 5) {
                throw new BaseException("等待锁次数超出预设范围");
            }
        }
        try {
            int expireTime = 5000;
            if (redisUtil.tryLock(lockKey,expireTime, TimeUnit.MICROSECONDS)) {
                String contractNoSeqType = "ContractNoSeq";
                QlQueryWrapper qlQueryWrapper = QlWrappers.query(contractNoSeqType).eq("contractHeadId",contractHeadId);
                List<ContractNoSqDto> contractNoSqDtos  = qlService.queryByWrapper(qlQueryWrapper, ContractNoSqDto.class);
                long seq = 1L;
                ContractNoSqDto contractNoSqDto;
                if(CollUtil.isNotEmpty(contractNoSqDtos)){
                    contractNoSqDto = contractNoSqDtos.get(0);
                    seq = contractNoSqDto.getContractExtMaxSeq() + 1L;
                    contractNoSqDto.setContractExtMaxSeq(seq);
                    qlService.update(contractNoSeqType, Collections.singletonList(contractNoSqDto));
                } else {
                    contractNoSqDto = new ContractNoSqDto();
                    QlQueryWrapper contractHeadWrapper = QlWrappers.query(ContractMqlSchemaType.ContractHead.getType()).eq("contractHeadId",contractHeadId);
                    List<Record> records = qlService.queryByWrapper(contractHeadWrapper,Record.class);
                    log.info("inputId:"+contractHeadId);
                    if(CollUtil.isEmpty(records)){
                        throw new BaseException("原合同不存在");
                    }
                    Record contractHead = records.get(0);
                    contractNoSqDto.setContractHeadId(contractHeadId);
                    contractNoSqDto.setContractNo(contractHead.getString("contractNo"));
                    contractNoSqDto.setContractExtMaxSeq(seq);
                    qlService.create(contractNoSeqType, Collections.singletonList(contractNoSqDto));
                }
                return contractNoSqDto.getContractNo()+"_"+seq;
            } else {
                throw new BaseException("获取锁异常");
            }
        } finally {
            redisUtil.unLock(lockKey);
        }
    }

    @Override
    public void fillCompanyInfo(Record partner, OrganizationEditDto organizationEditDto) {
        List<OrgInvoiceInfo> orgInvoiceInfos = organizationEditDto.getOrgInvoiceInfoList();
        Organization organization = organizationEditDto.getOrganization();
        partner.put(ContractPartner::getPartnerType, ContractPartnerType.PARTY_A.getName());
        if(CollUtil.isNotEmpty(orgInvoiceInfos)){
            OrgInvoiceInfo orgInvoiceInfo  = orgInvoiceInfos.get(0);
            partner.put(ContractPartner::getAddress,orgInvoiceInfo.getAddress());
            partner.put(ContractPartner::getBankAccount,orgInvoiceInfo.getOpeningAccount());
            partner.put(ContractPartner::getBankName,orgInvoiceInfo.getOpeningName());
            partner.put(ContractPartner::getPhone,orgInvoiceInfo.getPhone());
            partner.put(ContractPartner::getTaxPayer,orgInvoiceInfo.getTaxpayerNum());
        }
        if(ObjectUtil.isNotNull(organization)){
            partner.put(ContractPartner::getPartnerName,organization.getOrganizationName());
        }


    }

    @Override
    public void fillSupInfo(Record partner, InfoDTO infoDTO) {
        //填充乙方数据
        BankInfo bankInfo = getMainBankInfo(infoDTO.getBankInfos());
        CompanyInfo companyInfo = infoDTO.getCompanyInfo();
        //公司信息
        if(ObjectUtil.isNotNull(companyInfo)){
            partner.put(ContractPartner::getAddress,companyInfo.getCompanyAddress());
            partner.put(ContractPartner::getTaxPayer,companyInfo.getLcCode());
            partner.put(ContractPartner::getPartnerName,companyInfo.getCompanyName());
            partner.put(ContractPartner::getOuId,companyInfo.getCompanyId());
        }
        //银行信息
        if(ObjectUtil.isNotNull(bankInfo)){
            partner.put(ContractPartner::getBankAccount,bankInfo.getBankAccount());
            partner.put(ContractPartner::getBankName,bankInfo.getBankName());
            partner.put(ContractPartner::getPartnerType, ContractPartnerType.PARTY_B.getName());
        }
    }



    @Override
    public void fillPartners(Record contract, OrganizationEditDto organizationEditDto, InfoDTO info) {
        List<Record> partners = new ArrayList<>();
        /*if(organizationEditDto!=null){
            Record aPartner = new Record();
            fillCompanyInfo(aPartner,organizationEditDto);
            partners.add(aPartner);
        }*/
        if(info!=null){
            Record bPartner = new Record();
            fillSupInfo(bPartner,info);
            partners.add(bPartner);
        }
        contract.put("contractPartners",partners);
    }

    @Override
    public void fillIncorporatedPartner(List<Record> contractPartners,Record contract){
        Record partner = new Record();
        partner.put(ContractPartner::getPartnerType, ContractPartnerType.PARTY_A.getName());
        partner.put(ContractPartner::getPartnerName,contract.get(ContractHead::getBuName));
        partner.put(ContractPartner::getOuId,contract.get(ContractHead::getBuId));
        partner.put(ContractPartner::getTaxPayer,contract.get("creditCode"));
        contractPartners.add(partner);
    }

    @Override
    public void fillBaseInfo(Record contract, OrganizationEditDto organizationEditDto, InfoDTO info, String sourceType) {
        Organization organization = organizationEditDto.getOrganization();
        CompanyInfo companyInfo = info.getCompanyInfo();

        /*contract.put(ContractHead::getBuId,organization.getOrganizationId());
        contract.put(ContractHead::getBuCode,organization.getOrganizationCode());
        contract.put(ContractHead::getBuName,organization.getOrganizationName());*/

        contract.put(ContractHead::getVendorId,companyInfo.getCompanyId());
        contract.put(ContractHead::getVendorName,companyInfo.getCompanyName());
        contract.put(ContractHead::getVendorCode,companyInfo.getCompanyCode());
        contract.put(ContractHead::getSourceType, sourceType);

        contract.put(ContractHead::getOperationType, ContractOperationType.SAVE_TEMP.getCode());
        contract.put(ContractHead::getContractType, ContractType.MIAN_CONTRACT_ADD.name());
        contract.put(ContractHead::getContractStatus, ContractStatus.DRAFT.name());
    }

    @Override
    public void fillTotalInfoForPurFix(Record contract, List<ExtPurFixPriceLineContractDTO> priceLineList){
        if(CollUtil.isNotEmpty(priceLineList)){
            ExtPurFixPriceLineContractDTO extFixPriceLine= priceLineList.get(0);
            Long ouId = extFixPriceLine.getCreateUserOrgOuId();
            List<String> unitCodes = priceLineList.stream().map(ExtPurFixPriceLineContractDTO::getUnit).collect(Collectors.toList());
            OrganizationEditDto organizationEditDto = pjContractClient.findList(ouId);
            InfoDTO info = getVendorInfo(extFixPriceLine.getVendorId());
            List<PurchaseUnit> purchaseUnits = baseClient.listPurchaseUnitByCodeList(unitCodes);
            checkData(organizationEditDto,info);
            fillBaseInfo(contract,organizationEditDto,info,ContractSourceTypeEnums.CENT_PURCHASE.getCode());
            fillPartners(contract,organizationEditDto,info);
            fillMaterialsByPurFixPriceLine(contract,priceLineList,getSite(organizationEditDto),purchaseUnits);

        }
    }

    @Override
    public void fillTotalInfo(Record contract, List<ExtFixPriceLine> priceLineList) {
        if(CollUtil.isNotEmpty(priceLineList)){
            ExtFixPriceLine extFixPriceLine= priceLineList.get(0);
            Long ouId = extFixPriceLine.getOrgOuId();
            OrganizationEditDto organizationEditDto = pjContractClient.findList(ouId);
            InfoDTO info = getVendorInfo(extFixPriceLine.getVendorId());
            List<PurchaseUnit> purchaseUnits = baseClient.listPurchaseUnitByCodeList(priceLineList.stream().map(ExtFixPriceLine::getUnit).collect(Collectors.toList()));
            checkData(organizationEditDto,info);
            fillBaseInfo(contract,organizationEditDto,info,ContractSourceTypeEnums.TEMP_PROCURE.getCode());
            fillMaterials(contract,priceLineList,getSite(organizationEditDto),purchaseUnits);
            fillPartners(contract,organizationEditDto,info);
        }
    }

    private void checkData(OrganizationEditDto organizationEditDto,InfoDTO info){
        if(ObjectUtil.isEmpty(organizationEditDto)||ObjectUtil.isEmpty(organizationEditDto.getOrganization())){
            throw new BaseException("公司不存在");
        }
        if(ObjectUtil.isEmpty(info) || ObjectUtil.isNull(info.getCompanyInfo())){
            throw new BaseException("供应商不存在");
        }
    }



    @Override
    public InfoDTO getVendorInfo(Long vendorId) {
        InfoDTO infoDTO = new InfoDTO();
        CompanyInfo companyInfo = contractSupExtClient.getCompanyInfoById(vendorId);
        BankInfo bankInfo = new BankInfo();
        bankInfo.setCompanyId(vendorId);
        bankInfo.setCeeaEnabled(Enable.Y.name());
        bankInfo.setCeeaMainAccount(Enable.Y.name());
        SiteInfo siteInfo = new SiteInfo();
        siteInfo.setCompanyId(vendorId);
        infoDTO.setCompanyInfo(companyInfo);
        BankInfo bankInfoResult = contractSupExtClient.getBankInfoByParmForAnon(bankInfo);
        if(ObjectUtil.isNotEmpty(bankInfoResult)){
            infoDTO.setBankInfos(Collections.singletonList(bankInfoResult));
        }
        return infoDTO;
    }

    @Override
    public List<Serializable> createContractByFixPriceLine(List<String> fixPriceLineIds) {
       List<ExtFixPriceLine> priceLineList = contractSouExtClient.fixPriceLineList(fixPriceLineIds);
       List<Serializable> ids = new ArrayList<>();
       if(CollUtil.isNotEmpty(priceLineList)){
           Record contract = new Record();
           fillTotalInfo(contract,priceLineList);
           contract.put(ContractHead::getModelEnable, "Y");
           contract.put(ContractHead::getNeedVendorConfirm, "Y");
           ids = qlService.create(ContractMqlSchemaType.ContractHead.getType(), Collections.singletonList(contract));
       }
       return ids;
    }

    @Override
    public List<Serializable> createContractByJcFixPriceLine(List<String> fixPriceLineIds){
       List<ExtPurFixPriceLineContractDTO> priceLineList = contractSouExtClient.purFixPriceLineList(fixPriceLineIds);
        List<Serializable> ids = new ArrayList<>();
        if(CollUtil.isNotEmpty(priceLineList)){
            Record contract = new Record();
            fillTotalInfoForPurFix(contract,priceLineList);
            contract.put(ContractHead::getModelEnable, "Y");
            contract.put(ContractHead::getNeedVendorConfirm, "Y");
            ids = qlService.create(ContractMqlSchemaType.ContractHead.getType(), Collections.singletonList(contract));
        }
        return ids;
    }

    private Site getSite(OrganizationEditDto organizationEditDto){
        return null;
    }

    private BankInfo getMainBankInfo(List<BankInfo> bankInfos){
        BankInfo bankInfo = null;
        if(CollUtil.isNotEmpty(bankInfos)){
           List<BankInfo> enableBanks = bankInfos.stream().filter(e-> (!Enable.N.name().equalsIgnoreCase(e.getCeeaEnabled()))&&Enable.Y.name().equalsIgnoreCase(e.getCeeaMainAccount()))
                    .collect(Collectors.toList());
           if(CollUtil.isNotEmpty(enableBanks)){
               bankInfo = enableBanks.get(0);
           }
        }
        return bankInfo;
    }

    /**
     * 填充物料数据
     * @param contract
     * @param priceLineList
     * @param site
     * @param purchaseUnits
     */
    private void fillMaterials(Record contract, List<ExtFixPriceLine> priceLineList, Site site, List<PurchaseUnit> purchaseUnits){
        if(CollUtil.isNotEmpty(priceLineList)){
            String currencyCode = priceLineList.get(0).getCurrencyCode();
            if(ObjectUtil.isNotEmpty(currencyCode)){
                contract.set(ContractHeadExt::getCurrencyCode,currencyCode);
                contract.set(ContractHeadExt::getContractName, BasicDataUtil.newInstance(baseClient).getCurrencyName(currencyCode));
            }
            List<Record> contractMaterials = makeMaterials(priceLineList,site,purchaseUnits);
            contract.put("contractMaterials",contractMaterials);
            //计算总数
            countTotal(contract,contractMaterials);
        }


    }



    /**
     * 填充物料数据
     * @param contract
     * @param priceLineList
     * @param site
     * @param purchaseUnits
     */
    private void fillMaterialsByPurFixPriceLine(Record contract, List<ExtPurFixPriceLineContractDTO> priceLineList, Site site, List<PurchaseUnit> purchaseUnits){
        if(CollUtil.isNotEmpty(priceLineList)){
            List<Record> contractMaterials = makeMaterialsByPurFixPriceLine(priceLineList,site,purchaseUnits);
            contract.put("contractMaterials",contractMaterials);
            countTotal(contract,contractMaterials);
        }

    }

    private void countTotal(Record contract, List<Record> contractMaterials) {
        if(CollUtil.isNotEmpty(contractMaterials)){
            BigDecimal includeTaxAmount = contractMaterials.stream()
                    .filter(e->ObjectUtil.isNotNull(e.get(ContractMaterialExt::getTaxedPrice)))
                    .map(e->e.get(ContractMaterialExt::getTaxedPrice).multiply(e.get(ContractMaterialExt::getContractQuantity)))
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            contract.put(ContractHeadExt::getIncludeTaxAmount,includeTaxAmount);
        }
    }

    private List<Record> makeMaterialsByPurFixPriceLine(List<ExtPurFixPriceLineContractDTO> priceLineList, Site site, List<PurchaseUnit> purchaseUnits) {
        List<Record> records = new ArrayList<>();
        Map<String,String> purchaseUnitMap =  new HashMap<>(16);
        if(CollUtil.isNotEmpty(purchaseUnits)){
            for (PurchaseUnit unit:purchaseUnits){
                purchaseUnitMap.put(unit.getUnitCode(),unit.getUnitName());
            }
        }
        for (ExtPurFixPriceLineContractDTO extFixPriceLine:priceLineList){
            Record record = new Record();
            record.put(ContractMaterial::getMaterialCode,extFixPriceLine.getItemCode());
            record.put(ContractMaterial::getMaterialName,extFixPriceLine.getItemDesc());
            record.put(ContractMaterial::getMaterialId,extFixPriceLine.getItemId());
            record.put(ContractMaterial::getTaxRate,extFixPriceLine.getTaxRate());
            record.put(ContractMaterial::getTaxKey,extFixPriceLine.getTaxKey());
            record.put(ContractMaterial::getUntaxedPrice,extFixPriceLine.getNotaxPrice());
            record.put(ContractMaterial::getContractQuantity,extFixPriceLine.getRequireQuantity());
            record.put(ContractMaterial::getUnitCode,extFixPriceLine.getUnit());
            record.put(ContractMaterial::getTaxedPrice,extFixPriceLine.getTaxPrice());
            record.put(ContractMaterial::getContractQuantity,extFixPriceLine.getRequireQuantity());
            if(ObjectUtil.isNotNull(extFixPriceLine.getTaxPrice())
                    &&ObjectUtil.isNotNull(extFixPriceLine.getRequireQuantity())
            ){
                BigDecimal amount = extFixPriceLine.getRequireQuantity().multiply(extFixPriceLine.getTaxPrice());
                record.put(ContractMaterial::getAmount,amount);
            }
            if(ObjectUtil.isNotNull(extFixPriceLine.getNotaxPrice())
                    &&ObjectUtil.isNotNull(extFixPriceLine.getRequireQuantity())){
                BigDecimal unAmount = extFixPriceLine.getRequireQuantity().multiply(extFixPriceLine.getNotaxPrice());
                record.put(ContractMaterial::getUnAmount,unAmount);
            }
            setPurFixPriceLineUnit(purchaseUnitMap,record,extFixPriceLine.getUnit());

            record.put(ContractMaterial::getVendorId,extFixPriceLine.getVendorId());
            record.put(ContractMaterial::getVendorName,extFixPriceLine.getVendorName());
            record.put(ContractMaterial::getVendorCode,extFixPriceLine.getVendorCode());

            record.put(ContractMaterial::getBuCode,extFixPriceLine.getCreateUserOrgCode());
            record.put(ContractMaterial::getBuName,extFixPriceLine.getCreateUserOrgName());
            record.put(ContractMaterial::getBuId,extFixPriceLine.getCreateUserOrgOuId());
            record.put(ContractMaterialExt::getExtFixPriceLineId,extFixPriceLine.getPurFixPriceLineId());
            records.add(record);
        }
        return records;
    }



    /**
     * 创建合同物料
     * @param priceLineList
     * @param purchaseUnits
     * @return
     */
    private List<Record> makeMaterials(List<ExtFixPriceLine> priceLineList, Site site, List<PurchaseUnit> purchaseUnits){
        List<Record> records = new ArrayList<>();
        Map<String,String> purchaseUnitMap = new HashMap<>(16);
        if(CollUtil.isNotEmpty(purchaseUnits)){
            for (PurchaseUnit unit:purchaseUnits){
                purchaseUnitMap.put(unit.getUnitCode(),unit.getUnitName());
            }
        }

        for (ExtFixPriceLine extFixPriceLine:priceLineList){
            Record record = new Record();
            record.put(ContractMaterial::getMaterialCode,extFixPriceLine.getItemCode());
            record.put(ContractMaterial::getMaterialName,extFixPriceLine.getItemDesc());
            record.put(ContractMaterial::getMaterialId,extFixPriceLine.getItemId());
            record.put(ContractMaterial::getCurrency,extFixPriceLine.getCurrencyCode());

            record.put(ContractMaterial::getUnAmount,extFixPriceLine.getNotaxTotalPrice());
            record.put(ContractMaterial::getTaxRate,extFixPriceLine.getTaxRate());
            record.put(ContractMaterial::getTaxKey,extFixPriceLine.getTaxKey());
            record.put(ContractMaterial::getUntaxedPrice,extFixPriceLine.getNotaxPrice());
            record.put(ContractMaterial::getContractQuantity,extFixPriceLine.getQuantity());
            record.put(ContractMaterial::getUnitCode,extFixPriceLine.getUnit());
            setPurFixPriceLineUnit(purchaseUnitMap,record,extFixPriceLine.getUnit());
            BigDecimal notaxPrice = extFixPriceLine.getNotaxPrice();
            BigDecimal taxRate = extFixPriceLine.getTaxRate();
            BigDecimal notaxTotalPrice = extFixPriceLine.getNotaxTotalPrice();
            setTaxInfo(record,taxRate,notaxPrice,notaxTotalPrice);
            record.put(ContractMaterial::getBuId,extFixPriceLine.getOrgOuId());
            record.put(ContractMaterial::getBuCode,extFixPriceLine.getOrgOuCode());
            record.put(ContractMaterial::getBuName,extFixPriceLine.getOrgOuName());

            record.put(ContractMaterial::getVendorId,extFixPriceLine.getVendorId());
            record.put(ContractMaterial::getVendorName,extFixPriceLine.getVendorName());
            record.put(ContractMaterialExt::getExtFixPriceLineId,extFixPriceLine.getFixPriceLineId());
            records.add(record);
        }
        return records;
    }

    private void setTaxInfo(Record record,BigDecimal taxRate,BigDecimal notaxPrice,BigDecimal notaxTotalPrice){
        if(ObjectUtil.isNotNull(taxRate)){
            BigDecimal multiRateEl= taxRate.add(new BigDecimal(100)).divide(new BigDecimal(100),2, RoundingMode.DOWN);
            if(ObjectUtil.isNotNull(notaxPrice)){
                BigDecimal taxedPrice = notaxPrice.multiply(multiRateEl);
                record.put(ContractMaterial::getTaxedPrice,taxedPrice);
            }
            if(ObjectUtil.isNotNull(notaxTotalPrice)){
                BigDecimal amount = notaxTotalPrice.multiply(multiRateEl);
                record.put(ContractMaterial::getAmount,amount);
                record.put(ContractMaterial::getTaxQuota,amount.subtract(notaxTotalPrice));
            }
        }
    }

    private void setPurFixPriceLineUnit(Map<String,String> purchaseUnitMap,Record record,String unitCode){
        if(ObjectUtil.isNotEmpty(unitCode)){
            record.put(ContractMaterial::getUnitName,purchaseUnitMap.get(unitCode));
        }
    }



    @Override
    public  List<ExcelContractMaterialDTO> readExcelWithContractMaterial(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            AnalysisEventListenerImpl<ExcelContractMaterialDTO> listener = new AnalysisEventListenerImpl<>();
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).headRowNumber(2).head(ExcelContractMaterialDTO.class).build();
            excelReader.read(readSheet);
            List<ExcelContractMaterialDTO> datas = listener.getDatas();
            List<String> categoryCodes = datas.stream().filter(e -> e.getCategoryCode() != null).map(ExcelContractMaterialDTO::getCategoryCode).collect(Collectors.toList());
            Map<String, PurchaseCategory> categorys = new HashMap<>(16);
            if (CollUtil.isNotEmpty(categoryCodes)) {
                categorys = baseClient.getCategoryByCodes(categoryCodes);
            }

            List<DictItem> items = baseClient.listDictItemByDictCode("EXT_SOU_PURINQ_ORDER_INVOICE_TYPE");
//            List<String> taxKeys = datas.stream().map(ExcelContractMaterialDTO::getTaxRateDesc).filter(e->e.contains("/")).map(e->e.split("/")[0]).collect(Collectors.toList());
            Map<String, PurchaseTax> taxPurchaseMap = getNumberForTaxRateDesc();
            for (ExcelContractMaterialDTO excelContractMaterialDTO : datas) {
                if (ObjectUtil.isNotEmpty(excelContractMaterialDTO.getCategoryCode())) {
                    PurchaseCategory category = categorys.get(excelContractMaterialDTO.getCategoryCode());
                    if (ObjectUtil.isNotNull(category)) {
                        excelContractMaterialDTO.setCategoryId(category.getCategoryId());
                    }
                }
                if (ObjectUtil.isNotEmpty(excelContractMaterialDTO.getMaterialCode())) {
                    MaterialItem item = baseClient.findMaterialItemByMaterialCode(excelContractMaterialDTO.getMaterialCode());
                    if (ObjectUtil.isNotNull(item)) {
                        excelContractMaterialDTO.setMaterialId(item.getMaterialId());
                    }
                }
                if (ObjectUtil.isNotNull(excelContractMaterialDTO.getTaxRateDesc())) {
//                    PurchaseTax purchaseTax = taxPurchaseMap.get(excelContractMaterialDTO.getTaxRateDesc());
//                    if (ObjectUtil.isNotNull(purchaseTax)) {
//                        excelContractMaterialDTO.setTaxRate(purchaseTax.getTaxCode());
                        excelContractMaterialDTO.setTaxKey(getTaxKey(excelContractMaterialDTO.getTaxRateDesc()));
//                    }
                }
                excelContractMaterialDTO.setExtInvoiceType(getInvoiceType(excelContractMaterialDTO.getExtInvoiceTypeName(), items));


            }
            return datas;
        } catch (IOException var7) {
            throw new BaseException("excel解析出错");
        }
    }


    private Map<String, PurchaseTax> getNumberForTaxRateDesc() {
        List<PurchaseTax> purchaseTaxes = baseClient.listTaxAll();
        Map<String, PurchaseTax> taxMap = new HashMap<>(16);
        if (CollUtil.isNotEmpty(purchaseTaxes)) {
            for (PurchaseTax purchaseTax : purchaseTaxes) {
                taxMap.put(String.join("/", purchaseTax.getTaxKey(), purchaseTax.getTaxName()), purchaseTax);
            }
        }
        return taxMap;
    }

    private String getTaxKey(String taxDesc){
        return taxDesc.split("/")[0];
    }

    private String getInvoiceType(String extInvoiceTypeName, List<DictItem> items) {
        if (ObjectUtil.isNotEmpty(extInvoiceTypeName)) {
            if (CollUtil.isNotEmpty(items)) {
                for (DictItem dictItem : items) {
                    if (dictItem.getDictItemName().equals(extInvoiceTypeName)) {
                        return dictItem.getDictItemCode();
                    }
                }
            }
        }
        return null;

    }





}
