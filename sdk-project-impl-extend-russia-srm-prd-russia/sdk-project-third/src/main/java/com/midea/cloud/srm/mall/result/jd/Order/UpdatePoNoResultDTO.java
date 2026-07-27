package com.midea.cloud.srm.mall.result.jd.Order;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 物流信息
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdatePoNoResultDTO extends JDBaseResult implements CommonResultDTO {

    private Boolean result;

}
