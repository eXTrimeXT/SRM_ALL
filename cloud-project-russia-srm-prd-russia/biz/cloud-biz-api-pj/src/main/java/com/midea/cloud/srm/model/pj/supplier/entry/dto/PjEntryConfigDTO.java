package com.midea.cloud.srm.model.pj.supplier.entry.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfig;
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
 *  修改日期: 2023/10/5 10:46
 *  修改内容:
 * </pre>
 */
@Data
public class PjEntryConfigDTO extends EntryConfig {

    @ApiModelProperty("二开-是否需要现场评审")
    @JSONField(name = "pjIfAuth")
    private String pjIfAuth;

    @ApiModelProperty("二开-是样品认证")
    @JSONField(name = "pjIfAuthSample")
    private String pjIfAuthSample;

    @ApiModelProperty("二开-是否物料试用")
    @JSONField(name = "pjIfMaterial")
    private String pjIfMaterial;

}
