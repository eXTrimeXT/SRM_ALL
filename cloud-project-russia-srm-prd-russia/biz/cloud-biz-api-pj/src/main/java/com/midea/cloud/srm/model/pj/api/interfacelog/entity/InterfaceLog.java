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

import java.util.Date;

/**
 * <pre>
 *  接口日志表 模型
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
@TableName("scc_api_interface_log")
@ApiModel(description = "接口日志")
public class InterfaceLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志ID")
    @TableId("LOG_ID")
    private Long logId;

    @ApiModelProperty(value = "接口名称")
    @TableField("SERVICE_NAME")
    private String serviceName;

    @ApiModelProperty(value = "接口类型(HTTP,WEBSERVICE)")
    @TableField("SERVICE_TYPE")
    private String serviceType;

    @ApiModelProperty(value = "传输类型(RECEIVE:接收，SEND:发送)")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "状态(SUCCESS:成功，FAIL:失败)")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "业务单据ID")
    @TableField("BILL_ID")
    private String billId;

    @ApiModelProperty(value = "单据类型")
    @TableField("BILL_TYPE")
    private String billType;

    @ApiModelProperty(value = "推送次数")
    @TableField("DEAL_TIME")
    private Long dealTime;

    @ApiModelProperty(value = "完成时间")
    @TableField("FINISH_DATE")
    private Date finishDate;

    @ApiModelProperty(value = "目标系统")
    @TableField("TARGET_SYS")
    private String targetSys;

    @ApiModelProperty(value = "访问地址")
    @TableField("URL")
    private String url;

    @ApiModelProperty(value = "最新行ID")
    @TableField("LINE_LOG_ID")
    private Long lineLogId;

    @ApiModelProperty(value = "传输内容")
    @TableField("SERVICE_INFO")
    private String serviceInfo;

    @ApiModelProperty(value = "返回信息")
    @TableField("RETURN_INFO")
    private String returnInfo;

    @ApiModelProperty(value = "报错信息")
    @TableField("ERROR_INFO")
    private String errorInfo;

}
