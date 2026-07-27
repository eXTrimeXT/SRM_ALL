package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 竞价openAPI - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouItemVO extends ApiSouItemVO {

    /** @see CompSouItem#getOrderCurrency */
    @ApiModelProperty("报价币种(由采购商指定)")
    private String orderCurrency;

    /** @see CompSouItem#getTaxKey */
    @ApiModelProperty("税率编码(由采购商指定)")
    private String taxKey;

    /** @see CompSouItem#getTaxRate */
    @ApiModelProperty("税率(由采购商指定)")
    private BigDecimal taxRate;

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

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("付款条款")
    private List<CompSouItemPayment> paymentList;

    @SuppressWarnings("rawtypes")
    public static List<ApiCompSouItemVO> convertFromSouVO(List<ApiSouItemVO> souVOList) {
        if (CollectionUtils.isEmpty(souVOList)) { return Collections.emptyList(); }

        List<ApiCompSouItemVO> compVOList;
        if (souVOList instanceof Page) {
            compVOList = new Page<>();
            ((Page)compVOList).setTotal(((Page)souVOList).getTotal());
            ((Page)compVOList).setPageSize(((Page)souVOList).getPageSize());
            ((Page)compVOList).setPageNum(((Page)souVOList).getPageNum());
        } else {
            compVOList = new ArrayList<>(souVOList.size());
        }
        souVOList.forEach(souVO -> compVOList.add(SouObjectXUtil.convertTargetObj(souVO, ApiCompSouItemVO.class)));

        return compVOList;
    }

    @SuppressWarnings("rawtypes")
    public static List<ApiSouItemVO> convertSouVO(List<ApiCompSouItemVO> compVOList) {
        if (CollectionUtils.isEmpty(compVOList)) { return Collections.emptyList(); }

        List<ApiSouItemVO> souVOList;
        if (compVOList instanceof Page) {
            souVOList = new Page<>();
            ((Page)souVOList).setTotal(((Page)compVOList).getTotal());
            ((Page)souVOList).setPageSize(((Page)compVOList).getPageSize());
            ((Page)souVOList).setPageNum(((Page)compVOList).getPageNum());
        } else {
            souVOList = new ArrayList<>(compVOList.size());
        }
        compVOList.forEach(compVO -> souVOList.add(SouObjectXUtil.convertTargetObj(compVO, ApiSouItemVO.class)));

        return souVOList;
    }

}
