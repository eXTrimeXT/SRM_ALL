package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简易询价 - 评选管理信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2021/10/28
 */
@ApiModel(description = "简易询价 - 评选管理信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InqSouSelectionManagementWebVO {

    @ApiModelProperty("询价单")
    private ApiInqSouProjectVO header;

    @ApiModelProperty("当前轮次该报价供应商数量")
    private Integer currentRoundTotalCtn;

    @ApiModelProperty("当前轮次已报价供应商数量")
    private Integer currentRoundQuotedCtn;

    @ApiModelProperty("供应商报价单信息集合")
    private List<InqSouOrderTrackingWebVO> trackingList;

    /**
     * 转换方法
     *
     * @param projectVO                   询价单信息
     * @param currentRoundQuoteHeaderList 本轮的供应商报价信息
     * @param vendorList                  本轮应报价供应商信息
     * @param souProject                  寻源核心-询价单
     */
    public static InqSouSelectionManagementWebVO convert(ApiInqSouProjectVO projectVO, List<SouOrder> currentRoundQuoteHeaderList, List<SouVendor> vendorList, SouProject souProject) {

        SouProjectStatusEnum projectStatus = souProject.getProjectStatus();
        Enable needEncryptPrice = souProject.getNeedEncryptPrice();

        InqSouSelectionManagementWebVO managementVO = new InqSouSelectionManagementWebVO();
        managementVO.header = projectVO;
        // 受邀供应商数量
        managementVO.currentRoundTotalCtn = vendorList.size();
        // 已提交报价的供应商数量
        managementVO.currentRoundQuotedCtn = (int) currentRoundQuoteHeaderList.stream().filter(e -> SouOrderStatusEnum.SUBMISSION.equals(e.getOrderStatus())).count();

        if (vendorList.isEmpty()) {
            managementVO.setTrackingList(Collections.emptyList());
        } else {
            Map<Long/* vendorId */, SouOrder> souOrderMap = currentRoundQuoteHeaderList.stream()
                    .collect(Collectors.toMap(SouOrder::getVendorId, Function.identity()));

            List<InqSouOrderTrackingWebVO> trackingList = new ArrayList<>(vendorList.size());
            InqSouOrderTrackingWebVO vo;
            SouOrder souOrder;
            for (SouVendor vendor : vendorList) {
                vo = new InqSouOrderTrackingWebVO();
                trackingList.add(vo);

                BeanUtils.copyProperties(vendor, vo);
                souOrder = souOrderMap.get(vendor.getVendorId());
                if (souOrder != null) {
                    BeanUtils.copyProperties(souOrder, vo);
                    vo.setOrderStatus(souOrder.getOrderStatus());
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
