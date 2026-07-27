package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.comp.entity.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 竞价openAPI - 评选列表查询结果
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouSelectQueryVO extends ApiSouSelectQueryVO {

    /** @see CompSouCurrency#getPriceTax */
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    /** @see CompSouItem#getStartOrderNotaxPrice */
    @ApiModelProperty("起拍价(原币未税)")
    private BigDecimal startOrderNotaxPrice;

    /** @see CompSouItem#getStartOrderTaxPrice */
    @ApiModelProperty("起拍价(原币含税)")
    private BigDecimal startOrderTaxPrice;

    /** @see CompSouItem#getStartStandardNotaxPrice */
    @ApiModelProperty("起拍价(本币未税)")
    private BigDecimal startStandardNotaxPrice;

    /** @see CompSouItem#getStartStandardTaxPrice */
    @ApiModelProperty("起拍价(本币未税)")
    private BigDecimal startStandardTaxPrice;

    /** @see CompSouItem#getStartStandardGroupNotaxPrice */
    @ApiModelProperty("起拍价(本币未税-组合)")
    private BigDecimal startStandardGroupNotaxPrice;

    /** @see CompSouItem#getStartStandardGroupTaxPrice */
    @ApiModelProperty("起拍价(本币含税-组合)")
    private BigDecimal startStandardGroupTaxPrice;

    /** @see CompSouItem#getRowType */
    @ApiModelProperty("行类型")
    private String rowType;

    /** @see CompSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see CompSouItem#getPriceType */
    @ApiModelProperty("价格类型")
    private String priceType;

    /** @see CompSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see CompSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    /** @see CompSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人ID")
    private Long submitById;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人账号")
    private String submitBy;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    /** @see CompSouItemHis#getPricePercent */
    @ApiModelProperty("本轮次该物料涨降幅百分比(最新价与起拍价的对比)")
    private BigDecimal pricePercent;

    /** @see CompSouItemHis#getOrderNotaxPriceAmount */
    @ApiModelProperty("原币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal orderNotaxPriceAmount;

    /** @see CompSouItemHis#getOrderTaxPriceAmount */
    @ApiModelProperty("原币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal orderTaxPriceAmount;

    /** @see CompSouItemHis#getStandardNotaxPriceAmount */
    @ApiModelProperty("本币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal standardNotaxPriceAmount;

    /** @see CompSouItemHis#getStandardTaxPriceAmount */
    @ApiModelProperty("本币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal standardTaxPriceAmount;

    /** @see CompSouItemHis#getLatestOrderNotaxPrice */
    @ApiModelProperty("原币未税最新报价")
    private BigDecimal latestOrderNotaxPrice;

    /** @see CompSouItemHis#getLatestOrderTaxPrice */
    @ApiModelProperty("原币含税最新报价")
    private BigDecimal latestOrderTaxPrice;

    /** @see CompSouItemHis#getLatestStandardNotaxPrice */
    @ApiModelProperty("本币未税最新报价")
    private BigDecimal latestStandardNotaxPrice;

    /** @see CompSouItemHis#getLatestStandardTaxPrice */
    @ApiModelProperty("本币含税最新报价")
    private BigDecimal latestStandardTaxPrice;

    /** @see CompSouItemHis#getLatestStandardGroupNotaxPrice */
    @ApiModelProperty("本币组合未税最新报价")
    private BigDecimal latestStandardGroupNotaxPrice;

    /** @see CompSouItemHis#getLatestStandardGroupTaxPrice */
    @ApiModelProperty("本币组合含税最新报价")
    private BigDecimal latestStandardGroupTaxPrice;

    @ApiModelProperty("针对该物料的报价")
    private BigDecimal totalOrderNotaxPrice;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("账期")
    private List<CompSouItemPayment> paymentList;

    /**
     * 原型字段
     */
    @ApiModelProperty("计算供应商及金额")
    private ApiSouSelectResultVO apiSouSelectVendorPriceDTO;

    /**
     * 单价*月产量
     */
    @ApiModelProperty("月总金额")
    private BigDecimal monthlyTotalAmount;

}
