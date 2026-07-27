package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源openAPI - 商务开标
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/02
 */
@Data
@ApiModel(description = "商务开标参数")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiSouBusinessOpenDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouProject#getNeedPwdOperations */
    @ApiModelProperty("商务开标密码(需要时才传递)")
    private String openPwd;

    /** 当前登录人ID，不是所有人都具有商务开标权限 */
    private Long currentUserId;

    public ApiSouBusinessOpenDTO(long projectId) {
        this.projectId = projectId;
    }

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        openPwd = StringUtils.trimToNull(openPwd);
    }

}
