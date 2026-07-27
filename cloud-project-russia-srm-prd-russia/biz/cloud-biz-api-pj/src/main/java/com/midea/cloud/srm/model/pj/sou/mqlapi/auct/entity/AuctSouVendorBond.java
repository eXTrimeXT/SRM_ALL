package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouVendorBond;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouVendorBondStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价 MQL - 供应商保证金
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@TableName("scc_sou_auct_vendor_bond")
@EqualsAndHashCode(callSuper = true)
public class AuctSouVendorBond extends ExtAuctSouVendorBond {

    @TableId("VENDOR_BOND_ID")
    @ApiModelProperty("ID")
    private Long vendorBondId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("联系人名称")
    @TableField("LINKMAN_NAME")
    private String linkmanName;

    @ApiModelProperty("电话")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;

    @TableField("PAY_DATE")
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    @TableField("PAY_DOC_ID")
    @ApiModelProperty("缴纳证明文件ID")
    private Long payDocId;

    @TableField("PAY_FILE_NAME")
    @ApiModelProperty("缴纳证明文件名称")
    private String payFileName;

    @TableField("BOND_STATUS")
    @ApiModelProperty("保证金缴纳状态")
    private AuctSouVendorBondStatus bondStatus;

}
