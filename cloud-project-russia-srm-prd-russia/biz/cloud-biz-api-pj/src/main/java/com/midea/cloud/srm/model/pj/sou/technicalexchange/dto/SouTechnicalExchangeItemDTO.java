package com.midea.cloud.srm.model.pj.sou.technicalexchange.dto;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <pre>
 *  技术交流-物料信息dto 模型
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

@ApiModel(description = "技术交流-物料信息dto")
@Data
@Accessors(chain = true)
public class SouTechnicalExchangeItemDTO implements Serializable {

    private static final long serialVersionUID = 3269796898562556690L;
    /**
     * 技术交流-物料ID
     */
    @ApiModelProperty("技术交流-物料ID")
    private Long technicalExchangeItemId;
    /**
     * 技术交流单据ID
     */
    @ApiModelProperty("技术交流单据ID")
    private Long technicalExchangeId;
    /**
     * 物料ID
     */
    @ApiModelProperty("物料ID")
    private Long itemId;
    /**
     * 物料编码
     */
    @ApiModelProperty("物料编码")
    private String itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty("物料名称")
    private String itemName;
    /**
     * 品类id
     */
    @ApiModelProperty("品类id")
    private Long categoryId;
    /**
     * 品类编码
     */
    @ApiModelProperty("品类编码")
    private String categoryCode;
    /**
     * 品类名称
     */
    @ApiModelProperty("品类名称")
    private String categoryName;
    /**
     * 物料需求数量
     */
    @ApiModelProperty("物料需求数量")
    private BigDecimal demandQuantity;
    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;
    /**
     * 物料备注
     */
    @ApiModelProperty("物料备注")
    private String remark;
    /**
     * 是否无物料号
     */
    @ApiModelProperty("是否无物料号，Y:无物料号/N:有物料号")
    private String isNoCodeItem;

    @ApiModelProperty(value = "物料-技术要求附件")
    private List<SceneFile> itemFiles;
}