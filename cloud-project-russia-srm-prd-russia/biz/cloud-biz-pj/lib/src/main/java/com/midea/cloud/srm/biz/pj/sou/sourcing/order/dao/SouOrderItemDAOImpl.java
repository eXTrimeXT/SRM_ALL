package com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.SouOrderItemJudgeHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源.核心 - 供应商报价明细
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Service
public class SouOrderItemDAOImpl extends BaseServiceImpl<SouOrderItemMapper, SouOrderItem> implements BaseService<SouOrderItem> {

    /**
     * 创建价格审批单前，校验报价行信息
     *
     * @author: hesl41
     * @Date: 2023/2/14 16:55
     */
    public List<SouOrderItem> checkCreatePricingApproval(SouProject souProject) {
        List<SouOrderItem> orderItemList = this.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN.name())
                .eq(souProject.getSouType().equalsIgnoreCase(SouTypeEnum.inq.name()), SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .eq(souProject.getSouType().equalsIgnoreCase(SouTypeEnum.inq.name()), SouOrderItem::getIsValid, Enable.Y)
                .list();

        SouActiveBeanUtils.getActiveBean(souProject.getSouType(), SouOrderItemJudgeHandler.class)
                .checkCreatePricingApproval(souProject, orderItemList);

        return orderItemList;
    }


}
