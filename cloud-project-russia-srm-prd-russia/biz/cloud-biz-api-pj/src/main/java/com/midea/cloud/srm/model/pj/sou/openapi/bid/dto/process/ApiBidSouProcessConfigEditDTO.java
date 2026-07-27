package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招投标openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouProcessConfigEditDTO extends SouProcessConfig {

    /** @see BidSouProcessConfig#getBargainType */
    @ApiModelProperty("询价类型")
    protected BidSouTypeEnum bargainType;
    /** @see BidSouProcessConfig#getBondManagement */
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (bargainType == null) {
            throw new IllegalArgumentException("请选择询价类型");
        }
        switch (bargainType) {
            //商务标
            case BUSINESS:
                businessManagement = Enable.Y;
                techManagement = Enable.N;
                break;
                //技术+商务
            case TECHNOLOGY_BUSINESS:
                businessManagement = Enable.Y;
                techManagement = Enable.Y;
                break;
            default:
                throw new IllegalArgumentException("不支持的询价类型");
        }
        if (bondManagement == null) {
            bondManagement = Enable.N;
        }
        souType = SouTypeEnum.bid.name();
    }

}
