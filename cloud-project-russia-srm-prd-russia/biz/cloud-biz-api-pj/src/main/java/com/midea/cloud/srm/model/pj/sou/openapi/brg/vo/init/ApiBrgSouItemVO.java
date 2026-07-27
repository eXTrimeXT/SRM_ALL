package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init.ApiBrgSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 项目式询价openAPI - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouItemVO extends ApiSouItemVO {

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

    /** @see ApiBrgSouItemDTO#getItemFiles */
    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    public static List<ApiBrgSouItemVO> convertBrgVO(List<SouItem> souItemList,
                                                         List<BrgSouItem> brgItemList) {
        if (brgItemList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* souItemId */, BrgSouItem> brgItemMap = brgItemList.stream()
                .collect(Collectors.toMap(BrgSouItem::getSouItemId, Function.identity()));

        List<ApiBrgSouItemVO> voList = new ArrayList<>(brgItemList.size());
        for (SouItem souItem : souItemList) {
            ApiBrgSouItemVO vo = new ApiBrgSouItemVO();
            voList.add(vo);

            BeanUtils.copyProperties(souItem, vo);

            BrgSouItem brgSouItem = brgItemMap.get(souItem.getSouItemId());
            BeanUtils.copyProperties(brgSouItem, vo);
        }
        return voList;
    }

}
