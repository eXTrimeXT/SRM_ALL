package com.midea.cloud.srm.sou.purfixprice;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/22
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm
 * 协议定价明细导出Vo
 * 单位字典
 * @author srm
 * @date 2024-08-08
 */
@Data
public class ExtPurFixPriceDownLoadVo {
    @ApiModelProperty("单位编码")
   private String unitCode;
    @ApiModelProperty("单位名称")
   private String unitName;
}
