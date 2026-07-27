package com.midea.cloud.srm.sou.inq.ext.plugin.event.select;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouChangeWinStatusDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouIntelligentSelectDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.inq.spi.select.InqSouSelectEventHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouSelectEventHandler extends InqSouSelectEventHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;

    @Override
    @ApiOperation("智能评选前的额外处理")
    public void doHandlerBeforeIntelligentSelect(ApiSouIntelligentSelectDTO param, String souType) {
        // 长城询比价不校验目标价
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
                    .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .in(SouOrderItem::getSouItemId, souItemIds)
                    .notIn(SouOrderItem::getOrderItemId, winOrderItemIds)
                    .update();
            inqSouOrderItemDAO.lambdaUpdate()
                    .set(InqSouOrderItem::getExtWinReason, StringUtils.trimToNull(param.getX(SouObjectXUtil.getFieldByLambda(ExtInqSouOrderItem::getExtWinReason))))
                    .in(InqSouOrderItem::getOrderItemId, winOrderItemIds)
                    .update();
            // 将未中标的"中标原因置空"
            Set<Long> notWinOrderItemIds = souOrderItemDAO.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.N)
                    .list().stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
            if (!notWinOrderItemIds.isEmpty()) {
                inqSouOrderItemDAO.lambdaUpdate()
                        .set(InqSouOrderItem::getExtWinReason, null)
                        .in(InqSouOrderItem::getOrderItemId, notWinOrderItemIds)
                        .update();
            }
        }
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
