package com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouOrderVendorConfirmHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private ExtSouInviteService extSouInviteService;

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    private IExtSouVendorService vendorService;

    @ApiModelProperty("校验数据和转换PO")
    public ApiExtSouOrderPO formateValidAndConvert(ExtSouOrderDto param, String souType) {
        //1.数据校验
        this.formateValid(param, souType);
        //2.数据转换
        ApiExtSouOrderPO po =  this.convert(param, souType);
        //数据转换后校验
        this.convertAfterValid(param, souType, po);
        return po;
    }

    @ApiModelProperty("数据校验")
    public void convertAfterValid(ExtSouOrderDto param, String souType, ApiExtSouOrderPO po) {
//        List<ExtSouMargin> marginList = marginService.lambdaQuery().eq(ExtSouMargin::getProjectId, po.getSouOrder().getProjectId())
//                .eq(ExtSouMargin::getVendorId, po.getSouOrder().getVendorId()).list();
//        if(CollectionUtils.isNotEmpty(marginList)) {
//            ExtSouMargin margin = marginList.get(0);
//            Boolean check = false;
//
//            if(Arrays.asList(SouBidMarginStatusEnum.PAY.getCode(), SouBidMarginStatusEnum.NOT_CONVER.getCode()).contains(margin.getMarginStatus())) {
//                check = true;
//            }
//
//            AssertUtils.isTrue(check, "未缴纳清保证金，不允许进行该操作！");
//
//        }
    }


        @ApiModelProperty("数据校验")
    public void formateValid(ExtSouOrderDto param, String souType) {

    }

    @ApiModelProperty("数据转换PO")
    ApiExtSouOrderPO convert(ExtSouOrderDto param, String souType) {
        ApiExtSouOrderPO po = new ApiExtSouOrderPO();
        ExtSouOrder souOrder = orderService.getById(param.getOrderId());
        AssertUtils.notNull(souOrder, "投标信息不存在！");

        souOrder.setExtTenderEmail(param.getExtTenderEmail());
        souOrder.setExtTenderName(param.getExtTenderName());
        souOrder.setExtTenderPhone(param.getExtTenderPhone());
        souOrder.setExtTenderFlag(YesOrNo.YES.getValue());

        po.setSouOrder(souOrder);
        return po;
    }

    @ApiOperation("投标确认前置处理")
    public void doHandlerBeforeOrderConfirm(ExtSouOrderDto param, String souType) {

    }

    @ApiOperation("寻源分页查询的后置处理")
    public void doHandlerAfterOrderConfirm(ExtSouOrderDto param, String souType, ApiExtSouOrderPO po) {
        //是否投标---是
        extSouInviteService.updateIsBid(projectService.getById(po.getSouOrder().getProjectId()), po.getSouOrder().getVendorId(), YesOrNo.YES.getValue());

        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, po.getSouOrder().getProjectId())
                .eq(ExtSouVendor::getVendorId, po.getSouOrder().getVendorId()).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(po.getSouOrder().getProjectId(), vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), "确认投标"));
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
