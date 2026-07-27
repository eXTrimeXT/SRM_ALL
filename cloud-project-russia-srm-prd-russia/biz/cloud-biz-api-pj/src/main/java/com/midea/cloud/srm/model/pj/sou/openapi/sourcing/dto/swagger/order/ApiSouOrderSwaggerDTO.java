package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.order;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.order.ApiSouOrderItemSwaggerDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 可用币种 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouOrderDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "供应商报价信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderSwaggerDTO extends ApiSouOrderEditDTO {

    @ApiModelProperty("报价明细")
    protected List<ApiSouOrderItemSwaggerDTO> orderItemList;
    @ApiModelProperty("报价附件")
    @Nullable
    protected List<ApiSouOrderFileDTO> orderFileList;
    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
