package com.midea.cloud.srm.sou.sourcing.vendor.spi.editorderitems;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderDetailDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders.ApiExtSouOrderPO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
@Api("报价编辑行业包")
public class ApiExtSouOrderItemEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    private IExtSouVendorService vendorService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiModelProperty("校验数据和转换PO")
    public ApiExtSouOrderItemPO formateValidAndConvert(ApiExtSouOrderDetailDto param, String souType) {
        //1.数据校验
        this.formateValid(param, souType);
        //2.数据转换
        return this.convert(param, souType);
    }

    @ApiModelProperty("数据校验")
    protected void formateValid(ApiExtSouOrderDetailDto param, String souType) {

    }

    @ApiModelProperty("数据转换PO")
    protected ApiExtSouOrderItemPO convert(ApiExtSouOrderDetailDto param, String souType) {
        ApiExtSouOrderItemPO po = new ApiExtSouOrderItemPO();

        po.setSouOrder(this.doConvertSouOrder(param, souType));

        po.setSouOrderFileList(this.doConvertSouOrderFile(param, souType, po.getSouOrder()));

        po.setSouOrderItemList(this.doConvertSouOrderItem(param, souType, po.getSouOrder()));

        return po;
    }

    protected void convertSouOrderThenValid(ExtSouOrder souOrder, ExtSouProject project) {
        //第一轮商务报价校验
        if(ExtOrderTypeEnum.BUS.getCode().equals(souOrder.getExtOrderType()) && Integer.compare(souOrder.getRound(), 1) == 0) {
            //是否需要保证金
            if(YesOrNo.YES.getValue().equals(project.getExtEarnestFlag())) {
                //查询保证金
                List<ExtSouMargin> marginList = marginService.lambdaQuery().eq(ExtSouMargin::getProjectId, project.getProjectId())
                        .eq(ExtSouMargin::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()).list();
                ExtSouMargin souMargin = CollectionUtils.isNotEmpty(marginList)?marginList.get(0):new ExtSouMarginDto();
                if(!Arrays.asList(SouBidMarginStatusEnum.PAY.getCode(), SouBidMarginStatusEnum.NOT_CONVER.getCode()).contains(souMargin.getMarginStatus())) {
                    throw new BaseException("供应商未缴纳保证金，不允许进行商务投标！");
                }
            }
        }
    }

    @ApiModelProperty("转换投标头表")
    protected ExtSouOrder doConvertSouOrder(ApiExtSouOrderDetailDto param, String souType) {
        ExtSouOrder souOrder = orderService.getById(param.getOrderId());
        if(Objects.isNull(souOrder)) {
            throw new BaseException("报价单信息不存在");
        }

        ExtSouProject project = projectService.getById(souOrder.getProjectId());
        //修正招标状态
        project = fixedProjectStatusService.fixedProjectStatus(project, souType);
        if(Integer.compare(ObjectUtils.defaultIfNull(project.getCurrentRound(), 0), ObjectUtils.defaultIfNull(souOrder.getRound(), 0)) != 0) {
            throw new BaseException("未进入本轮投标范围，不允许投标！");
        }
        //未技术投标，不允许商务投标
        if(SouBiddingProStatusEnum.BUS_BID.getCode().equals(project.getProjectStatus()) && ExtOrderTypeEnum.TECH.getCode().equals(souOrder.getExtOrderType())) {
            throw new BaseException("未被组织商务报价，不允许投标！");
        }

        AssertUtils.isTrue(Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.BUS_BID.getCode()).contains(project.getProjectStatus()), "非投标中状态，不允许投标！");

        AssertUtils.isFalse(SouOrderStatusEnum.SUBMISSION.equals(souOrder.getOrderStatus()), "已投标，请勿重复操作！");

        AssertUtils.isTrue(Arrays.asList(SouOrderStatusEnum.DRAFT, SouOrderStatusEnum.WITHDRAW).contains(ObjectUtils.defaultIfNull(souOrder.getOrderStatus(), SouOrderStatusEnum.DRAFT)), "投标状态为未投标或撤回投标时才可进行投标！");

        if(!param.isTempSave()) {
            souOrder.setSubmitBy(AppUserUtil.getLoginAppUser().getUsername());
            souOrder.setSubmitFullName(AppUserUtil.getLoginAppUser().getNickname());
            souOrder.setSubmitById(AppUserUtil.getLoginAppUser().getUserId());

            souOrder.setOrderStatus(SouOrderStatusEnum.SUBMISSION);
            if(SouBiddingProStatusEnum.TECH_BID.getCode().equals(project.getProjectStatus())) {
                //技术标
                souOrder.setExtOrderType(ExtOrderTypeEnum.TECH.getCode());
                souOrder.setExtTechFlag(YesOrNo.YES.getValue());
            } else {
                souOrder.setExtOrderType(ExtOrderTypeEnum.BUS.getCode());
            }
            souOrder.setSubmitTime(new Date());
        }
        return souOrder;
    }

    @ApiModelProperty("需要移除的投标附件类型")
    public List<String> removeOrderFileTypeRange(ApiExtSouOrderItemPO po, String souType) {
        if(Integer.compare(po.getSouOrder().getRound(), 1) == 0) {
            return Arrays.asList(ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode(), ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode(), ExtSouFileConfigTypeEnum.BUS_BID.getCode(), ExtSouFileConfigTypeEnum.BUS_OTHER.getCode());
        }
        return Arrays.asList(ExtSouFileConfigTypeEnum.BUS_BID.getCode(), ExtSouFileConfigTypeEnum.BUS_OTHER.getCode());
    }


    @ApiModelProperty("转换投标附件")
    protected List<ExtSouOrderFile> doConvertSouOrderFile(ApiExtSouOrderDetailDto param, String souType, ExtSouOrder souOrder) {
        List<ExtSouOrderFile> orderFileList = new ArrayList<>();
        //技术文件
        if(CollectionUtils.isNotEmpty(param.getTechOrderFileList()) && Integer.compare(souOrder.getRound(), 1) == 0) {
            param.getTechOrderFileList().stream().forEach(file -> {
                if(Objects.isNull(file.getOrderFileId())) {
                    file.setOrderFileId(IdGenrator.generate());
                }
                if(StringUtils.isBlank(file.getExtSignStatus())) {
                    file.setExtSignStatus(BidSignStatusEnum.NOT_SIGN.getCode());
                }
                if(!param.isTempSave()) {
                    file.setExtSubmitTime(souOrder.getSubmitTime());
                }
                file.setExtOrderStatus(souOrder.getOrderStatus().name());
                file.setOrderId(param.getOrderId());
                file.setRound(souOrder.getRound());
                file.setProjectId(param.getProjectId());
                file.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
                file.formattingPackageListToName();
                orderFileList.add(file);
            });
        }

        //商务文件
        if(CollectionUtils.isNotEmpty(param.getBusOrderFileList())) {
            param.getBusOrderFileList().stream().forEach(file -> {
                if(Objects.isNull(file.getOrderFileId())) {
                    file.setOrderFileId(IdGenrator.generate());
                }
                if(StringUtils.isBlank(file.getExtSignStatus())) {
                    file.setExtSignStatus(BidSignStatusEnum.NOT_SIGN.getCode());
                }
                if(!param.isTempSave()) {
                    file.setExtSubmitTime(souOrder.getSubmitTime());
                }
                if(StringUtils.isBlank(file.getFileType())) {
                    file.setFileType(ExtSouFileConfigTypeEnum.BUS_BID.getCode());
                }

                file.setExtOrderStatus(souOrder.getOrderStatus().name());
                file.setOrderId(param.getOrderId());
                file.setRound(souOrder.getRound());
                file.setProjectId(param.getProjectId());
                file.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
                file.formattingPackageListToName();
                orderFileList.add(file);
            });
        }
        return orderFileList;
    }

    @ApiModelProperty("转换投标报价信息")
    protected List<ExtSouOrderItem> doConvertSouOrderItem(ApiExtSouOrderDetailDto param, String souType, ExtSouOrder souOrder) {
        List<ExtSouOrderItem> orderItemList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(param.getOrderItemList())) {
            param.getOrderItemList().stream().forEach(itemDto -> {
                //转换成报价单价格
                itemDto.coverOrderFields();
                ExtSouOrderItem extSouOrderItem = new ExtSouOrderItem();
                BeanCopyUtil.copyProperties(extSouOrderItem, itemDto);
                if(Objects.isNull(extSouOrderItem.getOrderItemId())) {
                    extSouOrderItem.setOrderItemId(IdGenrator.generate());
                }
                if(!param.isTempSave()) {
                    extSouOrderItem.setExtSubmitTime(souOrder.getSubmitTime());
                }
                extSouOrderItem.setOrderId(souOrder.getOrderId());
                extSouOrderItem.setOrderStatus(souOrder.getOrderStatus());
                extSouOrderItem.setRound(souOrder.getRound());
                orderItemList.add(extSouOrderItem);
            });
        }
        return orderItemList;
    }

    @ApiOperation("投标确认前置处理")
    public void doHandlerBeforeOrderItemEdit(ApiExtSouOrderDetailDto param, String souType) {

    }

    @ApiOperation("寻源分页查询的后置处理")
    public void doHandlerAfterOrderItemEdit(ApiExtSouOrderDetailDto param, String souType, ApiExtSouOrderItemPO po) {
        //更新到扩展表
        extNpmSouOrderService.extendSouOrder(Collections.singletonList(po.getSouOrder()));

        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, po.getSouOrder().getProjectId())
                .eq(ExtSouVendor::getVendorId, po.getSouOrder().getVendorId()).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(po.getSouOrder().getProjectId(), vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), "投标"));
        }
    }
}
