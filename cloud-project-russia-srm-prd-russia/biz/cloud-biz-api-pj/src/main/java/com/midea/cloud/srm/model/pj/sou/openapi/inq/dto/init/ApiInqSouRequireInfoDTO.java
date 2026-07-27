package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 简易询价openAPI - 物料需求信息
 * PS: 参考 {@link ApiSouRequireInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouRequireInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("报价类型(可不填)")
    protected SouOrderTypeEnum orderType;
    @ApiModelProperty(value = "物料需求信息", required = true)
    protected List<ApiInqSouItemDTO> itemList;
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
