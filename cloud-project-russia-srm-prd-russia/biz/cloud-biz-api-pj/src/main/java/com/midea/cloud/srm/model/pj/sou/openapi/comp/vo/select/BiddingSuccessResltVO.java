package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSelectFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author lcw
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiddingSuccessResltVO extends BaseObjectX {

    @ApiModelProperty("sou_item_id")
    private Long souItemId;

    @ApiModelProperty("WIN_VENDOR_ID")
    private Long winVendorId;

    @ApiModelProperty("中标价格")
    private BigDecimal winVendorPrice;

    @ApiModelProperty("中标单位")
    private String winVendorName;

    @ApiModelProperty("备注")
    private String winNoticeRemark;

    @ApiModelProperty("联系人")
    private String linkmanName;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("物资名称")
    private String itemDesc;

    @ApiModelProperty("计量单位")
    private String meteringUnit;

    @ApiModelProperty("单位名称")
    private String affiliatedUnit;

    @ApiModelProperty("招标负责人")
    private String createdFullName;

    @ApiModelProperty("项目编号")
    private String souNo;


}
