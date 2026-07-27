package com.midea.cloud.srm.model.extapi.sou.purinq.dto.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderEditDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouOrderDTO extends ApiSouOrderEditDTO {

    @ApiModelProperty("报价明细")
    protected List<ApiPurInqSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    @Nullable
    protected List<ApiSouOrderFileDTO> orderFileList;
    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField", "AlibabaPojoNoDefaultValue"})
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

    /** @see ExtPurInqSouOrder#getOrderByNickname */
    @ApiModelProperty("报价人")
    private String orderByNickname;
    /** @see ExtPurInqSouOrder#getOrderPhone */
    @ApiModelProperty("报价电话")
    private String orderPhone;
    /** @see ExtPurInqSouOrder#getOrderEmail */
    @ApiModelProperty("报价邮箱")
    private String orderEmail;

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
        orderByNickname = StringUtils.trimToNull(orderByNickname);
        orderPhone = StringUtils.trimToNull(orderPhone);
        orderEmail = StringUtils.trimToNull(orderEmail);
    }

}
