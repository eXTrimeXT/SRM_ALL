package com.midea.cloud.srm.model.base.notice;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.notice.entry.Notice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_base_notice")
@ApiModel(
        description = "公告"
)
public class PjNotice extends Notice {

    @ApiModelProperty("有效期")
    @TableField("EXT_VALIDITY_DATE")
    private Date extValidityDate;

}
