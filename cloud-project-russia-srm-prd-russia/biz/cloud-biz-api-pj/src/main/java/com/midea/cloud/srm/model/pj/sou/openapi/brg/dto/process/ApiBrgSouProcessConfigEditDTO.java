package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.brg.enums.BrgSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目式询价openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/22
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouProcessConfigEditDTO extends SouProcessConfig {

    /** @see BrgSouProcessConfig#getBargainType */
    @ApiModelProperty("询价类型")
    protected BrgSouTypeEnum bargainType;
    /** @see BrgSouProcessConfig#getBondManagement */
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
        souType = SouTypeEnum.brg.name();
    }

}
