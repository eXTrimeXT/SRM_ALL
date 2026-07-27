package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招投标openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouProcessConfigVO extends SouProcessConfig {

    /** @see BidSouProcessConfig#getBargainType */
    @ApiModelProperty("询价类型")
    private BidSouTypeEnum bargainType;

    /** @see BidSouProcessConfig#getBondManagement */
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

}
