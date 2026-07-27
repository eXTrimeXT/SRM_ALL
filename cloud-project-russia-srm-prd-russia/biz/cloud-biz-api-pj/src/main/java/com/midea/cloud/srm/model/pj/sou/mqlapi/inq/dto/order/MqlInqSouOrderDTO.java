package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.order.MqlInqSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouOrderDTO extends SouOrder {

    @ApiModelProperty("物料需求")
    private List<MqlInqSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileDTO> orderFileList;
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
