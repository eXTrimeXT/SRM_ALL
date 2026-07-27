package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceHeadDTO extends ExtFixPriceHead {

    @ApiModelProperty("物料明细")
    private List<ExtFixPriceLine> lineList;

    @ApiModelProperty("附件")
    private List<ExtFixPriceFile> fileList;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = true;

    /**
     * 校验编码 返回0通过，返回1存在提示信息
     */
    private String checkCode;

    /**
     * 校验提示语
     */
    private String checkMsg;

}
