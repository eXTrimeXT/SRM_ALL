package com.midea.cloud.srm.model.sou.openapi.inq.vo.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderItemVO extends ApiSouOrderItemVO {

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

    /** @see InqSouOrderItem#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息")
    private String formulaAttrValues;

    @ApiModelProperty("物料需求附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("报价附件")
    private List<SceneFile> orderItemFiles;

    @ApiModelProperty("账期信息")
    private List<InqSouOrderItemPayment> paymentList;

    // -------------------------------- 长城询比价报价明细额外信息 -----------------------------------
    /** @see ExtInqSouItem#getExtMaterialModel */
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    /** @see ExtInqSouItem#getExtBrand */
    @ApiModelProperty("品牌")
    private String extBrand;

    /** @see ExtInqSouItem#getExtAreaId */
    @ApiModelProperty("区域ID")
    private String extAreaId;

    /** @see ExtInqSouItem#getExtAreaCode */
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    /** @see ExtInqSouItem#getExtAreaName */
    @ApiModelProperty("区域名称")
    private String extAreaName;

    /** @see ExtInqSouItem#getExtSourceFromLineIds */
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    /** @see ExtInqSouItem#getHasClose */
    @ApiModelProperty("是否关闭")
    private Enable hasClose;

    /** @see ExtInqSouOrderItem#getInvoiceType */
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    /** @see ExtInqSouOrderItem#getPriceTaxTotal */
    @ApiModelProperty("价税合计")
    private BigDecimal priceTaxTotal;

    /** @see ExtInqSouOrderItem#getAdvancePaymentRemark */
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;

    /** @see ExtInqSouOrderItem#getSpecialPaymentRemark */
    @ApiModelProperty("特殊付款说明")
    private String specialPaymentRemark;

    /** @see ExtInqSouOrderItem#getExtLeadTime */
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    /** @see ExtInqSouOrderItem#getExtWarrantyPeriod */
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @ApiModelProperty("中标原因")
    private String extWinReason;

    private String remark;

}
