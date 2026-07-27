package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 简易询价openAPI - 报价行
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderItemDTO extends ApiSouOrderItemEditDTO {

    /** @see InqSouOrderItem#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息")
    protected String formulaAttrValues;

    @ApiModelProperty("附件")
    private List<SceneFile> orderItemFiles;

    @ApiModelProperty("账期")
    private List<InqSouOrderItemPayment> paymentList;

}
