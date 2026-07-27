package com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/12
 */
@Data
@TableName("scc_catalog_on_shelves")
public class OnShelvesDto {
@TableField("EXT_REFERENCE_PRICE")
    private BigDecimal extReferencePrice;
@TableField("ORDER_QUANTITY_MINIMUM")
    private BigDecimal orderQuantityMinimum;
}
