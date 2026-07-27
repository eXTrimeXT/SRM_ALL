package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouProjectEditDTO extends ApiSouProjectEditDTO {

    @TableField("INQUIRY_TYPE")
    @ApiModelProperty("询价类型[字典值: SOU_INQUIRY_TYPE]")
    private String inquiryType;

    @TableField("EXCHANGE_RATE_TYPE")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    @TableField("CURRENCY_EXCHANGE_DATE")
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

    @TableField("IS_TARGET_PRICE_OK")
    @ApiModelProperty("是否已设定目标价(Y/N)")
    private Enable isTargetPriceOk;

    @TableField("EXCLUDE_BLACK_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商(Y/N)")
    private Enable excludeBlackVendors;

    @TableField("EXCLUDE_NO_CURRENT_ORG_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除非本业务实体供应商(Y/N)")
    private Enable excludeNoCurrentOrgVendors;

    @TableField("EXCLUDE_ORG_QUIT_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除业务实体退出供应商(Y/N)")
    private Enable excludeOrgQuitVendors;

    @TableField("EXCLUDE_ORG_CATEGORY_STATUS")
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

}
