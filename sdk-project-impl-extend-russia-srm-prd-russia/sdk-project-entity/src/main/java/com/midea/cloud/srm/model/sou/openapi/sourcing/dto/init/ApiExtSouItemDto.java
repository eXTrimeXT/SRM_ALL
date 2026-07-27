package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ApiExtSouItemDto extends BaseObjectX {

    public static final String ERROR_CHECK = "errorMsg";

    public static final String ERROR_MSG = "错误信息";

    @ApiModelProperty("导入校验标识")
    private AtomicBoolean importCheck = new AtomicBoolean(true);

    @ApiModelProperty("报价信息列表")
    private List<ExtSouItem> itemList;

    /**
     * 导入数据
     */
    @ApiModelProperty("导入报价信息列表")
    private List<Map<String, Object>> importList;

    /**
     * 关联招标基本信息主键ID
     */
    @ApiModelProperty("关联招标基本信息主键ID")
    private Long projectId;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean tempSave;

}
