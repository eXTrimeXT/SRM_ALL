package com.midea.cloud.srm.model.sou.purfixprice.dto;

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
public class ExtPurFixPriceEditDTO extends ExtPurFixPriceHead {

    @ApiModelProperty("最终询价对比表附件")
    private List<ExtPurFixPriceFile> fileList;

    @SuppressWarnings({"AlibabaPojoMustUsePrimitiveField", "AlibabaPojoNoDefaultValue"})
    @ApiModelProperty("true-暂存/false-提交")
    private boolean tempSave = false;

}
