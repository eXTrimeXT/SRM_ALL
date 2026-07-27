package com.midea.cloud.srm.model.pj.sou.technicalexchange.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <pre>
 *  技术交流-物料信息 模型
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Apr 28, 2022 10:41:01 AM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "技术交流-物料信息")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_technical_exchange_item")
public class SouTechnicalExchangeItem extends BaseEntity {
    @ApiModelProperty("")
    private static final long serialVersionUID = 121408L;
    /**
     * 技术交流-物料ID
     */
    @ApiModelProperty("技术交流-物料ID")
    @TableId("TECHNICAL_EXCHANGE_ITEM_ID")
    private Long technicalExchangeItemId;
    /**
     * 技术交流单据ID
     */
    @ApiModelProperty("技术交流单据ID")
    @TableField("TECHNICAL_EXCHANGE_ID")
    private Long technicalExchangeId;
    /**
     * 物料ID
     */
    @ApiModelProperty("物料ID")
    @TableField("ITEM_ID")
    private Long itemId;
    /**
     * 物料编码
     */
    @ApiModelProperty("物料编码")
    @TableField("ITEM_CODE")
    private String itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty("物料名称")
    @TableField("ITEM_NAME")
    private String itemName;
    /**
     * 品类id
     */
    @ApiModelProperty("品类id")
    @TableField("CATEGORY_ID")
    private Long categoryId;
    /**
     * 品类编码
     */
    @ApiModelProperty("品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;
    /**
     * 品类名称
     */
    @ApiModelProperty("品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;
    /**
     * 物料需求数量
     */
    @ApiModelProperty("物料需求数量")
    @TableField("DEMAND_QUANTITY")
    private BigDecimal demandQuantity;
    /**
     * 单位
     */
    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;
    /**
     * 物料备注
     */
    @ApiModelProperty("物料备注")
    @TableField("REMARK")
    private String remark;
    /**
     * 是否无物料号
     */
    @ApiModelProperty("是否无物料号，Y:无物料号/N:有物料号")
    @TableField("IS_NOCODE_ITEM")
    private String isNoCodeItem;
}