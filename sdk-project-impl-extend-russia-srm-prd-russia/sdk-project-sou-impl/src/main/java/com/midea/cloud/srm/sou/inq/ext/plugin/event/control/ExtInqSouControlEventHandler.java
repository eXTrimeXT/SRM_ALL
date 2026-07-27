package com.midea.cloud.srm.sou.inq.ext.plugin.event.control;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouStartNewRoundDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouVendorRound;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.inq.filter.ExtInqSouSelectFilterUtils;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouChangeOrderEndTimeDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouVendorAddDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouIntelligentSelectDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouVendorRoundDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.inq.spi.control.InqSouControlEventHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendoradd.SouVendorAddPO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouControlEventHandler extends InqSouControlEventHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouSelectEventService souSelectEventService;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDAO;
    @Autowired
    private ExtPjInqSouVendorRoundDAO extPjInqSouVendorRoundDAO;
    @Autowired
    private ExtPJInqSouVendorDAO extPjInqSouVendorDao;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;

    @Override
    @ApiOperation("修改报价截止时间后的额外操作")
    public void doHandlerAfterChangeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType) {
        super.doHandlerAfterChangeOrderEndTime(param, souType);

        if (param.isEndNow()) {
            this.extDoHandleAfterOrderTimeEnd(param.getProjectId());
        }
    }

    @Override
    @ApiOperation("报价截止后的额外处理")
    public void doHandlerAfterOrderEnd(SouProject souProject, SouProcessConfig souProcessConfig) {
        super.doHandlerAfterOrderEnd(souProject, souProcessConfig);

        this.extDoHandleAfterOrderTimeEnd(souProject.getProjectId());
    }

    public void extDoHandleAfterOrderTimeEnd(long projectId) {
        SouProject souProject = souProjectDAO.getById(projectId);
        // 1: 长城询比价额外处理
        // 说明立即截止报价了，需要自动做智能评选
        ApiSouIntelligentSelectDTO selectParam = new ApiSouIntelligentSelectDTO();
        selectParam.setProjectId(projectId);
        selectParam.setNeedAutoScore(false);
        souSelectEventService.intelligentSelect(selectParam, SouTypeEnum.inq.name());
        // 计算排名，获取最低价供应商
        // 查询本轮次供应商的报价明细
        List<SouOrderItem> currentRoundSubmitOrderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        /** 过滤报价为0的数据 */
        currentRoundSubmitOrderItemList = currentRoundSubmitOrderItemList.stream().filter(ExtInqSouSelectFilterUtils.selectOrderPriceWithoutNullOrZero()).collect(Collectors.toList());
        if (!currentRoundSubmitOrderItemList.isEmpty()) {
            Map<Long/* souItemId */, List<SouOrderItem>> currentRoundSubmitOrderItemMap = currentRoundSubmitOrderItemList
                    .stream().collect(Collectors.groupingBy(SouOrderItem::getSouItemId));
            Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO
                    .listByIds(currentRoundSubmitOrderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
            currentRoundSubmitOrderItemMap.forEach((orderItemId, orderItemList) -> {
                orderItemList.sort((a, b) -> {
                    int rs = a.getStandardNotaxPrice().compareTo(b.getStandardNotaxPrice());
                    if (rs != 0) { return rs; }
                    // 价格相同，进一步比较"到货周期"
                    InqSouOrderItem inqA = inqOrderItemMap.get(a.getOrderItemId());
                    InqSouOrderItem inqB = inqOrderItemMap.get(b.getOrderItemId());
                    if (inqA.getExtLeadTime() < inqB.getExtLeadTime()) {
                        return -1;
                    } else if (inqA.getExtLeadTime() > inqB.getExtLeadTime()) {
                        return 1;
                    }
                    // 进一步比较"质保期"
                    if (inqA.getExtWarrantyPeriod() < inqB.getExtWarrantyPeriod()) {
                        return 1;
                    } else if (inqA.getExtWarrantyPeriod() > inqB.getExtWarrantyPeriod()) {
                        return -1;
                    }
                    // 最后，根据报价时间进行比较
                    return a.getLastUpdateDate().before(b.getLastUpdateDate()) ? -1: 1;
                });
                int index = 0;
                for (SouOrderItem oi : orderItemList) {
                    oi.setRanking(++index);
                }
            });
            currentRoundSubmitOrderItemList.forEach(e -> {
                if (e.getRanking() == 1) {
                    e.setWinStatus(SouWinStatusEnum.Y);
                } else if (e.getRanking() > 1) {
                    e.setWinStatus(SouWinStatusEnum.N);
                }
            });

            souOrderItemDAO.updateBatchById(currentRoundSubmitOrderItemList);
            //将评选结果为D的数据修改成N
            souOrderItemDAO.lambdaUpdate().set(SouOrderItem::getWinStatus, SouWinStatusEnum.N)
                    .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.D);
        }
    }

    @Override
    @ApiOperation("发起新一轮前的额外处理")
    public void doHandlerBeforeStartNewRound(ApiSouStartNewRoundDTO param, String souType) {
        super.doHandlerBeforeStartNewRound(param, souType);

        ExtInqSouStartNewRoundDTO inqParam = SouObjectXUtil.convertTargetObj(param, ExtInqSouStartNewRoundDTO.class);
        AssertUtils.notEmpty(inqParam.getInqChooseVendorList(), "缺少inqChooseVendorList参数");
        AssertUtils.notEmpty(inqParam.getInqChooseSouItemIds(), "缺少inqChooseSouItemIds参数");
        inqParam.getInqChooseSouItemIds().removeIf(Objects::isNull);
        AssertUtils.notEmpty(inqParam.getInqChooseSouItemIds(), "缺少inqChooseSouItemIds参数");
        // 1: 过滤出纯新增的供应商
        List<ApiSouVendorDTO> newVendors = new ArrayList<>(10); {
            Set<Long> existVendorIds = souVendorDAO.list(SouVendor::getProjectId, param.getProjectId()).stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
            for (ExtPjInqSouVendorDTO vendor : inqParam.getInqChooseVendorList()) {
                AssertUtils.notNull(vendor.getVendorId(), "缺少vendorId参数");
                if (!existVendorIds.contains(vendor.getVendorId())) {
                    newVendors.add(SouObjectXUtil.convertTargetObj(vendor, ApiSouVendorDTO.class));
                }
            }
        }
        inqParam.setNewVendors(newVendors);
        // 2: 确保选择的物料需求，是现有的物料需求
        Map<Long/* souItemId */, SouItem> existSouItemMap = souItemDAO.list(SouItem::getProjectId, param.getProjectId()).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(existSouItemMap.keySet()).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        inqParam.getInqChooseSouItemIds().forEach(souItemId -> {
            SouItem souItem = existSouItemMap.get(souItemId);
            AssertUtils.notNull(souItem, "物料需求[{0}]不存在", souItemId);
            InqSouItem inqSouItem = inqSouItemMap.get(souItemId);
            AssertUtils.isTrue(Enable.N.equals(inqSouItem.getHasClose()), "物料需求[{0}]已关闭", souItem.getItemDesc());
        });

        SouObjectXUtil.mergeProperties(inqParam, param);
    }

    @Override
    @ApiOperation("发起新一轮后的额外处理")
    public void doHandlerAfterStartNewRound(ApiSouStartNewRoundDTO param, String souType) {
        super.doHandlerAfterStartNewRound(param, souType);
        ExtInqSouStartNewRoundDTO inqParam = SouObjectXUtil.convertTargetObj(param, ExtInqSouStartNewRoundDTO.class);

        SouProject souProject = souProjectDAO.getById(param.getProjectId());

        // 1: 新增供应商/物料轮次信息
        List<ExtPjInqSouItemRound> itemRoundList = new ArrayList<>(inqParam.getInqChooseSouItemIds().size()); {
            for (Long souItemId : inqParam.getInqChooseSouItemIds()) {
                ExtPjInqSouItemRound itemRound = new ExtPjInqSouItemRound();
                itemRoundList.add(itemRound);

                itemRound.setInqSouItemRoundId(IdGenrator.generate());
                itemRound.setProjectId(souProject.getProjectId());
                itemRound.setSouItemId(souItemId);
                itemRound.setRound(souProject.getCurrentRound());
                itemRound.setCanOrder(Enable.Y);
            }
        }
        List<ExtPjInqSouVendorRound> vendorRoundList = new ArrayList<>(inqParam.getInqChooseVendorList().size()); {
            for (ExtPjInqSouVendorDTO vendor : inqParam.getInqChooseVendorList()) {
                ExtPjInqSouVendorRound vendorRound = new ExtPjInqSouVendorRound();
                vendorRoundList.add(vendorRound);

                vendorRound.setInqSouVendorRoundId(IdGenrator.generate());
                vendorRound.setProjectId(souProject.getProjectId());
                vendorRound.setVendorId(vendor.getVendorId());
                vendorRound.setRound(souProject.getCurrentRound());
                vendorRound.setCanOrder(Enable.Y);
            }
        }
        extPjInqSouItemRoundDAO.saveBatch(itemRoundList);
        extPjInqSouVendorRoundDAO.saveBatch(vendorRoundList);
        // 2: 将指定物料的历史报价，均设置为非最新报价
        Set<Long> unLatestOrderItemIds = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .in(SouOrderItem::getSouItemId, inqParam.getInqChooseSouItemIds())
                .lt(SouOrderItem::getRound, souProject.getCurrentRound())
                .select(SouOrderItem::getOrderItemId)
                .list().stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
        if (!unLatestOrderItemIds.isEmpty()) {
            inqSouOrderItemDAO.lambdaUpdate()
                    .set(InqSouOrderItem::getLatestPriceTag, Enable.N)
                    .in(InqSouOrderItem::getOrderItemId, unLatestOrderItemIds)
                    .update();
        }
    }

    @Override
    @ApiOperation("执行追加供应商后的额外处理")
    public void doHandlerAfterVendorAdd(ApiSouVendorAddDTO param, String souType, SouVendorAddPO po) {
        super.doHandlerAfterVendorAdd(param, souType, po);

        // 1: 新增额外的邀请供应商信息
        List<ExtPJInqSouVendor> extVendorList = new ArrayList<>(po.getSaveVendorList().size());
        for (SouVendor newVendor : po.getSaveVendorList()) {
            ExtPJInqSouVendor extVendor = new ExtPJInqSouVendor();
            extVendorList.add(extVendor);

            extVendor.setSouVendorId(newVendor.getSouVendorId());
            extVendor.setProjectId(newVendor.getProjectId());
            extVendor.setSourceFromType(ExtPjInqSouVendorSourceFromTypeEnum.HAND_MAKE);
            extVendor.setNewVendorTag(Enable.Y);
        }
        extPjInqSouVendorDao.saveBatch(extVendorList);
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
