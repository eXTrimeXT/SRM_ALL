package com.midea.cloud.srm.model.sou.purfixprice.vo;

import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceVO extends ExtPurFixPriceHead {

    @ApiModelProperty("物料明细")
    private ExtPurFixPriceLineGroupVO itemInfo;
    @ApiModelProperty("最终询价对比表附件")
    private List<ExtPurFixPriceFile> fileList;

}
