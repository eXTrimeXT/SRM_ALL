package com.midea.cloud.srm.mall.result.jd.common;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/28 14:28
 *  修改内容:
 * </pre>
 */
@Data
public class GetMessageResultDTO extends JDBaseResult implements CommonResultDTO {

    private List<GetMessage> result;

    @Data
    public static class GetMessage {
        @ApiModelProperty("推送id")
        private Long id;

        @ApiModelProperty("推送时间")
        private Date time;

        @ApiModelProperty("12(配送单生成成功消息)")
        private Integer type;

        @ApiModelProperty("京东订单编号")
        private Order result;
    }

    @Data
    public static class Order {
        @ApiModelProperty("京东订单编号")
        private String orderId;
    }
}
