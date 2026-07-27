package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.order;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.order.ApiBrgSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 项目式询价openAPI - 报价头
 * PS: 参考{@link ApiSouOrderDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/04
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderDTO extends ApiSouOrderEditDTO {

    @ApiModelProperty("报价明细")
    protected List<ApiBrgSouOrderItemDTO> orderItemList;
    @ApiModelProperty("供应商报价附件")
    protected List<ApiSouOrderFileDTO> orderFileList;
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode = "SEQ_SOU_BRG_ORDER_NO";
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

    /**
     * 格式化/校验参数
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new IllegalArgumentException("缺少报价信息");
        }
    }

}
