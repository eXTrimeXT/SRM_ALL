package com.midea.cloud.srm.model.pj.api.interfacelog.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *
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
@ApiModel(description = "日志回写类DTO")
public class LogWriteBackDto {

    /**
     * 状态(SUCCESS:成功，FAIL:失败)
     * 用来判断是否成功或失败
     *
     * @see com.midea.cloud.common.enums.api.ResultStatus
     */
    @TableField("STATUS")
    @ApiModelProperty(value = "状态(SUCCESS:成功，FAIL:失败)")
    private String status;

    @ApiModelProperty(value = "报错信息(没有可以为空)")
    private String errorInfo;
}
