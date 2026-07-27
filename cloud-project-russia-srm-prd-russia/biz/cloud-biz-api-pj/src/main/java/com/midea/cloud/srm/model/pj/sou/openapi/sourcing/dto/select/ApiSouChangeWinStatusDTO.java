package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 寻源openAPI - 入围/淘汰
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/03
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "入围/淘汰")
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeWinStatusDTO extends BaseObjectX {

    @ApiModelProperty("竞价单ID")
    protected Long projectId;

    @ApiModelProperty("报价单ID")
    protected Long orderId;

    @ApiModelProperty("报价行信息")
    protected List<ApiSouChangeSelectStatusItemDTO> selects;

    @ApiModelProperty("评选附件")
    protected List<ApiSouSelectFileDTO> selectFileList;

    @ApiModelProperty("审批流")
    private String approvalProcess;

    @ApiModelProperty("true-入围/false-淘汰")
    protected boolean toWin;

    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

    public void formatParams() {
        if (CollectionUtils.isEmpty(selects)) {
            throw new IllegalArgumentException("请选择要入围/淘汰的数据");
        }
    }

}
