package com.midea.cloud.srm.model.pj.sou.brg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.brg.entity.ExtBrgSouVendorBond;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 项目式询价 - 供应商保证金缴纳信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_brg_vendor_bond")
@ApiModel("询价供应商保证金缴纳信息")
public class BrgSouVendorBond extends ExtBrgSouVendorBond {

    @TableId("VENDOR_BOND_ID")
    @ApiModelProperty("ID")
    private Long vendorBondId;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

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
