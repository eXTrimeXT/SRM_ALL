package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderDTO extends SouOrder {

    @ApiModelProperty("物料需求")
    private List<MqlSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileDTO> orderFileList;
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean tempSave = true;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (super.getProjectId() == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (super.getVendorId() == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        if (!Enable.Y.equals(super.getIsProxy())) {
            // 非代理报价
            super.setIsProxy(Enable.N);
            super.setProxyDocId(null);
            super.setProxyFileName(null);
            super.setProxyRemark(null);
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
