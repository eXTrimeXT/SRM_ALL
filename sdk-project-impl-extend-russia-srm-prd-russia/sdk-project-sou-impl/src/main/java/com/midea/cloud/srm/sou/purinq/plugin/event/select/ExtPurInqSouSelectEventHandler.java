package com.midea.cloud.srm.sou.purinq.plugin.event.select;

import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouChangeWinStatusDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouIntelligentSelectDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.select.ApiSouSelectEventHandler;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouSelectEventHandler extends ApiSouSelectEventHandler {

    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;

    @Override
    @ApiOperation("智能评选后的额外处理")
    public void doHandlerAfterIntelligentSelect(ApiSouIntelligentSelectDTO param, String souType) {
        // 1: 更新简易询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.EVALUATING)
                .eq(ExtPurInqSouProject::getProjectId, param.getProjectId())
                .update();
    }

    @Override
    @ApiOperation("入围/淘汰后的额外处理")
    public void doHandlerAfterChangeWinStatus(ApiSouChangeWinStatusDTO param, String souType, List<SouOrderItem> orderItemList) {
        super.doHandlerAfterChangeWinStatus(param, souType, orderItemList);

        if (param.isToWin()) {
            SouProject souProject = souProjectDAO.getById(orderItemList.get(0).getProjectId());
            // 长城询比价，入围时将该物料下本轮次其他所有供应商的报价均设为淘汰
            Set<Long> souItemIds = orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet());
            Set<Long> winOrderItemIds = orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());

            souOrderItemDAO.lambdaUpdate()
                    .set(SouOrderItem::getWinStatus, SouWinStatusEnum.N)
                    .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                    .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                    .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .in(SouOrderItem::getSouItemId, souItemIds)
                    .notIn(SouOrderItem::getOrderItemId, winOrderItemIds)
                    .update();
            extPurInqSouOrderItemDAO.lambdaUpdate()
                    .set(ExtPurInqSouOrderItem::getExtWinReason, StringUtils.trimToNull(param.getX(SouObjectXUtil.getFieldByLambda(ExtPurInqSouOrderItem::getExtWinReason))))
                    .in(ExtPurInqSouOrderItem::getOrderItemId, winOrderItemIds)
                    .update();
            // 将未中标的"中标原因置空"
            Set<Long> notWinOrderItemIds = souOrderItemDAO.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.N)
                    .list().stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
            if (!notWinOrderItemIds.isEmpty()) {
                extPurInqSouOrderItemDAO.lambdaUpdate()
                        .set(ExtPurInqSouOrderItem::getExtWinReason, null)
                        .in(ExtPurInqSouOrderItem::getOrderItemId, notWinOrderItemIds)
                        .update();
            }
        }
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
