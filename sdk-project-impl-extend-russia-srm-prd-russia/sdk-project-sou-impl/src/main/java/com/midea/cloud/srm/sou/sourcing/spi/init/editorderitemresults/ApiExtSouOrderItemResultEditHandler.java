package com.midea.cloud.srm.sou.sourcing.spi.init.editorderitemresults;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.enums.SouBidAttachmentTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemResultEditParam;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ExtSouProjectEditPO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouOrderItemResultEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouOrderItemService orderItemService;
    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouInviteService extSouInviteService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ApiExtSouOrderItemResultPO formatValidateAndConvert(ApiExtSouOrderItemResultEditParam param, String souType) {

        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouOrderItemResultEditParam param, String souType) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(param.getOrderItemList()), "请勾选投标明细行数据再进行操作！");
    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ApiExtSouOrderItemResultPO convert(ApiExtSouOrderItemResultEditParam param, String souType) {
        ApiExtSouOrderItemResultPO po = new ApiExtSouOrderItemResultPO();
        po.setOrderItemList(doConvertOrderItem(param, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouOrderItem> doConvertOrderItem(ApiExtSouOrderItemResultEditParam param, String souType) {

        LambdaQueryWrapper<ExtSouOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrderItem::getProjectId, param.getProjectId());
        queryWrapper.in(ExtSouOrderItem::getOrderItemId, param.getOrderItemList().stream().map(ApiExtSouOrderItemDto::getOrderItemId).collect(Collectors.toList()));
        List<ExtSouOrderItem> souOrderItemList  = orderItemService.list(queryWrapper);

        if(SouSelectStatusEnum.WIN.name().equals(param.getType())) {
            souOrderItemList.stream().forEach(item -> {
                item.setWinStatus(SouWinStatusEnum.Y);
                item.setSelectStatus(SouSelectStatusEnum.WIN);
            });
        } else {
            souOrderItemList.stream().forEach(item -> {
                item.setWinStatus(SouWinStatusEnum.N);
                item.setSelectStatus(SouSelectStatusEnum.FAIL);
            });
        }

        return souOrderItemList;
    }

    public void doHandlerAfterEditeOrderItemResult(ApiExtSouOrderItemResultEditParam param, String souType, ApiExtSouOrderItemResultPO po) {
        ExtSouProject project = projectService.getById(param.getProjectId());
        List<SouInviteItem> souInviteItems = new ArrayList<>();
        Set<Long> vendorIdSet = new HashSet<>();
        po.getOrderItemList().stream().forEach(extSouOrderItem -> {
            if(vendorIdSet.contains(extSouOrderItem.getVendorId())) {
                return;
            }
            vendorIdSet.add(extSouOrderItem.getVendorId());
            SouInviteItem inviteItem = new SouInviteItem();
            inviteItem.setVendorId(extSouOrderItem.getVendorId());
            inviteItem.setIsSuccBid(ObjectUtils.defaultIfNull(extSouOrderItem.getWinStatus(), SouWinStatusEnum.N).name());
            souInviteItems.add(inviteItem);
        });
        extSouInviteService.updateIsSuccBidBatch(project, souInviteItems);
    }
}
