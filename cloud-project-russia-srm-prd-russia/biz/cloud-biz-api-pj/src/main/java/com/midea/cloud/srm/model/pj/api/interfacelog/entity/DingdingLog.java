package com.midea.cloud.srm.model.pj.api.interfacelog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  订单接口日志 模型
 * </pre>
 *
 * @author kuangzm@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-28 10:58:43
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_api_dingding_log")
@ApiModel(description = "订单接口日志")
public class DingdingLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "钉钉日志ID")
    @TableId("DINGDING_LOG_ID")
    private Long dingdingLogId;

    @ApiModelProperty(value = "请求参数")
    @TableField("PARAM")
    private String param;

    @ApiModelProperty(value = "请求参数")
    @TableField("RETURN_STR")
    private String returnStr;
}
