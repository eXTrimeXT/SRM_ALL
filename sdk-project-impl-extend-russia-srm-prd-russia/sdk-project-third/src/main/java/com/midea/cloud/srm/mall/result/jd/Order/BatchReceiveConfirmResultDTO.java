package com.midea.cloud.srm.mall.result.jd.Order;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

// 物流信息
@EqualsAndHashCode(callSuper = true)
@Data
public class BatchReceiveConfirmResultDTO extends JDBaseResult implements CommonResultDTO {

    private List<ReceiveConfirm> result;

    @Data
    public static class ReceiveConfirm {
        private Long jdOrderId;
        private Integer confirmState;
        private String errorMsg;
    }


}
