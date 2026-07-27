package com.midea.cloud.srm.supcooperate.report.purchase.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;
import com.midea.cloud.srm.supcooperate.report.purchase.mapper.PurchaseOrderReportMapper;
import com.midea.cloud.srm.supcooperate.report.purchase.service.PurchaseOrderReportService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
@Api("采购进度报表-实现类")
public class PurchaseOrderReportServiceImpl implements PurchaseOrderReportService<PurchaseOrderProcessDto> {

    @Resource
    private PurchaseOrderReportMapper purchaseOrderReportMapper;

    @Resource
    private BaseClient baseClient;

    @Override
    public PageInfo<PurchaseOrderProcessDto> listPage(Map<String, Object> query) {
        /** 分页查询-底表数据 */
        PageUtil.startPage(MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageNum).getFieldName(), 1), MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageSize).getFieldName(), 15));

        List<PurchaseOrderProcessDto> dataList = purchaseOrderReportMapper.listPurchaseOrderProcess(query);
        List<DictItemDTO> qyList = baseClient.listAllByDictCode("REGION");
        dataList.forEach(e -> e.setExtAreaName(dealRegion(e.getExtAreaCode(), qyList)));
        for(PurchaseOrderProcessDto purchaseOrderProcessDto:dataList){
            if(purchaseOrderProcessDto.getWarehouseQuantity()!=null){
                  purchaseOrderProcessDto.setReceivedNum(purchaseOrderProcessDto.getWarehouseQuantity());
            }
            else if(purchaseOrderProcessDto.getReceivedNum()!=null){
                purchaseOrderProcessDto.setWarehouseQuantity(purchaseOrderProcessDto.getReceivedNum());
            }

            if(purchaseOrderProcessDto.getExtStorageTime()!=null){
                purchaseOrderProcessDto.setReceivedTime(purchaseOrderProcessDto.getExtStorageTime());
            }
            else if(purchaseOrderProcessDto.getReceivedTime()!=null){
                purchaseOrderProcessDto.setExtStorageTime(purchaseOrderProcessDto.getReceivedTime());
            }
        }
        PageInfo<PurchaseOrderProcessDto> dataPage = new PageInfo<>(dataList);

        /** 填充数据 */
        fillReportData(dataList);

        return dataPage;
    }

    @Override
    public void fillReportData(List<PurchaseOrderProcessDto> dataList) {
        if(CollectionUtils.isNotEmpty(dataList)) {
            dataList.stream().forEach(purchaseOrderProcessDto -> {
                /** 未入库数量=发货数量减入库数量 */

            });
        }
    }

    public String dealRegion(String code, List<DictItemDTO> qyList) {
        for (DictItemDTO e : qyList) {
            if (e.getDictItemCode().equals(code)) {
                return e.getDictItemName();
            }
        }
        return "";
    }
}
