package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.bond;

import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目式询价 - 供应商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/27
 */
@Data
public class BrgSouVendorBondForVendorWebVO {

    /** @see BrgSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see BrgSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see BrgSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式")
    private String bondMethod;

    /** @see BrgSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see BrgSouProject#getBankAccountNum */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see BrgSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see BrgSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see BrgSouVendorBond#getPayDate */
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    /** @see BrgSouVendorBond#getPayDocId */
    @ApiModelProperty("缴纳文件ID")
    private Long payDocId;

    /** @see BrgSouVendorBond#getPayFileName */
    @ApiModelProperty("缴纳文件名称")
    private String payFileName;

    public static BrgSouVendorBondForVendorWebVO convertBrgVO(BrgSouProject brgProject,
                                                              @Nullable BrgSouVendorBond bond) {
        BrgSouVendorBondForVendorWebVO vo = new BrgSouVendorBondForVendorWebVO();
        BeanUtils.copyProperties(brgProject, vo);
        if (bond != null) {
            BeanUtils.copyProperties(bond, vo);
        }
        return vo;
    }

}
