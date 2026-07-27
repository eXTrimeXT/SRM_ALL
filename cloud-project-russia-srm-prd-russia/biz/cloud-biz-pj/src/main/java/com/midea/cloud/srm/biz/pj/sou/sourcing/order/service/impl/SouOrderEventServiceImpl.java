package com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.impl;

import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemHisDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.SouOrderEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder.ApiSouOrderEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.importquotetemp.ApiSouOrderImportQuoteTempHandler;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataDetailVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 寻源 - 供应商报价 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/05
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouOrderEventServiceImpl implements SouOrderEventService {

    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouOrderItemHisDAOImpl souOrderItemHisDao;
    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    /**
     * 暂存/提交报价
     *
     * @param param   报价信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editOrder(ApiSouOrderDTO param, String souType) {
        List<ApiSouOrderItemDTO> orderItemList =  param.getOrderItemList();

        Long souItemId = param.getOrderItemList().get(0).getSouItemId();
        Long souvendorId = param.getOrderItemList().get(0).getVendorId();
        List<SouOrderItem> souOrderItemList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getSouItemId, souItemId)
                .eq(SouOrderItem::getVendorId,souvendorId)
                .list();
        int round =0;
        if(souOrderItemList.size()>0){
            if(null != souOrderItemList.get(0).getOrderRound()){
                round = souOrderItemList.get(0).getOrderRound() + 1;
            }else{
                round = 1;
            }

        }

        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
       /* SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeOrderAuth(param.getProjectId(), param.getVendorId(),
                Enable.Y.equals(param.getIsProxy()), souType);*/
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeOrderAuth1(param,param.getProjectId(), param.getVendorId(),
                Enable.Y.equals(param.getIsProxy()), souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerBeforeEditOrder(param, souType);
        // 4: 入参校验+转换处理
        SouOrderEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEditHandler.class).formatValidateAndConvert(param, souType);
        // 5: 保存数据
        souOrderDao.saveOrUpdate(po.getSouOrder());
        for(int i = 0 ; i < po.getOrderItemList().size(); i ++){
            po.getOrderItemList().get(i).setOrderRound(round);
        }
        souOrderItemDao.saveOrUpdateBatch(po.getOrderItemList());


        for(int i = 0 ; i <orderItemList.size(); i ++ ){
            souOrderItemDao.removeById(orderItemList.get(i).getOrderItemId());
        }

        souOrderItemHisDao.saveBatch(po.getHisPriceList());

        // 6: 新增供应商信息(公开招标+首次报价)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class)
                .doHandlerForNewVendorWhileOrder(param.getProjectId(), param.getVendorId(), souType);
        // 7: 更新本轮的应/已报价供应商数量
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerForOrderCountWhileOrder(param.getProjectId());
        // 8: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerAfterEditOrder(param, souType, po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void initOrder(ApiSouOrderDTO param) {
        String souType = SouTypeEnum.comp.name();
        // 1: 入参格式化
        param.formatParams();
        // 2: 入参校验+转换处理
        SouOrderEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEditHandler.class).formatAndConvert(param);
        // 3: 保存数据
        souOrderDao.saveOrUpdate(po.getSouOrder());
        souOrderItemDao.saveOrUpdate(po.getSouOrder().getOrderId(), po.getOrderItemList(), SouOrderItem::getOrderId);

    }

    /**
     * 撤回报价
     *
     * @param param   撤回报价信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void withdrawOrder(ApiSouOrderWithdrawDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class)
                .judgeRollbackAuth(param.getProjectId(), param.getVendorId(), souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerBeforeWithdrawOrder(param, souType);
        // 4: 撤回报价
        souOrderDao.lambdaUpdate()
                .set(SouOrder::getOrderStatus, SouOrderStatusEnum.WITHDRAW)
                .set(SouOrder::getWithdrawReason, param.getWithdrawReason())
                .set(SouOrder::getWithdrawTime, new Date())
                .eq(SouOrder::getProjectId, souProject.getProjectId())
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .eq(SouOrder::getVendorId, param.getVendorId())
                .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .update();
        souOrderItemDao.lambdaUpdate()
                .set(SouOrderItem::getOrderStatus, SouOrderStatusEnum.WITHDRAW)
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getVendorId, param.getVendorId())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .update();
        // 5: 更新本轮应/已报价供应商数量
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerForOrderCountWhileOrder(param.getProjectId());
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerAfterWithdrawOrder(param, souType);
    }

    /**
     * 作废报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelOrder(ApiSouOrderCancelDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouOrder order = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeCancelOrderAuth(param, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerBeforeCancelOrder(param, souType);
        // 4: 作废报价
        souOrderDao.lambdaUpdate()
                .set(SouOrder::getOrderStatus, SouOrderStatusEnum.CANCEL)
                .set(SouOrder::getRejectReason, param.getCancelReason())
                .set(SouOrder::getRejectTime, new Date())
                .eq(SouOrder::getOrderId, order.getOrderId())
                .update();
        souOrderItemDao.lambdaUpdate()
                .set(SouOrderItem::getOrderStatus, SouOrderStatusEnum.CANCEL)
                .eq(SouOrderItem::getOrderId, order.getOrderId())
                .update();
        // 5: 更新本轮的应/已报价供应商数量
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerForOrderCountWhileOrder(param.getProjectId());
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerAfterCancelOrder(param, souType);
    }

    /**
     * 物料维度报价模板导入
     */
    @Override
    public void importOrderItemQuoteTempExcel(long projectId, long vendorId, long souItemId, @Nullable Integer round, boolean isBuyer, String souType,
                                              MultipartFile file) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeImportOrderItemQuoteTempExcel(projectId, vendorId, souItemId, isBuyer, souType);
        // 2: 导入数据
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderImportQuoteTempHandler.class).execute(projectId, vendorId, souItemId, round, isBuyer, souType, file);
    }

    /**
     * 报价模板数据计算
     *
     * @param tempId     {@link SouQuoteTemp#getTempId}
     * @param businessId 业务ID
     * @param tempData   模板数据
     * @param isBuyer    true-采购商端/false-供应商端
     * @param vendorId   供应商ID(isBuyer=false时必填)
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouQuoteTempDataDetailVO computeQuoteTempData(long tempId, String businessId, Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData,
                                                         boolean isBuyer, @Nullable Long vendorId, String souType) {
        // 1: 校验操作条件/权限
        businessId = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class)
                .judgeGetQuoteTempOrderInfoAuth(tempId, businessId, isBuyer, vendorId, false, souType);
        // 2: 计算
        return souQuoteTempService.computeTempData(tempId, businessId, tempData, true, false);
    }

}
