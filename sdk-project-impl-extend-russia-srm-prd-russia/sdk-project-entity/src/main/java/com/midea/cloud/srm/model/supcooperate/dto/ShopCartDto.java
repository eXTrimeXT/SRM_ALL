package com.midea.cloud.srm.model.supcooperate.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.dto.CatalogOnShelvesDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/13
 */
@Data
@TableName("scc_catalog_on_shelves")
public class ShopCartDto extends CatalogOnShelvesDTO {

    @TableId("EXT_PRICE_LIBRARY_STATUS")
    @ApiModelProperty("协议状态")
    private String extPriceLibraryStatus;

    @TableId("EXT_AREA_ID")
    @ApiModelProperty("区域ID")
    private String extAreaId;

    @TableId("EXT_AREA_CODE")
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    @TableId("EXT_AREA_NAME")
    @ApiModelProperty("区域名称")
    private String extAreaName;

    @TableId("EXT_ORG_CODE_LIST")
    @ApiModelProperty("业务实体编码集合")
    private String extOrgCodeList;

}
