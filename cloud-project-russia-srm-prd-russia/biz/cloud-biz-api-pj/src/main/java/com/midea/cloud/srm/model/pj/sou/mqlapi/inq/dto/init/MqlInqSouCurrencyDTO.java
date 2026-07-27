package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouCurrencyDTO extends SouCurrency {

    @ApiModelProperty("简易询价拓展表")
    private InqSouCurrency inqSouCurrency;

}
