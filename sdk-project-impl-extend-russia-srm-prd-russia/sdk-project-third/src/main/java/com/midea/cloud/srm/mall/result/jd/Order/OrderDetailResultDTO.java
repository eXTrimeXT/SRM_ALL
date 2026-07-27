package com.midea.cloud.srm.mall.result.jd.Order;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import lombok.Data;

@Data
public class OrderDetailResultDTO extends JDBaseResult implements CommonResultDTO {
    private JSONObject result;
}
