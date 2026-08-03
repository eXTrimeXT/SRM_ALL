package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select;

import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouRoundDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 评选查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSelectQueryHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;

    @ApiOperation("查询评选列表数据后的处理")
    public List<ApiSouSelectQueryVO> doHandlerAfterListEvaluations(ApiSouSelectQueryDTO queryParam, String souType, List<ApiSouSelectQueryVO> voList) {
        this.hideCurrentRoundOrderPricesBeforeBusinessOpenAndDecryptPrice(queryParam, souType, voList);
        return voList;
    }

    @ApiOperation("商务开标/报价解密前隐藏当前轮次的供应商报价")
    protected void hideCurrentRoundOrderPricesBeforeBusinessOpenAndDecryptPrice(ApiSouSelectQueryDTO queryParam, String souType, List<ApiSouSelectQueryVO> voList) {
        SouProject project = souProjectDao.getById(queryParam.getProjectId());
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, project.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();

        boolean canShowCurrentRoundPrice = (Enable.Y.equals(project.getNeedEncryptPrice()) && Enable.Y.equals(currentRound.getPriceDecrypt()))
                || Enable.N.equals(project.getNeedEncryptPrice());
        if (!canShowCurrentRoundPrice) {
            voList.forEach(vo -> {
                // 1: 原币未税单价
                vo.setOrderNotaxPrice(null);
                // 2: 原币含税单价
                vo.setOrderTaxPrice(null);
                // 3: 本币未税单价
                vo.setStandardNotaxPrice(null);
                // 4: 本币含税单价
                vo.setStandardTaxPrice(null);
                // 5: 本币未税最低单价
                vo.setStandardNotaxMinPrice(null);
                // 6: 本币含税最低单价
                vo.setStandardTaxMinPrice(null);
                // 7: 本币未税最高单价
                vo.setStandardNotaxMaxPrice(null);
                // 8: 本币含税最高单价
                vo.setStandardTaxMaxPrice(null);
                // 9: 原币组合未税总价
                vo.setOrderNotaxGroupPrice(null);
                // 10: 原币组合含税总价
                vo.setOrderTaxGroupPrice(null);
                // 11: 本币组合未税总价
                vo.setStandardNotaxGroupPrice(null);
                // 12: 本币组合含税总价
                vo.setStandardTaxGroupPrice(null);
            });
        }
    }

    @ApiOperation("获取报价报表信息后的额外处理")
    public ApiSouOrderReportVO doHandlerAfterGeneratePriceReport(long projectId, String souType, ApiSouOrderReportVO vo) {
        return vo;
    }

    @ApiOperation("查询得到关联的同组合报价(只查询有效的，如果物料需求被修改，失效的不会返回)")
    public List<SouOrderItem> getSameItemGroupOrderItems(List<SouOrderItem> orderItemList) {
        long projectId = orderItemList.get(0).getProjectId();
        SouProject project = souProjectDao.getById(projectId);
        if (SouOrderWayEnum.COMBINED.equals(project.getOrderWay())) {
            // 组合报价
            Set<Long> vendorIds = orderItemList.stream().map(SouOrderItem::getVendorId).collect(Collectors.toSet());
            Set<String/* vendorId_itemGroup */> vendorItemGroups = orderItemList.stream()
                    .map(e -> e.getVendorId() + "_" + e.getItemGroup())
                    .collect(Collectors.toSet());

            return souOrderItemDao.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, projectId)
                    .eq(SouOrderItem::getRound, project.getCurrentRound())
                    .in(SouOrderItem::getVendorId, vendorIds)
                    .eq(SouOrderItem::getIsValid, Enable.Y)
                    .list()
                    .stream()
                    .filter(orderLine -> vendorItemGroups.contains(orderLine.getVendorId() + "_" + orderLine.getItemGroup()))
                    .collect(Collectors.toList());
        } else {
            // 单项报价
            return orderItemList;
        }
    }

    @ApiOperation("获取模板报价的比价数据后的额外处理")
    public ApiSouTempSelectVO doHandlerAfterSouTempSelectReport(Long projectId, ApiSouTempSelectVO vo, String souType) {
        return vo;
    }


    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
