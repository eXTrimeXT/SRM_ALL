package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.bond;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞价 - 供应商查看保证金缴纳信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
public class CompSouVendorBondForVendorWebVO {

    /** @see CompSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see CompSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see CompSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式")
    private String bondMethod;

    /** @see CompSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see CompSouProject#getBankAccountNum */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see CompSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see CompSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see CompSouVendorBond#getPayDate */
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    /** @see CompSouVendorBond#getPayDocId */
    @ApiModelProperty("缴纳文件ID")
    private Long payDocId;

    /** @see CompSouVendorBond#getPayFileName */
    @ApiModelProperty("缴纳文件名称")
    private String payFileName;

    public static CompSouVendorBondForVendorWebVO convertCompVO(CompSouProject compProject,
                                                                @Nullable CompSouVendorBond bond) {
        CompSouVendorBondForVendorWebVO vo = new CompSouVendorBondForVendorWebVO();
        BeanUtils.copyProperties(compProject, vo);
        if (bond != null) {
            BeanUtils.copyProperties(bond, vo);
        }
        return vo;
    }

}
