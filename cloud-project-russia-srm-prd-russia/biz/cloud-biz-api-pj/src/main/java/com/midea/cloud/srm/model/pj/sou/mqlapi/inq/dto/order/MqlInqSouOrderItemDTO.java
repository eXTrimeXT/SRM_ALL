package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouOrderItemDTO extends MqlSouOrderItemDTO {

    @ApiModelProperty("简易询价拓展表")
    private InqSouOrderItem inqSouOrderItem;

    @ApiModelProperty("附件")
    private List<SceneFile> orderItemFileList;

    @ApiModelProperty("账期")
    private List<InqSouOrderItemPayment> paymentList;

    // ---------------------------------------------------------- 下面是简易询价物料需求拓展数据(冗余信息) -------------------------------------------------------
    /** @see InqSouItem#getLadderType */
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)")
    private String ladderType;

    /** @see InqSouItem#getIsFormula */
    @ApiModelProperty("是否公式报价(Y/N)")
    private Enable isFormula;

    /** @see InqSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see InqSouItem#getFormulaId */
    @ApiModelProperty("公式ID")
    private Long formulaId;

    /** @see InqSouItem#getItemType */
    @ApiModelProperty("行类型[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /** @see InqSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see InqSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see InqSouItem#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;

}
