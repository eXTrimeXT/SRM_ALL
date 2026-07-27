package com.midea.cloud.srm.sou.inq.ext.plugin.query.select;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.select.ApiInqSouSelectQueryVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouCurrencyDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemPaymentDAO;
import com.midea.cloud.srm.sou.inq.spi.select.InqSouSelectQueryHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouSelectQueryHandler extends InqSouSelectQueryHandler {

    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private InqSouOrderItemPaymentDAO inqSouOrderItemPaymentDAO;
    @Autowired
    private InqSouCurrencyDAO inqSouCurrencyDAO;
    @Autowired
    private BaseClient baseClient;

    @SuppressWarnings("rawtypes")
    @ApiOperation("查询评选列表数据后的处理")
    @Override
    public List<ApiSouSelectQueryVO> doHandlerAfterListEvaluations(ApiSouSelectQueryDTO queryParam, String souType, List<ApiSouSelectQueryVO> voList) {
        if (voList.isEmpty()) { return Collections.emptyList(); }

        List<ApiInqSouSelectQueryVO> selectList = SouObjectXUtil.convertTargetObj(voList, new TypeReference<List<ApiInqSouSelectQueryVO>>() {});
        // 1: 查询转化额外物料需求
        Map<Long/* souItemId */, InqSouItem> inqItemMap = inqSouItemDAO.list(InqSouItem::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        selectList.forEach(select -> SouObjectXUtil.mergeProperties(inqItemMap.get(select.getSouItemId()), select));
        // 2: 查询转化额外报价明细
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO
                .listByIds(selectList.stream().map(ApiInqSouSelectQueryVO::getOrderItemId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        selectList.forEach(select -> SouObjectXUtil.mergeProperties(inqOrderItemMap.get(select.getOrderItemId()), select));
        // 4: 转化币种信息
        Map<String/* currency */, InqSouCurrency> currencyMap = inqSouCurrencyDAO.list(InqSouCurrency::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(InqSouCurrency::getCurrencyCode, Function.identity()));
        selectList.forEach(select -> SouObjectXUtil.mergeProperties(currencyMap.get(select.getOrderCurrency()), select));
        // 5: 查询转化额外物料需求附件
        Map<Long/* souItemId */, List<SceneFile>> sceneFileMap; {
            if (selectList.isEmpty()) {
                sceneFileMap = Collections.emptyMap();
            } else {
                List<SceneFile> sceneFileList = baseClient.listSceneFileBatch(new ArrayList<>(selectList.stream()
                        .map(ApiInqSouSelectQueryVO::getSouItemId).collect(Collectors.toSet())));
                if (sceneFileList.isEmpty()) {
                    sceneFileMap = Collections.emptyMap();
                } else {
                    sceneFileMap = sceneFileList.stream().collect(Collectors.groupingBy(SceneFile::getBusinessId));
                }
            }
        }
        selectList.forEach(select -> select.setItemFiles(sceneFileMap.get(select.getSouItemId())));
        // 6: 数据转化
        List<ApiSouSelectQueryVO> apiVOList; {
            if (voList instanceof Page) {
                apiVOList = new Page<>();
                ((Page)apiVOList).setTotal(((Page)voList).getTotal());
                ((Page)apiVOList).setPageSize(((Page)voList).getPageSize());
                ((Page)apiVOList).setPageNum(((Page)voList).getPageNum());
            } else {
                apiVOList = new ArrayList<>(voList.size());
            }
            selectList.forEach(select -> apiVOList.add(SouObjectXUtil.convertTargetObj(select, ApiSouSelectQueryVO.class)));
        }
        super.hideCurrentRoundOrderPricesBeforeBusinessOpenAndDecryptPrice(queryParam, souType, apiVOList);
        return apiVOList;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
