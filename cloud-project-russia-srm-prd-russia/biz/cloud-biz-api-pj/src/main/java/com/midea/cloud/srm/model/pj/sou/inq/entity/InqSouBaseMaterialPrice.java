package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.formula.entity.BaseMaterialPrice;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouBaseMaterialPrice;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 简易询价-基价信息
 * PS: 来源于{@link BaseMaterialPrice}，用于记录下当前询价单在哪一轮次需要使用的基价信息
 * 基价信息可能随时变动，但询价单中如果采用公式报价，具体的某个基价应该在当前轮次中保持不变
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/06
 */
@ApiModel(description = "简易询价-基价信息")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_inq_base_mt_price")
public class InqSouBaseMaterialPrice extends ExtInqSouBaseMaterialPrice {
    /**
     * 简易询价基价信息ID
     */
    @ApiModelProperty("简易询价基价信息ID")
    @TableId("INQ_MT_PRICE_ID")
    private Long inqMtPriceId;

    /**
     * 寻源核心-询价单ID
     *
     * @see SouProject#getProjectId()
     */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /**
     * 轮次
     *
     * @see SouProject#getCurrentRound()
     * @see SouRound#getRound()
     */
    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    /**
     * 公式元素ID
     *
     * @see EssentialFactor#getEssentialFactorId
     */
    @ApiModelProperty("公式元素ID")
    @TableField("FACTOR_ID")
    private Long factorId;

    /**
     * 基本材料价格ID
     *
     * @see BaseMaterialPrice#getBaseMaterialPriceId
     */
    @ApiModelProperty("基本材料价格ID")
    @TableField("BASE_MATERIAL_PRICE_ID")
    private Long baseMaterialPriceId;

    /**
     * 状态
     *
     * @see BaseMaterialPrice#getBaseMaterialPriceStatus
     */
    @ApiModelProperty("状态")
    @TableField("BASE_MATERIAL_PRICE_STATUS")
    private String baseMaterialPriceStatus;

    /**
     * 基价类型
     *
     * @see BaseMaterialPrice#getBaseMaterialPriceType
     */
    @ApiModelProperty("基价类型")
    @TableField("BASE_MATERIAL_PRICE_TYPE")
    private String baseMaterialPriceType;

    /**
     * 有效期起始日期
     *
     * @see BaseMaterialPrice#getActiveDateFrom
     */
    @ApiModelProperty("有效期起始日期")
    @TableField("ACTIVE_DATE_FROM")
    private Date activeDateFrom;

    /**
     * 有效期结束日期
     *
     * @see BaseMaterialPrice#getActiveDateTo
     */
    @ApiModelProperty("有效期结束日期")
    @TableField("ACTIVE_DATE_TO")
    private Date activeDateTo;

    /**
     * 基材ID
     *
     * @see BaseMaterialPrice#getBaseMaterialId
     */
    @ApiModelProperty("基材ID")
    @TableField("BASE_MATERIAL_ID")
    private Long baseMaterialId;

    /**
     * 基材编码
     *
     * @see BaseMaterialPrice#getBaseMaterialCode
     */
    @ApiModelProperty("基材编码")
    @TableField("BASE_MATERIAL_CODE")
    private String baseMaterialCode;

    /**
     * 基材名称
     *
     * @see BaseMaterialPrice#getBaseMaterialName
     */
    @ApiModelProperty("基材名称")
    @TableField("BASE_MATERIAL_NAME")
    private String baseMaterialName;

    /**
     * 单位
     *
     * @see BaseMaterialPrice#getBaseMaterialUnit
     */
    @ApiModelProperty("单位")
    @TableField("BASE_MATERIAL_UNIT")
    private String baseMaterialUnit;

    /**
     * 币种
     *
     * @see BaseMaterialPrice#getCurrencyType
     */
    @ApiModelProperty("币种")
    @TableField("CURRENCY_TYPE")
    private String currencyType;

    /**
     * 价格
     *
     * @see BaseMaterialPrice#getBaseMaterialPrice
     */
    @ApiModelProperty("价格")
    @TableField("BASE_MATERIAL_PRICE")
    private BigDecimal baseMaterialPrice;

    /**
     * 数据来源
     *
     * @see BaseMaterialPrice#getPriceFrom
     */
    @ApiModelProperty("数据来源")
    @TableField("PRICE_FROM")
    private String priceFrom;

}
