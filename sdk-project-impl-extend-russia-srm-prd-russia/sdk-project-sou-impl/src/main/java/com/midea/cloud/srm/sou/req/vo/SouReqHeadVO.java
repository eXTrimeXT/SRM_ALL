package com.midea.cloud.srm.sou.req.vo;

import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import lombok.Data;

/**
 * 寻源需求单头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@Data
public class SouReqHeadVO extends SouReqHead {
    private SccPjSourcePubconfig pjSourcePubconfig;
}
