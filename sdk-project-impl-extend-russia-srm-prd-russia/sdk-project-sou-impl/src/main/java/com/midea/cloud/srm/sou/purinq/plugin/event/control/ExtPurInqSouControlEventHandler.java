package com.midea.cloud.srm.sou.purinq.plugin.event.control;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouStartNewRoundDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.*;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouIntelligentSelectDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.sourcing.control.service.SouControlEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.sou.sourcing.spi.control.ApiSouControlEventHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendoradd.SouVendorAddPO;
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
public class ExtPurInqSouControlEventHandler extends ApiSouControlEventHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouSelectEventService souSelectEventService;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;
    @Autowired
    private ExtPurInqSouVendorRoundDAO extPurInqSouVendorRoundDAO;
    @Autowired
    private ExtPurInqSouVendorDAO extPurInqSouVendorDAO;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SouRoundDAO souRoundDAO;

    @Override
    @ApiOperation("修改报价开始时间后的额外操作")
    public void doHandlerAfterChangeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType) {
        SouProject souProject = souProjectDAO.getById(param.getProjectId());
        // 1: 更新简易询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.valueOf(souProject.getProjectStatus().name()))
                .eq(ExtPurInqSouProject::getProjectId, souProject.getProjectId())
                .update();
    }

    @Override
    @ApiOperation("修改报价截止时间后的额外操作")
    public void doHandlerAfterChangeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType) {
        SouProject souProject = souProjectDAO.getById(param.getProjectId());
        // 1: 更新询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, param.isEndNow() ? ExtPurInqSouProjectStatusEnum.ORDER_END : ExtPurInqSouProjectStatusEnum.ACCEPT_ORDER)
                .eq(ExtPurInqSouProject::getProjectId, souProject.getProjectId())
                .update();
        // 2: 商务开标 or 重置商务开标
        if (param.isEndNow()) {
            ApiSouBusinessOpenDTO dto = new ApiSouBusinessOpenDTO();
            dto.setProjectId(param.getProjectId());
            souControlEventService.businessOpen(dto, souProject.getSouType());
        } else {
            souRoundDAO.lambdaUpdate()
                    .set(SouRound::getBusinessOpen, Enable.N)
                    .set(SouRound::getBusinessOpenTime, null)
                    .eq(SouRound::getProjectId, souProject.getProjectId())
                    .eq(SouRound::getRound, souProject.getCurrentRound())
                    .update();
        }
        // 3: 自动解密 or 重置解密状态
        if (Enable.Y.equals(souProject.getNeedEncryptPrice())) {
            if (param.isEndNow()) {
                // 立即截止报价，自动解密
                souControlEventService.decryptPrice(new ApiSouDecryptPriceDTO(souProject.getProjectId(), null), souProject.getSouType());
            } else {
                // 延长报价，需要将解密状态置为非解密
                souRoundDAO.lambdaUpdate()
                        .set(SouRound::getPriceDecrypt, Enable.N)
                        .set(SouRound::getPriceDecryptTime, null)
                        .eq(SouRound::getProjectId, souProject.getProjectId())
                        .eq(SouRound::getRound, souProject.getCurrentRound())
                        .update();
            }
        }

        if (param.isEndNow()) {
            this.extDoHandleAfterOrderTimeEnd(param.getProjectId());
        }
    }

    @Override
    @ApiOperation("报价截止后的额外处理")
    public void doHandlerAfterOrderEnd(SouProject souProject, SouProcessConfig souProcessConfig) {
        super.doHandlerAfterOrderEnd(souProject, souProcessConfig);
        // 1: 更新询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.ORDER_END)
                .eq(ExtPurInqSouProject::getProjectId, souProject.getProjectId())
                .update();
        // 2: 商务开标
        ApiSouBusinessOpenDTO openDTO = new ApiSouBusinessOpenDTO(); {
            openDTO.setProjectId(souProject.getProjectId());
        }
        souControlEventService.businessOpen(openDTO, souProject.getSouType());
        // 3: 解密报价
        if (Enable.Y.equals(souProject.getNeedEncryptPrice())) {
            souControlEventService.decryptPrice(new ApiSouDecryptPriceDTO(souProject.getProjectId(), null), souProject.getSouType());
        }

        this.extDoHandleAfterOrderTimeEnd(souProject.getProjectId());
    }

    public void extDoHandleAfterOrderTimeEnd(long projectId) {
        SouProject souProject = souProjectDAO.getById(projectId);
        // 1: 长城询比价额外处理
        // 说明立即截止报价了，需要自动做智能评选
        ApiSouIntelligentSelectDTO selectParam = new ApiSouIntelligentSelectDTO();
        selectParam.setProjectId(projectId);
        selectParam.setNeedAutoScore(false);
        souSelectEventService.intelligentSelect(selectParam, ExtPurInqSouTypeEnum.ext_pur_inq.name());
        // 计算排名，获取最低价供应商
        // 查询本轮次供应商的报价明细
        List<SouOrderItem> currentRoundSubmitOrderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        if (!currentRoundSubmitOrderItemList.isEmpty()) {
            Map<Long/* souItemId */, List<SouOrderItem>> currentRoundSubmitOrderItemMap = currentRoundSubmitOrderItemList
                    .stream().collect(Collectors.groupingBy(SouOrderItem::getSouItemId));
            Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO
                    .listByIds(currentRoundSubmitOrderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
            currentRoundSubmitOrderItemMap.forEach((orderItemId, orderItemList) -> {
                orderItemList.sort((a, b) -> {
                    int rs = a.getStandardNotaxPrice().compareTo(b.getStandardNotaxPrice());
                    if (rs != 0) { return rs; }
                    // 价格相同，进一步比较"到货周期"
                    ExtPurInqSouOrderItem inqA = inqOrderItemMap.get(a.getOrderItemId());
                    ExtPurInqSouOrderItem inqB = inqOrderItemMap.get(b.getOrderItemId());
                    if (inqA.getExtLeadTime() < inqB.getExtLeadTime()) {
                        return -1;
                    } else if (inqA.getExtLeadTime() > inqB.getExtLeadTime()) {
                        return 1;
                    }
                    // 进一步比较"质保期"
                    if (inqA.getExtWarrantyPeriod() < inqB.getExtWarrantyPeriod()) {
                        return -1;
                    } else if (inqA.getExtWarrantyPeriod() > inqB.getExtWarrantyPeriod()) {
                        return 1;
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
        }
    }

    @ApiOperation("报价开始前的额外处理")
    @Override
    public void doHandlerAfterOrderStart(SouProject souProject, SouProcessConfig souProcessConfig) {
        // 1: 更新简易询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.ACCEPT_ORDER)
                .eq(ExtPurInqSouProject::getProjectId, souProject.getProjectId())
                .update();
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
        // 2: 集采这边是全选物料
        Set<Long> existSouItemIds = souItemDAO.list(SouItem::getProjectId, param.getProjectId()).stream().map(SouItem::getSouItemId).collect(Collectors.toSet());
        inqParam.setInqChooseSouItemIds(existSouItemIds);

        SouObjectXUtil.mergeProperties(inqParam, param);
    }

    @Override
    @ApiOperation("发起新一轮后的额外处理")
    public void doHandlerAfterStartNewRound(ApiSouStartNewRoundDTO param, String souType) {
        ExtInqSouStartNewRoundDTO inqParam = SouObjectXUtil.convertTargetObj(param, ExtInqSouStartNewRoundDTO.class);

        SouProject souProject = souProjectDAO.getById(param.getProjectId());
        // 1: 更新简易询价单据状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.valueOf(souProject.getProjectStatus().name()))
                .eq(ExtPurInqSouProject::getProjectId, souProject.getProjectId())
                .update();

        // 2: 新增供应商/物料轮次信息
        List<ExtPurInqSouItemRound> itemRoundList = new ArrayList<>(inqParam.getInqChooseSouItemIds().size()); {
            for (Long souItemId : inqParam.getInqChooseSouItemIds()) {
                ExtPurInqSouItemRound itemRound = new ExtPurInqSouItemRound();
                itemRoundList.add(itemRound);

                itemRound.setInqSouItemRoundId(IdGenrator.generate());
                itemRound.setProjectId(souProject.getProjectId());
                itemRound.setSouItemId(souItemId);
                itemRound.setRound(souProject.getCurrentRound());
                itemRound.setCanOrder(Enable.Y);
            }
        }
        List<ExtPurInqSouVendorRound> vendorRoundList = new ArrayList<>(inqParam.getInqChooseVendorList().size()); {
            for (ExtPjInqSouVendorDTO vendor : inqParam.getInqChooseVendorList()) {
                ExtPurInqSouVendorRound vendorRound = new ExtPurInqSouVendorRound();
                vendorRoundList.add(vendorRound);

                vendorRound.setInqSouVendorRoundId(IdGenrator.generate());
                vendorRound.setProjectId(souProject.getProjectId());
                vendorRound.setVendorId(vendor.getVendorId());
                vendorRound.setRound(souProject.getCurrentRound());
                vendorRound.setCanOrder(Enable.Y);
            }
        }
        extPurInqSouItemRoundDAO.saveBatch(itemRoundList);
        extPurInqSouVendorRoundDAO.saveBatch(vendorRoundList);
        // 3: 将指定物料的历史报价，均设置为非最新报价
        Set<Long> unLatestOrderItemIds = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .in(SouOrderItem::getSouItemId, inqParam.getInqChooseSouItemIds())
                .lt(SouOrderItem::getRound, souProject.getCurrentRound())
                .select(SouOrderItem::getOrderItemId)
                .list().stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
        if (!unLatestOrderItemIds.isEmpty()) {
            extPurInqSouOrderItemDAO.lambdaUpdate()
                    .set(ExtPurInqSouOrderItem::getLatestPriceTag, Enable.N)
                    .in(ExtPurInqSouOrderItem::getOrderItemId, unLatestOrderItemIds)
                    .update();
        }
    }

    @Override
    @ApiOperation("执行追加供应商后的额外处理")
    public void doHandlerAfterVendorAdd(ApiSouVendorAddDTO param, String souType, SouVendorAddPO po) {
        super.doHandlerAfterVendorAdd(param, souType, po);

        // 1: 新增额外的邀请供应商信息
        List<ExtPurInqSouVendor> extVendorList = new ArrayList<>(po.getSaveVendorList().size());
        for (SouVendor newVendor : po.getSaveVendorList()) {
            ExtPurInqSouVendor extVendor = new ExtPurInqSouVendor();
            extVendorList.add(extVendor);

            extVendor.setSouVendorId(newVendor.getSouVendorId());
            extVendor.setProjectId(newVendor.getProjectId());
            extVendor.setVendorId(newVendor.getVendorId());
            extVendor.setSourceFromType(ExtPurInqSouVendorSourceFromTypeEnum.HAND_MAKE);
            extVendor.setNewVendorTag(Enable.Y);
        }
        extPurInqSouVendorDAO.saveBatch(extVendorList);
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
