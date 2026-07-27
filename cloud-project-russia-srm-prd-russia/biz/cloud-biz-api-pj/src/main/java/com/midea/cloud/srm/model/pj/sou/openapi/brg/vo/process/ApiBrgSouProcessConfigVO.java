package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.brg.enums.BrgSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目式询价openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouProcessConfigVO extends SouProcessConfig {

    /** @see BrgSouProcessConfig#getBargainType */
    @ApiModelProperty("询价类型")
    private BrgSouTypeEnum bargainType;

    /** @see BrgSouProcessConfig#getBondManagement */
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

}
