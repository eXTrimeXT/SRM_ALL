package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouVendorBond;
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
 * 项目式询价 - 采购商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/27
 */
@Data
public class BrgSouVendorBondForBuyerWebVO {

    /** @see BrgSouVendorBond#getVendorBondId */
    @ApiModelProperty("供应商缴纳ID")
    private Long vendorBondId;

    /** @see BrgSouVendorBond#getVendorId */
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

    /** @see BrgSouVendorBond#getPayDate */
    @ApiModelProperty("保证金缴纳时间")
    private Date payDate;

    /** @see BrgSouVendorBond#getPayDocId */
    @ApiModelProperty("保证金缴纳文件ID")
    private Long payDocId;

    /** @see BrgSouVendorBond#getPayFileName */
    @ApiModelProperty("保证金缴纳文件名称")
    private String payFileName;

    /** @see BrgSouVendorBond#getHasPay */
    @ApiModelProperty("是否已缴纳")
    private Enable hasPay;

    public static List<BrgSouVendorBondForBuyerWebVO> convertBrgVO(List<BrgSouVendorBond> vendorBondList,
                                                                   Map<Long/* vendorId */, SouVendor> vendorMap) {
        List<BrgSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        BrgSouVendorBondForBuyerWebVO vo;
        SouVendor vendor;
        for (BrgSouVendorBond bond : vendorBondList) {
            vo = new BrgSouVendorBondForBuyerWebVO();
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

    public static List<BrgSouVendorBondForBuyerWebVO> convertBrgVO(List<BrgSouVendorBond> vendorBondList,
                                                                   Map<Long/* vendorId */, CompanyInfo> vendorMap,
                                                                   Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap) {
        List<BrgSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        BrgSouVendorBondForBuyerWebVO vo;
        CompanyInfo companyInfo;
        for (BrgSouVendorBond bond : vendorBondList) {
            vo = new BrgSouVendorBondForBuyerWebVO();
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
