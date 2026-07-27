package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 评选列表信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouSelectQueryVO extends MqlSouOrderItemVO {

    @ApiModelProperty("寻源单")
    private MqlSouProjectVO souProject;
    @ApiModelProperty("供应商")
    private SouVendor souVendor;

}
