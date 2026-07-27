package com.midea.cloud.srm.mall.request.jd.afs;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;

@Data
public class AfsApplyRequestDTO extends BaseRequestDTO {
    /**
     * 请求的参数,详情参考京东文档
     */
    private String param;

}
