package com.midea.cloud.srm.supcooperate.historyprices.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiRequestDto;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiResponseDto;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.SccScHistoryPriceTempDto;
import com.midea.cloud.srm.model.supcooperate.orderhistorys.dto.SccScOrderHistoryDto;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.historyprices.service.HistoryPriceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description: 历史价格实现类
 */
@Service
@Slf4j
public class HistoryPriceServiceImpl implements HistoryPriceService {
    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    private static final String SUCCESS = "0";

    private static final String FAIL = "1";

    private static final String MATERIAL_NOT_EXSITS = "MATERIAL_NOT_EXSITS";

    private static final String SUPPLIER_NOT_EXSITS = "SUPPLIER_NOT_EXSITS";

    @Override
    public HistoryPriceApiResponseDto receiveHistoryPrice(HistoryPriceApiRequestDto requestDto) {

        HistoryPriceApiResponseDto responseDto = new HistoryPriceApiResponseDto();

        saveTemp(requestDto);

        Boolean check = checkTemp(responseDto, requestDto);

        if(check) {
            Boolean result = handlerToOrderHistory(requestDto);
            if(!result) {
                updateTemp(requestDto.getSerialNum(), ProcessStatusEnum.ERROR.getCode(), "处理数据存在异常");
            } else {
                updateTemp(requestDto.getSerialNum(), ProcessStatusEnum.COMPLETED.getCode(), "已成功处理数据");
                //更新部分错误信息
                updatePartErrorInfo(requestDto);
            }

        } else {
            updateTemp(requestDto.getSerialNum(), ProcessStatusEnum.ERROR.getCode(), "校验存在异常信息");
        }


        return responseDto;
    }

