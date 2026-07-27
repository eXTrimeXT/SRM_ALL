package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init.ApiSouItemSwaggerDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 物料需求信息保存 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouRequireInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "物料需求信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouRequireInfoSwaggerDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("报价类型(可不填)")
    protected SouOrderTypeEnum orderType;
    @ApiModelProperty(value = "物料需求信息", required = true)
    protected List<ApiSouItemSwaggerDTO> itemList;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

}
