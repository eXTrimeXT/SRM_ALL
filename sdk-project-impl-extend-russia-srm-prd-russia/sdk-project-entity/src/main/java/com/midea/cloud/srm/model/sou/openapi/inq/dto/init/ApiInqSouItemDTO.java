package com.midea.cloud.srm.model.sou.openapi.inq.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
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
public class ApiInqSouItemDTO extends ApiSouItemDTO {
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)")
    private String ladderType;
    @ApiModelProperty("是否公式报价(Y/N)")
    private Enable isFormula;
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;
    @ApiModelProperty("公式ID")
    private Long formulaId;
    @ApiModelProperty("行类型[字典值: DMAND_LINE_TYPE]")
    private String itemType;
    @ApiModelProperty("公式名称")
    private String formulaName;
    @ApiModelProperty("公式值")
    private String formulaValue;
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;
    /** @see InqSouItem#getExtMaterialModel */
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;
    @ApiModelProperty("品牌")
    private String extBrand;
    @ApiModelProperty("区域ID")
    private String extAreaId;
    @ApiModelProperty("区域编码")
    private String extAreaCode;
    @ApiModelProperty("区域名称")
    private String extAreaName;
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    private List<SceneFile> itemFiles;
}
