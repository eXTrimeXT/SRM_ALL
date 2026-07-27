package com.midea.cloud.srm.model.pj.sign.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@ApiModel("用印结束回调")
public class SignCallback {
    private static final long serialVersionUID = 829475282755112L;

    /**
     * 签署单业务单据ID
     */
    private Long businessId;

    /**
     * SRM业务单据ID
     */
    private Long srmOrderId;

    /**
     * 状态 USING:表示用印中，COMPLETE:表示正常结束
     */
    private String status;

    /**
     * 业务实现类全路径
     */
    private String bussinessClass;

    /**
     * 合同id signId
     * **/

    private String contractId;
}
