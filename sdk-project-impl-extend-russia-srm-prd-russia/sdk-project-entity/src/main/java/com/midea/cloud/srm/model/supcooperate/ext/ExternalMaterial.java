package com.midea.cloud.srm.model.supcooperate.ext;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 外部物料与系统物料映射表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-02-26
 */
@ApiModel(description = "外部物料与系统物料映射表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("scc_npm_external_material")
public class ExternalMaterial extends BaseEntity {
	@ApiModelProperty("主键")
	@TableId
	private Long externalMaterialId;

	@ApiModelProperty("外部物料编码")
	private String externalMaterialCode;

	@ApiModelProperty("外部物料名称")
	private String externalMaterialName;

	@ApiModelProperty("物料id")
	private Long materialId;

	@ApiModelProperty("物料编码")
	private String materialCode;

	@ApiModelProperty("物料名称")
	private String materialName;

	@ApiModelProperty("品牌")
	private String brand;

	@ApiModelProperty("外部物料最后更新时间")
	private Date externalMaterialUpdateDate;

	@ApiModelProperty("物料最后更新时间")
	private Date materialUpdateDate;

	@ApiModelProperty("是否已綁定（N否，Y是）")
	private String mappingFlag;

	@ApiModelProperty("商品类型,长城（CC）,京东（JD）")
	private String materialType;

	@ApiModelProperty("sku编号")
	private String skuId;

	@ApiModelProperty("sku名称")
	private String skuName;

	@ApiModelProperty("外部物料上下架状态 (1上架 0下架)")
	private String materialState;
}
