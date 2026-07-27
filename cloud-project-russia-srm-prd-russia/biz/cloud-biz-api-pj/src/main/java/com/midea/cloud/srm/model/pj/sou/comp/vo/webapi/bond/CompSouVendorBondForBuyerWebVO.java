package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouVendorBond;
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
 * 竞价 - 采购商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
public class CompSouVendorBondForBuyerWebVO {

    /** @see CompSouVendorBond#getVendorBondId */
    @ApiModelProperty("供应商缴纳ID")
    private Long vendorBondId;

    /** @see CompSouVendorBond#getVendorId */
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

    /** @see CompSouVendorBond#getPayDate */
    @ApiModelProperty("保证金缴纳时间")
    private Date payDate;

    /** @see CompSouVendorBond#getPayDocId */
    @ApiModelProperty("保证金缴纳文件ID")
    private Long payDocId;

    /** @see CompSouVendorBond#getPayFileName */
    @ApiModelProperty("保证金缴纳文件名称")
    private String payFileName;

    /** @see CompSouVendorBond#getHasPay */
    @ApiModelProperty("是否已缴纳")
    private Enable hasPay;

    public static List<CompSouVendorBondForBuyerWebVO> convertCompVO(List<CompSouVendorBond> vendorBondList,
                                                                     Map<Long/* vendorId */, SouVendor> vendorMap) {
        List<CompSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        CompSouVendorBondForBuyerWebVO vo;
        SouVendor vendor;
        for (CompSouVendorBond bond : vendorBondList) {
            vo = new CompSouVendorBondForBuyerWebVO();
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

    public static List<CompSouVendorBondForBuyerWebVO> convertCompVO(List<CompSouVendorBond> vendorBondList,
                                                                     Map<Long/* vendorId */, CompanyInfo> vendorMap,
                                                                     Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap) {
        List<CompSouVendorBondForBuyerWebVO> voList = new ArrayList<>(vendorBondList.size());

        CompSouVendorBondForBuyerWebVO vo;
        CompanyInfo companyInfo;
        for (CompSouVendorBond bond : vendorBondList) {
            vo = new CompSouVendorBondForBuyerWebVO();
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
