package com.midea.cloud.srm.model.pj.api.interfacelog.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * 日志信息类
 * </pre>
 *
 * @author wangpr@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-27 09:24:20
 *  修改内容:
 * </pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "日志信息类DTO")
public class LogInfoDTO extends BaseDTO {
    @ApiModelProperty(value = "服务名")
    private String serviceName;

    /**
     * 接口类型(HTTP,WEBSERVICE)
     *
     * @emum com.midea.cloud.common.enums.api.ServiceType
     */
    @ApiModelProperty(value = "接口类型(HTTP,WEBSERVICE)")
    private String serviceType;

    /**
     * 传输类型(RECEIVE:接收，SEND:发送)
     *
     * @emum com.midea.cloud.common.enums.api.InterfaceType
     */
    @ApiModelProperty(value = "传输类型(RECEIVE:接收，SEND:发送)")
    private String type;

    @ApiModelProperty(value = "业务单据ID")
    private String billId;

    @ApiModelProperty(value = "单据类型")
    private String billType;

    @ApiModelProperty(value = "目标系统")
    private String targetSys;
}
