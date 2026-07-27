package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouRequireInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("报价类型(可不填)")
    protected SouOrderTypeEnum orderType;
    @ApiModelProperty(value = "物料需求信息", required = true)
    protected List<ApiPurInqSouItemDTO> itemList;
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;
    @ApiModelProperty("报价模板id")
    protected Long quoteTempId;
    @ApiModelProperty("报价模板名称")
    protected String quoteTempName;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(itemList)) {
            throw new IllegalArgumentException("缺少itemList数据");
        }
    }

}
