package com.midea.cloud.srm.model.pj.sou.bid.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.bid.entity.ExtBidSouVendorBond;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 招投标 - 供应商保证金缴纳信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_bid_vendor_bond")
@ApiModel("询价供应商保证金缴纳信息")
public class BidSouVendorBond extends ExtBidSouVendorBond {

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

    @TableField("HAS_PAY")
    @ApiModelProperty("是否已缴纳")
    private Enable hasPay;

}
