package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 标前交流通知-技术交流供应商表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-11-13
 */

@ApiModel(description = "标前交流通知-技术交流供应商表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_npm_pre_bid_notice_vendor")
public class PreBidNoticeVendor extends BaseEntity {
    @ApiModelProperty("主键")
    @TableId
    private Long bidNoticeVendorId;

    @ApiModelProperty("标前交流通知表主键")
    private Long bidNoticeId;

    @ApiModelProperty("供应商id")
    private Long vendorId;

    @ApiModelProperty("供应商编码/企业标识")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("报名联系人")
    private String contactName;

    @ApiModelProperty("供应商属性")
    private String vendorAttribute;

    @ApiModelProperty("报名联系手机号")
    private String phone;
}