    /**
     * 更新部分错误信息
     * @param requestDto
     */
    private void updatePartErrorInfo(HistoryPriceApiRequestDto requestDto) {
        List<SccScHistoryPriceTempDto> partErrorList = requestDto.getData().stream().filter(tempDto -> ProcessStatusEnum.ERROR.getCode().equals(tempDto.getProcessStatus())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(partErrorList)) {
            return;
        }
        Map<String, List<SccScHistoryPriceTempDto>> partErrorGroup = partErrorList.stream().collect(Collectors.groupingBy(k -> k.getProcessMessage()));

        //物料编码不存在
        if(partErrorGroup.containsKey(MATERIAL_NOT_EXSITS)) {
            qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_SC_HISTORY_PRICE_TEMP).set(SccScHistoryPriceTempDto::getProcessStatus, ProcessStatusEnum.ERROR.getCode())
                    .set(SccScHistoryPriceTempDto::getProcessMessage, "物料编码不存在")
                    .set(SccScHistoryPriceTempDto::getProcessDate, new Date())
                    .eq(SccScHistoryPriceTempDto::getProcessSerialNum, requestDto.getSerialNum())
                    .in(SccScHistoryPriceTempDto::getMaterialCode, partErrorGroup.get(MATERIAL_NOT_EXSITS).stream().map(k->k.getMaterialCode()).distinct().collect(Collectors.toList())));
        }

        //供应商编码不存在
        if(partErrorGroup.containsKey(SUPPLIER_NOT_EXSITS)) {
            qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_SC_HISTORY_PRICE_TEMP).set(SccScHistoryPriceTempDto::getProcessStatus, ProcessStatusEnum.ERROR.getCode())
                    .set(SccScHistoryPriceTempDto::getProcessMessage, "供应商信息不存在")
                    .set(SccScHistoryPriceTempDto::getProcessDate, new Date())
                    .eq(SccScHistoryPriceTempDto::getProcessSerialNum, requestDto.getSerialNum())
                    .in(SccScHistoryPriceTempDto::getSupCode, partErrorGroup.get(SUPPLIER_NOT_EXSITS).stream().map(k->k.getSupCode()).distinct().collect(Collectors.toList())));
        }
    }

    /**
     * 处理到业务表
     * @param requestDto
     * @return
     */
    private Boolean handlerToOrderHistory(HistoryPriceApiRequestDto requestDto) {
        try {
            /** 处理物料编码 */
            List<String> materialCodes = requestDto.getData().stream().map(k -> k.getMaterialCode()).distinct().collect(Collectors.toList());
            Map<String, MaterialItem> materialItemMap = baseClient.listMaterialItemsByCodes(materialCodes);

            /** 处理供应商编码 */
            List<String> vendorCodes = requestDto.getData().stream().map(k -> k.getSupCode()).distinct().collect(Collectors.toList());
            List<CompanyInfo> companyInfoList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER).in(CompanyInfo::getCompanyCode, vendorCodes), CompanyInfo.class);
            Map<String, CompanyInfo> companyInfoMap = new HashMap<>(16);
            Map<Long, CompanyInfo> companyInfoIdMap = new HashMap<>(16);
            Map<String, ContactInfoDto> contactMap = new HashMap<>(16);
            if(CollectionUtils.isNotEmpty(companyInfoList)) {
                companyInfoMap = companyInfoList.stream().collect(Collectors.toMap(k -> k.getCompanyCode(), Function.identity(), (k1, k2)->k2));
                companyInfoIdMap = companyInfoList.stream().collect(Collectors.toMap(k -> k.getCompanyId(), Function.identity(), (k1, k2)->k2));
                /** 处理供应商编默认联系人 */
                List<ContactInfoDto> vendorContactList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, companyInfoList.stream().map(CompanyInfo::getCompanyId).collect(Collectors.toList())).eq(ContactInfoDto::getCeeaDefaultContact, YesOrNo.YES.getValue()), ContactInfoDto.class);
                if(CollectionUtils.isNotEmpty(vendorContactList)) {
                    Map<Long, CompanyInfo> finalCompanyInfoMap = companyInfoIdMap;
                    vendorContactList.stream().forEach(contactInfoDto -> {
                        CompanyInfo companyInfo = finalCompanyInfoMap.get(contactInfoDto.getCompanyId());
                        contactMap.put(companyInfo.getCompanyCode(), contactInfoDto);
                    });
                }
            }

            List<SccScOrderHistoryDto> orderHistoryDtoList = new ArrayList<>(16);

            Map<String, CompanyInfo> finalCompanyInfoMap = companyInfoMap;
            requestDto.getData().stream().forEach(tempDto -> {
                /** 补充信息*/
                fillData(tempDto, materialItemMap, finalCompanyInfoMap, contactMap);
                if(!ProcessStatusEnum.ERROR.getCode().equals(tempDto.getProcessStatus())) {
                    orderHistoryDtoList.add(toOrderHistory(tempDto));
                }
            });

            if(CollectionUtils.isNotEmpty(orderHistoryDtoList)) {
                qlService.create(MqlType.SCC_SC_ORDER_HISTORY, orderHistoryDtoList);
            }
            return true;
        } catch (Exception e) {
            log.error("handlerToOrderHistory Exception", e);
            return false;
        }
    }

    /**
     * 补充数据
     * @param tempDto
     * @param materialItemMap
     * @param companyInfoMap
     * @param contactMap
     */
    private void fillData(SccScHistoryPriceTempDto tempDto, Map<String, MaterialItem> materialItemMap, Map<String, CompanyInfo> companyInfoMap, Map<String, ContactInfoDto> contactMap) {
        if(!materialItemMap.containsKey(tempDto.getMaterialCode())) {
            tempDto.setProcessStatus(ProcessStatusEnum.ERROR.getCode());
            tempDto.setProcessMessage(MATERIAL_NOT_EXSITS);
            return;
        }
        MaterialItem materialItem = materialItemMap.get(tempDto.getMaterialCode());
        tempDto.setMaterialName(materialItem.getMaterialName());
        tempDto.setMaterialDescribe(materialItem.getDescription());

        if(!companyInfoMap.containsKey(tempDto.getSupCode())) {
            tempDto.setProcessStatus(ProcessStatusEnum.ERROR.getCode());
            tempDto.setProcessMessage(SUPPLIER_NOT_EXSITS);
            return;
        }

        ContactInfoDto contactInfoDto = contactMap.get(tempDto.getSupCode());
        if(!Objects.isNull(contactInfoDto)) {
            tempDto.setSupTel(contactInfoDto.getCeeaContactMethod());
        }

    }

    /**
     * 实体类转换
     * @param tempDto
     * @return
     */
    private SccScOrderHistoryDto toOrderHistory(SccScHistoryPriceTempDto tempDto) {
        SccScOrderHistoryDto orderHistoryDto = new SccScOrderHistoryDto();
        orderHistoryDto.setMaterialCode(tempDto.getMaterialCode());
        orderHistoryDto.setMaterialName(tempDto.getMaterialName());
        orderHistoryDto.setMaterialDescribe(tempDto.getMaterialDescribe());
        orderHistoryDto.setBrand(tempDto.getBrand());
        orderHistoryDto.setAreaCode(tempDto.getAreaCode());
        orderHistoryDto.setOrgCode(tempDto.getOrgCode());
        orderHistoryDto.setOrgName(tempDto.getOrgName());
        orderHistoryDto.setTaxRate(toBigDecimal(tempDto.getTaxRate()));
        orderHistoryDto.setNoTaxPrice(toBigDecimal(tempDto.getNoTaxPrice()));
        orderHistoryDto.setLeadTime(tempDto.getLeadTime());
        orderHistoryDto.setSupCode(tempDto.getSupCode());
        orderHistoryDto.setSupName(tempDto.getSupName());
        orderHistoryDto.setSupTel(tempDto.getSupTel());
        orderHistoryDto.setOrderDate(tempDto.getOrderDate());

        return orderHistoryDto;
    }

    /**
     * 字符转换数字
     * @param value
     * @return
     */
    private BigDecimal toBigDecimal(String value) {
        if(StringUtils.isNotBlank(value)) {
            try {
                return new BigDecimal(value);
            } catch (Exception e ) {
                log.error("历史价格接口字符串转换数字异常", e);
            }
        }
        return null;
    }

    /**
     * 更新临时表状态
     * @param serialNum
     * @param status
     * @param message
     */
    private void updateTemp(String serialNum, String status, String message) {
        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_SC_HISTORY_PRICE_TEMP).set(SccScHistoryPriceTempDto::getProcessStatus, status)
                .set(SccScHistoryPriceTempDto::getProcessMessage, message)
                .set(SccScHistoryPriceTempDto::getProcessDate, new Date())
                .eq(SccScHistoryPriceTempDto::getProcessSerialNum, serialNum));
    }

    /**
     * 数据校验
     * @param responseDto
     * @param requestDto
     * @return
     */
    private Boolean checkTemp(HistoryPriceApiResponseDto responseDto, HistoryPriceApiRequestDto requestDto) {
        AtomicReference<Boolean> check = new AtomicReference<>(true);
        responseDto.setSerialNum(requestDto.getSerialNum());
        responseDto.setSystemSource(requestDto.getSystemSource());
        responseDto.setCode(SUCCESS);
        if(CollectionUtils.isEmpty(requestDto.getData())) {
            responseDto.setCode(FAIL);
            responseDto.setMessage("历史价格数据列表为空");
            check.set(false);
            return check.get();
        }

        AtomicReference<Integer> index = new AtomicReference<>(0);

        List<String> checkList = new ArrayList<>(16);

        requestDto.getData().stream().forEach(temp -> {
            index.getAndSet(index.get() + 1);

            List<String> errorList = new ArrayList<>(16);
            if(StringUtils.isBlank(temp.getMaterialCode())) {
                errorList.add("物料编码为空");
            }
            /*if(StringUtils.isBlank(temp.getAreaCode())) {
                errorList.add("区域为空");
            }
            if(StringUtils.isBlank(temp.getBrand())) {
                errorList.add("品牌为空");
            }*/
            /*if(StringUtils.isBlank(temp.getOrgCode())) {
                errorList.add("业务实体为空");
            }*/
            /*if(StringUtils.isBlank(temp.getTaxPrice())) {
                errorList.add("税率为空");
            }*/
            if(StringUtils.isBlank(temp.getSupCode())) {
                errorList.add("供应商编码为空");
            }
           /* if(StringUtils.isBlank(temp.getSupTel())) {
                errorList.add("供应商联系方式为空");
            }
            if(Objects.isNull(temp.getOrderDate())) {
                errorList.add("订单日期为空");
            }*/
            if(CollectionUtils.isNotEmpty(errorList)) {
                check.set(false);
                checkList.add(StringUtils.joinWith("", MessageFormat.format("第{0}行存在异常信息：", index.get().toString()), errorList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));
            }
        });

        if(CollectionUtils.isNotEmpty(checkList)) {
            responseDto.setCode(FAIL);
            responseDto.setMessage(checkList.stream().collect(Collectors.joining(SrmConstant.SIG_1)));
        }

        return check.get();
    }

    /**
     * 保存到临时表
     * @param requestDto
     */
    private void saveTemp(HistoryPriceApiRequestDto requestDto) {

        Long batchId = Calendar.getInstance().getTimeInMillis();

        if(CollectionUtils.isNotEmpty(requestDto.getData())) {
            requestDto.getData().stream().forEach(temp -> {
                temp.setProcessSerialNum(requestDto.getSerialNum());
                temp.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
                temp.setProcessGroupId(batchId);
            });
            qlService.create(MqlType.SCC_SC_HISTORY_PRICE_TEMP, requestDto.getData());
        }
    }
}
