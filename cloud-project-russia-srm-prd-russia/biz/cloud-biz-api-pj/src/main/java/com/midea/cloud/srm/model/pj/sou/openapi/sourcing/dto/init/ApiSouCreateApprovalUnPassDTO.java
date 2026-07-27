package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

/**
 * 寻源openAPI - 立项审批未通过时的回调
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/03
 */
@Data
@ApiModel(description = "立项审批未通过时的回调")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiSouCreateApprovalUnPassDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouProject#getCreateApprovalStatus */
    @ApiModelProperty("立项审批状态")
    private SouApprovalStatusEnum createApprovalStatus;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (createApprovalStatus == null) {
            throw new IllegalArgumentException("缺少createApprovalStatus参数");
        }
        switch (createApprovalStatus) {
            //拟定
            case DRAFT:
                //已提交
            case SUBMITTED:
                //已审批
            case APPROVED:
                throw new IllegalArgumentException(MessageFormat.format("错误的接口调用:参数错误{0}", createApprovalStatus.name()));
            default:;
        }
    }

}
