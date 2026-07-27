package com.midea.cloud.srm.model.supcooperate.ext;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 京东推送信息表（内部商城）
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-03-12
 */
@ApiModel(description = "京东推送信息表（内部商城）")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("scc_npm_jd_msg_push")
public class JdMsgPush extends BaseEntity {
	@ApiModelProperty("主键")
	@TableId
	private Long msgPushId;

	@ApiModelProperty("主订单号")
	private String orderId;

	@ApiModelProperty("子订单号")
	private String subOrderId;

	@ApiModelProperty("推送ID")
	private Long jdPushId;

	@ApiModelProperty("消息类型（12配送单生成成功消息）")
	private String type;

	@ApiModelProperty("推送时间")
	private Date pushTime;

	@ApiModelProperty("返回的结果集")
	private String magBody;

	@ApiModelProperty("JD是否已发货（N否，Y是）")
	private String sendFlag;

	@ApiModelProperty("是否已删除推送消息（N否，Y是）")
	private String deleteFlag;

	@ApiModelProperty("是否已创建送货单（N否，Y是）")
	private String deliveryFlag;
}
