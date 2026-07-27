package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.bond;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 招投标MQL - 供应商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/30
 */
@Data
public class MqlBidSouVendorBondForVendorWebVO {

    /** @see BidSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see BidSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see BidSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式")
    private String bondMethod;

    /** @see BidSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see BidSouProject#getBankAccountNum */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see BidSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see BidSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see BidSouVendorBond#getPayDate */
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    /** @see BidSouVendorBond#getPayDocId */
    @ApiModelProperty("缴纳文件ID")
    private Long payDocId;

    /** @see BidSouVendorBond#getPayFileName */
    @ApiModelProperty("缴纳文件名称")
    private String payFileName;

    public static MqlBidSouVendorBondForVendorWebVO convertBidVO(BidSouProject bidProject, @Nullable BidSouVendorBond bond) {
        MqlBidSouVendorBondForVendorWebVO vo = new MqlBidSouVendorBondForVendorWebVO();
        BeanUtils.copyProperties(bidProject, vo);
        if (bond != null) {
            BeanUtils.copyProperties(bond, vo);
        }
        return vo;
    }

}
