package com.midea.cloud.srm.model.pj.sou.bid.dto.webapi.order;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 招投标 - 供应商报价结果查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BidSouOrderResultQueryWebDTO extends ApiSouOrderResultQueryDTO {

    @ApiModelProperty("供应商名称")
    private String vendorName;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否显示中标供应商信息")
    private Boolean showWinVendor = false;

    @Override
    public void formatParams() {
        super.formatParams();
        vendorName = StringUtils.trimToNull(vendorName);
    }

}
