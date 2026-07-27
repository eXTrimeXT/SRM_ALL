package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.process.ApiBidSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.process.ApiBrgSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.process.ApiCompSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程配置保存 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouProcessConfigEditDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "流程配置保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigEditSwaggerDTO extends SouProcessConfig {

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

    /**
     * @see ApiBidSouProcessConfigEditDTO#getBargainType
     * @see ApiBrgSouProcessConfigEditDTO#getBargainType
     */
    @ApiModelProperty("询价类型(仅用于招投标-bid/项目式询价-brg)")
    protected String bargainType;

    /**
     * @see ApiBidSouProcessConfigEditDTO#getBondManagement
     * @see ApiBrgSouProcessConfigEditDTO#getBondManagement
     * @see ApiCompSouProcessConfigEditDTO#getBondManagement
     */
    @ApiModelProperty("保证金管理(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    protected Enable bondManagement;

    /** @see ApiCompSouProcessConfigEditDTO#getCompHall */
    @ApiModelProperty("竞价大厅(仅用于竞价-comp)")
    protected Enable compHall;

}
