package com.midea.cloud.srm.sou.inq.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.select.ApiSouSelectJudgeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@Slf4j
public class ExtApiSouSelectJudgeHandler extends ApiSouSelectJudgeHandler {

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Autowired
    private SouOrderItemDAO souOrderItemDao;

    @Autowired
    private SouProjectDAO souProjectDao;

    @Override
    public List<SouOrderItem> judgeChangeOrderWinStatusAuth(Set<Long> orderItemIds, String souType) {
        log.info("ExtApiSouSelectJudgeHandler judgeChangeOrderWinStatusAuth start...");
        AssertUtils.notEmpty(orderItemIds, "请选择需要入围/淘汰的数据", new Object[0]);
        List<SouOrderItem> orderItemList = this.souOrderItemDao.listByIds(orderItemIds);
        AssertUtils.notEmpty(orderItemList, "找不到供应商报价信息", new Object[0]);
        long projectId = ((SouOrderItem)orderItemList.get(0)).getProjectId();
        boolean isOneProject = orderItemList.stream().allMatch((e) -> {
            return projectId == e.getProjectId();
        });
        AssertUtils.isTrue(isOneProject, "禁止同时操作多个寻源单据", new Object[0]);

        /** 查询最新轮次 */
        QueryWrapper<SouOrderItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.select("MAX(ROUND) ROUND, ITEM_ID, VENDOR_ID")
                        .lambda().eq(SouOrderItem::getProjectId, projectId)
                        .in(SouOrderItem::getItemId, orderItemList.stream().map(SouOrderItem::getItemId).distinct().collect(Collectors.toList()))
                        .in(SouOrderItem::getVendorId, orderItemList.stream().map(SouOrderItem::getVendorId).distinct().collect(Collectors.toList()))
                        .groupBy(SouOrderItem::getItemId, SouOrderItem::getVendorId);
        List<SouOrderItem> maxRoundItemList = souOrderItemDao.list(itemQueryWrapper);
        Map<String, Integer> maxRoundItemMap = maxRoundItemList.stream().collect(Collectors.toMap(i -> StringUtils.joinWith(SrmConstant.UNDER_LINE, i.getItemId(), i.getVendorId()), i -> i.getRound(), (k1, k2)->k2));

        SouProject project = (SouProject)this.souProjectDao.getById(projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型[{0}]不匹配"), new Object[]{souType});
        switch(project.getProjectStatus()) {
            case EVALUATING:
            case PRICE_REJECT:
                Optional<SouOrderItem> notMatch = orderItemList.stream().filter(i -> {
                    String roundKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, i.getItemId(), i.getVendorId());
                    return MapUtils.getInteger(maxRoundItemMap, roundKey, 0).compareTo(ObjectUtils.defaultIfNull(i.getRound(), 0)) != 0;
                }).findAny();

                boolean isAllCurrentRound = !notMatch.isPresent();
                AssertUtils.isTrue(isAllCurrentRound, "禁止修改历史轮次的信息", new Object[0]);
                boolean isAllSubmit = orderItemList.stream().allMatch((e) -> {
                    return SouOrderStatusEnum.SUBMISSION.equals(e.getOrderStatus());
                });
                AssertUtils.isTrue(isAllSubmit, "只能对已提交的供应商报价进行操作", new Object[0]);
                return orderItemList;
            case PRICING:
            case PRICE_END:
            case BUSINESS_EVAL:
            case TECH_EVAL:
            default:
                throw new IllegalArgumentException("非评选阶段，禁止操作");
            case ORDER_NOT_START:
            case ACCEPT_ORDER:
                throw new IllegalArgumentException("报价未截止，禁止操作");
            case ORDER_END:
                throw new IllegalArgumentException("请先进行智能评选");
        }
    }
}
