package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
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
public class ApiCompSouProcessConfigEditDTO extends SouProcessConfig {

    /** @see CompSouProcessConfig#getBondManagement */
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;
    /** @see CompSouProcessConfig#getCompHall */
    @ApiModelProperty("竞价大厅")
    protected Enable compHall;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (bondManagement == null) {
            bondManagement = Enable.N;
        }
        compHall = Enable.Y;
        souType = SouTypeEnum.comp.name();
    }

}
