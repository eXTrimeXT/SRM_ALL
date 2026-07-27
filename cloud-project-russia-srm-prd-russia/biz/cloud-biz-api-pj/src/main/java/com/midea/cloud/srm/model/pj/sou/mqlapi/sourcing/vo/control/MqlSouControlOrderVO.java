package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
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
 * MQL - 供应商报价信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouControlOrderVO extends SouOrder {

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("寻源供应商表ID")
    private Long souVendorId;

    /** @see SouVendor#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编号")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouVendor#getLinkmanName */
    @ApiModelProperty("联系人")
    private String linkmanName;

    /** @see SouVendor#getPhone */
    @ApiModelProperty("电话")
    private String phone;

    /** @see SouVendor#getEmail */
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("商务标附件")
    private List<MqlSouOrderFileVO> orderFileList;

    public static List<MqlSouControlOrderVO> convertMqlVO(SouProject project, SouRound currentRound, List<SouVendor> vendorList,
                                                          List<SouOrder> orderList, List<SouFileConfig> fileConfigList,
                                                          List<SouOrderFile> orderFileList) {
        if (vendorList.isEmpty()) { return Collections.emptyList(); }
        Map<Long/* vendorId */, SouOrder> orderMap = orderList.stream().collect(Collectors.toMap(SouOrder::getVendorId, Function.identity()));
        Map<Long/* vendorId */, List<SouOrderFile>> orderFileMap = orderFileList.stream()
                .filter(file -> SouFileConfigTypeEnum.BUSINESS_FILE.equals(file.getFileType()))
                .collect(Collectors.groupingBy(SouOrderFile::getVendorId));
        fileConfigList = fileConfigList.stream().filter(file -> SouFileConfigTypeEnum.BUSINESS_FILE.equals(file.getFileType()))
                .collect(Collectors.toList());

        List<MqlSouControlOrderVO> voList = new ArrayList<>(vendorList.size());
        for (SouVendor vendor : vendorList) {
            MqlSouControlOrderVO vo = new MqlSouControlOrderVO();
            voList.add(vo);

            BeanUtils.copyProperties(vendor, vo);

            SouOrder order = orderMap.get(vendor.getVendorId());
            if (order != null) {
                BeanUtils.copyProperties(order, vo);
            } else {
                vo.setOrderStatus(SouOrderStatusEnum.DRAFT);
                vo.setIsProxy(Enable.N);
            }

            boolean canShowOrderFiles = Enable.Y.equals(currentRound.getBusinessOpen()) || Enable.N.equals(project.getNeedEncryptPrice());
            if (canShowOrderFiles) {
                List<SouOrderFile> orderFiles = orderFileMap.get(vendor.getVendorId());
                vo.setOrderFileList(MqlSouOrderFileVO.convertMqlVO(fileConfigList, orderFiles != null ? orderFiles : new ArrayList<>()));
            }

            vo.setRound(project.getCurrentRound());
        }
        return voList;
    }

}
