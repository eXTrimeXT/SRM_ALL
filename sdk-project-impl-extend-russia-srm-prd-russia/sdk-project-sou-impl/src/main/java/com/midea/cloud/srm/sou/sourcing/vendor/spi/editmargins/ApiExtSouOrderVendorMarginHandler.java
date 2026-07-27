
package com.midea.cloud.srm.sou.sourcing.vendor.spi.editmargins;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders.ApiExtSouOrderPO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouOrderVendorMarginHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    private IExtSouVendorService vendorService;

    @ApiModelProperty("校验数据和转换PO")
    public ApiExtSouOrderMarginPO formateValidAndConvert(ExtSouMarginDto param, String souType) {
        //1.数据校验
        this.formateValid(param, souType);
        //2.数据转换
        return this.convert(param, souType);
    }

    @ApiModelProperty("数据校验")
    public void formateValid(ExtSouMarginDto param, String souType) {

    }

    @ApiModelProperty("数据转换PO")
    ApiExtSouOrderMarginPO convert(ExtSouMarginDto param, String souType) {
        ApiExtSouOrderMarginPO po = new ApiExtSouOrderMarginPO();

        ExtSouMargin margin = marginService.getById(param.getMarginId());

        if(!Objects.isNull(margin) && YesOrNo.YES.getValue().equals(margin.getYearFlag())) {
            margin = marginService.getById(margin.getRelYearMarginId());
        }

        AssertUtils.notNull(margin, "保证金信息不存在！");

        //缴纳凭证
        margin.setPayVoucher(param.getPayVoucher());
        margin.setPayVoucherFileId(param.getPayVoucherFileId());
        margin.setPayName(param.getPayName());
        margin.setExtIsBehalfPay(param.getExtIsBehalfPay());

        //缴纳失败原因
        margin.setMarginFailCause(param.getMarginFailCause());
        if(!SouBidMarginStatusEnum.PAY.getCode().equals(margin.getMarginStatus())) {
            margin.setMarginStatus(SouBidMarginStatusEnum.CONFIRM_TODO.getCode());
            //清除处理方式和失败原因
            margin.setHanderMode("");
            margin.setCauseDesc("");
        }

        po.setSouMargin(margin);

        return po;
    }

    @ApiOperation("投标确认前置处理")
    public void doHandlerBeforeOrderMargin(ExtSouMarginDto param, String souType) {

    }

    @ApiOperation("寻源分页查询的后置处理")
    public void doHandlerAfterOrderMargin(ExtSouMarginDto param, String souType, ApiExtSouOrderMarginPO po) {
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, po.getSouMargin().getProjectId())
                .eq(ExtSouVendor::getVendorId, po.getSouMargin().getVendorId()).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(po.getSouMargin().getProjectId(), vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), "缴纳保证金"));
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
