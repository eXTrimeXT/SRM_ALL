package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetMessageRequestDTO extends BaseRequestDTO {

    /**
     * 推送类型。支持多个组合，英文逗号间隔。例如1,2,3。支持的参考枚举值：2 商品价格变更4 商品上下架变更消息……
     */
    private String type;
}
