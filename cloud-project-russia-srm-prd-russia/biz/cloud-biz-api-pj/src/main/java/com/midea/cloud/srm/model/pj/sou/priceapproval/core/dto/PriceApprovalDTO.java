package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApproval;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 价格审批单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalDTO extends PriceApproval {

    @ApiModelProperty("中标行信息")
    private List<PriceApprovalItemDTO> itemList;

    @ApiModelProperty("附件信息")
    private List<PriceApprovalFile> fileList;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = true;

}
