package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 简易询价openAPI - 报价信息
 * PS: 参考 {@link ApiSouOrderDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/07
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderDTO extends ApiSouOrderEditDTO {

    @ApiModelProperty("报价明细")
    protected List<ApiInqSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    @Nullable
    protected List<ApiSouOrderFileDTO> orderFileList;
    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        if (!Enable.Y.equals(isProxy)) {
            // 非代理报价
            isProxy = Enable.N;
            proxyDocId = null;
            proxyFileName = null;
            proxyRemark = null;
        }
        orderNoGenerateCode = StringUtils.trimToNull(orderNoGenerateCode);
        if (orderNoGenerateCode == null) {
            throw new IllegalArgumentException("缺少orderNoGenerateCode参数");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new IllegalArgumentException("缺少报价行数据");
        }
    }

}
