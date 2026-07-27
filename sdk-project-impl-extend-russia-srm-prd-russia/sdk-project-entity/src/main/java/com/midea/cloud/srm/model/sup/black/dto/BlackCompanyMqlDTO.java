package com.midea.cloud.srm.model.sup.black.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.black.entity.BlackCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/7 10:46
 *  修改内容:
 * </pre>
 */
@Data
public class BlackCompanyMqlDTO extends BlackCompany {

    @ApiModelProperty("股东")
    private String shareholder;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty("原因")
    private String reason;

}
