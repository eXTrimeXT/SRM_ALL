package com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 撤回投标
 * @author huangbf3
 */
@Service
public class ApiExtSouOrderVendorWithdrawHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    private IExtSouVendorService vendorService;

    @ApiModelProperty("校验数据和转换PO")
    public ApiExtSouOrderPO formateValidAndConvert(ExtSouOrderDto param, String souType) {
        //1.数据校验
        this.formateValid(param, souType);
        //2.数据转换
        return this.convert(param, souType);
    }

    @ApiModelProperty("数据校验")
    public void formateValid(ExtSouOrderDto param, String souType) {
        ExtSouOrder souOrder = orderService.getById(param.getOrderId());
        AssertUtils.notNull(souOrder, "投标信息不存在！");
        ExtSouProject project = projectService.getById(souOrder.getProjectId());
        project = fixedProjectStatusService.fixedProjectStatus(project, souType);
        if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.BUS_BID.getCode()).contains(project.getProjectStatus())) {
            throw new BaseException("非投标中不允许撤回投标!");
        }
    }

    @ApiModelProperty("数据转换PO")
    ApiExtSouOrderPO convert(ExtSouOrderDto param, String souType) {
        ApiExtSouOrderPO po = new ApiExtSouOrderPO();
        ExtSouOrder souOrder = orderService.getById(param.getOrderId());
        AssertUtils.notNull(souOrder, "投标信息不存在！");

        souOrder.setOrderStatus(SouOrderStatusEnum.WITHDRAW);
        souOrder.setWithdrawTime(new Date());
        souOrder.setWithdrawReason(param.getWithdrawReason());

        po.setSouOrder(souOrder);
        return po;
    }

    @ApiOperation("撤回投标前置处理")
    public void doHandlerBeforeOrderWithdraw(ExtSouOrderDto param, String souType) {

    }

    @ApiOperation("撤回投标后置处理")
    public void doHandlerAfterOrderWithdraw(ExtSouOrderDto param, String souType, ApiExtSouOrderPO po) {
        //回写扩展表
        extNpmSouOrderService.extendSouOrder(Collections.singletonList(po.getSouOrder()));

        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, po.getSouOrder().getProjectId())
                .eq(ExtSouVendor::getVendorId, po.getSouOrder().getVendorId()).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(po.getSouOrder().getProjectId(), vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), "撤回投标"));
        }
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
