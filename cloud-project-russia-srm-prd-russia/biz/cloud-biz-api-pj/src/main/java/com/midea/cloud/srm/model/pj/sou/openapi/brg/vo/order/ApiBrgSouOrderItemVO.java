package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouItem;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 项目式询价openAPI - 报价行信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderItemVO extends ApiSouOrderItemVO {

    /** @see BrgSouItem#getOrderType */
    @ApiModelProperty("报价方式(冗余字段)")
    private SouOrderTypeEnum orderType;

    /** @see BrgSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see BrgSouItem#getFormulaId */
    @ApiModelProperty("公式id")
    private Long formulaId;

    /** @see BrgSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see BrgSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see BrgSouItem#getTargetPrice */
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

    /** @see BrgSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see BrgSouItem#getPriceType */
    @ApiModelProperty("价格类型[字典值: PRICE_TYPE]")
    private String priceType;

    /** @see BrgSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see BrgSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    /** @see BrgSouItem#getTransportType */
    @ApiModelProperty("运输方式[字典值: TRANSF_TYPE]")
    private String transportType;

    /** @see BrgSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    /** @see BrgSouOrderItem#getMqo */
    @ApiModelProperty("最小订单量")
    private String mqo;

    /** @see BrgSouOrderItem#getLeadTime */
    @ApiModelProperty("供货周期")
    private String leadTime;

    /** @see BrgSouOrderItem#getDeliverDate */
    @ApiModelProperty("承诺交货期")
    private Date deliverDate;

    /** @see BrgSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("账期")
    private List<BrgSouOrderItemPayment> paymentList;

    @SuppressWarnings("rawtypes")
    public static List<ApiBrgSouOrderItemVO> convertBrgVO(List<ApiSouOrderItemVO> voList) {
        if (voList.isEmpty()) { return Collections.emptyList(); }
        List<ApiBrgSouOrderItemVO> brgVOList; {
            if (voList instanceof Page) {
                brgVOList = new Page<>();
                ((Page)brgVOList).setTotal(((Page)voList).getTotal());
                ((Page)brgVOList).setPageNum(((Page)voList).getPageNum());
                ((Page)brgVOList).setPageSize(((Page)voList).getPageSize());
            } else {
                brgVOList = new ArrayList<>(voList.size());
            }
        }
        voList.forEach(vo -> brgVOList.add(SouObjectXUtil.convertTargetObj(vo, ApiBrgSouOrderItemVO.class)));

        return brgVOList;
    }

}
