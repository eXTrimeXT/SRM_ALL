package com.midea.cloud.srm.model.pj.sou.bid.vo.webapi.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouVendorBond;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标 - 采购商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BidSouVendorBondForBuyerWebVO extends BidSouVendorBond {

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * @see SouVendor#getLinkmanName
     * @see ContactInfo#getContactName
     */
    @ApiModelProperty("联系人")
    private String linkManName;

    /**
     * @see SouVendor#getPhone
     * @see ContactInfo#getCeeaContactMethod
     */
    @ApiModelProperty("电话")
    private String phone;

    /**
     * @see SouVendor#getEmail
     * @see ContactInfo#getEmail
     */
    @ApiModelProperty("邮箱")
    private String email;

    /** @see BidSouVendorBond#getPayDate */
    @ApiModelProperty("保证金缴纳时间")
    private Date payDate;

    /** @see BidSouVendorBond#getPayDocId */
    @ApiModelProperty("保证金缴纳文件ID")
    private Long payDocId;

    /** @see BidSouVendorBond#getPayFileName */
    @ApiModelProperty("保证金缴纳文件名称")
    private String payFileName;

    /** @see BidSouVendorBond#getHasPay */
    @ApiModelProperty("是否已缴纳")
    private Enable hasPay;

    public static List<BidSouVendorBondForBuyerWebVO> convertBidVO(List<BidSouVendorBond> vendorBondList,
                                                                   List<SouVendor> vendorList) {
        List<BidSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        Map<Long/* vendorId */, BidSouVendorBond> bondMap = vendorBondList.stream()
                .collect(Collectors.toMap(BidSouVendorBond::getVendorId, Function.identity()));

        for (SouVendor vendor : vendorList) {
            BidSouVendorBondForBuyerWebVO vo = new BidSouVendorBondForBuyerWebVO();
            voList.add(vo);

            BidSouVendorBond bond = bondMap.get(vendor.getVendorId());
            if (bond == null) {
                BeanUtils.copyProperties(vendor, vo);
                vo.setHasPay(Enable.N);
            } else {
                BeanUtils.copyProperties(bond, vo);
                BeanUtils.copyProperties(vendor, vo);
            }
        }

        return voList;
    }

}
