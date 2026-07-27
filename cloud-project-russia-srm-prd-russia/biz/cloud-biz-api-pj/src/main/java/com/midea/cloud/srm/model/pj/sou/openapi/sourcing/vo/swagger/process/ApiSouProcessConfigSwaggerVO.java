package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程配置信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "流程配置信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigSwaggerVO extends SouProcessConfig {

    /**
     * @see BidSouProcessConfig#getBargainType
     * @see BrgSouProcessConfig#getBargainType
     */
    @ApiModelProperty("询价类型(仅用于招投标-bid/项目式询价-brg)")
    private BidSouTypeEnum bargainType;

    /**
     * @see BidSouProcessConfig#getBondManagement
     * @see BrgSouProcessConfig#getBondManagement
     * @see CompSouProcessConfig#getBondManagement
     */
    @ApiModelProperty("保证金管理(仅用于招投标-bid/项目式询价-brg)")
    protected Enable bondManagement;

    /** @see CompSouProcessConfig#getCompHall */
    @ApiModelProperty("竞价大厅(仅用于竞价-comp)")
    private Enable compHall;

}
