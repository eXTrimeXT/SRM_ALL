package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order.MqlAuctSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderDTO extends SouOrder {

    @ApiModelProperty("物料需求")
    private List<MqlAuctSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileDTO> orderFileList;
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean tempSave = true;
    @ApiModelProperty("寻源场景")
    private String souType;

}
