package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouVendorBond;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 招投标MQL - 采购商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/30
 */
@Data
public class MqlBidSouVendorBondForBuyerWebVO {

    /** @see BidSouVendorBond#getVendorBondId */
    @ApiModelProperty("供应商缴纳ID")
    private Long vendorBondId;

    /** @see BidSouVendorBond#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

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

    public static List<MqlBidSouVendorBondForBuyerWebVO> convertBidVO(List<BidSouVendorBond> vendorBondList,
                                                                      Map<Long/* vendorId */, SouVendor> vendorMap) {
        List<MqlBidSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        MqlBidSouVendorBondForBuyerWebVO vo;
        SouVendor vendor;
        for (BidSouVendorBond bond : vendorBondList) {
            vo = new MqlBidSouVendorBondForBuyerWebVO();
            voList.add(vo);
            BeanUtils.copyProperties(bond, vo);
            // 供应商编码/名称
            vendor = vendorMap.get(bond.getVendorId());
            vo.setVendorCode(vendor.getVendorCode());
            vo.setVendorName(vendor.getVendorName());
            // 联系人/电话/邮箱
            vo.setEmail(vendor.getEmail());
            vo.setPhone(vendor.getPhone());
            vo.setLinkManName(vendor.getLinkmanName());
        }

        return voList;
    }

    public static List<MqlBidSouVendorBondForBuyerWebVO> convertBidVO(List<BidSouVendorBond> vendorBondList,
                                                                      Map<Long/* vendorId */, CompanyInfo> vendorMap,
                                                                      Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap) {
        List<MqlBidSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        MqlBidSouVendorBondForBuyerWebVO vo;
        CompanyInfo companyInfo;
        for (BidSouVendorBond bond : vendorBondList) {
            vo = new MqlBidSouVendorBondForBuyerWebVO();
            voList.add(vo);
            BeanUtils.copyProperties(bond, vo);
            // 供应商编码/名称
            companyInfo = vendorMap.get(bond.getVendorId());
            if (companyInfo != null) {
                vo.setVendorCode(companyInfo.getCompanyCode());
                vo.setVendorName(companyInfo.getCompanyName());
            }
            // 联系人/电话/邮箱
            List<ContactInfo> infos = contactInfoMap.get(bond.getVendorId());
            if (CollectionUtils.isNotEmpty(infos)) {
                ContactInfo info = null;
                for (ContactInfo cc : infos) {
                    if (Enable.Y.name().equals(cc.getCeeaDefaultContact())) {
                        // 默认联系人
                        info = cc;
                        break;
                    }
                }
                if (info == null) {
                    info = infos.get(0);
                }
                vo.setEmail(info.getEmail());
                vo.setPhone(info.getPhoneNumber());
                vo.setLinkManName(info.getContactName());
            }
        }

        return voList;
    }

}
