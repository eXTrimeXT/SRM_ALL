package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.formula.entity.BaseMaterialPrice;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouBaseMaterialPrice;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞价 - 缓存基材价格
 * PS: 来源于 {@link BaseMaterialPrice}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_comp_base_mt_price")
@ApiModel("竞价.缓存基材价格")
public class CompSouBaseMaterialPrice extends ExtCompSouBaseMaterialPrice {

    @ApiModelProperty("ID")
    @TableId("COMP_BASE_MATERIAL_PRICE_ID")
    private Long compBaseMaterialPriceId;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouProject#getCurrentRound */
    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    /** @see EssentialFactor#getEssentialFactorId */
    @ApiModelProperty("公式元素ID")
    @TableField("FACTOR_ID")
    private Long factorId;

    /** @see BaseMaterialPrice#getBaseMaterialPriceId */
    @ApiModelProperty("基本材料价格ID")
    @TableField("BASE_MATERIAL_PRICE_ID")
    private Long baseMaterialPriceId;

    /** @see BaseMaterialPrice#getBaseMaterialPriceStatus */
    @ApiModelProperty("状态")
    @TableField("BASE_MATERIAL_PRICE_STATUS")
    private String baseMaterialPriceStatus;

    /** @see BaseMaterialPrice#getBaseMaterialPriceType */
    @ApiModelProperty("基价类型")
    @TableField("BASE_MATERIAL_PRICE_TYPE")
    private String baseMaterialPriceType;

    /** @see BaseMaterialPrice#getActiveDateFrom */
    @ApiModelProperty("有效期起始日期")
    @TableField("ACTIVE_DATE_FROM")
    private Date activeDateFrom;

    /** @see BaseMaterialPrice#getActiveDateTo */
    @ApiModelProperty("有效期结束日期")
    @TableField("ACTIVE_DATE_TO")
    private Date activeDateTo;

    /** @see BaseMaterialPrice#getBaseMaterialId */
    @ApiModelProperty("基材ID")
    @TableField("BASE_MATERIAL_ID")
    private Long baseMaterialId;

    /** @see BaseMaterialPrice#getBaseMaterialCode */
    @ApiModelProperty("基材编码")
    @TableField("BASE_MATERIAL_CODE")
    private String baseMaterialCode;

    /** @see BaseMaterialPrice#getBaseMaterialName */
    @ApiModelProperty("基材名称")
    @TableField("BASE_MATERIAL_NAME")
    private String baseMaterialName;

    /** @see BaseMaterialPrice#getBaseMaterialUnit */
    @ApiModelProperty("单位")
    @TableField("BASE_MATERIAL_UNIT")
    private String baseMaterialUnit;

    /** @see BaseMaterialPrice#getCurrencyType */
    @ApiModelProperty("币种")
    @TableField("CURRENCY_TYPE")
    private String currencyType;

    /** @see BaseMaterialPrice#getBaseMaterialPrice */
    @ApiModelProperty("价格")
    @TableField("BASE_MATERIAL_PRICE")
    private BigDecimal baseMaterialPrice;

    /** @see BaseMaterialPrice#getPriceFrom */
    @ApiModelProperty("数据来源")
    @TableField("PRICE_FROM")
    private String priceFrom;

}
