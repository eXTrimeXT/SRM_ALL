package com.midea.cloud.srm.sou.sourcing.vendor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderDetailDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.sou.deposit.service.FinanceService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorEventService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editmargins.ApiExtSouOrderMarginPO;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editmargins.ApiExtSouOrderVendorMarginHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders.ApiExtSouOrderPO;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders.ApiExtSouOrderVendorConfirmHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders.ApiExtSouOrderVendorWithdrawHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorderitems.ApiExtSouOrderItemEditHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorderitems.ApiExtSouOrderItemPO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Api("寻源核心-供应商接口实现类")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExtBidSouForVendorEventServiceImpl implements ExtBidSouForVendorEventService {
    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Resource
    private FinanceService financeService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long confirmTender(ExtSouOrderDto param, String souType) {
        //行业包前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorConfirmHandler.class).doHandlerBeforeOrderConfirm(param, souType);

        //转PO
        ApiExtSouOrderPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorConfirmHandler.class).formateValidAndConvert(param, souType);

        //保存数据
        orderService.updateById(po.getSouOrder());

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorConfirmHandler.class).doHandlerAfterOrderConfirm(param, souType, po);

        return po.getSouOrder().getProjectId();
    }

    @Override
    public Long withdrawTender(ExtSouOrderDto param, String souType) {
        //行业包前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorWithdrawHandler.class).doHandlerBeforeOrderWithdraw(param, souType);

        //转PO
        ApiExtSouOrderPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorWithdrawHandler.class).formateValidAndConvert(param, souType);

        //保存数据
        orderService.updateById(po.getSouOrder());

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorWithdrawHandler.class).doHandlerAfterOrderWithdraw(param, souType, po);

        return po.getSouOrder().getProjectId();
    }

    @Override
    public Long editOrderMargin(ExtSouMarginDto param, String souType) {
        if(param.getProjectId() == null){
            throw new BaseException("projectId参数不能为空！");
        }

        String redisKey = "editOrderMargin_"+param.getProjectId();
        log.info("editOrderMargin redisKey="+redisKey);
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 300, TimeUnit.SECONDS);
        if(!lock){
            throw new BaseException(String.format("该单据正在提交中， 请稍等！"));
        }
        log.info(redisKey+"开始缴纳保证金...");

        try {
            //行业包前置处理
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorMarginHandler.class).doHandlerBeforeOrderMargin(param, souType);

            //转PO
            ApiExtSouOrderMarginPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorMarginHandler.class).formateValidAndConvert(param, souType);

            //保存数据
            marginService.updateById(po.getSouMargin());
            //针对年度保证金项目ID
            po.getSouMargin().setProjectId(param.getProjectId());

            //缴纳保证金
            financeService.dealEarnestMoneyDepositPayment(po.getSouMargin(), po.getSouMargin().getPayName(), po.getSouMargin().getVendorId());

            //行业包后置处理
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderVendorMarginHandler.class).doHandlerAfterOrderMargin(param, souType, po);

            return po.getSouMargin().getProjectId();
        }catch (Exception e){
            log.error(redisKey+"缴纳保证金失败：", e);
            throw new BaseException(e.getMessage());
        }finally {
            try {
                this.redisTemplate.delete(redisKey);
            } catch (Exception e) {
                log.info("删除redis锁失败 editOrderMargin redisKey="+redisKey);
            }
        }
    }

    @Override
    public Long editOrderItem(ApiExtSouOrderDetailDto param, String souType) {

        //行业包前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemEditHandler.class).doHandlerBeforeOrderItemEdit(param, souType);

        //转PO
        ApiExtSouOrderItemPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemEditHandler.class).formateValidAndConvert(param, souType);

        //保存数据
        if (!param.isTempSave()) {
            ExtSouOrder souOrder = po.getSouOrder();
            orderService.updateById(souOrder);
        }

        //获取附件类型范围，先移除再保存
        List<String> fileTypeList = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemEditHandler.class).removeOrderFileTypeRange(po, souType);

        LambdaQueryWrapper<ExtSouOrderFile> fileQuery = new LambdaQueryWrapper<>();
        fileQuery.eq(ExtSouOrderFile::getOrderId, param.getOrderId());
        fileQuery.eq(ExtSouOrderFile::getProjectId, param.getProjectId());
        fileQuery.eq(ExtSouOrderFile::getRound, po.getSouOrder().getRound());
        fileQuery.eq(ExtSouOrderFile::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
        fileQuery.in(ExtSouOrderFile::getFileType, fileTypeList);
        orderFileService.remove(fileQuery);

        if (CollectionUtils.isNotEmpty(po.getSouOrderFileList())) {
            orderFileService.saveOrUpdateBatch(po.getSouOrderFileList());
        }

        //保存报价
        if (CollectionUtils.isNotEmpty(po.getSouOrderItemList())) {
            po.getSouOrderItemList().stream().forEach(orderItem -> orderItem.setOrderStatus(po.getSouOrder().getOrderStatus()));
            orderItemService.saveOrUpdateBatch(po.getSouOrderItemList());
        }

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemEditHandler.class).doHandlerAfterOrderItemEdit(param, souType, po);
        return param.getProjectId();
    }

    @Override
    public Long updateBidFileDownloadTime(Long projectId) {
        extNpmSouOrderService.updateDownloadTime(projectId);
        return projectId;
    }
}
