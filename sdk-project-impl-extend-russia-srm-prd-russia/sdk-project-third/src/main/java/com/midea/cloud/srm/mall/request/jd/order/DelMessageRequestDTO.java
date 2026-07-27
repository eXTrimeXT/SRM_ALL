package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DelMessageRequestDTO extends BaseRequestDTO {

    /**
     * https://api-iop.jd.com/api/message/get 中获取的id，支持批量删除，英文逗号间隔，最大100个
     */
    private String id;
}
