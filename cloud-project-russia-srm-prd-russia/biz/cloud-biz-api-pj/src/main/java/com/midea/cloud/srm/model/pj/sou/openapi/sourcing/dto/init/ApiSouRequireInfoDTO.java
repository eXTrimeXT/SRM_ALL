package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 寻源openAPI - 物料需求信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@SuppressWarnings("ALL")
@Data
@ApiModel("物料需求信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouRequireInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("报价类型(可不填)")
    protected SouOrderTypeEnum orderType;
    @ApiModelProperty(value = "物料需求信息", required = true)
    protected List<ApiSouItemDTO> itemList;
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
