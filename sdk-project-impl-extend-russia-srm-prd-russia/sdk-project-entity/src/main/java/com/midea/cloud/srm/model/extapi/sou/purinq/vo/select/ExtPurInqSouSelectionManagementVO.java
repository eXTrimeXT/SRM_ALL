package com.midea.cloud.srm.model.extapi.sou.purinq.vo.select;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouSelectionManagementVO extends BaseObjectX {

    @ApiModelProperty("询价单")
    private ApiPurInqSouProjectVO header;

    @ApiModelProperty("当前轮次该报价供应商数量")
    private Integer currentRoundTotalCtn;

    @ApiModelProperty("当前轮次已报价供应商数量")
    private Integer currentRoundQuotedCtn;

    @ApiModelProperty("供应商报价单信息集合")
    private List<ExtPurInqSouOrderTrackingVO> trackingList;

    /**
     * 转换方法
     */
    public static ExtPurInqSouSelectionManagementVO convert(ApiPurInqSouProjectVO projectVO,
                                                            List<SouOrder> currentRoundOrderList,
                                                            Map<Long/* orderId */, ExtPurInqSouOrder> inqOrderMap,
                                                            List<SouVendor> vendorList, SouProject souProject,
                                                            Map<Long/* orderId */, List<SouOrderFile>> currentRoundOrderFileMap) {
        SouProjectStatusEnum projectStatus = souProject.getProjectStatus();
        Enable needEncryptPrice = souProject.getNeedEncryptPrice();

        ExtPurInqSouSelectionManagementVO managementVO = new ExtPurInqSouSelectionManagementVO();
        managementVO.header = projectVO;
        // 受邀供应商数
        managementVO.currentRoundTotalCtn = vendorList.size();
        // 已提交报价的供应商数量
        managementVO.currentRoundQuotedCtn = (int) currentRoundOrderList.stream().filter(e -> SouOrderStatusEnum.SUBMISSION.equals(e.getOrderStatus())).count();

        if (vendorList.isEmpty()) {
            managementVO.setTrackingList(Collections.emptyList());
        } else {
            Map<Long/* vendorId */, SouOrder> souOrderMap = currentRoundOrderList.stream()
                    .collect(Collectors.toMap(SouOrder::getVendorId, Function.identity()));

            List<ExtPurInqSouOrderTrackingVO> trackingList = new ArrayList<>(vendorList.size());
            ExtPurInqSouOrderTrackingVO vo;
            SouOrder souOrder;
            for (SouVendor vendor : vendorList) {
                vo = new ExtPurInqSouOrderTrackingVO();
                trackingList.add(vo);

                BeanUtils.copyProperties(vendor, vo);
                souOrder = souOrderMap.get(vendor.getVendorId());
                if (souOrder != null) {
                    BeanUtils.copyProperties(souOrder, vo);
                    vo.setOrderStatus(souOrder.getOrderStatus());
                    vo.setOrderFileList(currentRoundOrderFileMap.get(souOrder.getOrderId()));

                    ExtPurInqSouOrder inqOrder = inqOrderMap.get(souOrder.getOrderId());
                    vo.setOrderByNickname(inqOrder.getOrderByNickname());
                    vo.setOrderPhone(inqOrder.getOrderPhone());
                } else {
                    vo.setOrderStatus(SouOrderStatusEnum.DRAFT);
                }

                // 配置为密封报价，接受报价中状态，trackingList不能返回下面字段
                if (projectStatus.equals(SouProjectStatusEnum.ACCEPT_ORDER) && needEncryptPrice.equals(Enable.Y)) {
                    vo.setOrderId(null);
                    vo.setOrderNo(null);
                    vo.setStandardNotaxTotalPrice(null);
                    vo.setStandardTaxTotalPrice(null);
                }
            }
            managementVO.setTrackingList(trackingList);
        }
        return managementVO;
    }

}
