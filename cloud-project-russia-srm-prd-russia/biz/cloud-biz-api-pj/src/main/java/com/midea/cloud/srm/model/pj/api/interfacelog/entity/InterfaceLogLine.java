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
 *  接口日志行表 模型
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
@TableName("scc_api_interface_log_line")
@ApiModel(description = "接口日志行信息")
public class InterfaceLogLine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行ID")
    @TableId("LOG_LINE_ID")
    private Long logLineId;

    @ApiModelProperty(value = "日志ID")
    @TableField("LOG_ID")
    private Long logId;

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
